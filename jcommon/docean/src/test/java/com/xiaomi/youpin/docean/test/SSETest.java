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

package com.xiaomi.youpin.docean.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test for Server-Sent Events (SSE) functionality
 * This test starts an HTTP server, registers the example SSE controller,
 * and tests different SSE interaction patterns.
 */
@Slf4j
public class SSETest {

    private static final int PORT = 8895;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static DoceanHttpServer server;
    private static final Gson gson = new Gson();

    /**
     * Start the HTTP server before running tests
     */
    @BeforeClass
    public static void setUp() throws InterruptedException {
        // Initialize IOC container
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("com.xiaomi.youpin.docean");
        
        // Register controller package 
        ioc.putBean("$scan_path", "com.xiaomi.youpin.docean.mvc.example");
        
        // Initialize MVC
        Mvc.ins();
        
        // Create and start HTTP server
        server = new DoceanHttpServer(HttpServerConfig.builder()
                .httpVersion(HttpServerConfig.HttpVersion.http1)
                .ssl(false)
                .port(PORT)
                .websocket(false)
                .build());
        
        server.start();
        
        // Wait a moment for server to fully start
        TimeUnit.SECONDS.sleep(2);
        log.info("Test server started on port {}", PORT);
    }

    /**
     * Stop the server after all tests are done
     */
    @AfterClass
    public static void tearDown() {
        if (server != null) {
            try {
                server.stop();
                log.info("Test server stopped");
            } catch (Exception e) {
                log.error("Error stopping server", e);
            }
        }
    }
    
    /**
     * Test regular endpoint (non-SSE) to verify basic functionality 
     */
    @Test
    public void testRegularEndpoint() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .build();
        
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/sse/data")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            Assert.assertTrue(response.isSuccessful());
            String responseBody = response.body().string();
            log.info("Regular response: {}", responseBody);
            
            // Verify response contains expected fields
            JsonObject jsonResponse = new Gson().toJsonTree(responseBody).getAsJsonObject();
            Assert.assertTrue(jsonResponse.has("timestamp"));
            Assert.assertTrue(jsonResponse.has("message"));
            Assert.assertEquals("Regular endpoint response", jsonResponse.get("message").getAsString());
        }
    }
    
    /**
     * Test SSE endpoint using Accept header to trigger SSE mode
     */
    @Test
    public void testSSEWithAcceptHeader() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Create request with SSE Accept header
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/sse/stream")
                .header("Accept", "text/event-stream")
                .build();
        
        // We'll collect events to verify them later
        List<Map<String, Object>> receivedEvents = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(5); // Wait for at least 5 events
        
        // Execute request
        Call call = client.newCall(request);
        Response response = call.execute();
        
        // Verify response headers
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals("text/event-stream", response.header("Content-Type"));
        
        // Process SSE stream
        processSSEStream(response, receivedEvents, latch);
        
        // Wait for events (with timeout)
        Assert.assertTrue("Didn't receive enough SSE events", latch.await(10, TimeUnit.SECONDS));
        
        // Verify events
        Assert.assertTrue("No events received", !receivedEvents.isEmpty());
        log.info("Received {} events", receivedEvents.size());
        
        // Verify structure of events
        for (Map<String, Object> event : receivedEvents) {
            Assert.assertTrue(event.containsKey("timestamp"));
            Assert.assertTrue(event.containsKey("counter"));
            Assert.assertTrue(event.containsKey("message"));
            Assert.assertEquals("Server time update", event.get("message"));
        }
        
        // Verify counter is incrementing
        int firstCounter = ((Double)receivedEvents.get(0).get("counter")).intValue();
        int lastCounter = ((Double)receivedEvents.get(receivedEvents.size()-1).get("counter")).intValue();
        Assert.assertTrue("Counter should increment", lastCounter > firstCounter);
    }
    
    /**
     * Test SSE endpoint using path convention (/auto-sse)
     */
    @Test
    public void testSSEWithPathConvention() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Create request with path ending in -sse
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/sse/auto-sse")
                .build();
        
        // We'll collect events to verify them later
        List<String> receivedEvents = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(5); // Wait for at least 5 events
        
        // Execute request
        Call call = client.newCall(request);
        Response response = call.execute();
        
        // Verify response headers
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals("text/event-stream", response.header("Content-Type"));
        
        // Process SSE stream for string events
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            StringBuilder eventData = new StringBuilder();
            
            while ((line = reader.readLine()) != null && latch.getCount() > 0) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    eventData.append(data);
                } else if (line.isEmpty() && eventData.length() > 0) {
                    // End of an event
                    receivedEvents.add(eventData.toString());
                    eventData = new StringBuilder();
                    latch.countDown();
                }
            }
        } finally {
            call.cancel(); // Close the connection
        }
        
        // Wait for events (with timeout)
        Assert.assertTrue("Didn't receive enough SSE events", latch.await(10, TimeUnit.SECONDS));
        
        // Verify events
        Assert.assertTrue("No events received", !receivedEvents.isEmpty());
        log.info("Received {} string events", receivedEvents.size());
        
        // Verify events contain timestamp in expected format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (String event : receivedEvents) {
            Assert.assertTrue(event.startsWith("Current time: "));
            String timeString = event.substring("Current time: ".length());
            // Check if we can parse the timestamp
            try {
                LocalDateTime.parse(timeString, formatter);
            } catch (Exception e) {
                Assert.fail("Invalid timestamp format: " + timeString);
            }
        }
    }
    
    /**
     * Test SSE with custom header X-SSE-Request
     */
    @Test
    public void testSSEWithCustomHeader() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Create request with custom SSE header
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/sse/stream")
                .header("X-SSE-Request", "true")
                .build();
        
        // We'll collect events to verify them later
        List<Map<String, Object>> receivedEvents = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(5); // Wait for at least 5 events
        
        // Execute request
        Call call = client.newCall(request);
        Response response = call.execute();
        
        // Verify response headers
        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals("text/event-stream", response.header("Content-Type"));
        
        // Process SSE stream
        processSSEStream(response, receivedEvents, latch);
        
        // Wait for events (with timeout)
        Assert.assertTrue("Didn't receive enough SSE events", latch.await(10, TimeUnit.SECONDS));
        
        // Verify events
        Assert.assertTrue("No events received", !receivedEvents.isEmpty());
        log.info("Received {} events with custom header", receivedEvents.size());
    }
    
    /**
     * Helper method to process SSE stream and extract JSON events
     */
    private void processSSEStream(Response response, List<Map<String, Object>> events, CountDownLatch latch) {
        // Start a thread to read the response body
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                String line;
                StringBuilder eventData = new StringBuilder();
                
                while ((line = reader.readLine()) != null && latch.getCount() > 0) {
                    if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();
                        eventData.append(data);
                    } else if (line.isEmpty() && eventData.length() > 0) {
                        // End of an event
                        try {
                            // Parse the JSON data
                            @SuppressWarnings("unchecked")
                            Map<String, Object> event = gson.fromJson(eventData.toString(), Map.class);
                            events.add(event);
                            log.debug("Received event: {}", event);
                            latch.countDown();
                        } catch (Exception e) {
                            log.error("Error parsing event: {}", eventData, e);
                        }
                        eventData = new StringBuilder();
                    }
                }
            } catch (IOException e) {
                log.error("Error reading SSE stream", e);
            }
        }).start();
    }
} 