package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.exceptions.EmailAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final PostgresUserRepository postgresUserRepository;

    public UserRepositoryImpl(PostgresUserRepository postgresUserRepository) {
        this.postgresUserRepository = postgresUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return postgresUserRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        if (postgresUserRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.error("[USER] : A user with the email {} already exists", user.getEmail());
            throw new EmailAlreadyExistsException("A user with the email " + user.getEmail() + " already exists");
        }

        // TODO checking for username once I figure it out if I will keep it or not

        return postgresUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return postgresUserRepository.findById(id);
    }

    @Override
    public boolean hasVerifiedEmail(String email) {
        return postgresUserRepository.findByEmail(email).map(User::isEmailVerified).orElse(false);
    }
}
