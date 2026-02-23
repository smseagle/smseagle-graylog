package org.graylog.plugins.smseagle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.plugin.rest.ValidationResult;

import javax.annotation.Nullable;

@AutoValue
@JsonTypeName(SMSEagleEventNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = SMSEagleEventNotificationConfig.Builder.class)
public abstract class SMSEagleEventNotificationConfig implements EventNotificationConfig {
    public static final String TYPE_NAME = "smseagle-notification-v2";

    private static final String FIELD_URL = "smseagle_url";
    private static final String FIELD_AUTH_TOKEN = "auth_token";
    private static final String FIELD_TO_NUMBER = "to_number";
    private static final String FIELD_TO_CONTACT = "to_contact";
    private static final String FIELD_TO_GROUP = "to_group";
    private static final String FIELD_DATA_TYPE = "smseagle_type";
    private static final String FIELD_RING_DURATION = "ring_duration";
    private static final String FIELD_TTS_MODEL_ID = "tts_model";
    private static final String FIELD_ELEVENLABS_API_KEY = "elevenlabs_api_key";

    @JsonProperty(FIELD_URL)
    public abstract String smseagleUrl();

    @JsonProperty(FIELD_AUTH_TOKEN)
    public abstract String authToken();

    @JsonProperty(FIELD_TO_NUMBER)
    @Nullable
    public abstract String toNumber();

    @JsonProperty(FIELD_TO_CONTACT)
    @Nullable
    public abstract String toContact();

    @JsonProperty(FIELD_TO_GROUP)
    @Nullable
    public abstract String toGroup();

    @JsonProperty(FIELD_DATA_TYPE)
    public abstract String smseagleType();

    @JsonProperty(FIELD_RING_DURATION)
    public abstract int ringDuration();

    @JsonProperty(FIELD_TTS_MODEL_ID)
    public abstract int ttsModel();

    @JsonProperty(FIELD_ELEVENLABS_API_KEY)
    @Nullable
    public abstract String elevenlabsApiKey();

    @Override
    @JsonIgnore
    public JobTriggerData toJobTriggerData(EventDto dto) {
        return EventNotificationExecutionJob.Data.builder().eventDto(dto).build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    @Override
    @JsonIgnore
    public ValidationResult validate() {
        final ValidationResult validation = new ValidationResult();

        if (smseagleUrl() == null || smseagleUrl().isEmpty()) {
            validation.addError(FIELD_URL, "SMSEagle URL cannot be empty.");
        }
        if (authToken() == null || authToken().isEmpty()) {
            validation.addError(FIELD_AUTH_TOKEN, "Access token cannot be empty.");
        }

        boolean hasRecipient = (toNumber() != null && !toNumber().isEmpty())
                || (toContact() != null && !toContact().isEmpty())
                || (toGroup() != null && !toGroup().isEmpty());
        if (!hasRecipient) {
            validation.addError(FIELD_TO_NUMBER, "At least one recipient (number, contact, or group) must be specified.");
        }

        return validation;
    }

    @Override
    public EventNotificationConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        return null;
    }

    @AutoValue.Builder
    public abstract static class Builder implements EventNotificationConfig.Builder<Builder> {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_SMSEagleEventNotificationConfig.Builder()
                    .type(TYPE_NAME)
                    .smseagleType("SMS")
                    .ringDuration(10)
                    .ttsModel(0)
                    .toNumber("")
                    .toContact("")
                    .toGroup("")
                    .elevenlabsApiKey("");
        }

        @JsonProperty(FIELD_URL)
        public abstract Builder smseagleUrl(String smseagleUrl);

        @JsonProperty(FIELD_AUTH_TOKEN)
        public abstract Builder authToken(String authToken);

        @JsonProperty(FIELD_TO_NUMBER)
        public abstract Builder toNumber(String toNumber);

        @JsonProperty(FIELD_TO_CONTACT)
        public abstract Builder toContact(String toContact);

        @JsonProperty(FIELD_TO_GROUP)
        public abstract Builder toGroup(String toGroup);

        @JsonProperty(FIELD_DATA_TYPE)
        public abstract Builder smseagleType(String smseagleType);

        @JsonProperty(FIELD_RING_DURATION)
        public abstract Builder ringDuration(int ringDuration);

        @JsonProperty(FIELD_TTS_MODEL_ID)
        public abstract Builder ttsModel(int ttsModel);

        @JsonProperty(FIELD_ELEVENLABS_API_KEY)
        public abstract Builder elevenlabsApiKey(String elevenlabsApiKey);

        public abstract SMSEagleEventNotificationConfig build();
    }
}
