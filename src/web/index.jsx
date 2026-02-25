import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import packageJson from '../../package.json';
import SMSEagleNotificationForm from './SMSEagleNotificationForm';
import SMSEagleNotificationSummary from './SMSEagleNotificationSummary';

PluginStore.register(new PluginManifest(packageJson, {
  eventNotificationTypes: [
    {
      type: 'smseagle-notification-v2',
      displayName: 'SMSEagle Notification',
      formComponent: SMSEagleNotificationForm,
      summaryComponent: SMSEagleNotificationSummary,
      defaultConfig: SMSEagleNotificationForm.defaultConfig,
    },
  ],
}));