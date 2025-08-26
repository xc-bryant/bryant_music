FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .

# 复制源代码
COPY src ./src

# 安装Maven并构建项目
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests && \
    rm -rf ~/.m2

# 暴露端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/music-system-1.0.0.jar"] 