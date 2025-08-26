package com.bryant.songsheet.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bryant
 * @date 2023/11/8
 **/
@Component
@ConfigurationProperties(
        prefix = "secure"
)
@Data
public class SecurityProperties {
    private List<String> skipAuthorizeUrls = new ArrayList<>();
    private List<String> skipXssUrls = new ArrayList<>();
    private String csrfKey;
    private List<String> originUrls = new ArrayList<>();
}