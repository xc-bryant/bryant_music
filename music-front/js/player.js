// 音乐播放器类
class MusicPlayer {
    constructor() {
        this.audio = document.getElementById('audioPlayer');
        this.currentSong = null;
        this.playlist = [];
        this.currentIndex = -1;
        this.isPlaying = false;
        this.volume = 0.5;
        
        this.initializePlayer();
        this.bindEvents();
    }

    // 初始化播放器
    initializePlayer() {
        this.audio.volume = this.volume;
        this.updatePlayerDisplay();
    }

    // 绑定事件
    bindEvents() {
        // 播放/暂停按钮
        document.getElementById('playBtn').addEventListener('click', () => {
            this.togglePlay();
        });

        // 上一首
        document.getElementById('prevBtn').addEventListener('click', () => {
            this.playPrevious();
        });

        // 下一首
        document.getElementById('nextBtn').addEventListener('click', () => {
            this.playNext();
        });

        // 进度条
        document.getElementById('progressSlider').addEventListener('input', (e) => {
            this.seekTo(e.target.value);
        });
        // 进度条点击跳转
        document.getElementById('progressBar').addEventListener('click', (e) => {
            const bar = e.currentTarget;
            const rect = bar.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const percent = Math.max(0, Math.min(1, x / rect.width));
            if (this.audio.duration) {
                this.audio.currentTime = percent * this.audio.duration;
            }
        });

        // 音量控制
        document.getElementById('volumeSlider').addEventListener('input', (e) => {
            this.setVolume(e.target.value / 100);
        });

        // 歌词板弹出/收起
        document.getElementById('playerBar').addEventListener('click', () => {
            const panel = document.getElementById('lyricsPanel');
            panel.classList.toggle('collapsed');
        });

        // 音频事件
        this.audio.addEventListener('loadedmetadata', () => {
            this.updateTotalTime();
        });

        this.audio.addEventListener('timeupdate', () => {
            this.updateProgress();
        });

        this.audio.addEventListener('ended', () => {
            this.playNext();
        });

        this.audio.addEventListener('error', (e) => {
            console.error('音频播放错误:', e);
            this.showMessage('音频播放失败', 'error');
        });
    }

    // 播放歌曲
    async playSong(song) {
        try {
            this.showLoading(true);
            
            // 设置音频源
            const audioUrl = musicAPI.getAudioStream(song.id);
            this.audio.src = audioUrl;
            
            // 更新当前歌曲信息
            this.currentSong = song;
            this.updatePlayerInfo(song);
            
            // 增加播放次数
            try {
                await musicAPI.playSong(song.id);
            } catch (error) {
                console.warn('播放次数更新失败:', error);
            }
            
            // 播放音频
            await this.audio.play();
            this.isPlaying = true;
            this.updatePlayButton();
            
            // 加载歌词
            this.loadLyrics(song);

            this.showMessage(`正在播放: ${song.title}`, 'success');
        } catch (error) {
            console.error('播放失败:', error);
            this.showMessage('播放失败: ' + error.message, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    // 播放/暂停切换
    togglePlay() {
        if (!this.currentSong) {
            this.showMessage('请先选择歌曲', 'info');
            return;
        }

        if (this.isPlaying) {
            this.pause();
        } else {
            this.play();
        }
    }

    // 播放
    async play() {
        try {
            await this.audio.play();
            this.isPlaying = true;
            this.updatePlayButton();
        } catch (error) {
            console.error('播放失败:', error);
            this.showMessage('播放失败', 'error');
        }
    }

    // 暂停
    pause() {
        this.audio.pause();
        this.isPlaying = false;
        this.updatePlayButton();
    }

    // 停止
    stop() {
        this.audio.pause();
        this.audio.currentTime = 0;
        this.isPlaying = false;
        this.updatePlayButton();
    }

    // 播放上一首
    playPrevious() {
        if (this.playlist.length === 0) return;
        
        if (this.currentIndex > 0) {
            this.currentIndex--;
        } else {
            this.currentIndex = this.playlist.length - 1;
        }
        
        const song = this.playlist[this.currentIndex];
        this.playSong(song);
    }

    // 播放下一首
    playNext() {
        if (this.playlist.length === 0) return;
        
        if (this.currentIndex < this.playlist.length - 1) {
            this.currentIndex++;
        } else {
            this.currentIndex = 0;
        }
        
        const song = this.playlist[this.currentIndex];
        this.playSong(song);
    }

    // 设置播放列表
    setPlaylist(songs) {
        this.playlist = songs;
        this.currentIndex = -1;
    }

    // 跳转到指定时间
    seekTo(percent) {
        if (this.audio.duration) {
            const time = (percent / 100) * this.audio.duration;
            this.audio.currentTime = time;
        }
    }

    // 设置音量
    setVolume(volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        this.audio.volume = this.volume;
        
        // 更新音量图标
        const volumeIcon = document.querySelector('.volume-control i');
        if (this.volume === 0) {
            volumeIcon.className = 'fas fa-volume-mute';
        } else if (this.volume < 0.5) {
            volumeIcon.className = 'fas fa-volume-down';
        } else {
            volumeIcon.className = 'fas fa-volume-up';
        }
    }

    // 更新播放按钮状态
    updatePlayButton() {
        const playBtn = document.getElementById('playBtn');
        const icon = playBtn.querySelector('i');
        
        if (this.isPlaying) {
            icon.className = 'fas fa-pause';
        } else {
            icon.className = 'fas fa-play';
        }
    }

    // 更新播放器信息
    updatePlayerInfo(song) {
        document.getElementById('currentSongTitle').textContent = song.title || '未知歌曲';
        document.getElementById('currentSongArtist').textContent = song.artist || '未知艺术家';
    }

    // 更新进度条
    updateProgress() {
        if (this.audio.duration) {
            const percent = (this.audio.currentTime / this.audio.duration) * 100;
            document.getElementById('progressFill').style.width = percent + '%';
            document.getElementById('progressSlider').value = percent;
            
            // 更新当前时间显示
            const currentTime = musicAPI.formatTime(this.audio.currentTime);
            document.getElementById('currentTime').textContent = currentTime;
            // 歌词高亮
            this.updateLyricsHighlight();
        }
    }

    // 更新总时长
    updateTotalTime() {
        const totalTime = musicAPI.formatTime(this.audio.duration);
        document.getElementById('totalTime').textContent = totalTime;
    }

    // 更新播放器显示
    updatePlayerDisplay() {
        if (!this.currentSong) {
            document.getElementById('currentSongTitle').textContent = '未选择歌曲';
            document.getElementById('currentSongArtist').textContent = '未知艺术家';
            document.getElementById('currentTime').textContent = '0:00';
            document.getElementById('totalTime').textContent = '0:00';
        }
    }

    // 获取当前播放状态
    getCurrentSong() {
        return this.currentSong;
    }

    // 获取播放状态
    getPlayingState() {
        return this.isPlaying;
    }

    // 显示加载状态
    showLoading(show) {
        const overlay = document.getElementById('loadingOverlay');
        if (show) {
            overlay.classList.add('show');
        } else {
            overlay.classList.remove('show');
        }
    }

    // 显示消息
    showMessage(message, type = 'info') {
        const container = document.getElementById('messageContainer');
        const messageEl = document.createElement('div');
        messageEl.className = `message ${type}`;
        
        const icon = document.createElement('i');
        switch (type) {
            case 'success':
                icon.className = 'fas fa-check-circle';
                break;
            case 'error':
                icon.className = 'fas fa-exclamation-circle';
                break;
            default:
                icon.className = 'fas fa-info-circle';
        }
        
        const text = document.createElement('span');
        text.textContent = message;
        
        messageEl.appendChild(icon);
        messageEl.appendChild(text);
        container.appendChild(messageEl);
        
        // 3秒后自动移除
        setTimeout(() => {
            if (messageEl.parentNode) {
                messageEl.parentNode.removeChild(messageEl);
            }
        }, 3000);
    }

    // 歌词相关
    async loadLyrics(song) {
        const lyricsDiv = document.getElementById('lyrics');
        lyricsDiv.innerHTML = '<span style="color:#bbb">加载歌词中...</span>';
        if (!song.lyricsFileName) {
            lyricsDiv.innerHTML = '<span style="color:#bbb">暂无歌词</span>';
            this.lyricsData = null;
            return;
        }
        try {
            const url = musicAPI.getLyricsDownload(song.id);
            const res = await fetch(url);
            if (!res.ok) throw new Error('歌词文件加载失败');
            const text = await res.text();
            if (song.lyricsFileName.endsWith('.lrc')) {
                this.lyricsData = this.parseLRC(text);
                this.renderLyrics();
            } else {
                this.lyricsData = null;
                lyricsDiv.innerHTML = `<div class='lyric-line active'>${text.replace(/\n/g, '<br>')}</div>`;
            }
        } catch (e) {
            lyricsDiv.innerHTML = '<span style="color:#bbb">歌词加载失败</span>';
            this.lyricsData = null;
        }
    }
    // 解析LRC歌词（支持多时间戳，精确到毫秒）
    parseLRC(lrc) {
        const lines = lrc.split(/\r?\n/);
        const result = [];
        const timeExp = /\[(\d{1,2}):(\d{2})(?:\.(\d{1,3}))?\]/g;
        for (const line of lines) {
            let timeMatches = [...line.matchAll(timeExp)];
            if (timeMatches.length === 0) continue;
            const text = line.replace(timeExp, '').trim();
            for (const match of timeMatches) {
                const min = parseInt(match[1]);
                const sec = parseInt(match[2]);
                const ms = match[3] ? parseInt(match[3].padEnd(3, '0')) : 0;
                const time = min * 60 + sec + ms / 1000;
                result.push({ time, text });
            }
        }
        result.sort((a, b) => a.time - b.time);
        return result;
    }
    // 渲染歌词（带时间戳）
    renderLyrics() {
        const lyricsDiv = document.getElementById('lyrics');
        if (!this.lyricsData || this.lyricsData.length === 0) {
            lyricsDiv.innerHTML = '<span style="color:#bbb">暂无歌词</span>';
            return;
        }
        lyricsDiv.innerHTML = this.lyricsData.map((line, idx) => {
            const min = String(Math.floor(line.time / 60)).padStart(2, '0');
            const sec = String(Math.floor(line.time % 60)).padStart(2, '0');
            const ms = String(Math.floor((line.time * 100) % 100)).padStart(2, '0');
            const timeStr = `[${min}:${sec}.${ms}]`;
            return `<div class="lyric-line" data-idx="${idx}"><span style="color:#aaa;font-size:13px;margin-right:8px;">${timeStr}</span>${line.text}</div>`;
        }).join('');
    }
    // 歌词滚动和高亮（严格同步）
    updateLyricsHighlight() {
        if (!this.lyricsData || this.lyricsData.length === 0) return;
        const current = this.audio.currentTime;
        let idx = 0;
        for (let i = 0; i < this.lyricsData.length; i++) {
            const curTime = this.lyricsData[i].time;
            const nextTime = (i + 1 < this.lyricsData.length) ? this.lyricsData[i + 1].time : Number.POSITIVE_INFINITY;
            if (current >= curTime && current < nextTime) {
                idx = i;
                break;
            }
            if (current >= this.lyricsData[this.lyricsData.length - 1].time) {
                idx = this.lyricsData.length - 1;
            }
        }
        const lines = document.querySelectorAll('#lyrics .lyric-line');
        lines.forEach((el, i) => {
            el.classList.remove('active', 'prev', 'next', 'far');
            if (i === idx) {
                el.classList.add('active');
            } else if (i === idx - 1 || i === idx + 1) {
                el.classList.add('prev');
            } else if (i === idx - 2 || i === idx + 2) {
                el.classList.add('next');
            } else if (Math.abs(i - idx) > 2) {
                el.classList.add('far');
            }
        });
        // 歌词容器滚动到当前行居中
        const lyricsDiv = document.getElementById('lyrics');
        const activeLine = lines[idx];
        if (activeLine) {
            const containerHeight = lyricsDiv.clientHeight;
            const lineOffset = activeLine.offsetTop + activeLine.offsetHeight / 2 - containerHeight / 2;
            lyricsDiv.scrollTo({ top: lineOffset, behavior: 'smooth' });
        }
    }
}

// 创建全局播放器实例
window.musicPlayer = new MusicPlayer(); 