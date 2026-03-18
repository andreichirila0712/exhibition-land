package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.emailing.EmailService;
import andrei.chirila.prove_yourself.exception.AccountIsAlreadyActiveException;
import andrei.chirila.prove_yourself.user.UserRegistrationRequestDTO;
import andrei.chirila.prove_yourself.user.UserService;
import andrei.chirila.prove_yourself.verification_token.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, VerificationTokenService tokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
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

    public void generateTokenForEmail(final String email) {
        this.tokenService.generateTokenForValidation(email);
    }

    public void sendVerificationEmail(final String to) {
        UUID token = this.tokenService.getVerificationToken(to);
        String text = "To verify your account please click the link below: \nhttp://localhost:8080/auth/activate?token=" + token;

        this.emailService.sendEmail(to, "Verify your account", text);
    }

    public void activateAccount(final UUID token) {
        String email = this.tokenService.getUserEmailFromToken(token);

        boolean userActiveStatus = this.userService.getUserActiveStatus(email);

        if (userActiveStatus) {
            logger.error("Account is already activated", new AccountIsAlreadyActiveException("Account is already activated"));
        }

        this.userService.verifyEmail(email);
        this.tokenService.removeToken(email);
    }
}
