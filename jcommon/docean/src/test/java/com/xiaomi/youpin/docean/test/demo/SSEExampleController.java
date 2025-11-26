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

package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example controller demonstrating SSE (Server-Sent Events) functionality
 * 
 * Access the SSE stream at: /api/sse/stream
 * Access regular endpoint at: /api/sse/data
 */
@Slf4j
@Controller
@RequestMapping(path = "/api/sse")
public class SSEExampleController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Regular endpoint returning a single response
     */
    @RequestMapping(path = "/data")
    public Mono<Map<String, Object>> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", LocalDateTime.now().format(formatter));
        data.put("message", "Regular endpoint response");
        return Mono.just(data);
    }

    /**
     * SSE endpoint that streams time updates every second for 60 seconds
     * Client needs to set Accept: text/event-stream header
     */
    @RequestMapping(path = "/stream")
    public Flux<Map<String, Object>> getSSEStream() {
        AtomicInteger counter = new AtomicInteger(0);
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("timestamp", LocalDateTime.now().format(formatter));
                    event.put("counter", counter.incrementAndGet());
                    event.put("message", "Server time update");
                    return event;
                })
                .take(60) // Limit to 60 events (1 minute)
                .doOnSubscribe(s -> log.info("Client subscribed to SSE stream"))
                .doOnComplete(() -> log.info("SSE stream completed"))
                .doOnCancel(() -> log.info("Client canceled SSE stream"));
    }

    /**
     * SSE endpoint that streams time updates every second for 60 seconds
     * Path ends with /sse to automatically trigger SSE mode
     */
    @RequestMapping(path = "/auto-sse")
    public Flux<String> getAutoSSEStream(MvcContext context) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> "Current time: " + LocalDateTime.now().format(formatter))
                .take(60)
                .doOnSubscribe(s -> log.info("Client subscribed to auto SSE stream"))
                .doOnComplete(() -> log.info("Auto SSE stream completed"))
                .doOnCancel(() -> log.info("Client canceled auto SSE stream"));
    }
} 