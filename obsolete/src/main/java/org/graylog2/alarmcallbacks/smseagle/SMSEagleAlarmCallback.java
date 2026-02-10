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
package org.graylog2.alarmcallbacks.smseagle;

import static java.lang.Math.min;

import java.util.List;
import java.util.Map;

import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class SMSEagleAlarmCallback implements AlarmCallback {
    private static final Logger LOG = LoggerFactory.getLogger(SMSEagleAlarmCallback.class);

    private static final String NAME = "SMSEagle AlarmCallback";
    private static final int MAX_MSG_LENGTH = 140;

    private static final String CK_URL = "smseagle_url";
    private static final String CK_AUTH_TOKEN = "auth_token";
    private static final String CK_TO_NUMBER = "to_number";
    private static final String CK_TO_GROUP = "to_group";
    
    private static final String[] MANDATORY_CONFIGURATION_KEYS = new String[]{
    		CK_URL, CK_AUTH_TOKEN
    };
    private static final List<String> SENSITIVE_CONFIGURATION_KEYS = ImmutableList.of(CK_AUTH_TOKEN);

    private Configuration configuration;

    @Override
    public void initialize(final Configuration config) throws AlarmCallbackConfigurationException {
        this.configuration = config;
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
        final SMSEagleClient smsEagleClient = new SMSEagleClient(
                configuration.getString(CK_URL), configuration.getString(CK_AUTH_TOKEN));

        try {
			call(stream, result, smsEagleClient);
		} catch (SMSEagleException e) {
            LOG.error("Could not send alarm via SMSEagle", e);
            throw new AlarmCallbackException(e.getMessage(), e);
		}
    }

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

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
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
        if ( !configuration.stringIsSet(CK_TO_NUMBER) && !configuration.stringIsSet(CK_TO_GROUP) ) {
        	throw new ConfigurationException("Either Recipient Phone Number or Group Name must not be empty.");
        }
    }


    @VisibleForTesting
    void call(final Stream stream, final AlertCondition.CheckResult result, final SMSEagleClient smsEagleClient) throws SMSEagleException {
    	send(smsEagleClient, result);
    }

    public String getName() {
        return NAME;
    }

    private void send(final SMSEagleClient client, final AlertCondition.CheckResult result)
            throws SMSEagleException {
    	
        client.sendSMS(configuration.getString(CK_TO_NUMBER), configuration.getString(CK_TO_GROUP), buildMessage(result));

        LOG.debug("Sent SMS OK");
    }

    private String buildMessage(final AlertCondition.CheckResult result) {
        final String msg = "[Graylog] " + result.getResultDescription();

        return msg.substring(0, min(msg.length(), MAX_MSG_LENGTH));
    }
}