#!/bin/bash

# 音乐播放器前端服务器启动脚本

echo ""
echo "🎵 音乐播放器前端服务器"
echo "========================================"
echo ""

# 检查Python是否安装
if ! command -v python3 &> /dev/null; then
    echo "❌ 未找到Python3，请先安装Python 3.x"
    echo "💡 Ubuntu/Debian: sudo apt install python3"
    echo "💡 CentOS/RHEL: sudo yum install python3"
    echo "💡 macOS: brew install python3"
    exit 1
fi

# 检查必要文件
if [ ! -f "index.html" ]; then
    echo "❌ 缺少index.html文件"
    exit 1
fi

if [ ! -f "start.html" ]; then
    echo "❌ 缺少start.html文件"
    exit 1
fi

echo "✅ 检查完成，正在启动服务器..."
echo ""

# 启动Python服务器
python3 start-server.py 