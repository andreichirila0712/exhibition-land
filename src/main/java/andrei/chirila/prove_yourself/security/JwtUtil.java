package andrei.chirila.prove_yourself.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private String jwtExpiration;
    private SecretKey secretKey;
    Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final DateFormat dateFormat = DateFormat.getDateInstance();
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("exhibition-land")
                .issuedAt(new Date())
                .expiration(obtainExpirationDate())
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(final String token) {
        Claims claims = extractClaims(token);

        return claims.getSubject();
    }

    public boolean validateJwtToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty", ex);
        }
        return false;
    }

    private Date obtainExpirationDate() {
        try {
            return dateFormat.parse(dateFormat.format(new Date().getTime() + jwtExpiration));
        } catch (ParseException ex) {
            logger.error("Could not parse expiration date", ex);
        }

        return new Date();
    }

    private Claims extractClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
