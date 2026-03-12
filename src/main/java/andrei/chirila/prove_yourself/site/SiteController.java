package andrei.chirila.prove_yourself.site;

import andrei.chirila.prove_yourself.user.User;
import andrei.chirila.prove_yourself.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
    private final UserService userService;

    public SiteController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String index(@AuthenticationPrincipal OAuth2User loggedUser, Model model) {
        //User user = this.userService.retrieveCurrentUser(loggedUser.getName());

        //model.addAttribute("avatar", this.userService.getProfilePictureUrl(user.getProviderId()));
        //model.addAttribute("name", user.getName());


        return "index";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "site/welcome";
    }

}
