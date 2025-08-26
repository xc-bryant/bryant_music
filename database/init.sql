-- 创建数据库
CREATE DATABASE IF NOT EXISTS music_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE music_system;

-- 创建歌曲表
CREATE TABLE IF NOT EXISTS songs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '歌曲ID',
    title VARCHAR(200) NOT NULL COMMENT '歌曲标题',
    artist VARCHAR(100) COMMENT '艺术家',
    album VARCHAR(100) COMMENT '专辑',
    genre VARCHAR(50) COMMENT '流派',
    release_year INT COMMENT '发行年份',
    duration_seconds INT COMMENT '时长（秒）',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_type VARCHAR(20) COMMENT '文件类型',
    minio_object_name VARCHAR(255) COMMENT 'MinIO对象名称',
    play_count INT DEFAULT 0 COMMENT '播放次数',
    lyrics_file_name VARCHAR(255) COMMENT '歌词文件名',
    lyrics_file_path VARCHAR(500) COMMENT '歌词文件路径',
    lyrics_file_size BIGINT COMMENT '歌词文件大小（字节）',
    lyrics_file_type VARCHAR(20) COMMENT '歌词文件类型',
    lyrics_minio_object_name VARCHAR(255) COMMENT '歌词MinIO对象名称',
    language VARCHAR(20) COMMENT '语言',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_title (title),
    INDEX idx_artist (artist),
    INDEX idx_album (album),
    INDEX idx_genre (genre),
    INDEX idx_release_year (release_year),
    INDEX idx_play_count (play_count),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲信息表';

-- 插入示例数据
INSERT INTO songs (title, artist, album, genre, release_year, duration_seconds, file_name, file_path, file_size, file_type, minio_object_name, play_count, language) VALUES
('夜曲', '周杰伦', '十一月的萧邦', '流行', 2005, 240, '夜曲.mp3', 'http://localhost:9000/music-files/sample1.mp3', 5120000, 'audio/mpeg', 'sample1.mp3', 150, '中文'),
('稻香', '周杰伦', '魔杰座', '流行', 2008, 220, '稻香.mp3', 'http://localhost:9000/music-files/sample2.mp3', 4800000, 'audio/mpeg', 'sample2.mp3', 200, '中文'),
('青花瓷', '周杰伦', '我很忙', '流行', 2007, 235, '青花瓷.mp3', 'http://localhost:9000/music-files/sample3.mp3', 5200000, 'audio/mpeg', 'sample3.mp3', 180, '中文'),
('告白气球', '周杰伦', '周杰伦的床边故事', '流行', 2016, 215, '告白气球.mp3', 'http://localhost:9000/music-files/sample4.mp3', 4600000, 'audio/mpeg', 'sample4.mp3', 300, '中文'),
('晴天', '周杰伦', '叶惠美', '流行', 2003, 269, '晴天.mp3', 'http://localhost:9000/music-files/sample5.mp3', 5800000, 'audio/mpeg', 'sample5.mp3', 250, '中文');

-- 创建用户表（可选，用于未来扩展）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建播放历史表（可选，用于未来扩展）
CREATE TABLE IF NOT EXISTS play_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '播放历史ID',
    user_id BIGINT COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    play_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    duration_seconds INT COMMENT '播放时长（秒）',
    
    INDEX idx_user_id (user_id),
    INDEX idx_song_id (song_id),
    INDEX idx_play_time (play_time),
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放历史表';

-- 创建收藏表（可选，用于未来扩展）
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    
    UNIQUE KEY uk_user_song (user_id, song_id),
    INDEX idx_user_id (user_id),
    INDEX idx_song_id (song_id),
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 创建播放列表表（可选，用于未来扩展）
CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '播放列表ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '播放列表名称',
    description TEXT COMMENT '播放列表描述',
    cover_image VARCHAR(255) COMMENT '封面图片URL',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开：1-公开，0-私有',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_is_public (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表表';

-- 创建播放列表歌曲关联表（可选，用于未来扩展）
CREATE TABLE IF NOT EXISTS playlist_songs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    playlist_id BIGINT NOT NULL COMMENT '播放列表ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    
    UNIQUE KEY uk_playlist_song (playlist_id, song_id),
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_song_id (song_id),
    INDEX idx_sort_order (sort_order),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放列表歌曲关联表'; 