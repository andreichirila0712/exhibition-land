package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.UserAuthenticationRequestDTO;
import andrei.chirila.prove_yourself.user.UserRegistrationRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestBody UserAuthenticationRequestDTO user) {
        Authentication authentication = this.authService.authenticate(user.username(), user.password());
        UserDetails userDetails = this.authService.principal(authentication);

        return this.authService.jwtToken(userDetails.getUsername());
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationRequestDTO user) {
        this.authService.save(user);

        return "redirect:/index";
    }
}

