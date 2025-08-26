@echo off
echo ========================================
echo 音乐系统后端启动脚本
echo ========================================

echo.
echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK 8+
    pause
    exit /b 1
)

echo.
echo 正在检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven 3.6+
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo 正在启动应用...
echo 应用将在 http://localhost:8080 启动
echo 按 Ctrl+C 停止应用
echo.

mvn spring-boot:run

pause 