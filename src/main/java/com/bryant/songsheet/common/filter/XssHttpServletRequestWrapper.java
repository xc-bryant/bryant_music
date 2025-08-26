package com.bryant.songsheet.common.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author bryant
 * @date 2024/7/8
 **/
@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final HttpServletRequest orgRequest;
    private byte[] body;
    private static final XssHtmlFilter HTML_FILTER = new XssHtmlFilter();

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.orgRequest = request;
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public ServletInputStream getInputStream() throws IOException {
        if (super.getHeader("Content-Type") == null) {
            return super.getInputStream();
        } else if (super.getHeader("Content-Type").startsWith("multipart/form-data")) {
            return super.getInputStream();
        } else {
            if (this.body == null) {
                String str = IoUtil.readUtf8(super.getInputStream());
                this.body = this.xssEncode(str).getBytes();
            }

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
            return new ServletInputStream() {
                public int read() {
                    return byteArrayInputStream.read();
                }

                public boolean isFinished() {
                    return false;
                }

                public boolean isReady() {
                    return false;
                }

                public void setReadListener(ReadListener readListener) {
                }
            };
        }
    }

    public String getParameter(String name) {
        String value = super.getParameter(this.xssEncode(name));
        if (StrUtil.isNotBlank(value)) {
            value = this.xssEncode(value);
        }

        return value;
    }

    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (parameters != null && parameters.length != 0) {
            for(int i = 0; i < parameters.length; ++i) {
                parameters[i] = this.xssEncode(parameters[i]);
            }

            return parameters;
        } else {
            return null;
        }
    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap();
        Map<String, String[]> parameters = super.getParameterMap();
        Iterator var3 = parameters.keySet().iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            String[] values = (String[])parameters.get(key);

            for(int i = 0; i < values.length; ++i) {
                values[i] = this.xssEncode(values[i]);
            }

            map.put(key, values);
        }

        return map;
    }

    public String getHeader(String name) {
        String value = super.getHeader(this.xssEncode(name));
        if (StrUtil.isNotBlank(value)) {
            value = this.xssEncode(value);
        }

        return value;
    }

    private String xssEncode(String input) {
        return HTML_FILTER.filter(input);
    }

    public HttpServletRequest getOrgRequest() {
        return this.orgRequest;
    }

    public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
        return request instanceof XssHttpServletRequestWrapper ? ((XssHttpServletRequestWrapper)request).getOrgRequest() : request;
    }
}
