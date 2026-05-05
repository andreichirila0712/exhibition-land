package andrei.chirila.prove_yourself.application.listeners;

import andrei.chirila.prove_yourself.domain.UserChangedEmailEvent;
import andrei.chirila.prove_yourself.domain.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailChangeEventListener {
    private static final Logger logger = LoggerFactory.getLogger(EmailChangeEventListener.class);
    private final EmailService emailService;

    public EmailChangeEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(UserChangedEmailEvent event) {
        this.emailService.sendConfirmationEmail(event.getUserNewEmail(), event.getNewEmailConfirmationToken());
        logger.info("[EVENT] : Received {}", event.getClass().getSimpleName());
    }
}
