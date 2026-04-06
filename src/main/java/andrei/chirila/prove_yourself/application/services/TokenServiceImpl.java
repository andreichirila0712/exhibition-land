package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public TokenServiceImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        User currentUser = (User) authentication.getPrincipal();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(currentUser.getEmail())
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpiration, ChronoUnit.MINUTES))
                .build();

        var jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    @Override
    public String getUserFromToken(String token) {
        Jwt jwtToken = jwtDecoder.decode(token);

        return jwtToken.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception ex) {
            logger.error("[USER] : Error while trying to validate token", ex);
            throw new BadJwtException("Error while trying to validate token");
        }
    }
}
