package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.config.ApiConfig;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(ApiConfig.API_BASE_PATH + "/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PatchMapping("/update-language")
    public ResponseEntity<?> updateLanguage(@RequestParam String language, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.updateLanguage(userDetails.getUsername(), language);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-theme")
    public ResponseEntity<?> updateTheme(@RequestParam String theme, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.updateTheme(userDetails.getUsername(), theme);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-date-format")
    public ResponseEntity<?> updateDateFormat(@RequestParam String dateFormat, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.updateDateFormat(userDetails.getUsername(), dateFormat);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-visibility")
    public ResponseEntity<?> updateVisibility(@RequestParam String visibility, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.updateVisibility(userDetails.getUsername(), visibility);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-email")
    public String changeEmail(@RequestParam String newEmail, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.changeEmail(userDetails.getUsername(), newEmail);

        return "fragments/modals :: email-sent-success-modal";
    }

    @PatchMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, String currentPassword, @AuthenticationPrincipal UserDetails userDetails) {
        this.service.changePassword(userDetails.getUsername(), newPassword, currentPassword);

        return "fragments/modals :: password-sent-success-modal";
    }

    @GetMapping("/check-current-password")
    public String checkPasswordMatches(@RequestParam String currentPassword, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        boolean isValid = this.service.checkPassword(userDetails.getUsername(), currentPassword);

        model.addAttribute("passwordValid", isValid);

        return "fragments/modals :: password-feedback";
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response, Model model) throws Exception {
        this.service.deleteAccount(userDetails.getUsername());
        ResponseCookie cookie = ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader("HX-Redirect", WebSecurityConfig.ACCOUNT_DELETED_CONFIRMATION_URL_MATCHER);

        return ResponseEntity.ok().build();
    }
}
