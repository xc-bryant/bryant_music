package com.bryant.songsheet.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.bryant.songsheet.common.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bryant
 * @date 2024/7/8
 **/
@Component
public class JwtTokenUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_CREATED = "created";
    @Resource
    JwtConfig jwtConfig;

    public JwtTokenUtil() {
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(this.generateExpirationDate()).signWith(SignatureAlgorithm.HS512, this.jwtConfig.getSecret()).compact();
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims = null;

        try {
            claims = (Claims)Jwts.parser().setSigningKey(this.jwtConfig.getSecret()).parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            throw var4;
        }
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.jwtConfig.getExpiration() * 1000L);
    }

    public Object getFromToken(String token, String key) {
        Claims claims = this.getClaimsFromToken(token);
        return claims.get(key);
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap();
        claims.put("username", userName);
        claims.put("created", new Date());
        return this.generateToken((Map)claims);
    }

    public String refreshHeadToken(String oldToken) {
        if (StrUtil.isEmpty(oldToken)) {
            return null;
        } else {
            String token = oldToken.substring(this.jwtConfig.getTokenHead().length());
            if (StrUtil.isEmpty(token)) {
                return null;
            } else {
                Claims claims = this.getClaimsFromToken(token);
                if (claims == null) {
                    return null;
                } else if (this.tokenRefreshJustBefore(token, 1800)) {
                    return token;
                } else {
                    claims.put("created", new Date());
                    return this.generateToken((Map)claims);
                }
            }
        }
    }

    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = this.getClaimsFromToken(token);
        Date created = (Date)claims.get("created", Date.class);
        Date refreshDate = new Date();
        return refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time));
    }
}
