package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.infrastructure.config.ApiConfig;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.FragmentsRendering;

@RequestMapping(ApiConfig.API_BASE_PATH)
@Controller
public class SiteController {

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("loginForm", WebSecurityConfig.LOGIN_URL_MATCHER);
        model.addAttribute("signUp", WebSecurityConfig.REGISTRATION_URL_MATCHER);
        model.addAttribute("privacyPolicy", WebSecurityConfig.PRIVACY_POLICY_URL_MATCHER);
        model.addAttribute("termsOfService", WebSecurityConfig.TERMS_OF_SERVICE_URL_MATCHER);

        return "site/welcome";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("projectsPath", ApiConfig.API_BASE_PATH + "/projects");
        model.addAttribute("profilePath", ApiConfig.API_BASE_PATH + "/profile");
        model.addAttribute("settingsPath", ApiConfig.API_BASE_PATH + "/account-settings");
        model.addAttribute("logoutPath", WebSecurityConfig.LOGOUT_URL_MATCHER);

        return "site/home";
    }

    @GetMapping("/login")
    public FragmentsRendering login(Model model) {
        model.addAttribute("loginPath", WebSecurityConfig.LOGIN_AUTH_URL_MATCHER);

        return FragmentsRendering.fragment("site/login :: login-frag").build();
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "site/privacy-policy";
    }

    @GetMapping("/tos")
    public String tos() {
        return "site/tos";
    }
}
