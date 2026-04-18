package andrei.chirila.prove_yourself.infrastructure.config;

import andrei.chirila.prove_yourself.domain.Role;
import andrei.chirila.prove_yourself.domain.services.AuthService;
import andrei.chirila.prove_yourself.infrastructure.filters.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class WebSecurityConfig {
    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    @Value("${cookie.name}")
    private String cookieName;
    @Value("${cookie.http-only}")
    private boolean cookieHttpOnly;
    @Value("${cookie.same-site}")
    private String cookieSameSite;
    @Value("${cookie.path}")
    private String cookiePath;

    public WebSecurityConfig(AuthService authService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public static final String LOGIN_AUTH_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/login";
    public static final String LOGIN_FAILURE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/login?failure";
    public static final String REGISTRATION_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/register";
    public static final String CREATE_USER_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/create-user";
    public static final String ACCOUNT_VERIFICATION_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/verify";
    public static final String LOGOUT_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/logout";
    public static final String HOME_URL_MATCHER = ApiConfig.API_BASE_PATH + "/home";
    public static final String WELCOME_URL_MATCHER = ApiConfig.API_BASE_PATH + "/welcome";
    public static final String LOGIN_URL_MATCHER = ApiConfig.API_BASE_PATH  + "/login";
    public static final String PRIVACY_POLICY_URL_MATCHER = ApiConfig.API_BASE_PATH + "/privacy-policy";
    public static final String TERMS_OF_SERVICE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/tos";
    final String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws  Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers(HttpMethod.GET, WELCOME_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.POST, LOGIN_AUTH_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.GET, REGISTRATION_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.POST, CREATE_USER_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.GET, ACCOUNT_VERIFICATION_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.GET, LOGIN_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.GET, PRIVACY_POLICY_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.GET, TERMS_OF_SERVICE_URL_MATCHER).permitAll()
                        .requestMatchers(BASE_URL_MATCHER).authenticated()
                        .anyRequest().denyAll()
                )
                .logout(logout -> {
                    logout
                            .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, LOGOUT_URL_MATCHER))
                            .logoutSuccessHandler((request, response, authentication) -> {
                                ResponseCookie expiredCookie = ResponseCookie.from(cookieName, "")
                                        .httpOnly(cookieHttpOnly)
                                        .secure(false)
                                        .maxAge(0)
                                        .sameSite(cookieSameSite)
                                        .path(cookiePath)
                                        .build();

                                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                                response.sendRedirect(WebSecurityConfig.WELCOME_URL_MATCHER);
                            });
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sm) -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .authenticationManager(authenticationManager())
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        DefaultAuthorizationManagerFactory<MethodInvocation> factory = new DefaultAuthorizationManagerFactory<>();
        factory.setRoleHierarchy(roleHierarchy());
        expressionHandler.setAuthorizationManagerFactory(factory);

        return expressionHandler;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.USER.name())
                .build();
    }
}
