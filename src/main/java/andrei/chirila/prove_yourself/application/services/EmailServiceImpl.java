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
    private final SimpleMailMessage message = new SimpleMailMessage();
    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(final String to, final UUID token) {
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Verify your account");
        message.setText("To verify your account please click the link below:\nhttp://localhost:8080" + WebSecurityConfig.ACCOUNT_VERIFICATION_URL_MATCHER + "?token=" + token);

        javaMailSender.send(message);
        logger.info("[EMAIL] : Email successfully sent to {} with activation token {}", to, token);
    }

    @Override
    public void sendConfirmationEmail(final String to, final UUID token) {
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Confirm your new email address");
        message.setText("To confirm your new email address please click the link below:\nhttp://localhost:8080" + WebSecurityConfig.EMAIL_CHANGE_CONFIRMATION_URL_MATCHER + "?token=" + token);

        javaMailSender.send(message);
        logger.info("[EMAIL] : Email successfully sent to {} with email change token {}", to, token);
    }

    @Override
    public void sendConfirmationPassword(String to, UUID token) {
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Confirm your new password");
        message.setText("To confirm your new password please click the link below:\nhttp://localhost:8080" + WebSecurityConfig.PASSWORD_CHANGE_CONFIRMATION_URL_MATCHER + "?token=" + token);

        javaMailSender.send(message);
        logger.info("[EMAIL] : Email successfully sent to {} with password change token {}", to, token);
    }

    @Override
    public void sendConfirmationDelete(String to) {
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Account deletion status");
        message.setText("The account associated with the email address " + to + " has been successfully deleted." +
                " All data formerly associated with the account is permanently deleted.");

        javaMailSender.send(message);
        logger.info("[EMAIL] : Email successfully sent to {} to confirm account deletion", to);
    }
}
