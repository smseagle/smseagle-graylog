package org.graylog.plugins.smseagle;

import jakarta.inject.Inject;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationConfig;

public class SMSEagleEventNotificationFactory implements EventNotification.Factory {

    @Inject
    public SMSEagleEventNotificationFactory() {
    }

    @Override
    public EventNotification createNotification(final EventNotificationConfig config) {
        final SMSEagleNotificationConfig typed = (SMSEagleNotificationConfig) config;
        return new SMSEagleEventNotification(typed);
    }

    @Override
    public EventNotificationConfig getConfig() {
        return new SMSEagleNotificationConfig();
    }
}