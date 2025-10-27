@echo off
chcp 65001 >nul
echo ======================================
echo   竞赛集训系统 - Nginx 服务管理
echo ======================================
echo.

:menu
echo 请选择操作:
echo [1] 启动 Nginx
echo [2] 停止 Nginx
echo [3] 重新加载配置
echo [4] 测试配置
echo [5] 查看状态
echo [0] 退出
echo.
set /p choice=请输入选项 (0-5): 

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto reload
if "%choice%"=="4" goto test
if "%choice%"=="5" goto status
if "%choice%"=="0" goto end
echo 无效选项，请重新选择！
goto menu

:start
echo.
echo [启动 Nginx...]
start nginx.exe
timeout /t 2 >nul
echo Nginx 已启动！
echo 访问地址: http://localhost
echo.
pause
goto menu

:stop
echo.
echo [停止 Nginx...]
nginx.exe -s stop
echo Nginx 已停止！
echo.
pause
goto menu

:reload
echo.
echo [重新加载配置...]
nginx.exe -s reload
echo 配置已重新加载！
echo.
pause
goto menu

:test
echo.
echo [测试配置文件...]
nginx.exe -t
echo.
pause
goto menu

:status
echo.
echo [检查 Nginx 进程...]
tasklist | findstr nginx.exe
if %errorlevel%==0 (
    echo.
    echo ✓ Nginx 正在运行
) else (
    echo.
    echo ✗ Nginx 未运行
)
echo.
pause
goto menu

:end
echo.
echo 再见！
timeout /t 1 >nul
exit
