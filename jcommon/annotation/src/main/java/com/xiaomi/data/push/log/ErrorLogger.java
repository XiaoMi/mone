package com.xiaomi.data.push.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author goodjava@qq.com
 */
public class ErrorLogger {

    private static final String logName = "error.log";

    public Logger logger;

    private ErrorLogger() {
        String logPath = System.getProperty("user.home") + File.separator + "log" + File.separator + "error" + File.separator + logName;

        LoggerContext loggerContext = new LoggerContext();
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setName("ErrorLogAppender");
        rollingFileAppender.setFile(logPath);

        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy<>();
        String logPathHistory = logPath + ".%d";
        rollingPolicy.setFileNamePattern(logPathHistory);
        rollingPolicy.setMaxHistory(10);
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.start();
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%msg%n");
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(loggerContext);
        encoder.start();
        rollingFileAppender.setEncoder(encoder);

        rollingFileAppender.start();

        Logger rootLogger = loggerContext.getLogger("error_log");
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(rollingFileAppender);
        logger = rootLogger;
    }


    public static ErrorLogger ins() {
        return LazyHolder.ins;
    }

    private static class LazyHolder {
        private static ErrorLogger ins = new ErrorLogger();
    }


}
