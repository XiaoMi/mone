package io.opentelemetry.exporter.logging;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.internal.ThrottlingLogger;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "PrivateConstructorForUtilityClass"})
public class SpanToLogUtil {

  private static final ThrottlingLogger logger =
      new ThrottlingLogger(Logger.getLogger(SpanToLogUtil.class.getName()));

  private static final String split = " ### ";
  private static String hostName = "";
  private static String ipv4Env = System.getenv("host.ip");
  private static String env = System.getenv("MIONE_PROJECT_ENV_NAME");;
  private static String envId = System.getenv("MIONE_PROJECT_ENV_ID");;
  private static final String FUNCTION_MODULE_KEY = "service.function.module";
  private static final String FUNCTION_NAME_KEY = "service.function.name";
  private static final String FUNCTION_ID_KEY = "service.function.id";
  private static final int STRING_MAX_LENGTH = 500;
  private static final String KEY_INSTRUMENTATION_LIBRARY_NAME = "otel.library.name";
  private static final String DEFAULT_ENV = "default_env";
  static final String KEY_HERACONTEXT = "span.hera_context";

  static {
    if (StringUtils.isNullOrEmpty(env)) {
      env = DEFAULT_ENV;
    }
    if(envId == null){
      envId = "";
    }
    try {
      if (StringUtils.isNullOrEmpty(ipv4Env)) {
        ipv4Env = InetAddress.getLocalHost().getHostAddress();
      }
      hostName = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      logger.log(Level.WARNING, "fail to init SpanToLogbackUtil", e);
    }
  }

  /**
   * 日志格式：
   * 0：startTime|
   * 1：duration|
   * 2：ip|
   * 3：appName|
   * 4：spanName(operationName)|
   * 5：statusCode|
   * 6：traceId|
   * 7：spanId|
   * 8：tags[]:
   * 9：evnets(logs)[]|
   * 10：resource(process)[]|
   * 11：references[]
   */
  public static String convert(SpanData spanData) {
    StringBuilder sb = new StringBuilder();
    Resource resource = spanData.getResource();
    String applicationName = "";
    if(resource != null){
      applicationName = resource.getAttributes().get(ResourceAttributes.SERVICE_NAME);
      if (applicationName == null || applicationName.isEmpty()) {
        applicationName = Resource.getDefault().getAttributes().get(ResourceAttributes.SERVICE_NAME);
      }
    }
    sb.append(spanData.getStartEpochNanos()).append(split)
        .append(spanData.getEndEpochNanos() - spanData.getStartEpochNanos()).append(split)
        .append(notNull(ipv4Env)).append(split)
        .append(notNull(applicationName)).append(split)
        .append(notNull(spanData.getName())).append(split)
        .append(notNull(spanData.getStatus().getStatusCode().name())).append(split)
        .append(notNull(spanData.getTraceId())).append(split)
        .append(notNull(spanData.getSpanId())).append(split);
    // tags
    Attributes tagsAttributes = spanData.getAttributes();
    sb.append("[");
    if (tagsAttributes != null || tagsAttributes.size() > 0) {
      tagsAttributes.forEach((key, value) -> {
        if(filterTagsByKey(key.toString())) {
          sb.append("{");
          sb.append("\"").append("key").append("\"").append(":").append("\"").append(key)
              .append("\"")
              .append(",")
              .append("\"").append("type").append("\"").append(":").append("\"")
              .append(getType(key))
              .append("\"").append(",")
              .append("\"").append("value").append("\"").append(":").append("\"").append(subString(encodeLineBreak(value)))
              .append("\"")
              .append("}").append(",");
        }
      });
    }
    if (spanData.getKind() != SpanKind.INTERNAL) {
      sb.append("{");
      sb.append("\"").append("key").append("\"").append(":").append("\"").append("span.kind").append("\"")
          .append(",")
          .append("\"").append("type").append("\"").append(":").append("\"").append("string")
          .append("\"").append(",")
          .append("\"").append("value").append("\"").append(":").append("\"").append(spanData.getKind().name().toLowerCase(Locale.ROOT))
          .append("\"")
          .append("}").append(",");
    }
    if (spanData.getStatus().getStatusCode() == StatusCode.ERROR) {
      sb.append("{");
      sb.append("\"").append("key").append("\"").append(":").append("\"").append("error").append("\"")
          .append(",")
          .append("\"").append("type").append("\"").append(":").append("\"").append("bool")
          .append("\"").append(",")
          .append("\"").append("value").append("\"").append(":").append("\"").append(true).append("\"")
          .append("}").append(",");
    }
    // heraContext
    SpanContext spanContext = spanData.getSpanContext();
    String heraContext = spanContext.getHeraContext() == null ? "" : spanContext.getHeraContext().get("heracontext");
    if(heraContext == null){
      heraContext = "";
    }
    sb.append("{");
    sb.append("\"").append("key").append("\"").append(":").append("\"").append(KEY_HERACONTEXT).append("\"")
        .append(",")
        .append("\"").append("type").append("\"").append(":").append("\"").append("string")
        .append("\"").append(",")
        .append("\"").append("value").append("\"").append(":").append("\"").append(heraContext).append("\"")
        .append("}").append(",");
    sb.append("{");
    sb.append("\"").append("key").append("\"").append(":").append("\"").append(KEY_INSTRUMENTATION_LIBRARY_NAME).append("\"")
        .append(",")
        .append("\"").append("type").append("\"").append(":").append("\"").append("string")
        .append("\"").append(",")
        .append("\"").append("value").append("\"").append(":").append("\"").append(spanData.getInstrumentationLibraryInfo().getName()).append("\"")
        .append("}").append(",");
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    sb.append(split);
    // event(logs)
    sb.append("[");
    List<EventData> events = spanData.getEvents();
    if (events != null && events.size() > 0) {
      for (EventData ed : events) {
        if (ed != null) {
          sb.append("{")
              .append("\"").append("timestamp").append("\"")
              .append(":")
              .append(ed.getEpochNanos() / 1000).append(",")
              .append("\"").append("fields").append("\"").append(":")
              .append("[")
              .append("{");
          sb.append("\"").append("key").append("\"").append(":").append("\"").append("event")
              .append("\"").append(",")
              .append("\"").append("type").append("\"").append(":").append("\"").append("string")
              .append("\"").append(",")
              .append("\"").append("value").append("\"").append(":").append("\"")
              .append(ed.getName()).append("\"").append("}").append(",");
          Attributes eventAttributes = ed.getAttributes();
          if (eventAttributes != null || eventAttributes.size() > 0) {
            eventAttributes.forEach((key, value) -> {
              sb.append("{");
              sb.append("\"").append("key").append("\"").append(":").append("\"").append(key)
                  .append("\"").append(",")
                  .append("\"").append("type").append("\"").append(":").append("\"")
                  .append(getType(key)).append("\"").append(",")
                  .append("\"").append("value").append("\"").append(":").append("\"").append(encodeLineBreak(value))
                  .append("\"");
              sb.append("}").append(",");
            });
          }
          sb.deleteCharAt(sb.length() - 1);
          sb.append("]").append("}").append(",");
        }
      }
      if(sb.toString().endsWith(",")){
        sb.deleteCharAt(sb.length() - 1);
      }
    }
    sb.append("]");
    sb.append(split);
    // resource(process)
    sb.append("{").append("\"").append("serviceName").append("\"").append(":")
        .append("\"").append(applicationName).append("\"").append(",")
        .append("\"").append("tags").append("\"").append(":").append("[");
    sb.append("{")
        .append("\"").append("key").append("\"").append(":").append("\"").append("ip")
        .append("\"").append(",")
        .append("\"").append("type").append("\"").append(":").append("\"")
        .append("string").append("\"").append(",")
        .append("\"").append("value").append("\"").append(":").append("\"").append(ipv4Env)
        .append("\"");
    sb.append("}").append(",");
    sb.append("{")
        .append("\"").append("key").append("\"").append(":").append("\"").append("service.env")
        .append("\"").append(",")
        .append("\"").append("type").append("\"").append(":").append("\"")
        .append("string").append("\"").append(",")
        .append("\"").append("value").append("\"").append(":").append("\"").append(env)
        .append("\"");
    sb.append("}").append(",");
    sb.append("{")
        .append("\"").append("key").append("\"").append(":").append("\"").append("service.env.id")
        .append("\"").append(",")
        .append("\"").append("type").append("\"").append(":").append("\"")
        .append("string").append("\"").append(",")
        .append("\"").append("value").append("\"").append(":").append("\"").append(envId)
        .append("\"");
    sb.append("}").append(",");
    if (resource != null) {
      Attributes resouceAttributes = resource.getAttributes();
      if (resouceAttributes != null && resouceAttributes.size() > 0) {
        resouceAttributes.forEach((key, value) -> {
          if(filterProcessByKey(key.toString())) {
            sb.append("{")
                .append("\"").append("key").append("\"").append(":").append("\"").append(key)
                .append("\"").append(",")
                .append("\"").append("type").append("\"").append(":").append("\"")
                .append(getType(key)).append("\"").append(",")
                .append("\"").append("value").append("\"").append(":").append("\"").append(value)
                .append("\"");
            sb.append("}").append(",");
          }
        });
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]").append("}");
    sb.append(split);
    // reference
    sb.append("[");
    List<LinkData> links = spanData.getLinks();
    if(links != null && links.size() > 0) {
      for (LinkData linkData : links) {
        sb.append("{")
            .append("\"").append("traceID").append("\"").append(":").append("\"")
            .append(linkData.getSpanContext().getTraceId()).append("\"").append(",")
            .append("\"").append("spanID").append("\"").append(":").append("\"")
            .append(linkData.getSpanContext().getSpanId()).append("\"").append(",")
            .append("\"refType\":\"CHILD_OF\"").append("},");
      }
    }
    SpanContext parentSpanContext = spanData.getParentSpanContext();
    if (parentSpanContext.isValid()) {
      sb.append("{")
          .append("\"").append("traceID").append("\"").append(":").append("\"")
          .append(parentSpanContext.getTraceId()).append("\"").append(",")
          .append("\"").append("spanID").append("\"").append(":").append("\"")
          .append(parentSpanContext.getSpanId()).append("\"").append(",")
          .append("\"refType\":\"CHILD_OF\"").append("}");
    }
    sb.append("]");
    sb.append(split);
    return sb.toString();
  }

  public static String notNull(String string) {
    return string == null ? "" : string;
  }

  @SuppressWarnings("UnnecessaryDefaultInEnumSwitch")
  public static String getType(AttributeKey<?> key) {
    switch (key.getType()) {
      case STRING:
        return "string";
      case LONG:
        return "int64";
      case BOOLEAN:
        return "bool";
      case DOUBLE:
        return "float64";
      case STRING_ARRAY:
      case LONG_ARRAY:
      case BOOLEAN_ARRAY:
      case DOUBLE_ARRAY:
        return "string";
      default:
        return "string";
    }
  }

  private static String encodeLineBreak(Object value){
    String s = String.valueOf(value);
    if(!StringUtils.isNullOrEmpty(s)){
      return s.replaceAll("\\n","##n").replaceAll("\\r","##r").replaceAll("\\t","##t").replaceAll("\\tat","##tat").replaceAll("\\\\\"","##r'").replaceAll("\"","##'").replaceAll("\\\\","\\\\\\\\");
    }
    return s;
  }

  private static String subString(String value){
    if(value != null && value.length() > STRING_MAX_LENGTH){
      return value.substring(0, STRING_MAX_LENGTH) + "......";
    }
    return value;
  }

  private static boolean filterProcessByKey(String key){
    if(StringUtils.isNullOrEmpty(key)){
      return false;
    }
    if("telemetry.sdk.name".equals(key)){
      return false;
    }
    if("telemetry.sdk.language".equals(key)){
      return false;
    }
    if("telemetry.sdk.version".equals(key)){
      return false;
    }
    if("telemetry.auto.version".equals(key)){
      return false;
    }
    if("ip".equals(key)){
      return false;
    }
    return true;
  }

  private static boolean filterTagsByKey(String key){
    if(StringUtils.isNullOrEmpty(key)){
      return false;
    }
    if("thread.id".equals(key)){
      return false;
    }
    if("thread.name".equals(key)){
      return false;
    }
    return true;
  }
}
