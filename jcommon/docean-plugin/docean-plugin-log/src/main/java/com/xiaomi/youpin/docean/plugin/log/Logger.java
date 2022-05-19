package com.xiaomi.youpin.docean.plugin.log;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
@Data
public class Logger {

    private String name;

    private boolean showLineNumber = false;

    private Log log;

    private int logLevel;

    enum LogLevel {
        //0
        debug,
        //1
        info,
        //2
        error
    }

    public Logger(Class clazz) {
        this.name = clazz.getName();
        Config config = Ioc.ins().getBean(Config.class);
        showLineNumber = Boolean.valueOf(config.get("log_line_number", "false"));
        logLevel = LogLevel.valueOf(config.get("log_level", "info")).ordinal();
        log = Ioc.ins().getBean(Log.class);
    }

    public void info(String message) {
        if (this.logLevel <= LogLevel.info.ordinal()) {
            Pair<String, Integer> pair = getMethodNameAndLineNumber();
            log.log("info", this.name, message, pair.getKey(), pair.getValue());
        }
    }

    public void debug(String message) {
        if (this.logLevel <= LogLevel.debug.ordinal()) {
            Pair<String, Integer> pair = getMethodNameAndLineNumber();
            log.log("debug", this.name, message, pair.getKey(), pair.getValue());
        }
    }

    public void error(String message) {
        if (this.logLevel <= LogLevel.error.ordinal()) {
            Pair<String, Integer> pair = getMethodNameAndLineNumber();
            log.log("error", this.name, message, pair.getKey(), pair.getValue());
        }
    }


    private Pair<String, Integer> getMethodNameAndLineNumber() {
        if (showLineNumber) {
            Optional<StackTraceElement> optional = Arrays.stream(new Throwable().getStackTrace()).filter(it -> it.getClassName().equals(this.name)).findAny();
            if (optional.isPresent()) {
                return Pair.of(optional.get().getMethodName(), optional.get().getLineNumber());
            }
        }
        return Pair.of("", 0);
    }


}
