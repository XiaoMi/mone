/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package server

import static org.springframework.web.reactive.function.server.RequestPredicates.GET
import static org.springframework.web.reactive.function.server.RequestPredicates.POST
import static org.springframework.web.reactive.function.server.RouterFunctions.route

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Tracer
import java.time.Duration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@SpringBootApplication
@ComponentScan(basePackages = ["server"], excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "server.base.*"))
class SpringWebFluxTestApplication {

  private static final Tracer tracer = GlobalOpenTelemetry.getTracer("test")

  @Bean
  RouterFunction<ServerResponse> echoRouterFunction(EchoHandler echoHandler) {
    return route(POST("/echo"), new EchoHandlerFunction(echoHandler))
  }

  @Bean
  RouterFunction<ServerResponse> greetRouterFunction(GreetingHandler greetingHandler) {
    return route(GET("/greet"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.defaultGreet()
      }
    }).andRoute(GET("/greet/{name}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.customGreet(request)
      }
    }).andRoute(GET("/greet/{name}/{word}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.customGreetWithWord(request)
      }
    }).andRoute(GET("/double-greet"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.doubleGreet()
      }
    }).andRoute(GET("/greet-delayed"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.defaultGreet().delayElement(Duration.ofMillis(100))
      }
    }).andRoute(GET("/greet-failfast/{id}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        throw new IllegalStateException("bad things happen")
      }
    }).andRoute(GET("/greet-failmono/{id}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.error(new IllegalStateException("bad things happen"))
      }
    }).andRoute(GET("/greet-traced-method/{id}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.intResponse(Mono.just(tracedMethod(request.pathVariable("id").toInteger())))
      }
    }).andRoute(GET("/greet-mono-from-callable/{id}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.intResponse(Mono.fromCallable {
          return tracedMethod(request.pathVariable("id").toInteger())
        })
      }
    }).andRoute(GET("/greet-delayed-mono/{id}"), new HandlerFunction<ServerResponse>() {
      @Override
      Mono<ServerResponse> handle(ServerRequest request) {
        return greetingHandler.intResponse(Mono.just(request.pathVariable("id").toInteger()).delayElement(Duration.ofMillis(100)).map { i -> tracedMethod(i) })
      }
    })
  }

  @Component
  static class GreetingHandler {
    static final String DEFAULT_RESPONSE = "HELLO"

    Mono<ServerResponse> defaultGreet() {
      return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromObject(DEFAULT_RESPONSE))
    }

    Mono<ServerResponse> doubleGreet() {
      return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromObject(DEFAULT_RESPONSE + DEFAULT_RESPONSE))
    }

    Mono<ServerResponse> customGreet(ServerRequest request) {
      return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromObject(DEFAULT_RESPONSE + " " + request.pathVariable("name")))
    }

    Mono<ServerResponse> customGreetWithWord(ServerRequest request) {
      return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromObject(DEFAULT_RESPONSE + " " + request.pathVariable("name") + " " + request.pathVariable("word")))
    }

    Mono<ServerResponse> intResponse(Mono<FooModel> mono) {
      return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromPublisher(mono.map { i -> DEFAULT_RESPONSE + " " + i.id }, String))

    }
  }

  private static FooModel tracedMethod(long id) {
    tracer.spanBuilder("tracedMethod").startSpan().end()
    return new FooModel(id, "tracedMethod")
  }
}
