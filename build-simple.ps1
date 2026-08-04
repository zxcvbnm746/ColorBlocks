# ColorBlocks Simple Build Script

$ProjectDir = "E:\ModProjects\ColorBlocks"
$BuildDir = "$ProjectDir\build\simple"
$OutputDir = "D:\patrick"

if (Test-Path $BuildDir) {
    Remove-Item $BuildDir -Recurse -Force
}
New-Item -ItemType Directory -Path $BuildDir -Force | Out-Null
New-Item -ItemType Directory -Path "$BuildDir\classes\com\colorblocks" -Force | Out-Null
New-Item -ItemType Directory -Path "$BuildDir\classes\META-INF" -Force | Out-Null

Write-Host "Copying resources..."
Copy-Item -Path "$ProjectDir\src\main\resources\META-INF\neoforge.mods.toml" -Destination "$BuildDir\classes\META-INF\" -Force
Copy-Item -Path "$ProjectDir\src\main\resources\pack.mcmeta" -Destination "$BuildDir\classes\" -Force

Write-Host "Creating placeholder class..."
$code = @"
package com.colorblocks;

public class ColorBlocksPlaceholder {
    public static final String VERSION = "1.0.0";
    public static final int TOTAL_BLOCKS = 98304;
}
"@
[System.IO.File]::WriteAllText("$BuildDir\classes\com\colorblocks\ColorBlocksPlaceholder.java", $code, [System.Text.Encoding]::UTF8)

Write-Host "Compiling..."
javac -d "$BuildDir\classes" -encoding UTF-8 -source 21 -target 21 "$BuildDir\classes\com\colorblocks\ColorBlocksPlaceholder.java" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK"
    Remove-Item "$BuildDir\classes\com\colorblocks\ColorBlocksPlaceholder.java" -Force
}

Write-Host "Creating JAR..."
$JarPath = "$OutputDir\colorblocks-1.0.0.jar"
$ManifestContent = "Manifest-Version: 1.0`r`nAutomatic-Module-Name: colorblocks`r`n"
[System.IO.File]::WriteAllText("$BuildDir\classes\META-INF\MANIFEST.MF", $ManifestContent, [System.Text.Encoding]::ASCII)

Push-Location "$BuildDir\classes"
jar cfm $JarPath META-INF/MANIFEST.MF . 2>&1
Pop-Location

if (Test-Path $JarPath) {
    Write-Host "JAR created: $JarPath"
    Write-Host "Size: $([math]::Round((Get-Item $JarPath).Length / 1KB, 2)) KB"
    jar tf $JarPath
}
