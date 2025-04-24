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
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple SSE client test that connects to an existing server
 * 
 * This test:
 * 1. Connects to an SSE endpoint
 * 2. Consumes and displays SSE events
 * 3. Runs until canceled or connection is closed
 * 
 * To run this test:
 * 1. First start the HTTP server: run HttpServerTest.testHttpServer() method
 * 2. Then run this class to connect to the SSE endpoint
 */
public class SSEClientTest {

    // Connection parameters - adjust these to match your server configuration
    private static final String SERVER_URL = "http://localhost:8899";
    private static final String SSE_ENDPOINT = "/api/sse/stream";
    
    private static final Gson gson = new Gson();
    private static final AtomicInteger eventCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("=== SSE Client Test ===");
        System.out.println("Connecting to: " + SERVER_URL + SSE_ENDPOINT);
        System.out.println("Press Ctrl+C to stop");
        
        try {
            connectToSSE();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Connect to SSE endpoint and consume events
     */
    private static void connectToSSE() throws IOException {
        // Configure OkHttp client with no timeout for SSE
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        
        // Build request with SSE Accept header
        Request request = new Request.Builder()
                .url(SERVER_URL + SSE_ENDPOINT)
                .header("accept", "text/event-stream")
                .build();
        
        System.out.println("Connecting...");
        
        // Execute request
        Call call = client.newCall(request);
        Response response = call.execute();
        
        if (!response.isSuccessful()) {
            System.err.println("Failed to connect: " + response.code() + " " + response.message());
            return;
        }
        
        System.out.println("Connected successfully!");
        System.out.println("Content-Type: " + response.header("Content-Type"));
        System.out.println("Receiving events:");
        System.out.println("-----------------------------");
        
        // Process the SSE stream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            StringBuilder eventData = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                // Data line in SSE format
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    eventData.append(data);
                } 
                // Empty line indicates end of an event
                else if (line.isEmpty() && eventData.length() > 0) {
                    processEvent(eventData.toString());
                    eventData = new StringBuilder();
                }
                // Event type line
                else if (line.startsWith("event:")) {
                    String eventType = line.substring(6).trim();
                    System.out.println("Event type: " + eventType);
                }
                // Event ID line
                else if (line.startsWith("id:")) {
                    String eventId = line.substring(3).trim();
                    System.out.println("Event ID: " + eventId);
                }
            }
        }
        
        System.out.println("SSE stream ended");
    }
    
    /**
     * Process a received SSE event
     */
    private static void processEvent(String eventData) {
        try {
            // Try to parse as JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> event = gson.fromJson(eventData, Map.class);
            System.out.printf("Event #%d: %s%n", 
                    eventCounter.incrementAndGet(), 
                    formatEvent(event));
        } catch (Exception e) {
            // Not valid JSON, print as is
            System.out.printf("Event #%d: %s%n", 
                    eventCounter.incrementAndGet(), 
                    eventData);
        }
    }
    
    /**
     * Format a JSON event as a readable string
     */
    private static String formatEvent(Map<String, Object> event) {
        if (event == null || event.isEmpty()) {
            return "{}";
        }
        
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