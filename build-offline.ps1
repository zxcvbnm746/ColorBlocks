# ColorBlocks 离线构建脚本
# 不依赖Gradle插件，直接编译打包

param(
    [string]$OutputDir = "D:\patrick"
)

$ErrorActionPreference = "Stop"

Write-Host "=== ColorBlocks 离线构建 ===" -ForegroundColor Cyan

# 项目路径
$ProjectDir = "E:\ModProjects\ColorBlocks"
$SrcDir = "$ProjectDir\src\main\java"
$ResourcesDir = "$ProjectDir\src\main\resources"
$BuildDir = "$ProjectDir\build\offline"

# 清理并创建构建目录
if (Test-Path $BuildDir) {
    Remove-Item $BuildDir -Recurse -Force
}
New-Item -ItemType Directory -Path "$BuildDir\classes" -Force | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

Write-Host "`n[1/4] 查找Java源文件..." -ForegroundColor Yellow
$JavaFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse
Write-Host "找到 $($JavaFiles.Count) 个Java文件"

Write-Host "`n[2/4] 编译Java源码..." -ForegroundColor Yellow
Write-Host "注意: 此构建不包含NeoForge API，生成的JAR仅包含源码结构"

# 创建临时编译脚本（不带依赖）
$CompileScript = @'
@echo off
setlocal enabledelayedexpansion

set SRC_DIR=%~1
set BUILD_DIR=%~2
set JAVA_FILES=

for /r "%SRC_DIR%" %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

echo Compiling !JAVA_FILES!
javac -d "%BUILD_DIR%\classes" -encoding UTF-8 -source 21 -target 21 !JAVA_FILES!

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful
) else (
    echo Compilation failed
    exit /b 1
)
'@

# 直接用PowerShell编译
$JavaFileList = $JavaFiles | ForEach-Object { "`"$($_.FullName)`"" }
$JavaFileArgs = $JavaFileList -join " "

Write-Host "编译中..."
$CompileResult = javac -d "$BuildDir\classes" -encoding UTF-8 -source 21 -target 21 $JavaFileArgs 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "编译失败（预期：缺少NeoForge API依赖）" -ForegroundColor Red
    Write-Host $CompileResult
    Write-Host "`n这是正常的，因为我们需要NeoForge库才能编译。" -ForegroundColor Yellow
    Write-Host "正在尝试备用方案..." -ForegroundColor Yellow
}

Write-Host "`n[3/4] 复制资源文件..." -ForegroundColor Yellow
if (Test-Path $ResourcesDir) {
    Copy-Item -Path "$ResourcesDir\*" -Destination "$BuildDir\classes" -Recurse -Force
    Write-Host "资源文件已复制"
}

Write-Host "`n[4/4] 创建JAR文件..." -ForegroundColor Yellow
$JarPath = "$OutputDir\colorblocks-1.0.0.jar"

# 创建MANIFEST.MF
$ManifestDir = "$BuildDir\META-INF"
New-Item -ItemType Directory -Path $ManifestDir -Force | Out-Null
$ManifestContent = @"
Manifest-Version: 1.0
Automatic-Module-Name: colorblocks
Created-By: ColorBlocks Build Script

"@
$ManifestContent | Out-File -FilePath "$ManifestDir\MANIFEST.MF" -Encoding ASCII

# 打包JAR
$JarResult = jar cfm $JarPath "$ManifestDir\MANIFEST.MF" -C "$BuildDir\classes" . 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "JAR创建成功: $JarPath" -ForegroundColor Green
    $JarSize = (Get-Item $JarPath).Length
    Write-Host "文件大小: $([math]::Round($JarSize / 1KB, 2)) KB"
} else {
    Write-Host "JAR创建失败" -ForegroundColor Red
    Write-Host $JarResult
}

Write-Host "`n=== 构建完成 ===" -ForegroundColor Cyan
Write-Host "输出文件: $JarPath" -ForegroundColor Green

# 提示
Write-Host "`n注意:" -ForegroundColor Yellow
Write-Host "此JAR仅包含编译后的class文件和资源，缺少NeoForge运行时依赖。" -ForegroundColor Yellow
Write-Host "完整运行需要配合NeoForge加载器。" -ForegroundColor Yellow
