package KUSITMS.WITHUS.global.auth.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private Key key;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {


        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getEmail(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public String createAccessToken(String email, String role) {
        long accessTokenExpireMs = 1000L * 60 * 10; // 10분
        return createJwt(email, role, accessTokenExpireMs);
    }

    public String createRefreshToken(String email, String role) {
        long refreshTokenExpireMs = 1000L * 60 * 60 * 24 * 7; // 7일
        return createJwt(email, role, refreshTokenExpireMs);
    }

    public String createJwt(String email, String role, Long expiredMs) {

        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}