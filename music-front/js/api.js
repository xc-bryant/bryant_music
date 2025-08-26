// API配置
const API_BASE_URL = 'http://localhost:8080/api';

// API类
class MusicAPI {
    constructor() {
        this.baseURL = API_BASE_URL;
    }

    // 通用请求方法
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const finalOptions = { ...defaultOptions, ...options };

        try {
            const response = await fetch(url, finalOptions);
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || `HTTP ${response.status}`);
            }
            
            return data;
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }

    // 获取所有歌曲
    async getAllSongs() {
        return this.request('/songs');
    }

    // 根据ID获取歌曲
    async getSongById(id) {
        return this.request(`/songs/${id}`);
    }

    // 搜索歌曲
    async searchSongs(keyword) {
        return this.request(`/songs/search?keyword=${encodeURIComponent(keyword)}`);
    }

    // 按标题搜索
    async searchByTitle(title) {
        return this.request(`/songs/search/title?title=${encodeURIComponent(title)}`);
    }

    // 按艺术家搜索
    async searchByArtist(artist) {
        return this.request(`/songs/search/artist?artist=${encodeURIComponent(artist)}`);
    }

    // 按专辑搜索
    async searchByAlbum(album) {
        return this.request(`/songs/search/album?album=${encodeURIComponent(album)}`);
    }

    // 按流派搜索
    async searchByGenre(genre) {
        return this.request(`/songs/search/genre?genre=${encodeURIComponent(genre)}`);
    }

    // 按语言搜索
    async searchByLanguage(language) {
        return this.request(`/songs/search/language?language=${encodeURIComponent(language)}`);
    }

    // 获取热门歌曲
    async getTopPlayedSongs() {
        return this.request('/songs/top-played');
    }

    // 获取最新歌曲
    async getLatestSongs() {
        return this.request('/songs/latest');
    }

    // 播放歌曲（增加播放次数）
    async playSong(id) {
        return this.request(`/songs/${id}/play`, {
            method: 'POST'
        });
    }

    // 上传歌曲
    async uploadSong(formData) {
        return this.request('/songs/upload', {
            method: 'POST',
            body: formData,
            headers: {} // 让浏览器自动设置Content-Type为multipart/form-data
        });
    }

    // 更新歌曲信息
    async updateSong(id, songData) {
        return this.request(`/songs/${id}`, {
            method: 'PUT',
            body: JSON.stringify(songData)
        });
    }

    // 删除歌曲
    async deleteSong(id) {
        return this.request(`/songs/${id}`, {
            method: 'DELETE'
        });
    }

    // 获取歌词文件信息
    async getLyricsInfo(id) {
        return this.request(`/songs/${id}/lyrics`);
    }

    // 上传歌词文件
    async uploadLyricsFile(id, formData) {
        return this.request(`/songs/${id}/lyrics`, {
            method: 'POST',
            body: formData,
            headers: {}
        });
    }

    // 删除歌词文件
    async deleteLyricsFile(id) {
        return this.request(`/songs/${id}/lyrics`, {
            method: 'DELETE'
        });
    }

    // 获取音频文件流
    getAudioStream(id) {
        return `${this.baseURL}/songs/${id}/stream`;
    }

    // 获取音频文件下载链接
    getAudioDownload(id) {
        return `${this.baseURL}/songs/${id}/download`;
    }

    // 获取歌词文件下载链接
    getLyricsDownload(id) {
        return `${this.baseURL}/songs/${id}/lyrics/download`;
    }

    // 格式化时间
    formatTime(seconds) {
        if (!seconds || isNaN(seconds)) return '0:00';
        
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = Math.floor(seconds % 60);
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    }

    // 格式化文件大小
    formatFileSize(bytes) {
        if (!bytes || isNaN(bytes)) return '0 B';
        
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(1024));
        return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`;
    }
}

// 创建全局API实例
window.musicAPI = new MusicAPI(); 