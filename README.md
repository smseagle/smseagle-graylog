SMSEagle SMS Plugin for Graylog
=============================


Alert/notification callback plugin for integrating the [SMSEagle ](https://www.smseagle.eu) into [Graylog](https://www.graylog.org/).

**Required Graylog version:** 2.0.0 and later

## Installation

[Download the plugin](https://bitbucket.org/proximus/smseagle-graylog/downloads/)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.


Detailed setup instructions can be found at [SMSEagle Graylog SMS integration page](https://www.smseagle.eu/integration-plugins/graylog-sms-integration/)

## Build

This project is using Maven 3 and requires Java 8 or higher.

You can build the plugin (JAR) with `mvn package`.

DEB and RPM packages can be built with `mvn jdeb:jdeb` and `mvn rpm:rpm` respectively.

## Plugin Release

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. Travis CI will build the release artifacts and upload to GitHub automatically.
