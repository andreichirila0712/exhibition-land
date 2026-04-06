package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.services.AuthService;
import andrei.chirila.prove_yourself.infrastructure.config.ApiConfig;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.FragmentsRendering;

import java.util.UUID;

@Controller
@RequestMapping(ApiConfig.API_BASE_PATH + "/auth")
public class AuthController {
    private final AuthService authService;
    @Value("${cookie.name}")
    private String cookieName;
    @Value("${cookie.http-only}")
    private boolean cookieHttpOnly;
    @Value("${cookie.secure}")
    private boolean cookieSecure;
    @Value("${cookie.max-age}")
    private int cookieMaxAge;
    @Value("${cookie.same-site}")
    private String cookieSameSite;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseBody
    public void login(@Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        final String token = authService.login(loginRequestDto);
        final Cookie cookie = createAuthCookie(token);
        response.addCookie(cookie);
        response.addHeader("HX-Redirect", WebSecurityConfig.HOME_URL_MATCHER);
    }

    @PostMapping("/logout")
    @ResponseBody
    public void logout(HttpServletResponse response) {
        final Cookie cookie = new Cookie(cookieName, StringUtils.EMPTY);
        cookie.setMaxAge(0);
    }

    @GetMapping("/register")
    public FragmentsRendering register(Model model) {
        model.addAttribute("createUserPath", WebSecurityConfig.CREATE_USER_URL_MATCHER);
        return FragmentsRendering.fragment("site/registration :: registration-modal").build();
    }

    @PostMapping("/create-user")
    public FragmentsRendering createUser(@Valid CreateUserDto createUserDto) {
        authService.createUser(createUserDto);

        return FragmentsRendering.fragment("site/registration :: registration-successful").build();
    }

    @GetMapping("/verify")
    public String verify(UUID token, Model model) {
        authService.activateAccount(token);
        model.addAttribute("welcomePage", WebSecurityConfig.WELCOME_URL_MATCHER);

        return "site/verification";
    }

    private Cookie createAuthCookie(String token) {
        final String SAME_SITE_KEY = "SameSite";
        final Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setSecure(cookieSecure);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute(SAME_SITE_KEY, cookieSameSite);

        return cookie;
    }
}
