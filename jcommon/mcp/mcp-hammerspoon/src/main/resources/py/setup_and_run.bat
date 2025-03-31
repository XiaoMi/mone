@echo off
setlocal enabledelayedexpansion

echo ===== WeCom Automation Tool Startup Script =====
echo.

REM Check if cleanup is needed
if "%1"=="clean" (
    echo Clean mode: Will delete existing virtual environment and recreate it
    if exist .venv (
        echo Deleting existing virtual environment...
        rmdir /s /q .venv
        if exist .venv (
            echo Unable to delete virtual environment. Please manually delete the .venv directory and try again.
            pause
            exit /b 1
        )
        echo Virtual environment deleted.
    )
)

REM Check if uv is installed
echo Checking if uv is installed...
where uv >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo uv is already installed, skipping installation.
    
) else (
    echo uv is not installed, installing now...
    echo Downloading uv installer...

    powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"
    
)
echo.

REM Use uv sync to synchronize current environment
echo Synchronizing environment using uv sync...
if not exist pyproject.toml (
    echo Error: pyproject.toml file not found, cannot synchronize environment.
    pause
    exit /b 1
)

if exist uv.lock (
    echo Found uv.lock file, using it for synchronization...
    uv sync
) else (
    echo No uv.lock file found, synchronizing from pyproject.toml...
    uv sync
)

if %ERRORLEVEL% NEQ 0 (
    echo Failed to synchronize environment.
    pause
    exit /b 1
)
echo Environment synchronized successfully.

REM Activate virtual environment
echo Activating virtual environment...
call .venv\Scripts\activate.bat
if %ERRORLEVEL% NEQ 0 (
    echo Failed to activate virtual environment.
    pause
    exit /b 1
)
echo Virtual environment activated.

REM Run main.py
echo Starting application...
echo Application will run at http://0.0.0.0:8000
echo You can access this address in your browser to use the application
echo Press Ctrl+C to stop the application

python main.py
if %ERRORLEVEL% NEQ 0 (
    echo Application failed to run.
    pause
    exit /b 1
)

REM Script end
echo Application has been closed.
echo.
echo To recreate the virtual environment, run this script with the clean parameter:
echo setup_and_run.bat clean
echo.
pause
