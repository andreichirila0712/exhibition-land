package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.User;
import andrei.chirila.prove_yourself.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "site/login";
    }

    @GetMapping("/home")
    public String profile(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        if (oAuth2User == null) throw  new NullPointerException();

        User user = this.userService.retrieveCurrentUser(oAuth2User.getName());
        model.addAttribute("name", user.getName());
        model.addAttribute("userPhoto", this.userService.getProfilePictureUrl(user.getProviderId()));

        return "user/home";
    }


}
