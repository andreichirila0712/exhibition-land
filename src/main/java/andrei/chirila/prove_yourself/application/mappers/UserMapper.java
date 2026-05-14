package andrei.chirila.prove_yourself.application.mappers;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserProfileDto;

public class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("This is a utility class and should not be instantiated");
    }

    public static UserProfileDto toDto(final User user) {
        return new UserProfileDto(
                user.getName(),
                user.getAccountName(),
                user.getAbout(),
                user.getLocation(),
                user.getWebsite()
        );
    }
}
