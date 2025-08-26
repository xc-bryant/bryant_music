package com.bryant.songsheet.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bryant
 * @date 2023/11/8
 **/
@ConfigurationProperties(prefix = "jwt")
@Component
@Data
public class JwtConfig {
    String secret;
    Long expiration;
    String tokenHead;
    String tokenHeader = "Authorization";
}
