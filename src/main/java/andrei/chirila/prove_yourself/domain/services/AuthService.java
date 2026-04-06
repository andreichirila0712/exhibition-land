package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;

import java.util.UUID;

public interface AuthService {
    String login(LoginRequestDto loginRequestDto);
    boolean validateToken(String token);
    String getUserFromToken(String token);
    void createUser(CreateUserDto createUserDto);
    User getUser(UUID id);
    void activateAccount(UUID activationToken);
}
