package com.music.common.anotation;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    //窗口宽度 单位秒
    long period() default 60;

    //允许的请求次数
    long count() default 100;
}
