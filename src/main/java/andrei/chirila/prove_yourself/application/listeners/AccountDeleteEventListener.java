package andrei.chirila.prove_yourself.application.listeners;

import andrei.chirila.prove_yourself.domain.UserDeletedAccountEvent;
import andrei.chirila.prove_yourself.domain.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountDeleteEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AccountDeleteEventListener.class);
    private final EmailService emailService;

    public AccountDeleteEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserDeletedAccountEvent event) {
        this.emailService.sendConfirmationDelete(event.getUserEmail());
        logger.info("[EVENT] : Received {}", event.getClass().getSimpleName());
    }
}
