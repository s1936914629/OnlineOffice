package cn.org.sqx.emos.wx.aop;

import cn.org.sqx.emos.wx.common.util.R;
import cn.org.sqx.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 返回Token的切面类
 * @auther: sqx
 * @Date: 2022-11-21
 */
@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * cn.org.sqx.emos.wx.controller.*.*(..))")
    public void aspect(){

    }

    @Around("aspect()")
    public Object arund(ProceedingJoinPoint point) throws Throwable {
        //获取R对象
        R r = (R) point.proceed();
        //如果ThreadLocalToken中存在Token，则说明更新了Token
        String token = threadLocalToken.getToken();
        if (token != null) {
            //往响应中放置Token
            r.put("token", token);
            threadLocalToken.clear();
        }
        return r;

    }

}
