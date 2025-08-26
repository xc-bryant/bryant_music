# API测试文档

## 测试环境

- 应用地址: http://localhost:8080
- 数据库: MySQL 8.0
- 对象存储: MinIO

## 测试工具

推荐使用以下工具进行API测试：
- Postman
- curl
- 浏览器开发者工具

## 测试用例

### 1. 获取所有歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs
```

**预期响应:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "夜曲",
      "artist": "周杰伦",
      "album": "十一月的萧邦",
      "genre": "流行",
      "releaseYear": 2005,
      "durationSeconds": 240,
      "fileName": "夜曲.mp3",
      "filePath": "http://localhost:9000/music-files/sample1.mp3",
      "fileSize": 5120000,
      "fileType": "audio/mpeg",
      "minioObjectName": "sample1.mp3",
      "playCount": 150,
      "createdAt": "2023-12-01T10:00:00",
      "updatedAt": "2023-12-01T10:00:00"
    }
  ]
}
```

### 2. 上传歌曲

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/upload \
  -F "file=@/path/to/song.mp3" \
  -F "lyricsFile=@/path/to/lyrics.lrc" \
  -F "title=测试歌曲" \
  -F "artist=测试艺术家" \
  -F "album=测试专辑" \
  -F "genre=流行" \
  -F "releaseYear=2023" \
  -F "durationSeconds=240" \
  -F "language=中文"
```

**预期响应:**
```json
{
  "success": true,
  "message": "歌曲上传成功",
  "data": {
    "id": 6,
    "title": "测试歌曲",
    "artist": "测试艺术家",
    "album": "测试专辑",
    "genre": "流行",
    "releaseYear": 2023,
    "durationSeconds": 240,
    "fileName": "song.mp3",
    "filePath": "http://localhost:9000/music-files/uuid-generated.mp3",
    "fileSize": 5120000,
    "fileType": "audio/mpeg",
    "minioObjectName": "uuid-generated.mp3",
    "playCount": 0,
    "lyricsFileName": "lyrics.lrc",
    "lyricsFilePath": "http://localhost:9000/music-files/uuid-generated.lrc",
    "lyricsFileSize": 1024,
    "lyricsFileType": "text/plain",
    "lyricsMinioObjectName": "uuid-generated.lrc",
    "language": "中文",
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
}
```

### 3. 根据ID获取歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/1
```

**预期响应:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "夜曲",
    "artist": "周杰伦",
    "album": "十一月的萧邦",
    "genre": "流行",
    "releaseYear": 2005,
    "durationSeconds": 240,
    "fileName": "夜曲.mp3",
    "filePath": "http://localhost:9000/music-files/sample1.mp3",
    "fileSize": 5120000,
    "fileType": "audio/mpeg",
    "minioObjectName": "sample1.mp3",
    "playCount": 150,
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
}
```

### 4. 更新歌曲信息

**请求:**
```bash
curl -X PUT http://localhost:8080/api/songs/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "夜曲（更新版）",
    "artist": "周杰伦",
    "album": "十一月的萧邦",
    "genre": "流行",
    "releaseYear": 2005,
    "durationSeconds": 240
  }'
```

**预期响应:**
```json
{
  "success": true,
  "message": "歌曲更新成功",
  "data": {
    "id": 1,
    "title": "夜曲（更新版）",
    "artist": "周杰伦",
    "album": "十一月的萧邦",
    "genre": "流行",
    "releaseYear": 2005,
    "durationSeconds": 240,
    "fileName": "夜曲.mp3",
    "filePath": "http://localhost:9000/music-files/sample1.mp3",
    "fileSize": 5120000,
    "fileType": "audio/mpeg",
    "minioObjectName": "sample1.mp3",
    "playCount": 150,
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
}
```

### 5. 搜索歌曲

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search?keyword=周杰伦"
```

**预期响应:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "夜曲",
      "artist": "周杰伦",
      "album": "十一月的萧邦",
      "genre": "流行",
      "releaseYear": 2005,
      "durationSeconds": 240,
      "fileName": "夜曲.mp3",
      "filePath": "http://localhost:9000/music-files/sample1.mp3",
      "fileSize": 5120000,
      "fileType": "audio/mpeg",
      "minioObjectName": "sample1.mp3",
      "playCount": 150,
      "createdAt": "2023-12-01T10:00:00",
      "updatedAt": "2023-12-01T10:00:00"
    }
  ]
}
```

### 6. 按标题搜索

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search/title?title=夜曲"
```

### 7. 按艺术家搜索

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search/artist?artist=周杰伦"
```

### 8. 按专辑搜索

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search/album?album=十一月的萧邦"
```

### 9. 按流派搜索

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search/genre?genre=流行"
```

### 10. 按语言搜索

**请求:**
```bash
curl -X GET "http://localhost:8080/api/songs/search/language?language=中文"
```

### 11. 获取热门歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/top-played
```

### 11. 获取最新歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/latest
```

### 12. 获取歌词文件信息

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/1/lyrics
```

**预期响应:**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "夜曲",
    "artist": "周杰伦",
    "album": "十一月的萧邦",
    "genre": "流行",
    "releaseYear": 2005,
    "durationSeconds": 240,
    "fileName": "夜曲.mp3",
    "filePath": "http://localhost:9000/music-files/sample1.mp3",
    "fileSize": 5120000,
    "fileType": "audio/mpeg",
    "minioObjectName": "sample1.mp3",
    "playCount": 150,
    "lyricsFileName": "夜曲.lrc",
    "lyricsFilePath": "http://localhost:9000/music-files/lyrics1.lrc",
    "lyricsFileSize": 1024,
    "lyricsFileType": "text/plain",
    "lyricsMinioObjectName": "lyrics1.lrc",
    "language": "中文",
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
}
```

### 13. 上传歌词文件

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/1/lyrics \
  -F "lyricsFile=@/path/to/new_lyrics.lrc"
```

**预期响应:**
```json
{
  "success": true,
  "message": "歌词文件上传成功",
  "data": {
    "id": 1,
    "title": "夜曲",
    "artist": "周杰伦",
    "album": "十一月的萧邦",
    "genre": "流行",
    "releaseYear": 2005,
    "durationSeconds": 240,
    "fileName": "夜曲.mp3",
    "filePath": "http://localhost:9000/music-files/sample1.mp3",
    "fileSize": 5120000,
    "fileType": "audio/mpeg",
    "minioObjectName": "sample1.mp3",
    "playCount": 150,
    "lyricsFileName": "new_lyrics.lrc",
    "lyricsFilePath": "http://localhost:9000/music-files/uuid-generated.lrc",
    "lyricsFileSize": 2048,
    "lyricsFileType": "text/plain",
    "lyricsMinioObjectName": "uuid-generated.lrc",
    "language": "中文",
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
}
```

### 14. 删除歌词文件

**请求:**
```bash
curl -X DELETE http://localhost:8080/api/songs/1/lyrics
```

**预期响应:**
```json
{
  "success": true,
  "message": "歌词文件删除成功",
  "data": null
}
```

### 15. 下载歌词文件

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/1/lyrics/download -o downloaded_lyrics.lrc
```

### 16. 播放歌曲（增加播放次数）

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/1/play
```

**预期响应:**
```json
{
  "success": true,
  "message": "播放次数已更新",
  "data": null
}
```

### 17. 下载歌曲文件

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/1/download -o downloaded_song.mp3
```

### 18. 流式播放歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/1/stream
```

### 19. 删除歌曲

**请求:**
```bash
curl -X DELETE http://localhost:8080/api/songs/1
```

**预期响应:**
```json
{
  "success": true,
  "message": "歌曲删除成功",
  "data": null
}
```

## 错误测试用例

### 1. 上传非音频文件

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/upload \
  -F "file=@/path/to/text.txt" \
  -F "title=测试歌曲"
```

**预期响应:**
```json
{
  "success": false,
  "message": "歌曲上传失败: 只能上传音频文件",
  "data": null
}
```

### 2. 上传空文件

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/upload \
  -F "file=@/path/to/empty.mp3" \
  -F "title=测试歌曲"
```

**预期响应:**
```json
{
  "success": false,
  "message": "歌曲上传失败: 文件不能为空",
  "data": null
}
```

### 3. 缺少必填参数

**请求:**
```bash
curl -X POST http://localhost:8080/api/songs/upload \
  -F "file=@/path/to/song.mp3"
```

**预期响应:**
```json
{
  "success": false,
  "message": "参数验证失败: 歌曲标题不能为空",
  "data": null
}
```

### 4. 获取不存在的歌曲

**请求:**
```bash
curl -X GET http://localhost:8080/api/songs/999
```

**预期响应:**
```json
{
  "success": false,
  "message": "获取歌曲失败: 歌曲不存在",
  "data": null
}
```

## Postman测试集合

可以导入以下Postman集合进行测试：

```json
{
  "info": {
    "name": "音乐系统API测试",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "获取所有歌曲",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/songs"
      }
    },
    {
      "name": "上传歌曲",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/songs/upload",
        "body": {
          "mode": "formdata",
          "formdata": [
            {
              "key": "file",
              "type": "file",
              "src": []
            },
            {
              "key": "title",
              "value": "测试歌曲",
              "type": "text"
            },
            {
              "key": "artist",
              "value": "测试艺术家",
              "type": "text"
            }
          ]
        }
      }
    }
  ]
}
```

## 性能测试

### 并发上传测试

使用Apache Bench进行并发测试：

```bash
# 测试100个并发请求
ab -n 100 -c 10 -p song_data.json -T application/json http://localhost:8080/api/songs/upload
```

### 文件大小测试

测试不同大小的音频文件上传：
- 小文件（< 1MB）
- 中等文件（1-10MB）
- 大文件（10-100MB）

## 安全测试

### 1. 文件类型验证
- 尝试上传非音频文件
- 尝试上传恶意文件

### 2. 参数验证
- 测试SQL注入
- 测试XSS攻击
- 测试路径遍历

### 3. 权限测试
- 测试未授权访问
- 测试越权操作

## 测试报告模板

```markdown
# API测试报告

## 测试环境
- 应用版本: 1.0.0
- 测试时间: 2023-12-01
- 测试人员: [姓名]

## 测试结果
- 总测试用例: 15
- 通过: 15
- 失败: 0
- 成功率: 100%

## 性能测试结果
- 平均响应时间: 200ms
- 最大响应时间: 500ms
- 并发支持: 100用户

## 问题记录
- 无

## 建议
- 建议添加API限流
- 建议添加更详细的日志记录
``` 