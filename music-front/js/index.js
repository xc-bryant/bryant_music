
/**
 * 解析歌词字符串
 * 得到一个歌词对象的数组
 * 每个歌词的对象
 * {time:开始时间，words:歌词内容}
 */
function parseLrc(lrc) {
    var lines = lrc.split('\n')
    var result = []
    for (var i = 0; i < lines.length; i++) {
        var str = lines[i]
        var parts = str.split(']')
        var timeStr = parts[0].substring(1)
        var obj = {
            time: parseTime(timeStr),
            words: parts[1]
        }
        result.push(obj)
    }

    return result

}

function parseTime(timeStr) {
    var parse = timeStr.split(':')
    // console.log('parse', +parse[0] * 60 + parseFloat(parse[1]))
    return +parse[0] * 60 + parseFloat(parse[1]);
}

var lrcData = parseLrc(text)

var doms = {
    audio: document.querySelector('audio'),
    ul: document.querySelector('ul'),
    container: document.querySelector('.container')
}

/**
 * 计算出在当前的情况下
 * lrcData数组中，应该高亮显示的歌词下表
 */
function findIndex() {
    // 播放当前时间
    var currentTime = doms.audio.currentTime;
    for (var i = 0; i < lrcData.length; i++) {
        if (currentTime < lrcData[i].time) {
            return i - 1;
        }
    }

    // 找遍了都没找到（说明播放最后一句）
    return lrcData.length - 1
}

// 界面
// 创建歌词元素 li
function createLrcElments() {
    var frag = document.createDocumentFragment()
    for (var i = 0; i < lrcData.length; i++) {
        var li = document.createElement('li')
        li.textContent = lrcData[i].words;
        frag.appendChild(li); //改动dom树
    }
    doms.ul.appendChild(frag);
}

createLrcElments()

// 容器高度
var containerHeight = doms.container.clientHeight;
// 每个li的高度
var liHeight = doms.ul.children[0].clientHeight;

var maxOffset = doms.ul.clientHeight - containerHeight;


function setOffSet() {
    var index = findIndex();
    var offset = liHeight * index + liHeight / 2 - containerHeight/2;
    if (offset < 0) {
        offset = 0
    }

    if (offset > maxOffset ) {
        offset = maxOffset
    }

    // 去掉之前的样式
    var li = doms.ul.querySelector('.active')
    if(li){
        li.classList.remove('active')
    }
    doms.ul.style.transform = `translateY(-${offset}px)`

    li = doms.ul.children[index];
    if (li) {
        li.classList.add('active')
    }
}

doms.audio.addEventListener('timeupdate',function(){
    console.log('播放时间变化')
    setOffSet()
})

// 应用初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('音乐播放器应用已启动');
    
    // 检查API连接
    checkAPIConnection();
    
    // 添加一些CSS样式
    addCustomStyles();
});

// 检查API连接
async function checkAPIConnection() {
    try {
        const response = await fetch('http://localhost:8080/api/songs');
        if (response.ok) {
            console.log('✅ 后端API连接正常');
        } else {
            console.warn('⚠️ 后端API连接异常');
            showConnectionWarning();
        }
    } catch (error) {
        console.error('❌ 无法连接到后端API:', error);
        showConnectionWarning();
    }
}

// 显示连接警告
function showConnectionWarning() {
    const warning = document.createElement('div');
    warning.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: #f39c12;
        color: white;
        padding: 15px 20px;
        border-radius: 5px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        z-index: 1002;
        font-weight: 500;
    `;
    warning.innerHTML = `
        <i class="fas fa-exclamation-triangle"></i>
        无法连接到后端服务器，请确保后端服务已启动
    `;
    document.body.appendChild(warning);
    
    // 5秒后自动移除
    setTimeout(() => {
        if (warning.parentNode) {
            warning.parentNode.removeChild(warning);
        }
    }, 5000);
}

// 添加自定义样式
function addCustomStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .no-songs {
            text-align: center;
            padding: 50px;
            color: #7f8c8d;
            font-size: 16px;
        }
        
        .favorite-btn.active {
            background: #e74c3c !important;
        }
        
        .song-card {
            position: relative;
        }
        
        .song-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(45deg, #3498db, #9b59b6);
            opacity: 0;
            border-radius: 10px;
            transition: opacity 0.3s ease;
            z-index: -1;
        }
        
        .song-card:hover::before {
            opacity: 0.05;
        }
        
        .action-btn {
            position: relative;
            overflow: hidden;
        }
        
        .action-btn::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: width 0.3s ease, height 0.3s ease;
        }
        
        .action-btn:hover::before {
            width: 100%;
            height: 100%;
        }
        
        .loading-overlay {
            backdrop-filter: blur(5px);
        }
        
        .loading-spinner {
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }
        
        .message {
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.2);
        }
        
        .progress-bar {
            position: relative;
            overflow: hidden;
        }
        
        .progress-bar::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            animation: shimmer 2s infinite;
        }
        
        @keyframes shimmer {
            0% { left: -100%; }
            100% { left: 100%; }
        }
        
        .volume-control input[type="range"] {
            -webkit-appearance: none;
            appearance: none;
            height: 4px;
            border-radius: 2px;
            background: rgba(255,255,255,0.2);
            outline: none;
        }
        
        .volume-control input[type="range"]::-webkit-slider-thumb {
            -webkit-appearance: none;
            appearance: none;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #3498db;
            cursor: pointer;
        }
        
        .volume-control input[type="range"]::-moz-range-thumb {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #3498db;
            cursor: pointer;
            border: none;
        }
        
        .search-container input {
            background: rgba(255,255,255,0.9);
            backdrop-filter: blur(10px);
        }
        
        .search-container input:focus {
            background: white;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }
        
        .nav-item {
            position: relative;
        }
        
        .nav-item::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 0;
            height: 2px;
            background: #3498db;
            transition: width 0.3s ease;
        }
        
        .nav-item:hover::after,
        .nav-item.active::after {
            width: 100%;
        }
        
        .btn {
            position: relative;
            overflow: hidden;
        }
        
        .btn::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: width 0.3s ease, height 0.3s ease;
        }
        
        .btn:hover::before {
            width: 200%;
            height: 200%;
        }
    `;
    document.head.appendChild(style);
}

// 键盘快捷键支持
document.addEventListener('keydown', function(e) {
    // 空格键：播放/暂停
    if (e.code === 'Space' && !e.target.matches('input, textarea')) {
        e.preventDefault();
        musicPlayer.togglePlay();
    }
    
    // 左箭头：上一首
    if (e.code === 'ArrowLeft' && e.ctrlKey) {
        e.preventDefault();
        musicPlayer.playPrevious();
    }
    
    // 右箭头：下一首
    if (e.code === 'ArrowRight' && e.ctrlKey) {
        e.preventDefault();
        musicPlayer.playNext();
    }
    
    // Ctrl + S：搜索
    if (e.code === 'KeyS' && e.ctrlKey) {
        e.preventDefault();
        document.getElementById('searchInput').focus();
    }
    
    // Ctrl + U：上传
    if (e.code === 'KeyU' && e.ctrlKey) {
        e.preventDefault();
        uiManager.switchSection('upload');
    }
    
    // Ctrl + F：收藏
    if (e.code === 'KeyF' && e.ctrlKey) {
        e.preventDefault();
        uiManager.switchSection('favorites');
    }
});

// 页面可见性变化处理
document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        // 页面隐藏时暂停播放
        if (musicPlayer.isPlaying) {
            musicPlayer.pause();
        }
    }
});

// 错误处理
window.addEventListener('error', function(e) {
    console.error('应用错误:', e.error);
    musicPlayer.showMessage('应用发生错误，请刷新页面', 'error');
});

// 未处理的Promise拒绝
window.addEventListener('unhandledrejection', function(e) {
    console.error('未处理的Promise拒绝:', e.reason);
    musicPlayer.showMessage('操作失败，请重试', 'error');
});

// 导出全局变量供调试使用
window.debug = {
    musicAPI: window.musicAPI,
    musicPlayer: window.musicPlayer,
    uiManager: window.uiManager
};