package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.config.ApiConfig;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserSettingsDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.FragmentsRendering;

@Controller
@RequestMapping(ApiConfig.API_BASE_PATH)
public class SiteController {
    private final UserService userService;

    public SiteController(UserService userService) {
        this.userService = userService;
    }

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
        model.addAttribute("settingsPath", ApiConfig.API_BASE_PATH + "/settings");
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

    @GetMapping("/settings")
    public String settings(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("homePath", WebSecurityConfig.HOME_URL_MATCHER);
        model.addAttribute("updateLanguagePath", ApiConfig.API_BASE_PATH + "/user/update-language");
        model.addAttribute("updateThemePath", ApiConfig.API_BASE_PATH + "/user/update-theme");
        model.addAttribute("updateDateFormatPath", ApiConfig.API_BASE_PATH + "/user/update-date-format");
        model.addAttribute("updateVisibilityPath", ApiConfig.API_BASE_PATH + "/user/update-visibility");
        model.addAttribute("updateDiscoverabilityPath", ApiConfig.API_BASE_PATH + "/user/update-discoverability");
        model.addAttribute("changeEmailPath", ApiConfig.API_BASE_PATH + "/user/change-email");
        model.addAttribute("changePasswordPath", ApiConfig.API_BASE_PATH + "/user/change-password");
        model.addAttribute("deleteAccountPath", ApiConfig.API_BASE_PATH + "/user/delete-account");
        model.addAttribute("checkCurrentPasswordPath", ApiConfig.API_BASE_PATH + "/user/check-current-password");

        UserSettingsDto userSettingsDto = this.userService.getUserSettings(userDetails.getUsername());
        model.addAttribute("languagePreference", userSettingsDto.language());
        model.addAttribute("themePreference", userSettingsDto.theme());
        model.addAttribute("dateFormatPreference", userSettingsDto.dateFormat());
        model.addAttribute("profileVisibilityPreference", userSettingsDto.profileVisibility());
        model.addAttribute("profileDiscoverablePreference", userSettingsDto.profileDiscoverable());
        model.addAttribute("currentEmail", userDetails.getUsername());

        return "site/settings";
    }

    @GetMapping("/account-deleted")
    public String accountDeleted(Model model) {
        model.addAttribute("welcomePage", WebSecurityConfig.WELCOME_URL_MATCHER);

        return "site/account-deleted-confirmation";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("homePath", WebSecurityConfig.HOME_URL_MATCHER);
        model.addAttribute("updateProfilePath", ApiConfig.API_BASE_PATH + "/user/update-profile");

        return "site/profile";
    }
}
