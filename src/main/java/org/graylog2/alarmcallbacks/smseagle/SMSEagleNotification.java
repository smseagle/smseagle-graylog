package org.graylog2.alarmcallbacks.smseagle;

import com.google.common.collect.ImmutableList;
import org.graylog2.notifications.Notification;
import org.graylog2.notifications.NotificationContext;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.lang.Math.min;

public class SMSEagleAlarmCallback implements Notification {
    private static final Logger LOG = LoggerFactory.getLogger(SMSEagleAlarmCallback.class);

    private static final int MAX_MSG_LENGTH = 140;

    private static final String CK_URL = "smseagle_url";
    private static final String CK_AUTH_TOKEN = "auth_token";
    private static final String CK_TO_NUMBER = "to_number";
    private static final String CK_TO_GROUP = "to_group";

    @Inject
    public SMSEagleAlarmCallback() {
    }

    @Override
    public void execute(NotificationContext context) {
        final Configuration config = context.getNotificationConfiguration().getConfiguration();
        final SMSEagleClient smsEagleClient = new SMSEagleClient(
                config.getString(CK_URL), config.getString(CK_AUTH_TOKEN));

        try {
            String message = buildMessage(context);
            smsEagleClient.sendSMS(
                    config.getString(CK_TO_NUMBER),
                    config.getString(CK_TO_GROUP),
                    message
            );
            LOG.debug("Sent SMS via SMSEagle OK");
        } catch (SMSEagleException e) {
            LOG.error("Could not send notification via SMSEagle", e);
        }
    }

    private String buildMessage(NotificationContext context) {
        // W nowym systemie pobieramy opis zdarzenia (Event)
        String eventDescription = context.getEvent().getMessage();
        final String msg = "[Graylog] " + eventDescription;
        return msg.substring(0, min(msg.length(), MAX_MSG_LENGTH));
    }

    public interface Factory extends Notification.Factory<SMSEagleAlarmCallback> {
        @Override
        SMSEagleAlarmCallback create();

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends Notification.Descriptor {
        public Descriptor() {
            super("SMSEagle Notification", "https://www.smseagle.eu/", "Sends notifications via SMSEagle SMS Gateway");
        }
    }

    public static class Config implements Notification.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest cr = new ConfigurationRequest();
            cr.addField(new TextField(CK_URL, "URL of SMSEagle", "", "URL of your SMSEagle device",
                    ConfigurationField.Optional.NOT_OPTIONAL));
            cr.addField(new TextField(CK_AUTH_TOKEN, "Access Token", "", "SMSEagle Access Token",
                    ConfigurationField.Optional.NOT_OPTIONAL));
            cr.addField(new TextField(CK_TO_NUMBER, "Recipient Phone Number", "",
                    "Recipient telephone number (or numbers separated with comma)",
                    ConfigurationField.Optional.OPTIONAL));
            cr.addField(new TextField(CK_TO_GROUP, "Group Name", "",
                    "Group Name defined in SMSEagle Phonebook",
                    ConfigurationField.Optional.OPTIONAL));
            return cr;
        }
    }
}