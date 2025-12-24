package andrei.chirila.prove_yourself.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUserPostOAuthLogin(String provider, String providerId, String email, String name) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);

        if (user.isEmpty())
            this.userRepository.save(new User(provider, providerId, email, name));
        else throw new IllegalArgumentException("already present");
    }

}
