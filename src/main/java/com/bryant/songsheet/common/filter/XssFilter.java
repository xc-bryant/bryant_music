package com.bryant.songsheet.common.filter;

import com.bryant.songsheet.common.security.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author bryant
 * @date 2024/7/8
 **/
public class XssFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);
    SecurityProperties securityProperties;

    public XssFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.securityProperties.getSkipXssUrls().stream().noneMatch((v) -> {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            String regex = v.replaceAll("\\*\\*", ".*");
            return request.getRequestURI().matches(regex);
        })) {
            XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper((HttpServletRequest)servletRequest);
            filterChain.doFilter(xssHttpServletRequestWrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}
