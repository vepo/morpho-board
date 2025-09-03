package dev.vepo.morphoboard.mailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.vepo.morphoboard.notifications.NotificationEvent;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailerService {

    private static final Logger logger = LoggerFactory.getLogger(MailerService.class);
    private final Mailer mailer;

    @Inject
    public MailerService(Mailer mailer) {
        this.mailer = mailer;
    }

    public void listenNotifications(@ObservesAsync NotificationEvent notification) {
        logger.info("Processing CDI Event! event={}", notification);
        mailer.send(
                    Mail.withText("quarkus@quarkus.io",
                                  "Ahoy from Quarkus",
                                  "A simple email sent from a Quarkus application."));
    }
}
