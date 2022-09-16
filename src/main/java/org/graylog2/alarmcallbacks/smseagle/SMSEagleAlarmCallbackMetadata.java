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

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class SMSEagleAlarmCallbackMetadata implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return SMSEagleAlarmCallback.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "SMSEagle Alarmcallback Plugin";
    }

    @Override
    public String getAuthor() {
        return "SMSEagle Team <support@smseagle.eu>";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.smseagle.eu/");
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 1);
    }

    @Override
    public String getDescription() {
        return "Alarm callback plugin that sends all stream alerts as SMS to a defined phone number.";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(2, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
