package andrei.chirila.prove_yourself;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@SpringBootApplication
@Controller
public class ProveYourselfApplication {

	static void main(String[] args) {
		SpringApplication.run(ProveYourselfApplication.class, args);
	}
}
