#!/bin/bash

echo ""
echo "==================================="
echo "     酒店管理系统启动脚本"
echo "==================================="
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "[INFO] 正在启动后端服务 (端口: 8089)..."
cd "$SCRIPT_DIR/backend"

# 检查是否为Windows环境 (Git Bash)
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    start "后端服务" bash -c "mvn spring-boot:run; exec bash"
else
    # Linux/macOS环境
    gnome-terminal --title="后端服务" -- bash -c "mvn spring-boot:run; exec bash" 2>/dev/null || \
    xterm -title "后端服务" -e "mvn spring-boot:run; exec bash" 2>/dev/null || \
    bash -c "mvn spring-boot:run" &
fi

echo "[INFO] 等待后端服务启动..."
sleep 5

echo "[INFO] 正在启动前端服务 (端口: 5173)..."
cd "$SCRIPT_DIR/frontend"

# 检查是否为Windows环境 (Git Bash)
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    start "前端服务" bash -c "npm run dev; exec bash"
else
    # Linux/macOS环境
    gnome-terminal --title="前端服务" -- bash -c "npm run dev; exec bash" 2>/dev/null || \
    xterm -title "前端服务" -e "npm run dev; exec bash" 2>/dev/null || \
    bash -c "npm run dev" &
fi

echo ""
echo "[SUCCESS] 启动完成！"
echo ""
echo "访问地址:"
echo "  前端: http://localhost:5173"
echo "  后端: http://localhost:8089/api"
echo ""