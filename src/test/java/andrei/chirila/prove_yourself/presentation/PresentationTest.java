package andrei.chirila.prove_yourself.presentation;

import andrei.chirila.prove_yourself.user.User;
import andrei.chirila.prove_yourself.user.UserService;
import andrei.chirila.prove_yourself.utils.S3Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PresentationTest {
    private static final String USER_ID = "user123";

    @Mock
    private S3Utility s3Utility;
    @Mock
    private PresentationRepository presentationRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PresentationService presentationService;
    @Captor
    private ArgumentCaptor<Presentation> presentationCaptor;

    @Test
    public void postPresentation() {
        PresentationDTO presentationDTO = new PresentationDTO(
                "Test Presentation",
                "https://test.com",
                "Test Description"
        );

        Mockito.when(s3Utility.uploadFile(any(), any(), any())).thenReturn("thumbnail-user123.jpg");
        Mockito.when(presentationRepository.save(any())).thenReturn(new Presentation("Test Presentation", "thumbnail-user123.jpg", "https://test.com", "Test Description", new User(USER_ID, "u1", "u1", "u1", "u1")));

        Presentation savedPresentation = presentationService.createPresentation(USER_ID, presentationDTO, null);

        assertEquals("Test Presentation", savedPresentation.getName());
        assertEquals("Test Description", savedPresentation.getDescription());
        assertEquals("https://test.com", savedPresentation.getLink());
        assertEquals("thumbnail-user123.jpg", savedPresentation.getThumbnail());
    }

    @Test
    public void getPresentation() {
        Presentation presentation = new Presentation("Test Presentation", "thumbnail-user123.jpg", "https://test.com", "Test Description", new User(USER_ID, "u1", "u1", "u1", "u1"));
        Mockito.when(presentationRepository.findPresentationByUserIdAndName(any(), any())).thenReturn(Optional.of(presentation));
        Presentation retrievedPresentation = presentationService.retrievePresentation(USER_ID, "Test Presentation");

        assertEquals(presentation, retrievedPresentation);
    }

    @Test
    public void deletePresentation() {
        presentationService.deletePresentation(USER_ID, "Test Presentation");
        Mockito.verify(presentationRepository).deletePresentationByUserIdAndName(any(), any());
    }

    @Test
    public void patchPresentation() {
        Presentation presentation = new Presentation("Test Presentation", "thumbnail-user123.jpg", "https://test.com", "Test Description", new User(USER_ID, "u1", "u1", "u1", "u1"));
        Mockito.when(presentationRepository.findPresentationByUserIdAndName(any(), any())).thenReturn(Optional.of(presentation));
        Map<String, String> data = Map.of("name", "New Name", "description", "New Description", "link", "https://new.com");
        presentationService.patchPresentation(USER_ID, "Test Presentation", data);
        Mockito.verify(presentationRepository).save(presentationCaptor.capture());

        assertEquals("New Name", presentationCaptor.getValue().getName());
        assertEquals("New Description", presentationCaptor.getValue().getDescription());
        assertEquals("https://new.com", presentationCaptor.getValue().getLink());
    }

}
