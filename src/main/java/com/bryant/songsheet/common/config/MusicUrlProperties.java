package com.bryant.songsheet.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bryant
 * @date 2023/11/10
 **/
@Component
@ConfigurationProperties(prefix = "music-api-url")
@Data
public class MusicUrlProperties {
    private String find;

    private String download;
}
