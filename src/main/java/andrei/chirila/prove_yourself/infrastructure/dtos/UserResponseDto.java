package andrei.chirila.prove_yourself.infrastructure.dtos;

import andrei.chirila.prove_yourself.domain.Role;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Role role
) {
}
