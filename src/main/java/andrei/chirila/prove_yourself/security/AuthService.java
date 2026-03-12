package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.UserRegistrationRequestDTO;
import andrei.chirila.prove_yourself.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public Authentication authenticate(final String username, final String password) {
        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
    }

    public UserDetails principal(Authentication authentication) {
        return (UserDetails) authentication.getPrincipal();
    }

    public String jwtToken(final String username) {
        return this.jwtUtil.generateToken(username);
    }

    public void save(final UserRegistrationRequestDTO user) {
        this.userService.saveUser(user);
    }
}
