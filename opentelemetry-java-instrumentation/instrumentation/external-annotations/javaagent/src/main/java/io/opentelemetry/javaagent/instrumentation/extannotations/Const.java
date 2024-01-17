package io.opentelemetry.javaagent.instrumentation.extannotations;

import io.opentelemetry.instrumentation.api.config.Config;

public class Const {

    public static final int EVENT_TIME_THRESHOLD = Config.get().getInt("otel.trace.timeevent.threshold",1000);

}
