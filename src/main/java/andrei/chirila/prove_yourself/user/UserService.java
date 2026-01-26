package andrei.chirila.prove_yourself.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Client s3Client;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.s3Client = S3Client.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .httpClient(UrlConnectionHttpClient.create())
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
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
    public String uploadProfilePicture(String providerId, MultipartFile image) throws IOException {
        Optional<User> user = this.userRepository.findByProviderId(providerId);
        String urlToUploadedPhoto = "";

        if (user.isPresent()) {
            String bucketName = "garage-bucket";
            String contentType = image.getContentType();
            String extension = switch(contentType) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpg";
                default -> ".jpeg";
            };
            String objectName = user.get().getUserId() + extension;
            String fileName = image.getOriginalFilename();
            IO.println(objectName + " " + contentType + " " + fileName);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .key(objectName)
                    .build();
            RequestBody body = RequestBody.fromBytes(image.getBytes());

            s3Client.putObject(request, body);
            s3Client.close();
            user.get().setProfilePictureUrl(objectName);

            urlToUploadedPhoto = createPresignedUrlToPhoto(bucketName, objectName);
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

            urlToUploadedPhoto = createPresignedUrlToPhoto("garage-bucket", user.get().getProfilePictureUrl());
        }

        return urlToUploadedPhoto;
    }

    @Transactional
    public void deleteUserProfile(String providerId) {
        this.userRepository.deleteByProviderId(providerId);
    }

    private String createPresignedUrlToPhoto(String bucketName, String objectName) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toExternalForm();
        }
    }
}
