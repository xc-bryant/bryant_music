#!/usr/bin/env python3
"""
简单的HTTP服务器，用于运行音乐播放器前端项目
"""

import http.server
import socketserver
import os
import sys
import webbrowser
from urllib.parse import urlparse

# 配置
PORT = 3000
DIRECTORY = os.path.dirname(os.path.abspath(__file__))

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)
    
    def end_headers(self):
        # 添加CORS头，允许跨域请求
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        super().end_headers()
    
    def do_OPTIONS(self):
        # 处理预检请求
        self.send_response(200)
        self.end_headers()

def main():
    print("🎵 音乐播放器前端服务器")
    print("=" * 40)
    print(f"📁 服务目录: {DIRECTORY}")
    print(f"🌐 服务地址: http://localhost:{PORT}")
    print(f"📄 启动页面: http://localhost:{PORT}/start.html")
    print(f"🎮 主应用: http://localhost:{PORT}/index.html")
    print("=" * 40)
    
    # 检查必要文件
    required_files = ['index.html', 'start.html']
    missing_files = [f for f in required_files if not os.path.exists(os.path.join(DIRECTORY, f))]
    
    if missing_files:
        print(f"❌ 缺少必要文件: {', '.join(missing_files)}")
        sys.exit(1)
    
    try:
        with socketserver.TCPServer(("", PORT), CustomHTTPRequestHandler) as httpd:
            print(f"✅ 服务器启动成功！")
            print(f"🚀 正在打开浏览器...")
            
            # 自动打开浏览器
            try:
                webbrowser.open(f'http://localhost:{PORT}/start.html')
            except:
                print("⚠️  无法自动打开浏览器，请手动访问上述地址")
            
            print(f"⏹️  按 Ctrl+C 停止服务器")
            print("-" * 40)
            
            httpd.serve_forever()
            
    except KeyboardInterrupt:
        print("\n👋 服务器已停止")
    except OSError as e:
        if e.errno == 48:  # Address already in use
            print(f"❌ 端口 {PORT} 已被占用，请尝试其他端口")
            print(f"💡 可以修改脚本中的 PORT 变量")
        else:
            print(f"❌ 启动服务器失败: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 