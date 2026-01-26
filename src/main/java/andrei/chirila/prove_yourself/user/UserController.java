package andrei.chirila.prove_yourself.user;

import andrei.chirila.prove_yourself.validation.ValidFileType;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        User user = this.userService.retrieveCurrentUser(oAuth2User.getName());


        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("profilePicture", this.userService.getProfilePictureUrl(user.getProviderId()));

        return "/user/profile";
    }

    @PatchMapping(value = "/profile/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestBody Map<String, String> data) {
        this.userService.updateUser(oAuth2User.getName(), data);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile/photo/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfilePhoto(@AuthenticationPrincipal OAuth2User oAuth2User, @ValidFileType @Valid @RequestParam("file") MultipartFile image) throws IOException{
        this.userService.uploadProfilePicture(oAuth2User.getName(), image);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile/delete")
    public ResponseEntity<?> deleteProfile(@AuthenticationPrincipal OAuth2User oAuth2User) {
        this.userService.deleteUserProfile(oAuth2User.getName());

        return ResponseEntity.ok().build();
    }

}
