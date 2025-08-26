package com.bryant.songsheet.common.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author bryant
 * @date 2023/11/10
 **/
@Configuration
public class MinIOConfiguration {
    @Resource
    MinIOProperties minIOProperties;
    @Bean
    public MinioClient minioClient() {
        // Minio 配置。实际项目中，定义到 application.yml 配置文件中
        String endpoint = minIOProperties.getPoint();
        String accessKey = minIOProperties.getUsername();
        String secretKey = minIOProperties.getPassword();

        // 创建 MinioClient 客户端
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
