package andrei.chirila.prove_yourself.infrastructure.filters;

import andrei.chirila.prove_yourself.domain.services.AuthService;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final List<String> URL_MATCHERS = List.of(WebSecurityConfig.WELCOME_URL_MATCHER, WebSecurityConfig.LOGIN_AUTH_URL_MATCHER, WebSecurityConfig.REGISTRATION_URL_MATCHER, WebSecurityConfig.CREATE_USER_URL_MATCHER, WebSecurityConfig.ACCOUNT_VERIFICATION_URL_MATCHER, WebSecurityConfig.LOGOUT_URL_MATCHER, WebSecurityConfig.LOGIN_URL_MATCHER, WebSecurityConfig.PRIVACY_POLICY_URL_MATCHER, WebSecurityConfig.TERMS_OF_SERVICE_URL_MATCHER,"/css/prove.css");
    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    @Value("${cookie.name}")
    private String cookieName;

    public JwtAuthenticationFilter(AuthService authService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final Optional<String> token = getJwtFromCookie(request);

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authService.validateToken(token.get())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            logger.warn("[AUTH] : Token is invalid for request: {}", request.getRequestURI());
            return;
        }

        String userName = authService.getUserFromToken(token.get());
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        logger.info("[AUTH] : Request successfully authorized for user: {}", userName);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        final String requestURI = request.getRequestURI();
        return URL_MATCHERS.contains(requestURI);
    }

    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();

        if (cookies == null || ArrayUtils.isEmpty(cookies)) {
            return Optional.empty();
        }

        return (Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst());
    }
}
