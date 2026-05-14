package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserSettingsDto;
import org.springframework.web.multipart.MultipartFile;

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
    void updateProfile(String name, String username, String about, String location, String website, String email);
    void uploadAvatar(MultipartFile avatar, String email);
    String getUrlToAvatar(String email);
    User getUser(String email);
}
