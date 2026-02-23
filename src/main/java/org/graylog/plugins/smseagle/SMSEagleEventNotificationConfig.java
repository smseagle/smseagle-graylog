package org.graylog.plugins.smseagle;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.DropdownField;
import org.graylog2.plugin.configuration.fields.NumberField;
import org.graylog2.plugin.configuration.fields.TextField;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SMSEagleEventNotificationConfig implements EventNotificationConfig {
    public static final String TYPE_NAME = "smseagle-notification-v2";

    public static final String CK_URL = "smseagle_url";
    public static final String CK_AUTH_TOKEN = "auth_token";
    public static final String CK_TO_NUMBER = "to_number";
    public static final String CK_TO_CONTACT = "to_contact";
    public static final String CK_TO_GROUP = "to_group";
    public static final String CK_DATA_TYPE = "smseagle_type";
    public static final String CK_RING_DURATION = "ring_duration";
    public static final String CK_TTS_MODEL_ID = "tts_model";
    public static final String CK_ELEVENLABS_DIRECT_API_KEY = "elevenlabs_api_key";

    private static final List<String> SENSITIVE_CONFIGURATION_KEYS = ImmutableList.of(CK_AUTH_TOKEN);

    private final String id;
    private final Configuration configuration;

    public SMSEagleEventNotificationConfig(String id, Configuration configuration) {
        this.id = id;
        this.configuration = configuration;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String type() {
        return TYPE_NAME;
    }

    public Configuration configuration() {
        return configuration;
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        final ConfigurationRequest cr = new ConfigurationRequest();

        cr.addField(new TextField(CK_URL, "URL SMSEagle", "",
                "URL urządzenia SMSEagle",
                ConfigurationField.Optional.NOT_OPTIONAL));
        cr.addField(new TextField(CK_AUTH_TOKEN, "Access Token", "",
                "Token dostępu SMSEagle",
                ConfigurationField.Optional.NOT_OPTIONAL));
        cr.addField(new TextField(CK_TO_NUMBER, "Numer telefonu odbiorcy", "",
                "Numery rozdzielone przecinkami",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_TO_CONTACT, "Contact ID odbiorcy", "",
                "ID kontaktów rozdzielone przecinkami",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_TO_GROUP, "Group ID", "",
                "ID grup rozdzielone przecinkami",
                ConfigurationField.Optional.OPTIONAL));

        cr.addField(new DropdownField(
                CK_DATA_TYPE,
                "Typ",
                "SMS",
                (Map<String, String>) ImmutableMap.<String, String>builder()
                        .put("SMS", "SMS")
                        .put("FLASHSMS", "Flash SMS")
                        .put("MULTICHANNEL", "Ring + SMS")
                        .put("SIGNAL", "Signal")
                        .put("WHATSAPP", "WhatsApp")
                        .put("RING", "Ring call")
                        .put("TTS", "Text-to-Speech")
                        .put("TTS_ADV", "Text-to-Speech (Advanced)")
                        .put("ELEVENLABS", "ElevenLabs (local)")
                        .put("ELEVENLABS_DIRECT", "ElevenLabs (direct call)")
                        .build(),
                "Co ma zostać wysłane.",
                ConfigurationField.Optional.NOT_OPTIONAL
        ));

        cr.addField(new NumberField(CK_RING_DURATION, "Czas dzwonienia (s)", 10,
                "Czas połączenia w sekundach.",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new NumberField(CK_TTS_MODEL_ID, "TTS model ID", 0,
                "ID modelu TTS dla TTS_ADV/ELEVENLABS.",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_ELEVENLABS_DIRECT_API_KEY, "ElevenLabs API key", "",
                "Wymagane dla ELEVENLABS_DIRECT.",
                ConfigurationField.Optional.OPTIONAL));

        return cr;
    }

    
    @Override
    public EventNotificationConfigResponse toResponse() {
        return EventNotificationConfigResponse.builder()
            .type(TYPE_NAME)
            .url(CK_URL)
            .build();
    }

    @Override
    public Map<String, Object> toPersisted() {
        return (Map<String, Object>) (Map) configuration.getSource();
    }

    @Override
    public List<String> maskedFields() {
        return SENSITIVE_CONFIGURATION_KEYS;
    }

    @Override
    public boolean isV2() {
        return true;
    }
}