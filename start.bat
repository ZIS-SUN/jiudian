@echo off
chcp 65001 >nul
echo.
echo ===================================
echo     酒店管理系统启动脚本
echo ===================================
echo.

echo [INFO] 正在启动后端服务 (端口: 8089)...
cd /d "%~dp0\backend"
start "后端服务" cmd /k "mvn spring-boot:run"

timeout /t 5 /nobreak >nul

echo [INFO] 正在启动前端服务 (端口: 5173)...
cd /d "%~dp0\frontend"
start "前端服务" cmd /k "npm run dev"

echo.
echo [SUCCESS] 启动完成！
echo.
echo 访问地址:
echo   前端: http://localhost:5173
echo   后端: http://localhost:8089/api
echo.
echo 按任意键退出...
pause >nul