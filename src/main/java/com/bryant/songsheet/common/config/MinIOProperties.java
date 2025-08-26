package com.bryant.songsheet.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bryant
 * @date 2023/11/10
 **/
@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinIOProperties {
    private String point;

    private String username;

    private String password;
}
