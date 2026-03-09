package andrei.chirila.prove_yourself.security;

import andrei.chirila.prove_yourself.user.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = switch (provider) {
            case "github" -> Integer.toString(oAuth2User.getAttribute("id"));
            case "google" -> oAuth2User.getAttribute("sub");
            default -> "";
        };

        if (this.userService.retrieveCurrentUser(providerId) != null) {
            return oAuth2User;
        }
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profilePictureUrl = switch (provider) {
            case "github" -> oAuth2User.getAttribute("avatar_url");
            case "google" -> oAuth2User.getAttribute("picture");
            default -> "";
        };



        this.userService.saveUserPostOAuthLogin(provider, providerId, email, name, profilePictureUrl);

        return oAuth2User;
    }
}
