##################
Setting up Logging
##################

This guide documents how to configure logging in Drillflow.

************
Introduction
************

Drillflow uses logback-spring.xml to configure logging. The default logging configuration can be seen here on GitHub.
`here on GitHub <https://github.com/hashmapinc/Drillflow/blob/master/df-server/src/main/resources/logback-spring.xml/>`_.

A tutorial on how to modify this file can be found here: `logback.xml Example <https://www.mkyong.com/logging/logback-xml-example/>`_

This default configuration has 2 appenders, a console appender and a file appender. There are several parts of the file
that have been made configurable via environment variables to facilitate running in a container.

If you would like to configure it more deeply (as in adding additional appenders, or changing the logging functionality
overall, you will want to skip to externalizing the configuration.

***********************
Configurable Properties
***********************

In the default logback-spring.xml the following items are configurable via environment variables:

:Variable:
    LOGBACK_CONFIG_FILE
:Description:
    The path to the logback configuration file to use. This could be mounted from a volume
:Default:
    %d %p %C{1.} [%t] %m%n
:Example Environmental Switch in Docker:
    To Colorize: -e LOGBACK_CONFIG_FILE='/mnt/config/logback-spring.xml'

:Variable:
    CONSOLE_LOGGER_PATTERN
:Description:
    The pattern to use when logging to the console.
:Default:
    %d %p %C{1.} [%t] %m%n
:Example Environmental Switch in Docker:
    To Colorize: -e CONSOLE_LOGGER_PATTERN='%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable'

:Variable:
    FILE_LOGGER_PATTERN
:Description:
    The pattern to use when logging to a file.
:Default:
    %d %p %C{1.} [%t] %m%n
:Example Environmental Switch in Docker:
    Simplified output: -e CONSOLE_LOGGER_PATTERN='%d{yyyy-MM-dd HH:mm:ss} - %msg%n'

:Variable:
    FILE_LOGGER_MAX_SIZE
:Default:
    10MB
:Example Environmental Switch in Docker:
    To increase size: -e FILE_LOGGER_MAX_SIZE='20MB'

:Variable:
    LOGS
:Description:
    The root for a logs directory. It is suggested to log to a mounted volume.
:Example Environmental Switch in Docker:
    To increase size: -e FILE_LOGGER_MAX_SIZE='20MB'

Example Docker *run* Command to Set multiple properties:

``
docker run -p 7070:7070 -e VALVE_BASE_URL='https://test.com/' -e VALVE_API_KEY='secret' -e CONSOLE_LOGGER_PATTERN='%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable' hashmapinc/drillflow:latest
``

**********************
External Configuration
**********************

The configuration could be mounted externally via a docker volume that is mapped to the container internally.
This is ideal for the case when you would want to centralize a common logging configuration across many containers. You would
leverage the *LOGBACK_CONFIG_FILE* as mentioned above.