package com.detc.server.util;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.detc.server.model.Role;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * @author szqsy17
 */
@Log4j2
public class JwtUtils {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    private static final long EXPIRED_TIME = DAY;
    private static final String MY_SECRET = "]sdc58d.p/e33f8.";

    /**
     * 生成token
     */
    public static JSONObject createJwt(String id, String username, int role) {
        // 设置过期时间，精确到秒
        Date date = new Date((System.currentTimeMillis() + EXPIRED_TIME) / 1000 * 1000);
        Algorithm algorithm = Algorithm.HMAC256(MY_SECRET);
        String token = JWT.create()
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("role", role)
                .withExpiresAt(date)
                .sign(algorithm);
        JSONObject tokenObject = new JSONObject();
        tokenObject.put("token", token);
        return tokenObject;
    }

    /**
     * 校验token
     */
    public static boolean verifyJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(MY_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析token获取登录用户id
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 解析token获取登录用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 解析token获取登录用户角色
     */
    public static Role getUserRole(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("role").asInt() == 1 ? Role.ADMIN : Role.USER;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

}
