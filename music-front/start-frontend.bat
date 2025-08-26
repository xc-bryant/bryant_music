@echo off
chcp 65001 >nul
title 音乐播放器前端服务器

echo.
echo 🎵 音乐播放器前端服务器
echo ========================================
echo.

REM 检查Python是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 未找到Python，请先安装Python 3.x
    echo 💡 下载地址: https://www.python.org/downloads/
    pause
    exit /b 1
)

REM 检查必要文件
if not exist "index.html" (
    echo ❌ 缺少index.html文件
    pause
    exit /b 1
)

if not exist "start.html" (
    echo ❌ 缺少start.html文件
    pause
    exit /b 1
)

echo ✅ 检查完成，正在启动服务器...
echo.

REM 启动Python服务器
python start-server.py

pause 