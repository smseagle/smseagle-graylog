/**
 * Copyright 2013-2014 TORCH GmbH, 2015 Graylog, Inc.
 *
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.Notifications.smseagle;

import static java.lang.Math.min;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.Notification;
import org.graylog2.plugin.alarms.callbacks.NotificationConfigurationException;
import org.graylog2.plugin.alarms.callbacks.NotificationException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.DropdownField;
import org.graylog2.plugin.configuration.fields.NumberField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SMSEagleNotification implements Notification
 {
    private static final Logger LOG = LoggerFactory.getLogger(SMSEagleNotification.class);

    private static final String NAME = "SMSEagle Notification";
    private static final int MAX_MSG_LENGTH = 140;

    private static final String CK_URL = "smseagle_url";
    private static final String CK_AUTH_TOKEN = "auth_token";
    private static final String CK_TO_NUMBER = "to_number";
    private static final String CK_TO_CONTACT = "to_contact";
    private static final String CK_TO_GROUP = "to_group";
    private static final String CK_DATA_TYPE = "smseagle_type";
    private static final String CK_RING_DURATION = "ring_duration";
    private static final String CK_TTS_MODEL_ID = "tts_model";
    private static final String CK_ELEVENLABS_REMOTE_API_KEY = "elevenlabs_api_key";

    private static final String[] MANDATORY_CONFIGURATION_KEYS = new String[] {
            CK_URL, CK_AUTH_TOKEN
    };
    private static final List<String> SENSITIVE_CONFIGURATION_KEYS = ImmutableList.of(CK_AUTH_TOKEN);

    private Configuration configuration;

    @Override
    public void initialize(final Configuration config) throws NotificationConfigurationException {
        this.configuration = config;
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws NotificationException {
        final SMSEagleClient smsEagleClient = new SMSEagleClient(
                configuration.getString(CK_URL), configuration.getString(CK_AUTH_TOKEN));

        try {
            call(stream, result, smsEagleClient);
        } catch (SMSEagleException e) {
            LOG.error("Could not send alarm via SMSEagle", e);
            throw new NotificationException(e.getMessage(), e);
        }
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        final ConfigurationRequest cr = new ConfigurationRequest();

        cr.addField(new TextField(CK_URL, "URL of the SMSEagle SMS gate", "",
                "URL of your SMSEagle device",
                ConfigurationField.Optional.NOT_OPTIONAL));
        cr.addField(new TextField(CK_AUTH_TOKEN, "Access Token", "",
                "SMSEagle Access Token",
                ConfigurationField.Optional.NOT_OPTIONAL));
        cr.addField(new TextField(CK_TO_NUMBER, "Recipient Phone Number", "",
                "Recipient telephone number (or numbers separated with comma)",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_TO_CONTACT, "Recipient Contact ID", "",
                "Recipient contact ID (or contacts separated with comma)",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_TO_GROUP, "Group Name", "",
                "Group ID (or group IDs separated with comma) defined in SMSEagle Phonebook",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField((ConfigurationField) new DropdownField(CK_DATA_TYPE, "Data Type", "SMS",
                (Map<String, String>) ImmutableMap.<String, String>builder()
                            .put("SMS"               , "SMS")
                            .put("FLASHSMS"          , "Flash SMS")
                            .put("MULTICHANNEL"      , "Send a ring call and an SMS")
                            .put("SIGNAL"            , "Signal (beta)")
                            .put("WHATSAPP"          , "WhatsApp")
                            .put("RING"              , "Ring call")
                            .put("TTS"               , "Text-to-Speech")
                            .put("TTS_ADV"           , "Text-to-Speech (advanced)")
                            .put("ELEVENLABS"        , "ElevenLabs (local)")
                            .put("ELEVENLABS_REMOTE" , "ElevenLabs (remote, requires API key)"),
                "The type of data you want to send.",
                ConfigurationField.Optional.NOT_OPTIONAL));
        cr.addField(new NumberField(CK_RING_DURATION, "Ring duration", 10,
                "Duration of call (in seconds).",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new NumberField(CK_TTS_MODEL_ID, "TTS model ID", 0,
                "TTS model ID, defined in the SMSEagle management panel or remotely on elevenLabs",
                ConfigurationField.Optional.OPTIONAL));
        cr.addField(new TextField(CK_ELEVENLABS_REMOTE_API_KEY, "ElevenLabs API key", "",
                "The ElevenLabs API key. Do not share this with anyone. Used only with the Remote ElevenLabs",
                ConfigurationField.Optional.OPTIONAL));
        return cr;
    }

    @SuppressWarnings("null")
    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(@SuppressWarnings("null") String key, @SuppressWarnings("null") Object value) {
                if (SENSITIVE_CONFIGURATION_KEYS.contains(key)) {
                    return "****";
                }
                return value;
            }
        });
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        for (String key : MANDATORY_CONFIGURATION_KEYS) {
            if (!configuration.stringIsSet(key)) {
                throw new ConfigurationException(key + " is mandatory and must not be empty.");
            }
        }
        if (!configuration.stringIsSet(CK_TO_NUMBER) && !configuration.stringIsSet(CK_TO_CONTACT)
                && !configuration.stringIsSet(CK_TO_GROUP)) {
            throw new ConfigurationException(
                    "Either Recipient Phone Number, contact, or Group Name must not be empty.");
        }
        if (!configuration.stringIsSet(CK_TTS_MODEL_ID) && (configuration.getString(CK_DATA_TYPE) == "TTS-ADV"
                || Pattern.matches("ELEVENLABS.*", configuration.getString(CK_DATA_TYPE)))) {
            throw new ConfigurationException(
                    "Advanced Text-to-Speech calls and ElevenLabs Text-to-Speech calls require an ID of the TTS model");
        }
        if (!configuration.stringIsSet(CK_ELEVENLABS_REMOTE_API_KEY)
                && configuration.getString(CK_DATA_TYPE) == "ELEVENLABS_REMOTE") {
            throw new ConfigurationException(
                    "Remote ElevenLabs calls require an API key for ElevenLabs and it must not be empty");
        }
    }

    @VisibleForTesting
    void call(final Stream stream, final AlertCondition.CheckResult result, final SMSEagleClient smsEagleClient)
            throws SMSEagleException {
        send(smsEagleClient, result);
    }

    public String getName() {
        return NAME;
    }

    private void send(final SMSEagleClient client, final AlertCondition.CheckResult result)
            throws SMSEagleException {

            client.contact(configuration.getString(CK_TO_NUMBER), configuration.getString(CK_TO_CONTACT),
                configuration.getString(CK_TO_GROUP), buildMessage(result), configuration.getString(CK_DATA_TYPE),
                configuration.getInt(CK_RING_DURATION), configuration.getInt(CK_TTS_MODEL_ID),
                configuration.getString(CK_ELEVENLABS_REMOTE_API_KEY));

        LOG.debug("Contact Successful");
    }

    private String buildMessage(final AlertCondition.CheckResult result) {
        final String msg = "[Graylog] " + result.getResultDescription();

        return msg.substring(0, min(msg.length(), MAX_MSG_LENGTH));
    }
}