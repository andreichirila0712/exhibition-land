package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.services.EmailService;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(final String to, final UUID token) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Verify your account");
        message.setText("To verify your account please click the link below:\nhttp://localhost:8080" + WebSecurityConfig.ACCOUNT_VERIFICATION_URL_MATCHER + "?token=" + token);

        javaMailSender.send(message);
        logger.info("[EMAIL] : Email successfully sent to {}", to);
    }
}
