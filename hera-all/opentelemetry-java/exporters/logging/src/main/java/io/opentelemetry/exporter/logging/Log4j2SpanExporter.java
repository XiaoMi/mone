/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.exporter.logging;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.apache.logging.log4j.Logger;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "FieldCanBeFinal","FutureReturnValueIgnored","SystemOut","CatchAndPrintStackTrace"})
public final class Log4j2SpanExporter implements SpanExporter {

  private Logger log;

  // 生成日志文件的间隙，单位：m
  private static final int GENERATE_LOG_GAP = 20;
  private static final String[] traceIDChars = new String[]{"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  private static Random r = new Random();

  private static String ipv4Env = System.getenv("host.ip");

  public Log4j2SpanExporter() {
    log = Log4j2Factory.getLogger();
    // 自动生产trace log，防止长时间无数据之后，日志采集行号对应不上，从而无法采集
    scheduledSpanDateForLog();
  }

  public Log4j2SpanExporter(String logPath, String isAsync, String logInterval,
      String logDeleteAge) {
    System.setProperty(LogFileNameUtil.LOGPATH_PROPERTY_NAME, logPath);
    System.setProperty(Log4j2Factory.IS_ASYNC_PROPERTY_NAME, isAsync);
    System.setProperty(Log4j2Factory.LOG_INTERVAL_PROPERTY_NAME, logInterval);
    System.setProperty(Log4j2Factory.LOG_DELETE_AGE_PROPERTY_NAME, logDeleteAge);
    log = Log4j2Factory.getLogger();
  }

  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    for (SpanData span : spans) {
      String spanMessage = SpanToLogUtil.convert(span);
      log.info(spanMessage);
    }
    return CompletableResultCode.ofSuccess();
  }

  /**
   * Flushes the data.
   *
   * @return the result of the operation
   */
  @Override
  public CompletableResultCode flush() {
    CompletableResultCode resultCode = new CompletableResultCode();
    return resultCode.succeed();
  }

  @Override
  public CompletableResultCode shutdown() {
    return flush();
  }

  private void scheduledSpanDateForLog() {
    new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
        () -> {
          generateSpanDateForLog();
        },
        0,
        GENERATE_LOG_GAP,
        TimeUnit.MINUTES);
  }

  private void generateSpanDateForLog() {
    try {
      long currNano = System.currentTimeMillis() * 1000 * 1000;
      String autoGenerator = currNano + " ### 123 ### " + ipv4Env
          + " ### auto-generator ### dbDriver ### UNSET ### "+buildTraceId()+" ### "+buildSpanId()+" ### [] ### [] ### {\"tags\":[{\"key\":\"service.name\",\"type\":\"string\",\"value\":\"auto-generator\"}]} ### [] ### ";
      log.info(autoGenerator);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static String buildTraceId(){
    // 构造随机的traceID
    StringBuilder stringBuilder = new StringBuilder();
    for (int j = 0; j < 32; j++) {
      int i = r.nextInt(traceIDChars.length);
      stringBuilder.append(traceIDChars[i]);
    }
    return stringBuilder.toString();
  }

  private static String buildSpanId(){
    StringBuilder stringBuilder = new StringBuilder();
    for (int j = 0; j < 16; j++) {
      int i = r.nextInt(traceIDChars.length);
      stringBuilder.append(traceIDChars[i]);
    }
    return stringBuilder.toString();
  }
}
