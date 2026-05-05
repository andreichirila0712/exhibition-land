package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.infrastructure.dtos.UserSettingsDto;

import java.util.UUID;

public interface UserService {
    UserSettingsDto getUserSettings(String email);
    void updateLanguage(String email, String language);
    void updateTheme(String email, String theme);
    void updateDateFormat(String email, String dateFormat);
    void updateVisibility(String email, String visibility);
    void changeEmail(String email, String newEmail);
    void changePassword(String email, String newPassword, String inputPassword);
    boolean checkPassword(String email, String password);
    void deleteAccount(String email);
}
