# 音乐系统后端

一个基于Spring Boot的音乐管理系统后端，支持歌曲文件上传、存储和管理。

## 功能特性

- 🎵 歌曲文件上传到MinIO对象存储
- 📝 歌曲信息管理（增删改查）
- 🎤 歌词文件管理和语言支持
- 🔍 多维度搜索（标题、艺术家、专辑、流派、语言）
- 📊 播放统计和热门歌曲
- 🎧 流式播放和文件下载
- 🛡️ 完整的异常处理和参数验证

## 技术栈

- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **对象存储**: MinIO
- **ORM**: Spring Data JPA
- **构建工具**: Maven
- **Java版本**: JDK 8+

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- MinIO Server

### 1. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE music_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p music_system < database/init.sql
```

### 2. MinIO配置

1. 下载并启动MinIO Server：
```bash
# 下载MinIO
wget https://dl.min.io/server/minio/release/linux-amd64/minio

# 启动MinIO（默认端口9000）
./minio server /data --console-address ":9001"
```

2. 访问MinIO控制台：http://localhost:9001
   - 默认用户名：minioadmin
   - 默认密码：minioadmin

### 3. 应用配置

修改 `src/main/resources/application.yml` 中的数据库和MinIO配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/music_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucket: music-files
```

### 4. 启动应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动

## API接口文档

### 歌曲管理

#### 1. 上传歌曲
```
POST /api/songs/upload
Content-Type: multipart/form-data

参数:
- file: 音频文件
- lyricsFile: 歌词文件（可选，支持.txt、.lrc、.srt格式）
- title: 歌曲标题（必填）
- artist: 艺术家
- album: 专辑
- genre: 流派
- releaseYear: 发行年份
- durationSeconds: 时长（秒）
- language: 语言
```

#### 2. 获取所有歌曲
```
GET /api/songs
```

#### 3. 根据ID获取歌曲
```
GET /api/songs/{id}
```

#### 4. 更新歌曲信息
```
PUT /api/songs/{id}
Content-Type: application/json

{
  "title": "新标题",
  "artist": "新艺术家",
  "album": "新专辑",
  "genre": "新流派",
  "releaseYear": 2023,
  "durationSeconds": 240,
  "language": "中文"
}
```

#### 5. 删除歌曲
```
DELETE /api/songs/{id}
```

### 搜索功能

#### 1. 关键词搜索
```
GET /api/songs/search?keyword=关键词
```

#### 2. 按标题搜索
```
GET /api/songs/search/title?title=标题
```

#### 3. 按艺术家搜索
```
GET /api/songs/search/artist?artist=艺术家
```

#### 4. 按专辑搜索
```
GET /api/songs/search/album?album=专辑
```

#### 5. 按流派搜索
```
GET /api/songs/search/genre?genre=流派
```

#### 6. 按语言搜索
```
GET /api/songs/search/language?language=语言
```

### 播放功能

#### 1. 获取热门歌曲
```
GET /api/songs/top-played
```

#### 2. 获取最新歌曲
```
GET /api/songs/latest
```

#### 3. 播放歌曲（增加播放次数）
```
POST /api/songs/{id}/play
```

#### 4. 获取歌词文件信息
```
GET /api/songs/{id}/lyrics
```

#### 5. 上传歌词文件
```
POST /api/songs/{id}/lyrics
Content-Type: multipart/form-data
参数: lyricsFile - 歌词文件
```

#### 6. 删除歌词文件
```
DELETE /api/songs/{id}/lyrics
```

#### 7. 下载歌词文件
```
GET /api/songs/{id}/lyrics/download
```

#### 8. 流式播放歌曲
```
GET /api/songs/{id}/stream
```

#### 9. 下载歌曲文件
```
GET /api/songs/{id}/download
```

## 响应格式

所有API接口都返回统一的响应格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

## 项目结构

```
src/main/java/com/music/
├── MusicSystemApplication.java    # 主启动类
├── config/
│   └── MinioConfig.java          # MinIO配置
├── controller/
│   └── SongController.java       # 歌曲控制器
├── dto/
│   ├── ApiResponse.java          # API响应格式
│   ├── SongDTO.java              # 歌曲DTO
│   └── SongUploadRequest.java    # 上传请求DTO
├── entity/
│   └── Song.java                 # 歌曲实体
├── exception/
│   └── GlobalExceptionHandler.java # 全局异常处理
├── repository/
│   └── SongRepository.java       # 数据访问层
└── service/
    ├── MinioService.java         # MinIO服务
    └── SongService.java          # 歌曲服务
```

## 数据库设计

### 主要表结构

1. **songs表** - 歌曲信息
   - id: 主键
   - title: 歌曲标题
   - artist: 艺术家
   - album: 专辑
   - genre: 流派
   - release_year: 发行年份
   - duration_seconds: 时长
   - file_name: 文件名
   - file_path: 文件路径
   - file_size: 文件大小
   - file_type: 文件类型
   - minio_object_name: MinIO对象名
   - play_count: 播放次数
   - lyrics_file_name: 歌词文件名
   - lyrics_file_path: 歌词文件路径
   - lyrics_file_size: 歌词文件大小
   - lyrics_file_type: 歌词文件类型
   - lyrics_minio_object_name: 歌词MinIO对象名
   - language: 语言
   - created_at: 创建时间
   - updated_at: 更新时间

## 部署说明

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package

# 运行
java -jar target/music-system-1.0.0.jar
```

## 注意事项

1. 确保MinIO服务正常运行
2. 确保MySQL数据库连接正常
3. 文件上传大小限制为100MB
4. 只支持音频文件格式上传
5. 建议在生产环境中配置HTTPS

## 扩展功能

项目预留了以下扩展功能的数据库表：
- 用户管理（users表）
- 播放历史（play_history表）
- 收藏功能（favorites表）
- 播放列表（playlists表）

## 许可证

MIT License 