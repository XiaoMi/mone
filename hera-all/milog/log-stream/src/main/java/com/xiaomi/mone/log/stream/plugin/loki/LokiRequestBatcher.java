/*
 * Copyright (C) 2022 Xiaomi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaomi.mone.log.stream.plugin.loki;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.xiaomi.mone.log.parse.LogParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description
 * @author feig
 * @date 2022/01/13
 */
public class LokiRequestBatcher {
    private final int maxBatch;
    private final int maxSizeBytes;

    private final AtomicInteger requestLogLines = new AtomicInteger();
    private final AtomicInteger requestSizeBytes = new AtomicInteger();

    private Map<String, LokiRequest> streamsMap;

    public LokiRequestBatcher(LokiConfig conf) {
        this.maxBatch = conf.streamPushSendMaxBatch;
        this.maxSizeBytes = conf.streamPushSendMaxSizeBytes;

        this.streamsMap = new ConcurrentHashMap<>();
    }


    /**
     * Check before add stream.
     *
     * @param s the log stream
     * @return the boolean. false mean exceed limit
     */
    public boolean checkBeforeAdd(LokiLogStream s) {
        // validate log lines
        if ((requestLogLines.get() + s.getMessageLines()) > maxBatch) {
            return false;
        }

        // validate log size bytes
        if (requestSizeBytes.get() + s.getMessageUtf8SizeBytes() > maxSizeBytes) {
            return false;
        }

        return true;
    }

    public void add(LokiLogStream s, String tenantId) {
        if (s == null) {
            return;
        }

        if (this.streamsMap.get(tenantId) == null) {
            this.streamsMap.put(tenantId, new LokiRequest(new CopyOnWriteArrayList<LokiLogStream>() {{
                add(s);
            }}));
        } else {
            this.streamsMap.get(tenantId).getStreams().add(s);
        }

        this.requestLogLines.addAndGet(s.getMessageLines());
        this.requestSizeBytes.addAndGet(s.getMessageUtf8SizeBytes());
    }

    public void add(List<LokiLogStream> s, String tenantId) {
        if (s == null || s.isEmpty()) {
            return;
        }

        if (this.streamsMap.get(tenantId) == null) {
            this.streamsMap.put(tenantId, new LokiRequest(s));
        } else {
            this.streamsMap.get(tenantId).getStreams().addAll(s);
        }

        this.requestLogLines.addAndGet(s.stream().mapToInt(LokiLogStream::getMessageLines).sum());
        this.requestSizeBytes.addAndGet(s.stream().mapToInt(LokiLogStream::getMessageUtf8SizeBytes).sum());
    }

    public void addStream(Map<String, Object> msg, Map<String, Object> tags, Gson gson, String tenantId) {
        if (msg == null || msg.isEmpty()) {
            return;
        }

        add(toStream(msg, tags, gson), tenantId);
    }

    /**
     * Sets streams from rmqSinkJob msg, directly set stream for send
     *
     * @param msg the msg
     */
    public void setStreams(Map<String, Object> msg, Map<String, Object> tags, Gson gson, String tenantId) {
        if (msg == null || msg.isEmpty()) {
            return;
        }

        List<LokiLogStream> msgToStreams = new ArrayList<>();

        LokiLogStream stream = toStream(msg, tags, gson);
        msgToStreams.add(stream);

        this.streamsMap = new ConcurrentHashMap<String, LokiRequest>() {{
            put(tenantId, new LokiRequest(msgToStreams));
        }};

        this.requestLogLines.set(stream.getMessageLines());
        this.requestSizeBytes.set(stream.getMessageUtf8SizeBytes());
    }

    public LokiLogStream toStream(Map<String, Object> msg, Map<String, Object> tags, Gson gson) {
        if (msg == null || msg.isEmpty()) {
            return null;
        }

        long ts;

        try {
            ts = Long.parseLong(String.valueOf(msg.get(LogParser.esKeyMap_timestamp)));
        } catch (NumberFormatException ignore) {
            ts = System.currentTimeMillis();
        }
        long tsNano = ts * LokiConfig.STREAM_MILLION_TO_NANO;

        return new LokiLogStream(tags, new ArrayList<List<String>>() {{
            add(Arrays.asList(Long.toString(tsNano), gson.toJson(msg)));
        }});
    }

    public void reset() {
        this.streamsMap.clear();
        this.requestLogLines.set(0);
        this.requestSizeBytes.set(0);
    }

    public int getCapacity() {
        return this.streamsMap.size();
    }

    public Map<String, LokiRequest> getStreamsMap() {
        return streamsMap;
    }
}
