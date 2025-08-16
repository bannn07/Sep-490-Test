package io.fptu.sep490.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ACCOUNT_ID = "accountId";
    private static final String CLAIM_ROLE = "role";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenExpirationMs, "refresh");
    }

    private String buildToken(UserDetails userDetails, long expirationMillis, String type) {
        UserDetailsImpl customUser = (UserDetailsImpl) userDetails;

        Map<String, Object> claims = Map.of(
                CLAIM_TYPE, type,
                CLAIM_ACCOUNT_ID, customUser.getId(),
                CLAIM_ROLE, customUser.account().getRole().name()
        );

        return Jwts.builder()
                .subject(customUser.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignKey())
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);

        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("auth.token.expired", e);
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("auth.token.invalid", e);
        } catch (SignatureException e) {
            throw new BadCredentialsException("auth.token.signature.invalid", e);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(parseToken(token).getPayload());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public Integer extractAccountId(String token) {
        Object raw = extractClaim(token, claims -> claims.get(CLAIM_ACCOUNT_ID));
        return raw != null ? Integer.parseInt(raw.toString()) : null;
    }

    public String extractRole(String token) {
        return parseStringClaim(token, CLAIM_ROLE);
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseStringClaim(token, CLAIM_TYPE));
    }

    private String parseStringClaim(String token, String key) {
        Object raw = extractClaim(token, claims -> claims.get(key));
        return Objects.toString(raw, null);
    }
}
