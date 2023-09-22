/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.kafkastreams;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.record.TimestampType;

// This is necessary because SourceNodeRecordDeserializer drops the headers.  :-(
public class KafkaStreamsSourceNodeRecordDeserializerInstrumentation
    implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.kafka.streams.processor.internals.SourceNodeRecordDeserializer");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("deserialize"))
            .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerRecord")))
            .and(returns(named("org.apache.kafka.clients.consumer.ConsumerRecord"))),
        KafkaStreamsSourceNodeRecordDeserializerInstrumentation.class.getName()
            + "$SaveHeadersAdvice");
  }

  @SuppressWarnings("unused")
  public static class SaveHeadersAdvice {

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void saveHeaders(
        @Advice.Argument(0) ConsumerRecord<?, ?> incoming,
        @Advice.Return(readOnly = false) ConsumerRecord<?, ?> result) {
      result =
          new ConsumerRecord<>(
              result.topic(),
              result.partition(),
              result.offset(),
              result.timestamp(),
              TimestampType.CREATE_TIME,
              result.checksum(),
              result.serializedKeySize(),
              result.serializedValueSize(),
              result.key(),
              result.value(),
              incoming.headers());
    }
  }
}
