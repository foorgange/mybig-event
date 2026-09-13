package itheima;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTest {

    private static final String SECRET = "itheima";

    @Test
    @DisplayName("生成并解析 JWT，载荷内容保持一致")
    void testGenAndParse() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("username", "张三");

        // 生成 JWT：有效期 1 小时，确保解析时尚未过期
        String token = JWT.create()
                .withClaim("user", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000L))
                .sign(Algorithm.HMAC256(SECRET));

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length, "JWT 应由 header.payload.signature 三段组成");

        // 解析并校验 Token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        Map<String, Claim> parsedClaims = decodedJWT.getClaims();
        assertNotNull(parsedClaims.get("user"));
        assertEquals(1, parsedClaims.get("user").asMap().get("id"));
        assertEquals("张三", parsedClaims.get("user").asMap().get("username"));
    }

    @Test
    @DisplayName("密钥不匹配时校验失败")
    void testParseRejectsWrongSecret() {
        String token = JWT.create()
                .withClaim("user", Map.of("id", 1))
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000L))
                .sign(Algorithm.HMAC256(SECRET));

        JWTVerifier wrongVerifier = JWT.require(Algorithm.HMAC256("wrong-secret")).build();
        assertThrows(Exception.class, () -> wrongVerifier.verify(token));
    }

    @Test
    @DisplayName("Token 过期后校验失败")
    void testParseRejectsExpiredToken() {
        String expiredToken = JWT.create()
                .withClaim("user", Map.of("id", 1))
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000L))
                .sign(Algorithm.HMAC256(SECRET));

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        assertThrows(Exception.class, () -> jwtVerifier.verify(expiredToken));
    }
}
