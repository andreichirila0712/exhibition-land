package andrei.chirila.prove_yourself.application.listeners;

import andrei.chirila.prove_yourself.domain.UserChangedPasswordEvent;
import andrei.chirila.prove_yourself.domain.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordChangeEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PasswordChangeEventListener.class);
    private final EmailService emailService;

    public PasswordChangeEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(UserChangedPasswordEvent event) {
        this.emailService.sendConfirmationPassword(event.getUserEmail(), event.getNewPasswordConfirmationToken());
        logger.info("[EVENT] : Received {}", event.getClass().getSimpleName());
    }
}
