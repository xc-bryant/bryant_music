package com.bryant.songsheet.core;

import com.bryant.songsheet.core.exception.BussException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

/**
 * @author bryant
 * @date 2024/7/8
 **/
@RestControllerAdvice(
        annotations = {RestController.class}
)
@Slf4j
public class XcControllerAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            if (mediaType.includes(MediaType.APPLICATION_JSON)) {
                return o instanceof AjaxResult ? o : AjaxResult.success(o);
            } else {
                return o;
            }
        } catch (Throwable var8) {
            throw var8;
        }
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public AjaxResult handle(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("系统异常");
    }

    @ResponseBody
    @ExceptionHandler({IOException.class})
    public AjaxResult handle(IOException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("系统IO错误");
    }

    @ResponseBody
    @ExceptionHandler({BussException.class})
    public AjaxResult handle(BussException e) {
        return AjaxResult.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public AjaxResult handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }

        return AjaxResult.error(message);
    }

    @ResponseBody
    @ExceptionHandler({NullPointerException.class})
    public AjaxResult handleValidException(NullPointerException e) {
        String message = "空指针异常";
        log.error(e.getMessage(), e);
        return AjaxResult.error(message);
    }
}
