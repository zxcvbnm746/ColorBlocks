@echo off
REM Gradle wrapper with custom Java options for SSL bypass

setlocal

REM 禁用SSL验证（尝试绕过DPI检测）
set JAVA_OPTS=-Djavax.net.ssl.trustAll=true -Dsun.net.ssl.allowUnsafeRenegotiation=true

REM 设置自定义DNS（使用Google DNS）
set JAVA_OPTS=%JAVA_OPTS% -Dsun.net.spi.nameservice.provider.1=dns,sun -Dsun.net.spi.nameservice.nameservers=8.8.8.8,8.8.4.4

REM 增加超时时间
set JAVA_OPTS=%JAVA_OPTS% -Dsun.net.client.defaultConnectTimeout=60000 -Dsun.net.client.defaultReadTimeout=60000

REM 调用原始gradlew
call "%~dp0gradlew.bat" %*

endlocal
