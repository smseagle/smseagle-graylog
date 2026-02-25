SMSEagle SMS Plugin for Graylog
=============================


Notification plugin for integrating the [SMSEagle](https://www.smseagle.eu) into [Graylog](https://www.graylog.org/).

**Required Graylog version:** 7.0.0 and later

## Installation

[Download the plugin](https://bitbucket.org/proximus/smseagle-graylog/downloads/)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugin/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.


Detailed setup instructions can be found at [SMSEagle Graylog SMS integration page](https://www.smseagle.eu/integration-plugins/graylog-sms-integration/)

## Build

This project is using Maven 3 and requires Java 17 or higher.

**Requirements:**
- Local `graylog2-server` source repository cloned in the parent directory (provides `web-parent` POM needed for frontend build)
- Node.js and Yarn (installed automatically by Maven during the build)

You can build the plugin (JAR) with `mvn package`.

## Plugin Release

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

## Supported Message Types

- SMS
- Flash SMS
- Signal
- WhatsApp
- Ring call
- Text-To-Speech
- Text-To-Speech Advanced
- ElevenLabs
- ElevenLabs (Direct)

For more information about message types and their parameters, please check the [SMSEagle APIv2 documentation](https://www.smseagle.eu/docs/apiv2/).
