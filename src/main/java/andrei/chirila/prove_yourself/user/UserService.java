package andrei.chirila.prove_yourself.user;

import andrei.chirila.prove_yourself.utils.S3Utility;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Utility s3Utility;

    public UserService(UserRepository userRepository, S3Utility s3Utility) {
        this.userRepository = userRepository;
        this.s3Utility = s3Utility;
    }

    @Transactional
    public void saveUserPostOAuthLogin(String provider, String providerId, String email, String name, String profilePictureUrl) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);

        user.ifPresentOrElse(u -> {
            if (!Objects.equals(u.getEmail(), email)) u.setEmail(email);
            if (!Objects.equals(u.getName(), name)) u.setName(name);
        }, () -> this.userRepository.save(new User(provider, providerId, email, name, profilePictureUrl)));
    }

    public User retrieveCurrentUser(String providerId) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);

        return user.orElse(null);
    }

    @Transactional
    public void updateUser(String providerId, Map<String, String> data) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);

        user.ifPresent(u-> {
            if (data.containsKey("name") && !data.get("name").isEmpty()) u.setName(data.get("name"));
            if (data.containsKey("email") && !data.get("email").isEmpty()) u.setEmail(data.get("email"));

            this.userRepository.save(u);
        });
    }

    @Transactional
    public String uploadProfilePicture(String providerId, MultipartFile image) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);
        String urlToUploadedPhoto = "";

        if (user.isPresent()) {
            String objectName = user.get().getUserId().toString();
            String bucketName = "garage-bucket";

            user.get().setProfilePictureUrl(this.s3Utility.uploadFile(bucketName, objectName, image));

            urlToUploadedPhoto = this.s3Utility.createPresignedUrl(bucketName, objectName);
        }

        return urlToUploadedPhoto;
    }

    public String getProfilePictureUrl(String providerId) {
        Optional<User> user = this.userRepository.findByProviderId(providerId);
        String urlToUploadedPhoto = "";

        if (user.isPresent()) {
            if (user.get().getProfilePictureUrl().contains("http")) {
                return user.get().getProfilePictureUrl();
            }

            urlToUploadedPhoto = this.s3Utility.createPresignedUrl("garage-bucket", user.get().getProfilePictureUrl());
        }

        return urlToUploadedPhoto;
    }

    @Transactional
    public void deleteUserProfile(String providerId) {
        this.userRepository.deleteByProviderId(providerId);
    }
}
