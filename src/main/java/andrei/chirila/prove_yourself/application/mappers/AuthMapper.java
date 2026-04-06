package andrei.chirila.prove_yourself.application.mappers;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserResponseDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthMapper {
    private AuthMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static User fromDto(final CreateUserDto createUserDto) {
        return new User(
                createUserDto.name(),
                createUserDto.email()
        );
    }

    public static Authentication fromDto(final LoginRequestDto loginRequestDto) {
        return new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
    }

    public static UserResponseDto toDto(final User user) {
        return new UserResponseDto(user.getId(), user.getName(),user.getEmail(), user.getRole());
    }
}
