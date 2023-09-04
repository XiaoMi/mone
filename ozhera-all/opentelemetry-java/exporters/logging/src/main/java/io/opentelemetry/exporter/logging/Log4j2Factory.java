package io.opentelemetry.exporter.logging;

import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.sdk.common.EnvOrJvmProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;

@SuppressWarnings({"unused","rawtypes","SystemOut","unchecked","PrivateConstructorForUtilityClass"})
public class Log4j2Factory {

  public static final String IS_ASYNC_PROPERTY_NAME = EnvOrJvmProperties.JVM_OTEL_EXPORTER_LOG_ISASYNC.getKey();
  public static final String LOG_INTERVAL_PROPERTY_NAME = EnvOrJvmProperties.JVM_OTEL_EXPORTER_LOG_INTERVAL.getKey();
  public static final String LOG_DELETE_AGE_PROPERTY_NAME = EnvOrJvmProperties.JVM_OTEL_EXPORTER_LOG_DELETE_AGE.getKey();

  public static Logger getLogger() {
    Configuration config = createConfiguration("TraceConfiguration");
    LoggerContext ctx = new LoggerContext("TraceLogContext");
    ctx.setConfiguration(config);
    return ctx.getLogger("TraceLogger");
  }


  static Configuration createConfiguration(final String name) {
    String interval = StringUtils.isNullOrEmpty(System.getProperty(LOG_INTERVAL_PROPERTY_NAME)) ? "30" : System.getProperty(LOG_INTERVAL_PROPERTY_NAME);
    String deleteAge = StringUtils.isNullOrEmpty(System.getProperty(LOG_DELETE_AGE_PROPERTY_NAME)) ? "PT2H" : System.getProperty(LOG_DELETE_AGE_PROPERTY_NAME);
    String logPath = LogFileNameUtil.getLogPathFile();
    ConfigurationBuilder<BuiltConfiguration> builder = new DefaultConfigurationBuilder();
    builder.setConfigurationName(name);
    builder.setStatusLevel(Level.ERROR);
    builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
        addAttribute("level", Level.INFO));
    LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
        .addAttribute("pattern", "%d ||| %msg%n");
    ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
        .addComponent(builder.newComponent("TimeBasedTriggeringPolicy").addAttribute("interval",interval));
    ComponentBuilder defaultRolloverStrategy = builder.newComponent("DefaultRolloverStrategy")
        .addAttribute("max", 5)
        .addComponent(builder.newComponent("Delete")
            .addAttribute("basePath",LogFileNameUtil.getLogPath())
            .addComponent(builder.newComponent("IfLastModified")
                .addAttribute("age",deleteAge)));
    AppenderComponentBuilder appenderBuilder = builder.newAppender("rolling", "RollingFile")
        .addAttribute("fileName", logPath)
        .addAttribute("filePattern", logPath+"-%d{yyyy-MM-dd-HH-mm}");
    String property = System.getProperty(IS_ASYNC_PROPERTY_NAME);
    System.out.println("log4j2 isAsync : " + property);
    if ("true".equals(property)) {
      appenderBuilder.addAttribute("immediateFlush", false);
    }
    appenderBuilder.add(layoutBuilder)
        .addComponent(triggeringPolicy)
        .addComponent(defaultRolloverStrategy);
    builder.add(appenderBuilder);

    if (!"true".equals(property)) {
      builder.add(builder.newRootLogger(Level.INFO)
          .add(builder.newAppenderRef("rolling")));
      builder.add(builder.newLogger("TraceLogger", Level.INFO)
          .addComponent(builder.newAppenderRef("rolling"))
          .addAttribute( "additivity", false ));
    } else {
      // 异步rootlogger
      builder.add(builder.newAsyncRootLogger(Level.INFO)
          .add(builder.newAppenderRef("rolling")));
      builder.add(builder.newAsyncLogger("TraceLogger", Level.INFO)
          .addComponent(builder.newAppenderRef("rolling"))
          .addAttribute( "additivity", false ));
    }
    return builder.build();
  }
}
