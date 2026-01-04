package util;

import entity.TokenInfoDO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET =
            "your-256-bit-secret-your-256-bit-secret";

    private static final long EXPIRATION =
            24 * 60 * 60 * 1000; // 1 天

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private static final String TOKEN_INFO_KEY = "tokenInfo";

    /**
     * 生成 Token（claims 为 TokenInfo 对象）
     */
    public static String generateToken(String subject, TokenInfoDO tokenInfo) {
        try {
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + EXPIRATION);

            return Jwts.builder()
                    .setSubject(subject)
                    .claim(TOKEN_INFO_KEY, tokenInfo)
                    .setIssuedAt(now)
                    .setExpiration(expireDate)
                    .signWith(KEY, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("生成 JWT 失败", e);
        }
    }

    /**
     * 从 Token 中解析 TokenInfo
     */
    public static TokenInfoDO parseTokenInfo(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get(TOKEN_INFO_KEY, TokenInfoDO.class);
        } catch (Exception e) {
            throw new RuntimeException("解析 JWT 失败", e);
        }
    }

    /**
     * 解析 Claims
     */
    private static Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 Token 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
