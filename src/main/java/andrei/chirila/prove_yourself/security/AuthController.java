package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.UserAuthenticationRequestDTO;
import andrei.chirila.prove_yourself.user.UserRegistrationRequestDTO;
import andrei.chirila.prove_yourself.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

