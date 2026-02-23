package org.graylog.plugins.smseagle;

import static java.lang.Math.min;

import jakarta.inject.Inject;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSEagleEventNotification implements EventNotification {
    private static final Logger LOG = LoggerFactory.getLogger(SMSEagleEventNotification.class);
    private static final int MAX_MSG_LENGTH = 140;

    public interface Factory extends EventNotification.Factory<SMSEagleEventNotification> {
        @Override
        SMSEagleEventNotification create();
    }

    @Inject
    public SMSEagleEventNotification() {
    }

    @Override
    public void execute(EventNotificationContext ctx) throws PermanentEventNotificationException {
        final EventNotificationConfig notificationConfig = ctx.notificationConfig();
        if (!(notificationConfig instanceof SMSEagleEventNotificationConfig)) {
            throw new PermanentEventNotificationException("Unknown notification config type: " + notificationConfig.getClass());
        }

        final SMSEagleEventNotificationConfig config = (SMSEagleEventNotificationConfig) notificationConfig;

        final SMSEagleClient client = new SMSEagleClient(
                config.smseagleUrl(),
                config.authToken()
        );

        try {
            client.contact(
                    config.toNumber(),
                    config.toContact(),
                    config.toGroup(),
                    buildMessage(ctx),
                    config.smseagleType(),
                    config.ringDuration(),
                    config.ttsModel(),
                    config.elevenlabsApiKey()
            );
        } catch (SMSEagleException e) {
            LOG.error("Could not send SMSEagle notification", e);
            throw new PermanentEventNotificationException("Could not send SMSEagle notification: " + e.getMessage(), e);
        }
    }

    private String buildMessage(EventNotificationContext ctx) {
        String msg = "[Graylog] Event notification";

        try {
            if (ctx.event() != null && ctx.event().message() != null) {
                msg = "[Graylog] " + ctx.event().message();
            }
        } catch (Exception ignored) {
            LOG.error("Couldn't build an SMSEagle notification message", e);
        }

        return msg.substring(0, min(msg.length(), MAX_MSG_LENGTH));
    }
}
