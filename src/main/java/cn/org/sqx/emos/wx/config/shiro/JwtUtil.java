package cn.org.sqx.emos.wx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 *
 * @auther: sqx
 * @Date: 2022-11-20
 */
@Component
@Slf4j
public class JwtUtil {
    @Value("${emos.jwt.secret}")
    private String secret;

    @Value("${emos.jwt.expire}")
    private int expire;

    //TODO 创建Token
    public String createToken(int userId) {
        //计算过期天数
        DateTime offset = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 5);
        //加密,根据设置的秘钥
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //创建JWT的内部类来封装Token
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(offset).sign(algorithm);
        return token;
    }

    //TODO 通过Token返回用户ID
    public int getUserId(String token) {
        //解析token
        DecodedJWT jwt = JWT.decode(token);
        //因为UserID是数字，所以要调用asInt()方法转换
        Integer userId = jwt.getClaim("userId").asInt();
        return userId;
    }

    //TODO 验证令牌有效性
    //如果验证失败就会直接出现异常，因为异常是RuntimeException类型的，所以不需要抛出
    public void verifierToken(String token) {
        //创建算法对象
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //创建验证对象，用来解密
        JWTVerifier verifier = JWT.require(algorithm).build();
        //解密
        verifier.verify(token);
    }

}
