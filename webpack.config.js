const path = require('path');
const { PluginWebpackConfig } = require('graylog-web-plugin');
const { loadBuildConfig } = require('graylog-web-plugin');

module.exports = new PluginWebpackConfig(
  __dirname,
  'org.graylog.plugins.smseagle.SMSEagleNotificationPlugin',
  loadBuildConfig(path.resolve(__dirname, './build.config')),
  {}
);