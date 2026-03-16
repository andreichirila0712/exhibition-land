package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.UserAuthenticationRequestDTO;
import andrei.chirila.prove_yourself.user.UserRegistrationRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.FragmentsRendering;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String authenticateUser(UserAuthenticationRequestDTO user) {
        Authentication authentication = this.authService.authenticate(user.username(), user.password());
        UserDetails userDetails = this.authService.principal(authentication);

        return this.authService.jwtToken(userDetails.getUsername());
    }

    @PostMapping("/register")
    public FragmentsRendering registerUser(UserRegistrationRequestDTO user) {
        this.authService.save(user);

        return FragmentsRendering.fragment("site/registration :: registration-successful").build();
    }

    @GetMapping("/registration-form")
    public FragmentsRendering registrationForm() {
        return FragmentsRendering.fragment("site/registration :: registration-modal").build();
    }
}

