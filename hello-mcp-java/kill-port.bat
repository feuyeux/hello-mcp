@echo off
setlocal enabledelayedexpansion

if "%~1"=="" (
    echo Usage: kill-port PORT
    echo Example: kill-port 9900
    exit /b 1
)

set PORT=%~1

echo Finding process using port %PORT%...

set PID=
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT% ^| findstr LISTENING') do (
    set PID=%%a
    goto :found
)

:found
if not defined PID (
    echo No process found using port %PORT%
    exit /b 1
)

echo Found process with PID: %PID%
echo Killing process...

taskkill //F //PID %PID% >nul 2>nul
if %errorlevel%==0 (
    echo Successfully killed process %PID% on port %PORT%
) else (
    echo Failed to kill process %PID%
    exit /b 1
)

exit /b 0
