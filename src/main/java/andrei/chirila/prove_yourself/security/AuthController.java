package andrei.chirila.prove_yourself.security;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/home")
    public String profile(OAuth2AuthenticationToken token, Model model) {
        if (token.getPrincipal() == null) throw  new NullPointerException();

        model.addAttribute("name", token.getPrincipal().getAttribute("name"));
        switch (token.getAuthorizedClientRegistrationId()) {
            case "github" -> model.addAttribute("avatarUrl", token.getPrincipal().getAttribute("avatar_url"));
            case "google" -> model.addAttribute("picture", token.getPrincipal().getAttribute("picture"));
        }

        return "user/home";
    }


}
