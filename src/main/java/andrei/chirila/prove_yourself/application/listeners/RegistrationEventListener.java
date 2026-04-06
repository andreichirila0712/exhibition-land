package andrei.chirila.prove_yourself.application.listeners;

import andrei.chirila.prove_yourself.domain.UserRegisteredEvent;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RegistrationEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationEventListener.class);
    private final EmailService emailService;

    public RegistrationEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(UserRegisteredEvent event) {
        emailService.sendEmail(event.getUserEmail(), event.getActivationToken());
        logger.info("[EVENT] : Received {}.", event.getClass().getSimpleName());
    }
}
