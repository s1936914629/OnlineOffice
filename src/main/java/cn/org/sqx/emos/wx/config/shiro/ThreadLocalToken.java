package cn.org.sqx.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

/**
 * 媒介类
 *
 * @auther: sqx
 * @Date: 2022-11-20
 */
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();

    //取令牌
    public String getToken() {
        return local.get();
    }

    //存令牌
    public void setToken(String token) {
        local.set(token);
    }

    //清除令牌
    public void clear() {
        local.remove();
    }
}
