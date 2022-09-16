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

import org.graylog2.alarmcallbacks.smseagle.SMSEagleAlarmCallbackMetadata;
import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class SMSEagleAlarmCallbackPlugin implements Plugin {
    @Override
    public Collection<PluginModule> modules() {
        return Collections.<PluginModule>singleton(new SMSEagleAlarmCallbackModule());
    }

    @Override
    public PluginMetaData metadata() {
        return new SMSEagleAlarmCallbackMetadata();
    }
}
