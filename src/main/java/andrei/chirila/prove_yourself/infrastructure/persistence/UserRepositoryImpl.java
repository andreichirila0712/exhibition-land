package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
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

    @Override
    public void deleteUser(User user) {
        this.postgresUserRepository.delete(user);
    }

    @Override
    public Optional<User> findByAccountName(String username) {
        return postgresUserRepository.findByAccountName(username);
    }
}
