package andrei.chirila.prove_yourself.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/user", "/error", "/webjars/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(auth -> {
                    auth.loginPage("/user");
                    auth.successHandler((request, response, authentication) -> {
                        if (request.getRequestURI().contains("github"))
                            response.sendRedirect("/profile-github");
                        else if (request.getRequestURI().contains("google"))
                            response.sendRedirect("/profile-google");
                    });
                })
                .build();
    }
}
