package andrei.chirila.prove_yourself.emailing;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(final String to, final String subject, final String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(System.getenv("EMAIL_ADDRESS"));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        emailSender.send(message);
    }
}
