package andrei.chirila.prove_yourself.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserSettingsDto(
        @NotBlank String language,
        @NotBlank String theme,
        @NotBlank String dateFormat,
        @NotBlank String profileVisibility,
        @NotBlank String profileDiscoverable) {
}
