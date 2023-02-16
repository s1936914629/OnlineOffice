package cn.org.sqx.emos.wx.config.shiro;

import cn.org.sqx.emos.wx.domain.TbUser;
import cn.org.sqx.emos.wx.service.impl.UserServiceImpl;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 认证与授权
 *
 * @auther: sqx
 * @Date: 2022-11-20
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权（验证权限时调用）
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        TbUser user = (TbUser) principalCollection.getPrimaryPrincipal();
        int userId = user.getId();

        //TODO 查询用户的权限列表
        Set<String> permsSet = userService.searchUserPermissions(userId);

        //TODO 把权限列表添加到info对象中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证（验证登录时调用）
     *
     * @param authenticationToken 令牌
     * @return  认证对象
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String accessToken = (String) authenticationToken.getPrincipal();
        //TODO 从令牌中获取userID
        int userId = jwtUtil.getUserId(accessToken);

        //查询用户信息
        TbUser user = userService.searchById(userId);

        //判断user是否是null
        if (user == null) {
            throw new LockedAccountException("账号已被锁定，请联系管理员");
        }

        //TODO 往info对象中添加用户信息，Token字符串
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,accessToken,getName());
        return info;
    }
}
