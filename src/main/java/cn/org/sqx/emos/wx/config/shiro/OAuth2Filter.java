package cn.org.sqx.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 过滤器
 * @auther: sqx
 * @Date: 2022-11-20
 */
@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 拦截请求之后，用于把令牌字符串封装成令牌对象
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //自定义方法需要的参数是HttpServletRequest所以需要类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取token
        String token = getRequestToken(request);
        //如果Token为空，则直接返回空
        if (StrUtil.isBlank(token)) {
            return null;
        }

        return new OAuth2Token(token);
    }

    /**
     * 拦截请求，判断请求是否需要被Shiro处理
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        //Ajax提交 application/json 数据的时候，会先发出 option 请求
        //这里要放行 options 请求，不需要处理Shiro处理
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        //处理 options 请求之外，所有请求都会被 Shiro 处理
        return false;
    }

    /**
     * 该方法用于处理所有应该被Shiro处理的请求
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        //跨域处理
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));

        threadLocalToken.clear();

        String token = getRequestToken(request);
        if (StrUtil.isBlank(token)) {
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            response.getWriter().print("无效的令牌");
            return false;
        }

        try {
            jwtUtil.verifierToken(token);
        } catch (TokenExpiredException e) {
            //如果出现了异常，先判断缓存的令牌是不是过期了
            if (redisTemplate.hasKey(token)) {
                redisTemplate.delete(token);
                int userId = jwtUtil.getUserId(token);
                token = jwtUtil.createToken(userId);
                redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);
                threadLocalToken.setToken(token);
            }else {
                response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                response.getWriter().print("令牌以过期");
                return false;
            }
        }catch (Exception e){ //自己伪造的Token
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            response.getWriter().print("无效的令牌");
            return false;
        }

        boolean b = executeLogin(servletRequest, servletResponse);

        return b;
    }

    /**
     * 认证失败后返回
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        //跨域处理
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));

        resp.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        try {
            resp.getWriter().print(e.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * 放行方法
     */
    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }

    //从请求头获取令牌
    private String getRequestToken(HttpServletRequest request){
        //从请求头中获取token
        String token = request.getHeader("token");

        //从请求体中获取token
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        return token;
    }
}
