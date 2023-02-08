/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.jcommon.log;

import ch.qos.logback.classic.pattern.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.google.gson.Gson;
import com.xiaomi.youpin.dubbo.filter.TraceIdUtils;
import lombok.Setter;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author goodjava@qq.com
 * @author 海纳百川->丁海洋
 */
public class YouPinLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Setter
    private String appName;

    @Setter
    private String group;

    /**
     * 下游需要提供
     */
    @Setter
    private String talosAccessKey;

    /**
     * 下游需要提供
     */
    @Setter
    private String talosAccessSecret;

    @Setter
    private String talosTopicName = "youpin_common";

    @Setter
    private String talosSendpoint = "http://127.0.0.1";

    @Setter
    private String whitelist = "";

    @Setter
    private boolean needCatLog = false;

    /**
     * 是否开启disruptor
     */
    @Setter
    private boolean disruptor = false;

    @Setter
    private String mdcKey = "";

    private static final AtomicBoolean isInit = new AtomicBoolean(false);

    private LineOfCallerConverter lineOfCallerConverter = new LineOfCallerConverter();

    private ClassOfCallerConverter classOfCallerConverter = new ClassOfCallerConverter();

    private MethodOfCallerConverter methodOfCallerConverter = new MethodOfCallerConverter();

    private ThrowableProxyConverter throwableProxyConverter = new ThrowableProxyConverter();

    private DateConverter dateConverter = new DateConverter();

    private MessageConverter messageConverter = new MessageConverter();

    private TalosClient talosClient;

    private Set<String> whitelistSet = new HashSet<>();

    private String ip = "";

    private LogDisruptor logDisruptor;


    @Override
    public void start() {
        super.start();
        ip = Optional.ofNullable(System.getenv("host.ip")).orElse(TraceIdUtils.ins().ip());
        TraceIdUtils.ins().pid();
        new Thread(() -> initYouPinLogAppender()).start();
    }

    private void initYouPinLogAppender() {
        System.out.println(new LogVersion());
        dateConverter.start();
        throwableProxyConverter.start();
        talosClient = new TalosClient(talosAccessKey, talosAccessSecret, talosTopicName, talosSendpoint);
        talosClient.init();

        if (!whitelist.equals("")) {
            String[] array = whitelist.split(",");
            Arrays.stream(array).forEach(it -> whitelistSet.add(it));
        }
        if (this.disruptor) {
            this.logDisruptor = new LogDisruptor();
            this.logDisruptor.start(lr -> {
                recordLog(lr);
            });
        }

        isInit.compareAndSet(false, true);
    }

    @Override
    public void stop() {
        super.stop();
        if (this.talosClient != null) {
            this.talosClient.shutdown();
        }
        this.talosClient = null;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (isInit.get() == false) {
            return;
        }

        String className = classOfCallerConverter.convert(eventObject);

        //这里的一定会过滤掉(不然会死锁...思考下就明白为啥了)
        if (className.contains("com.xiaomi.infra") || eventObject.getLoggerName().contains("com.xiaomi.infra")) {
            return;
        }
        //根据白名单打印日志
        if (!whitelist.equals("")) {
            boolean match = whitelistSet.stream().anyMatch(it -> className.contains(it));
            if (!match) {
                return;
            }
        }

        String threadName = eventObject.getThreadName();
        String msg = (!StringUtils.isEmpty(mdcKey) && !StringUtils.isEmpty(MDC.get(mdcKey)))
                ? MDC.get(mdcKey) + " " + messageConverter.convert(eventObject)
                : messageConverter.convert(eventObject);
        String level = eventObject.getLevel().levelStr;
        String traceId = LogbackPatternConverter.traceId(eventObject);
        String time = dateConverter.convert(eventObject);
        String line = lineOfCallerConverter.convert(eventObject);
        String methodName = methodOfCallerConverter.convert(eventObject);
        String pid = TraceIdUtils.ins().pid();

        if (disruptor) {
            logDisruptor.publishEvent(lr -> {
                initLogRecord(eventObject, className, threadName, msg, level, traceId, time, line, methodName, pid, lr);
            });
            return;
        }

        LogRecord log = new LogRecord();
        initLogRecord(eventObject, className, threadName, msg, level, traceId, time, line, methodName, pid, log);

        recordLog(log);
    }

    private void recordLog(LogRecord log) {
        talosClient.sendMsg(new Gson().toJson(log));
        catLog(log);
    }

    private void initLogRecord(ILoggingEvent eventObject, String className, String threadName, String msg, String level, String traceId, String time, String line, String methodName, String pid, LogRecord log) {
        log.setClassName(className);
        log.setThreadName(threadName);
        log.setMessage(msg);
        log.setLevel(level);
        log.setTraceId(traceId);
        log.setTime(time);
        log.setLine(line);
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setIp(this.ip);
        log.setPid(pid);
        log.setAppName(appName);
        log.setGroup(group);
        log.setTimestamp(eventObject.getTimeStamp());
        setProps(eventObject, log);
        setException(eventObject, log);
    }


    private void catLog(LogRecord log) {
    }

    protected void setException(ILoggingEvent eventObject, LogRecord log) {
        log.setErrorInfo(throwableProxyConverter.convert(eventObject));
    }

    protected void setProps(ILoggingEvent eventObject, LogRecord log) {
        if (eventObject.getArgumentArray() != null
                && eventObject.getArgumentArray().length > 0
                && eventObject.getArgumentArray()[eventObject.getArgumentArray().length - 1] != null
                && eventObject.getArgumentArray()[eventObject.getArgumentArray().length - 1].getClass().equals(LogContext.class)) {
            LogContext context = (LogContext) eventObject.getArgumentArray()[eventObject.getArgumentArray().length - 1];
            log.setTag(context.getTag());
            log.setParams(context.getParam());
            log.setResult(context.getResult());
            log.setCode(context.getCode());
            log.setExtra(context.getExtra());
            log.setCostTime(context.getCostTime());
            log.setErrorSource(context.getErrorSource());

            if (context.getTimestamp() > 0) {
                log.setTimestamp(context.getTimestamp());
            }

            if (notEmpty(context.getTraceId())) {
                log.setTraceId(context.getTraceId());
            }

            if (notEmpty(context.getLevel())) {
                log.setLevel(context.getLevel());
            }

            if (notEmpty(context.getAppName())) {
                log.setAppName(context.getAppName());
            }

            if (notEmpty(context.getMethodName())) {
                log.setMethodName(context.getMethodName());
            }
        }
    }

    private boolean notEmpty(String str) {
        return null != str && !str.equals("");
    }


}
