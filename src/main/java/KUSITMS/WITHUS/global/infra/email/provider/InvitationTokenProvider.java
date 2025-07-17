package KUSITMS.WITHUS.global.infra.email.provider;

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
public class InvitationTokenProvider {

    private final Key key;
    private final long expirationMs = 1000L * 60 * 60 * 24; // 1일

    public InvitationTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 초대 토큰 생성
     */
    public String createToken(Long userId, Long organizationId) {
        Date now = new Date();
        return Jwts.builder()
                .claim("userId", userId)
                .claim("organizationId", organizationId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰을 파싱하여 payload 반환
     */
    public InvitationPayload parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = claims.get("userId", Number.class).longValue();
        Long organizationId = claims.get("organizationId", Number.class).longValue();
        return new InvitationPayload(userId, organizationId);
    }

    /**
     * 파싱된 토큰 페이로드
     */
    public record InvitationPayload(Long userId, Long organizationId) {}
}