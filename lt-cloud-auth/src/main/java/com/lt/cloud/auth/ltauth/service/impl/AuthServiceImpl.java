package com.lt.cloud.auth.ltauth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lt.cloud.auth.ltauth.moudle.dto.LoginDto;
import com.lt.cloud.auth.ltauth.service.AuthService;
import com.lt.cloud.common.ResultMsg;
import com.lt.cloud.utils.IdWorker;
import com.lt.cloud.vo.TokenVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${token.expire.time}")
    private long tokenExpireTime;

    @Value("${refresh.token.expire.time}")
    private long refreshTokenExpireTime;

    @Value("${jwt.refresh.token.key.format}")
    private String jwtRefreshTokenKeyFormat;

    @Value("${jwt.blacklist.key.format}")
    private String jwtBlacklistKeyFormat;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResultMsg login(LoginDto dto) {
        //账号密码校验
        if(StringUtils.equals(dto.getUsername(), "admin") &&
           StringUtils.equals(dto.getPassword(), "admin")){

            //生成JWT
            String token = buildJWT(dto.getUsername());
            //生成refreshToken
            String refreshToken = String.valueOf(IdWorker.generate());
            //保存refreshToken至redis，使用hash结构保存使用中的token以及用户标识
            String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
            stringRedisTemplate.opsForHash().put(refreshTokenKey,
                    "token", token);
            stringRedisTemplate.opsForHash().put(refreshTokenKey,
                    "userName", dto.getUsername());
            //refreshToken设置过期时间
            stringRedisTemplate.expire(refreshTokenKey,
                    refreshTokenExpireTime, TimeUnit.MILLISECONDS);
            TokenVo tokenVo = new TokenVo(token,refreshToken);
            return ResultMsg.SUCCESS(tokenVo);
        }
        return ResultMsg.ERROR("账号或密码错误");
    }

    /**
     * 刷新JWT
     * @param refreshToken
     * @return
     */
    public ResultMsg refreshToken(String refreshToken){
        String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
        String userName = (String)stringRedisTemplate.opsForHash().get(refreshTokenKey,
                "userName");
        if(StringUtils.isBlank(userName)){
            return ResultMsg.ERROR("refreshToken过期");
        }
        String newToken = buildJWT(userName);
        //替换当前token，并将旧token添加到黑名单
        String oldToken = (String)stringRedisTemplate.opsForHash().get(refreshTokenKey,
                "token");
        stringRedisTemplate.opsForHash().put(refreshTokenKey, "token", newToken);
        stringRedisTemplate.opsForValue().set(String.format(jwtBlacklistKeyFormat, oldToken), "",
                tokenExpireTime, TimeUnit.MILLISECONDS);
        return ResultMsg.SUCCESS(newToken);
    }

    public ResultMsg verifyToken(String token){
        if(token == null ||
           token.isEmpty() ||
           isBlackToken(token)) {
            return ResultMsg.Unauthorized;
        }
        //取出token包含的身份
        String userName = verifyJWT(token);
        if (StringUtils.isBlank(userName)) {
            return ResultMsg.InvalidToken;
        }
        return ResultMsg.SUCCESS(userName);
    }

    /***
     * 生成jwt
     * @param userName
     * @return
     */
    private String buildJWT(String userName){
        //生成jwt
        Date now = new Date();
        Algorithm algo = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer("MING")
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + tokenExpireTime))
                .withClaim("userName", userName)//保存身份标识
                .sign(algo);
        return token;
    }

    /**
     * 判断token是否在黑名单内
     * @param token
     * @return
     */
    private boolean isBlackToken(String token){
        assert token != null;
        return stringRedisTemplate.hasKey(String.format(jwtBlacklistKeyFormat, token));
    }

    /**
     * JWT验证
     * @param token
     * @return userName
     */
    private String verifyJWT(String token){
        String userName = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("MING")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            userName = jwt.getClaim("userName").asString();
        } catch (JWTVerificationException e){
            log.error(e.getMessage(), e);
            return "";
        }
        return userName;
    }
}
