package cn.org.sqx.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 封装Token对象
 *
 * @auther: sqx
 * @Date: 2022-11-20
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
