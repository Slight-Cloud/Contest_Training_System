package com.system.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 (适配 jjwt 0.12.5)
 */
public class JwtUtil {

    // 至少 32 字节 (256 bit) 秘钥
    private static final String SECRET = "change-this-secret-to-a-32bytes-minimum-abcdef123456";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // 过期时间 7 天
    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    /**
     * 生成 Token
     */
    public static String generateToken(Long userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_MS))
                .signWith(KEY) // 新版不再需要指定算法，key 会决定 HS256/HS512
                .compact();
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public static String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 检查 Token 是否过期
    public static boolean isExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.before(new Date());
    }

}