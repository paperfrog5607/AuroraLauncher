@echo off
setlocal enabledelayedexpansion
set WIX_PATH=C:\Program Files (x86)\WiX Toolset v3.14\bin
set SRC_DIR=..\ui\build\distribute\AuroraLauncher
set OUT_DIR=..\ui\build\distribute

echo === Aurora Launcher WiX Installer ===
echo.

if not exist "%SRC_DIR%\AuroraLauncher.exe" (
    echo Error: AuroraLauncher.exe not found
    echo Please run: gradlew :ui:packagePortable first
    pause
    exit /b 1
)

if not exist obj mkdir obj

echo [1/4] Harvesting files...
"%WIX_PATH%\heat.exe" dir "%SRC_DIR%" -cg HarvestComponents -gg -scom -sreg -sfrag -srd -dr INSTALLDIR -var var.SourceDir -out harvested.wxs
if errorlevel 1 (
    echo Error: heat failed
    pause
    exit /b 1
)

echo [2/4] Compiling WiX sources...
"%WIX_PATH%\candle.exe" -dSourceDir="%SRC_DIR%" AuroraLauncher.wxs harvested.wxs -out obj\ -ext WixUtilExtension
if errorlevel 1 (
    echo Error: candle failed
    pause
    exit /b 1
)

echo [3/4] Linking installer...
"%WIX_PATH%\light.exe" obj\AuroraLauncher.wixobj obj\harvested.wixobj -out "%OUT_DIR%\AuroraLauncher-1.0.0.msi" -ext WixUtilExtension -ext WixUIExtension -cultures:zh-CN
if errorlevel 1 (
    echo Error: light failed
    pause
    exit /b 1
)

echo [4/4] Cleaning up...
del harvested.wxs 2>nul
rd /s /q obj 2>nul

echo.
echo === Build Complete ===
echo Output: %OUT_DIR%\AuroraLauncher-1.0.0.msi
echo.
pause