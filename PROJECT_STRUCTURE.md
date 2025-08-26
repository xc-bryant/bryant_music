# 项目结构说明

```
music-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── music/
│       │           ├── MusicSystemApplication.java          # 主启动类
│       │           ├── config/
│       │           │   └── MinioConfig.java                # MinIO配置类
│       │           ├── controller/
│       │           │   └── SongController.java             # 歌曲控制器
│       │           ├── dto/
│       │           │   ├── ApiResponse.java                # API响应格式
│       │           │   ├── SongDTO.java                    # 歌曲DTO
│       │           │   └── SongUploadRequest.java          # 上传请求DTO
│       │           ├── entity/
│       │           │   └── Song.java                       # 歌曲实体类
│       │           ├── exception/
│       │           │   └── GlobalExceptionHandler.java     # 全局异常处理
│       │           ├── repository/
│       │           │   └── SongRepository.java             # 数据访问层
│       │           └── service/
│       │               ├── MinioService.java               # MinIO服务
│       │               └── SongService.java                # 歌曲服务
│       └── resources/
│           └── application.yml                             # 应用配置文件
├── database/
│   └── init.sql                                            # 数据库初始化脚本
├── pom.xml                                                 # Maven配置文件
├── Dockerfile                                              # Docker配置文件
├── docker-compose.yml                                      # Docker Compose配置
├── start.bat                                               # Windows启动脚本
├── start.sh                                                # Linux/Mac启动脚本
├── README.md                                               # 项目说明文档
├── API_TEST.md                                             # API测试文档
└── PROJECT_STRUCTURE.md                                    # 项目结构说明
```

## 核心组件说明

### 1. 主启动类
- **MusicSystemApplication.java**: Spring Boot应用程序入口

### 2. 配置层
- **MinioConfig.java**: MinIO客户端配置，包含连接参数和Bean定义

### 3. 控制器层
- **SongController.java**: 提供RESTful API接口，处理HTTP请求

### 4. 数据传输对象
- **ApiResponse.java**: 统一的API响应格式
- **SongDTO.java**: 歌曲数据传输对象
- **SongUploadRequest.java**: 歌曲上传请求对象

### 5. 实体层
- **Song.java**: 歌曲实体类，对应数据库表结构

### 6. 异常处理
- **GlobalExceptionHandler.java**: 全局异常处理器，统一处理各种异常

### 7. 数据访问层
- **SongRepository.java**: 歌曲数据访问接口，继承JpaRepository

### 8. 服务层
- **MinioService.java**: MinIO对象存储服务，处理文件上传下载
- **SongService.java**: 歌曲业务逻辑服务，处理核心业务功能

## 技术架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用      │    │   Spring Boot   │    │   MySQL数据库   │
│   (可选)        │◄──►│   后端应用      │◄──►│                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   MinIO存储     │
                       │   (文件存储)    │
                       └─────────────────┘
```

## 数据流

### 1. 文件上传流程
```
前端 → SongController → SongService → MinioService → MinIO存储
                    ↓
                 SongRepository → MySQL数据库
```

### 2. 文件下载流程
```
前端 → SongController → SongService → MinioService → MinIO存储
                    ↓
                 SongRepository → MySQL数据库
```

### 3. 数据查询流程
```
前端 → SongController → SongService → SongRepository → MySQL数据库
```

## 扩展点

### 1. 用户管理
- 在 `entity` 包中添加 `User.java`
- 在 `service` 包中添加 `UserService.java`
- 在 `controller` 包中添加 `UserController.java`

### 2. 播放列表
- 在 `entity` 包中添加 `Playlist.java` 和 `PlaylistSong.java`
- 在 `service` 包中添加 `PlaylistService.java`
- 在 `controller` 包中添加 `PlaylistController.java`

### 3. 收藏功能
- 在 `entity` 包中添加 `Favorite.java`
- 在 `service` 包中添加 `FavoriteService.java`
- 在 `controller` 包中添加 `FavoriteController.java`

### 4. 播放历史
- 在 `entity` 包中添加 `PlayHistory.java`
- 在 `service` 包中添加 `PlayHistoryService.java`
- 在 `controller` 包中添加 `PlayHistoryController.java`

## 部署架构

### 开发环境
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   IDE/编辑器    │    │   Spring Boot   │    │   MySQL 8.0     │
│                 │◄──►│   (本地8080)    │◄──►│   (本地3306)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   MinIO Server  │
                       │   (本地9000)    │
                       └─────────────────┘
```

### 生产环境
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   负载均衡器    │    │   Spring Boot   │    │   MySQL集群     │
│   (Nginx)       │◄──►│   (多实例)      │◄──►│                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   MinIO集群     │
                       │   (分布式存储)  │
                       └─────────────────┘
```

## 安全考虑

### 1. 文件上传安全
- 文件类型验证
- 文件大小限制
- 文件名安全处理

### 2. 数据库安全
- 参数化查询防止SQL注入
- 数据库连接池配置
- 敏感信息加密

### 3. API安全
- 输入参数验证
- 异常信息脱敏
- CORS配置

## 性能优化

### 1. 数据库优化
- 索引优化
- 查询优化
- 连接池配置

### 2. 文件存储优化
- 分片上传
- 断点续传
- CDN加速

### 3. 应用优化
- 缓存策略
- 异步处理
- 连接池管理 