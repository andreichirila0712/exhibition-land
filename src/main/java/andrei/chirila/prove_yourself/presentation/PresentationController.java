package andrei.chirila.prove_yourself.presentation;

import andrei.chirila.prove_yourself.validation.ValidFileType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.FragmentsRendering;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller
public class PresentationController {
    private final PresentationService service;

    public PresentationController(PresentationService service) {
        this.service = service;
    }

    /*
    @GetMapping("/presentation-dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User user, Model model) {
        List<Presentation> presentations = this.service.retrieveAllPresentations(user.getName());
        model.addAttribute("presentations", presentations);

        return "site/presentation-dashboard";
    }

    @PostMapping("/presentation/post")
    public FragmentsRendering postPresentation(@AuthenticationPrincipal OAuth2User user, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("link") String link, @RequestParam("thumbnail") MultipartFile thumbnail, Model model) throws IOException {
        PresentationDTO presentationDTO = new PresentationDTO(title, link, description);
        Presentation presentation = this.service.createPresentation(user.getName(), presentationDTO, thumbnail);
        model.addAttribute("presentation", presentation);

        return FragmentsRendering.fragment("site/presentation-post :: presentation-post-success")
                .fragment("site/presentation-post :: presentation-post-object")
                .build();
    }

    @DeleteMapping("/presentation/delete/{name}")
    public FragmentsRendering deletePresentation(@AuthenticationPrincipal OAuth2User user, @PathVariable String name) {
        this.service.deletePresentation(user.getName(), name);

        return FragmentsRendering.fragment("site/presentation-delete :: presentation-delete-success")
                .build();
    }

    @GetMapping("/presentation/create-form")
    public FragmentsRendering createPresentationForm() {
        return FragmentsRendering.fragment("site/presentation-post :: presentation-post-modal")
                .build();
    }

    @GetMapping("/presentation/patch-modal/{name}")
    public FragmentsRendering patchPresentationModal(@AuthenticationPrincipal OAuth2User user, @PathVariable String name, Model model) {
        Presentation presentation = this.service.retrievePresentation(user.getName(), name);
        model.addAttribute("presentation", presentation);

        return FragmentsRendering.fragment("site/presentation-patch :: presentation-patch-modal")
                .build();
    }

    @PatchMapping("/presentation/patch")
    public FragmentsRendering patchPresentation(@AuthenticationPrincipal OAuth2User user,  @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("link") String link, @RequestParam("thumbnail") MultipartFile thumbnail) {
        Map<String, String> data = Map.of("name", title, "description", description, "link", link);
        this.service.patchPresentation(user.getName(), title, data);

        return FragmentsRendering.fragment("site/presentation-patch :: presentation-patch-success")
                .build();
    }

     */
}
