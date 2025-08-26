// UI管理类
class UIManager {
    constructor() {
        this.currentSection = 'songs';
        this.songs = [];
        this.favorites = JSON.parse(localStorage.getItem('favorites') || '[]');
        
        this.initializeUI();
        this.bindEvents();
    }

    // 初始化UI
    initializeUI() {
        this.loadSongs();
        this.updateFavoritesDisplay();
    }

    // 绑定事件
    bindEvents() {
        // 导航菜单
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const section = item.dataset.section;
                this.switchSection(section);
            });
        });

        // 搜索功能
        document.getElementById('searchBtn').addEventListener('click', () => {
            this.performSearch();
        });

        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.performSearch();
            }
        });

        // 搜索过滤器
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                this.performSearch();
            });
        });

        // 刷新按钮
        document.getElementById('refreshSongs').addEventListener('click', () => {
            this.loadSongs();
        });

        // 上传表单
        document.getElementById('uploadForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleUpload();
        });
    }

    // 切换页面
    switchSection(section) {
        // 更新导航状态
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-section="${section}"]`).classList.add('active');

        // 更新内容区域
        document.querySelectorAll('.section').forEach(s => {
            s.classList.remove('active');
        });
        document.getElementById(`${section}-section`).classList.add('active');

        this.currentSection = section;

        // 根据页面加载相应数据
        switch (section) {
            case 'songs':
                this.loadSongs();
                break;
            case 'search':
                this.clearSearchResults();
                break;
            case 'favorites':
                this.loadFavorites();
                break;
        }
    }

    // 加载歌曲列表
    async loadSongs() {
        try {
            musicPlayer.showLoading(true);
            const response = await musicAPI.getAllSongs();
            
            if (response.success) {
                this.songs = response.data;
                this.renderSongs(this.songs);
                musicPlayer.setPlaylist(this.songs);
                musicPlayer.showMessage(`加载了 ${this.songs.length} 首歌曲`, 'success');
            } else {
                musicPlayer.showMessage('加载歌曲失败: ' + response.message, 'error');
            }
        } catch (error) {
            console.error('加载歌曲失败:', error);
            musicPlayer.showMessage('加载歌曲失败: ' + error.message, 'error');
        } finally {
            musicPlayer.showLoading(false);
        }
    }

    // 渲染歌曲列表
    renderSongs(songs) {
        const container = document.getElementById('songsGrid');
        container.innerHTML = '';

        if (songs.length === 0) {
            container.innerHTML = '<div class="no-songs">暂无歌曲</div>';
            return;
        }

        songs.forEach(song => {
            const card = this.createSongCard(song);
            container.appendChild(card);
        });
    }

    // 创建歌曲卡片
    createSongCard(song) {
        const card = document.createElement('div');
        card.className = 'song-card';
        card.dataset.songId = song.id;

        const isFavorite = this.favorites.includes(song.id);

        card.innerHTML = `
            <div class="song-card-header">
                <div class="song-cover">
                    <i class="fas fa-music"></i>
                </div>
                <div class="song-info">
                    <h3>${song.title || '未知歌曲'}</h3>
                    <p>${song.artist || '未知艺术家'}</p>
                    <p>${song.album || '未知专辑'}</p>
                    <p>${song.genre || '未知流派'} • ${musicAPI.formatTime(song.durationSeconds)}</p>
                </div>
            </div>
            <div class="song-card-actions">
                <button class="action-btn play-btn" onclick="uiManager.playSong(${song.id})">
                    <i class="fas fa-play"></i> 播放
                </button>
                <button class="action-btn download-btn" onclick="uiManager.downloadSong(${song.id})">
                    <i class="fas fa-download"></i> 下载
                </button>
                <button class="action-btn favorite-btn ${isFavorite ? 'active' : ''}" onclick="uiManager.toggleFavorite(${song.id})">
                    <i class="fas fa-heart"></i> ${isFavorite ? '取消收藏' : '收藏'}
                </button>
                ${song.lyricsFileName ? `
                    <button class="action-btn" onclick="uiManager.downloadLyrics(${song.id})">
                        <i class="fas fa-file-text"></i> 歌词
                    </button>
                ` : ''}
            </div>
        `;

        return card;
    }

    // 播放歌曲
    async playSong(songId) {
        const song = this.songs.find(s => s.id === songId);
        if (song) {
            await musicPlayer.playSong(song);
        }
    }

    // 下载歌曲
    downloadSong(songId) {
        const downloadUrl = musicAPI.getAudioDownload(songId);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = '';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        musicPlayer.showMessage('开始下载歌曲', 'success');
    }

    // 下载歌词
    downloadLyrics(songId) {
        const downloadUrl = musicAPI.getLyricsDownload(songId);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = '';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        musicPlayer.showMessage('开始下载歌词', 'success');
    }

    // 切换收藏状态
    toggleFavorite(songId) {
        const index = this.favorites.indexOf(songId);
        if (index > -1) {
            this.favorites.splice(index, 1);
            musicPlayer.showMessage('已取消收藏', 'info');
        } else {
            this.favorites.push(songId);
            musicPlayer.showMessage('已添加到收藏', 'success');
        }
        
        localStorage.setItem('favorites', JSON.stringify(this.favorites));
        this.updateFavoritesDisplay();
        
        // 更新当前页面的收藏按钮
        if (this.currentSection === 'songs') {
            this.renderSongs(this.songs);
        } else if (this.currentSection === 'favorites') {
            this.loadFavorites();
        }
    }

    // 更新收藏显示
    updateFavoritesDisplay() {
        const favoriteCount = this.favorites.length;
        const navItem = document.querySelector('[data-section="favorites"]');
        const span = navItem.querySelector('span');
        span.textContent = `收藏 (${favoriteCount})`;
    }

    // 加载收藏列表
    loadFavorites() {
        const favoriteSongs = this.songs.filter(song => this.favorites.includes(song.id));
        const container = document.getElementById('favoritesGrid');
        container.innerHTML = '';

        if (favoriteSongs.length === 0) {
            container.innerHTML = '<div class="no-songs">暂无收藏歌曲</div>';
            return;
        }

        favoriteSongs.forEach(song => {
            const card = this.createSongCard(song);
            container.appendChild(card);
        });
    }

    // 执行搜索
    async performSearch() {
        const keyword = document.getElementById('searchInput').value.trim();
        const activeFilter = document.querySelector('.filter-btn.active').dataset.type;

        if (!keyword) {
            this.clearSearchResults();
            return;
        }

        try {
            musicPlayer.showLoading(true);
            let response;

            switch (activeFilter) {
                case 'title':
                    response = await musicAPI.searchByTitle(keyword);
                    break;
                case 'artist':
                    response = await musicAPI.searchByArtist(keyword);
                    break;
                case 'album':
                    response = await musicAPI.searchByAlbum(keyword);
                    break;
                case 'genre':
                    response = await musicAPI.searchByGenre(keyword);
                    break;
                case 'language':
                    response = await musicAPI.searchByLanguage(keyword);
                    break;
                default:
                    response = await musicAPI.searchSongs(keyword);
            }

            if (response.success) {
                this.renderSearchResults(response.data);
                musicPlayer.showMessage(`找到 ${response.data.length} 首歌曲`, 'success');
            } else {
                musicPlayer.showMessage('搜索失败: ' + response.message, 'error');
            }
        } catch (error) {
            console.error('搜索失败:', error);
            musicPlayer.showMessage('搜索失败: ' + error.message, 'error');
        } finally {
            musicPlayer.showLoading(false);
        }
    }

    // 渲染搜索结果
    renderSearchResults(songs) {
        const container = document.getElementById('searchResults');
        container.innerHTML = '';

        if (songs.length === 0) {
            container.innerHTML = '<div class="no-songs">未找到相关歌曲</div>';
            return;
        }

        songs.forEach(song => {
            const card = this.createSongCard(song);
            container.appendChild(card);
        });
    }

    // 清空搜索结果
    clearSearchResults() {
        document.getElementById('searchResults').innerHTML = '';
    }

    // 处理文件上传
    async handleUpload() {
        const form = document.getElementById('uploadForm');
        const formData = new FormData(form);

        // 验证必填字段
        const title = formData.get('title');
        const file = formData.get('file');

        if (!title || !file) {
            musicPlayer.showMessage('请填写歌曲标题并选择音频文件', 'error');
            return;
        }

        try {
            musicPlayer.showLoading(true);
            const response = await musicAPI.uploadSong(formData);

            if (response.success) {
                musicPlayer.showMessage('歌曲上传成功', 'success');
                form.reset();
                this.loadSongs(); // 重新加载歌曲列表
            } else {
                musicPlayer.showMessage('上传失败: ' + response.message, 'error');
            }
        } catch (error) {
            console.error('上传失败:', error);
            musicPlayer.showMessage('上传失败: ' + error.message, 'error');
        } finally {
            musicPlayer.showLoading(false);
        }
    }
}

// 创建全局UI管理器实例
window.uiManager = new UIManager(); 