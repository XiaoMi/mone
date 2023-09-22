/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CONSUMER
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderTrace

import io.opentelemetry.instrumentation.test.InstrumentationSpecification
import io.opentelemetry.sdk.trace.data.SpanData
import java.util.concurrent.Executors
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.interceptor.GlobalChannelInterceptorWrapper
import org.springframework.messaging.Message
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.support.ExecutorSubscribableChannel
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
abstract class AbstractSpringIntegrationTracingTest extends InstrumentationSpecification {

  abstract Class<?> additionalContextClass()

  @Shared
  ConfigurableApplicationContext applicationContext

  def setupSpec() {
    def contextClasses = [MessageChannelsConfig]
    if (additionalContextClass() != null) {
      contextClasses += additionalContextClass()
    }

    def app = new SpringApplication(contextClasses as Class<?>[])
    app.setDefaultProperties([
      "spring.main.web-application-type": "none"
    ])
    applicationContext = app.run()
  }

  def cleanupSpec() {
    applicationContext?.close()
  }

  def "should propagate context (#channelName)"() {
    given:
    def channel = applicationContext.getBean(channelName, SubscribableChannel)

    def messageHandler = new CapturingMessageHandler()
    channel.subscribe(messageHandler)

    when:
    runUnderTrace("parent") {
      channel.send(MessageBuilder.withPayload("test")
        .build())
    }

    then:
    def capturedMessage = messageHandler.join()

    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          name "parent"
        }
        span(1) {
          name interceptorSpanName
          childOf span(0)
          kind CONSUMER
        }
        span(2) {
          name "handler"
          childOf span(1)
        }

        def interceptorSpan = span(1)
        verifyCorrectSpanWasPropagated(capturedMessage, interceptorSpan)
      }
    }

    cleanup:
    channel.unsubscribe(messageHandler)

    where:
    channelName       | interceptorSpanName
    "directChannel"   | "application.directChannel process"
    "executorChannel" | "executorChannel process"
  }

  def "should handle multiple message channels in a chain"() {
    given:
    def channel1 = applicationContext.getBean("linkedChannel1", SubscribableChannel)
    def channel2 = applicationContext.getBean("linkedChannel2", SubscribableChannel)

    def messageHandler = new CapturingMessageHandler()
    channel2.subscribe(messageHandler)

    when:
    runUnderTrace("parent") {
      channel1.send(MessageBuilder.withPayload("test")
        .build())
    }

    then:
    def capturedMessage = messageHandler.join()

    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          name "parent"
        }
        span(1) {
          name "application.linkedChannel1 process"
          childOf span(0)
          kind CONSUMER
        }
        span(2) {
          name "handler"
          childOf span(1)
        }

        def lastChannelSpan = span(1)
        verifyCorrectSpanWasPropagated(capturedMessage, lastChannelSpan)
      }
    }

    cleanup:
    channel2.unsubscribe(messageHandler)
  }

  static void verifyCorrectSpanWasPropagated(Message<?> capturedMessage, SpanData parentSpan) {
    def propagatedSpan = capturedMessage.headers.get("traceparent") as String
    assert propagatedSpan.contains(parentSpan.traceId), "wrong trace id"
    assert propagatedSpan.contains(parentSpan.spanId), "wrong span id"
  }

  @SpringBootConfiguration
  @EnableAutoConfiguration
  static class MessageChannelsConfig {
    @Bean
    SubscribableChannel directChannel() {
      new DirectChannel()
    }

    @Bean
    SubscribableChannel executorChannel(GlobalChannelInterceptorWrapper otelInterceptor) {
      def channel = new ExecutorSubscribableChannel(Executors.newSingleThreadExecutor())
      if (!Boolean.getBoolean("testLatestDeps")) {
        // spring does not inject the interceptor in 4.1 because ExecutorSubscribableChannel isn't ChannelInterceptorAware
        // in later versions spring injects the global interceptor into InterceptableChannel (which ExecutorSubscribableChannel is)
        channel.addInterceptor(otelInterceptor.channelInterceptor)
      }
      channel
    }

    @Bean
    SubscribableChannel linkedChannel1() {
      new DirectChannel()
    }

    @Bean
    SubscribableChannel linkedChannel2() {
      new DirectChannel()
    }

    @EventListener(ApplicationReadyEvent)
    void initialize() {
      linkedChannel1().subscribe { message ->
        linkedChannel2().send(message)
      }
    }
  }
}
