package andrei.chirila.prove_yourself.presentation;

import andrei.chirila.prove_yourself.user.User;
import andrei.chirila.prove_yourself.user.UserService;
import andrei.chirila.prove_yourself.utils.S3Utility;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PresentationService {
    private final PresentationRepository repository;
    private final UserService userService;
    private final S3Utility s3Utility;

    public PresentationService(PresentationRepository repository, UserService userService, S3Utility s3Utility) {
        this.repository = repository;
        this.userService = userService;
        this.s3Utility = s3Utility;
    }

    public Presentation retrievePresentation(String userId, String presentationName) {
        Optional<Presentation> presentation = this.repository.findPresentationByUserIdAndName(userId, presentationName);

        return presentation.orElseThrow(NullPointerException::new); // TODO change into custom exception if presentation is not found
    }

    public List<Presentation> retrieveAllPresentations(String userId) {
        return this.repository.findAllByUserId(userId);
    }

    @Transactional
    public void deletePresentation(String userId, String presentationName) {
        this.s3Utility.deleteFile("garage-bucket", this.repository.findThumbnailByUserIdAndName(userId, presentationName).orElse(""));
        this.repository.deletePresentationByUserIdAndName(userId, presentationName);
    }

    @Transactional
    public Presentation createPresentation(String userId, PresentationDTO presentationDTO, MultipartFile thumbnail) {
        String thumbnailUrl = this.s3Utility.uploadFile("garage-bucket", userId + "-thumbnail", thumbnail);

        Presentation presentation = new Presentation(
                presentationDTO.name(),
                thumbnailUrl,
                presentationDTO.link(),
                presentationDTO.description(),
                userService.retrieveCurrentUser(userId));

        return this.repository.save(presentation);
    }

    public String getThumbnailUrl(String userId, String presentationName) {
        Optional<String> thumbnailName = this.repository.findThumbnailByUserIdAndName(userId, presentationName);

        if (thumbnailName.isEmpty()) return "";

        return this.s3Utility.createPresignedUrl("garage-bucket", thumbnailName.get());
    }

    @Transactional
    public void patchPresentation(String userId, String presentationName, Map<String, String> data) {
        Optional<Presentation> presentation = this.repository.findPresentationByUserIdAndName(userId, presentationName);

        if (presentation.isPresent()) {
            Presentation updatedPresentation = presentation.get();
            updatedPresentation.setName(data.getOrDefault("name", updatedPresentation.getName()));
            updatedPresentation.setThumbnail(data.getOrDefault("thumbnail", updatedPresentation.getThumbnail()));
            updatedPresentation.setLink(data.getOrDefault("link", updatedPresentation.getLink()));
            updatedPresentation.setDescription(data.getOrDefault("description", updatedPresentation.getDescription()));

            this.repository.save(updatedPresentation);
        }
    }

    public List<PresentationResponseDTO> toResponseDTOList(List<Presentation> presentations) {
        List<PresentationResponseDTO> responseDTOList = new ArrayList<>();

        for (Presentation presentation : presentations) {
            PresentationResponseDTO responseDTO = new PresentationResponseDTO(
                    presentation.getName(),
                    this.s3Utility.createPresignedUrl("garage-bucket", presentation.getThumbnail()),
                    presentation.getDescription(),
                    presentation.getLink()
            );

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }
}
