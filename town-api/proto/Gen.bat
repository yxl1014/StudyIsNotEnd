@echo off
setlocal enabledelayedexpansion

REM 设置路径
set PROTO_SOURCE_DIR=source
set JAVA_OUTPUT_DIR=../src/main/java/
set PROTOC=bin/protoc.exe

REM 创建输出目录（如果不存在）
if not exist "%JAVA_OUTPUT_DIR%" mkdir "%JAVA_OUTPUT_DIR%"

REM 检查protoc是否存在
if not exist "%PROTOC%" (
    echo Error: protoc.exe not found in bin folder!
    pause
    exit /b 1
)

REM 检查源文件夹是否存在
if not exist "%PROTO_SOURCE_DIR%" (
    echo Error: source folder not found!
    pause
    exit /b 1
)

REM 统计处理的文件数量
set FILE_COUNT=0

echo Starting protobuf compilation...
echo ========================================

REM 遍历source文件夹中的所有.proto文件
for %%f in ("%PROTO_SOURCE_DIR%\*.proto") do (
    echo Processing: %%~nxf
    "%PROTOC%" --proto_path="%PROTO_SOURCE_DIR%" --java_out="%JAVA_OUTPUT_DIR%" "%%f"

    if errorlevel 1 (
        echo Failed to compile: %%~nxf
    ) else (
        echo Successfully compiled: %%~nxf
        set /a FILE_COUNT+=1
    )
    echo.
)

echo ========================================
echo Compilation completed!
echo Total files processed: %FILE_COUNT%
echo Java files generated in: %JAVA_OUTPUT_DIR%

REM 如果处理了文件，显示生成的目录结构
if %FILE_COUNT% gtr 0 (
    echo.
    echo Directory structure in %JAVA_OUTPUT_DIR%:
    dir "%JAVA_OUTPUT_DIR%" /b
)

pause