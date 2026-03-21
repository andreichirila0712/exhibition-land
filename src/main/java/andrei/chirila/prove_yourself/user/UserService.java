package andrei.chirila.prove_yourself.user;

import andrei.chirila.prove_yourself.exception.EmailAlreadyExistsException;
import andrei.chirila.prove_yourself.exception.UsernameAlreadyExistsException;
import andrei.chirila.prove_yourself.utils.S3Utility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Utility s3Utility;
    private final PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, S3Utility s3Utility, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.s3Utility = s3Utility;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveUser(UserRegistrationRequestDTO user) {
        if (this.userRepository.existsByUsername(user.username())) {
            logger.error("Username already exists", new UsernameAlreadyExistsException("Username already exists"));
        }

        if (this.userRepository.existsByEmail(user.email())) {
            logger.error("EmailConfig already exists", new EmailAlreadyExistsException("EmailConfig already exists"));
        }

        User newUser = new User(
                user.name(),
                user.email()
        );

        newUser.setUsername(user.username());
        newUser.setPassword(this.passwordEncoder.encode(user.password()));
        newUser.setEmailVerified(false);

        this.userRepository.save(newUser);
    }

    public boolean checkIfUsernameExists(final String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean getUserActiveStatus(final String email) {
        return this.userRepository.checkActiveStatusByEmail(email);
    }

    @Transactional
    public void verifyEmail(final String email) {
        User user = this.userRepository.findByEmail(email);

        user.setEmailVerified(true);
    }
}
