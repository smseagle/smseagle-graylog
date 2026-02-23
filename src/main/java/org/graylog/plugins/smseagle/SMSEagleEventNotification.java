package org.graylog.plugins.smseagle;

import static java.lang.Math.min;

import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog2.plugin.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSEagleEventNotification implements EventNotification {
    private static final Logger LOG = LoggerFactory.getLogger(SMSEagleEventNotification.class);
    private static final int MAX_MSG_LENGTH = 140;

    private final SMSEagleEventNotificationConfig config;

    public SMSEagleEventNotification(SMSEagleEventNotificationConfig config) {
        this.config = config;
    }

    @Override
    public void execute(EventNotificationContext ctx) throws Exception {
        final Configuration c = config.configuration();

        final SMSEagleClient client = new SMSEagleClient(
                c.getString(SMSEagleEventNotificationConfig.CK_URL),
                c.getString(SMSEagleEventNotificationConfig.CK_AUTH_TOKEN)
        );

        try {
            client.contact(
                    c.getString(SMSEagleEventNotificationConfig.CK_TO_NUMBER),
                    c.getString(SMSEagleEventNotificationConfig.CK_TO_CONTACT),
                    c.getString(SMSEagleEventNotificationConfig.CK_TO_GROUP),
                    buildMessage(ctx),
                    c.getString(SMSEagleEventNotificationConfig.CK_DATA_TYPE),
                    c.getInt(SMSEagleEventNotificationConfig.CK_RING_DURATION),
                    c.getInt(SMSEagleEventNotificationConfig.CK_TTS_MODEL_ID),
                    c.getString(SMSEagleEventNotificationConfig.CK_ELEVENLABS_DIRECT_API_KEY)
            );
        } catch (SMSEagleException e) {
            LOG.error("Nie udało się wysłać powiadomienia SMSEagle", e);
            throw e;
        }
    }

    private String buildMessage(EventNotificationContext ctx) {
        // W Event Notifications zamiast AlertCondition.CheckResult masz dane eventu w ctx.
        // Najczęściej da się wyciągnąć tytuł/wiadomość/event definition; poniżej „bezpieczny fallback”.
        String msg = "[Graylog] Event notification";

        try {
            if (ctx.event() != null && ctx.event().message() != null) {
                msg = "[Graylog] " + ctx.event().message();
            }
        } catch (Exception ignored) {
            // fallback zostaje
        }

        return msg.substring(0, min(msg.length(), MAX_MSG_LENGTH));
    }
}