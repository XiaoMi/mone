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

package com.xiaomi.youpin.docean.test.manual;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manual test for Server-Sent Events (SSE) functionality
 * Run this class directly as a Java application to test SSE.
 * 
 * This test:
 * 1. Starts an HTTP server with the example SSE controller
 * 2. Connects to the SSE endpoint using OkHttp
 * 3. Prints received events to the console
 * 4. Continues running until you press Enter
 */
public class ManualSSETest {

    private static final int PORT = 8896;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final Gson gson = new Gson();
    private static final AtomicInteger eventCounter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        // Start server
        DoceanHttpServer server = startServer();

        try {
            System.out.println("=== Manual SSE Test Started ===");
            System.out.println("Server running on " + BASE_URL);
            System.out.println("Testing SSE endpoint: " + BASE_URL + "/api/sse/stream");
            System.out.println("Press Enter to stop the test");
            
            // Start client in a separate thread
            Thread clientThread = new Thread(() -> {
                try {
                    // Wait a moment for server to fully start
                    TimeUnit.SECONDS.sleep(1);
                    testSSEEndpoint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            clientThread.setDaemon(true);
            clientThread.start();
            
            // Wait for Enter key to stop
            System.in.read();
        } finally {
            // Shutdown server
            System.out.println("Stopping server...");
            server.stop();
            System.out.println("Server stopped");
        }
    }
    
    /**
     * Start the HTTP server with our SSE controller
     */
    private static DoceanHttpServer startServer() throws InterruptedException {
        // Initialize IOC container
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("com.xiaomi.youpin.docean");
        
        // Register controller package 
        ioc.putBean("$scan_path", "com.xiaomi.youpin.docean.mvc.example");
        
        // Initialize MVC
        Mvc.ins();
        
        // Create HTTP server
        HttpServerConfig config = new HttpServerConfig();
        config.setHttpVersion(HttpServerConfig.HttpVersion.http1);
        config.setSsl(false);
        config.setPort(PORT);
        config.setWebsocket(false);
        
        DoceanHttpServer server = new DoceanHttpServer(config);
        
        // Start server
        server.start();
        
        return server;
    }
    
    /**
     * Test the SSE endpoint using OkHttp client
     */
    private static void testSSEEndpoint() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.SECONDS) // No timeout for SSE
                .build();
        
        // Create request with SSE Accept header
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/sse/stream")
                .header("Accept", "text/event-stream")
                .build();
        
        System.out.println("Connecting to SSE endpoint...");
        
        // Execute request
        Call call = client.newCall(request);
        Response response = call.execute();
        
        if (!response.isSuccessful()) {
            System.err.println("Failed to connect: " + response.code() + " " + response.message());
            return;
        }
        
        System.out.println("Connected! Content-Type: " + response.header("Content-Type"));
        System.out.println("Reading SSE events (press Enter to stop):");
        System.out.println("-----------------------------");
        
        // Process SSE stream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            StringBuilder eventData = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    eventData.append(data);
                } else if (line.isEmpty() && eventData.length() > 0) {
                    // End of an event
                    try {
                        // Parse and print the event
                        String jsonData = eventData.toString();
                        
                        // Try to parse as JSON if possible
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> event = gson.fromJson(jsonData, Map.class);
                            System.out.printf("Event #%d: %s%n", 
                                    eventCounter.incrementAndGet(), 
                                    formatEvent(event));
                        } catch (Exception e) {
                            // Not valid JSON, print as is
                            System.out.printf("Event #%d: %s%n", 
                                    eventCounter.incrementAndGet(), 
                                    jsonData);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing event: " + eventData);
                    }
                    eventData = new StringBuilder();
                }
            }
        }
        
        System.out.println("SSE stream ended");
    }
    
    /**
     * Format a JSON event as a readable string
     */
    private static String formatEvent(Map<String, Object> event) {
        if (event.size() <= 3) {
            return event.toString();
        }
        
        // For larger events, format in a more readable way
        StringBuilder result = new StringBuilder();
        result.append("{\n");
        
        for (Map.Entry<String, Object> entry : event.entrySet()) {
            result.append("  ")
                  .append(entry.getKey())
                  .append(": ")
                  .append(entry.getValue())
                  .append("\n");
        }
        
        result.append("}");
        return result.toString();
    }
} 