package com.music.common.aspect;

import com.music.common.anotation.RequestLimit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RequestLimitAspect {
    @Resource
    private RedisTemplate redisTemplate;


    //切点
    @Pointcut("@annotation(requestLimit)")
    public void controllerAspect(RequestLimit requestLimit) {

    }

    @Around("controllerAspect(requestLimit)")
    public Object doAround(ProceedingJoinPoint joinPoint, RequestLimit requestLimit) throws Throwable {
        //获取注解参数
        long period = requestLimit.period();
        long count = requestLimit.count();

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //请求ip
        String ip = request.getRemoteAddr();
        //请求地址
        String requestURI = request.getRequestURI();
        //拼接成redis的key
        String key = "req_limit_".concat(requestURI).concat(ip);

        ZSetOperations<String, Long> zSetOperations = redisTemplate.opsForZSet();
        //获取当前时间
        long currentMs = System.currentTimeMillis();
        //添加当前时间
        zSetOperations.add(key, currentMs, currentMs);
        //设置过期时间
        redisTemplate.expire(key, period, TimeUnit.SECONDS);
        //删除窗口之外的值
        zSetOperations.removeRangeByScore(key, 0, currentMs - period * 1000);
        //查询访问次数
        Long visitCount = zSetOperations.zCard(key);
        if (visitCount > count) {
            log.error("接口拦截：{} 请求超过限制频率【{}次/{}s】,IP为{}", requestURI, count, period, ip);
            throw new RuntimeException("请求接口过快，请稍后重试");
        }
        //放行
        return joinPoint.proceed();

    }
}
