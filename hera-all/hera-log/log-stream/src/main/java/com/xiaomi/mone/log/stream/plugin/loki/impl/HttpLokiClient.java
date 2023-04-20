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

package com.xiaomi.mone.log.stream.plugin.loki.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.stream.plugin.loki.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author feig
 * @description Loki senders implement base on http client
 * @date 2022/01/13
 */
@Slf4j
public class HttpLokiClient implements LokiClient {
    private LokiConfig conf;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final MediaType json = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;
    private String tenantId;
    private LokiRequestBatcher requestBatch;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> flushScheduledFuture;

    public HttpLokiClient() {
        try {
            conf = new LokiConfig();
            client = this.customOkHttpClient(conf);
            requestBatch = new LokiRequestBatcher(conf);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            flushScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
                        log.debug("[loki] scheduled flush request batch size {}", requestBatch.getCapacity());
                        flushRequest();
                    },
                    conf.streamPushSendMaxTimeoutMs, conf.streamPushSendMaxTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("HttpLokiClient init error", e);
        }
    }

    @Override
    public LokiConfig getConfig() {
        return this.conf;
    }

    @Override
    public LokiResponse send(byte[] batch, String tenantId) {
        Response resp = null;
        Exception e = null;
        String respBody = null;
        int respCode = 0;

        try {
            resp = client.newCall(buildRequest(batch, tenantId)).execute();
            respCode = resp.code();
            respBody = Objects.requireNonNull(resp.body()).string();
        } catch (Exception exception) {
            e = exception;
        }

        // deal response code if not succeed
        if (e != null) {
            log.error("[loki] send tenant{} log error: {}", tenantId, e);
            return null;
        } else if (resp.code() < 200 || resp.code() > 299) {
            log.error("[loki] send tenant{} log with code {} error: {}", tenantId, resp.code(), respBody);
        }

        return new LokiResponse(respCode, respBody);
    }

    @Override
    public LokiResponse send(Map<String, Object> msg, Map<String, Object> tags, String tenantId) {
        if (msg == null || msg.isEmpty() || tags == null || tags.isEmpty()) {
            return null;
        }

        if (!validateMsg(msg)) {
            log.debug("[loki] validate msg data error");
            return null;
        }

        tags.putAll(msg.entrySet().stream().
                filter(map -> conf.customStreamParseTags.contains(map.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        LokiLogStream s = requestBatch.toStream(msg, tags, gson);
        if (requestBatch.checkBeforeAdd(s)) {
            requestBatch.add(s, tenantId);
            return null;
        }

        LokiResponse resp = flushRequest();
        requestBatch.add(s, tenantId);

        return resp;
    }

    public synchronized LokiResponse flushRequest() {
        if (requestBatch.getCapacity() == 0) {
            return null;
        }

        LokiResponse resp = null;
        Map<String, LokiRequest> m = requestBatch.getStreamsMap();
        for (Map.Entry<String, LokiRequest> entry : m.entrySet()) {
            // utf8 charsets same with RmqExporter charsets
            resp = send(gson.toJson(entry.getValue()).getBytes(StandardCharsets.UTF_8), entry.getKey());
        }

        requestBatch.reset();

        return resp;
    }

    @Override
    public Map<String, Object> buildFixedTags(Long logTailId, Long logStoreId, Long logSpaceId) {
        return new HashMap<String, Object>() {{
            put(LokiConfig.STREAM_KEY_SERVICE_TYPE, LokiConfig.STREAM_VALUE_SERVICE_TYPE_MIONE);
            put(LokiConfig.STREAM_KEY_TAIL_ID, logTailId);
            put(LokiConfig.STREAM_KEY_STORE_ID, logStoreId);
            put(LokiConfig.STREAM_KEY_SPACE_ID, logSpaceId);
        }};
    }

    @Override
    public void close() throws Exception {
        flushRequest();

        flushScheduledFuture.cancel(false);
        scheduledExecutorService.shutdown();

        log.info("[loki] close loki plugin client done!");
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Validate msg by loki config.
     *
     * @param msg the msg map
     * @return the boolean. false when validate failed
     */
    private boolean validateMsg(Map<String, Object> msg) {
        // validate data timestamp with config
        try {
            return Math.abs(System.currentTimeMillis() -
                    Long.parseLong(String.valueOf(msg.get(LogParser.esKeyMap_timestamp)))) <
                    ((long) conf.streamRetentionSecond * LokiConfig.STREAM_SECOND_TO_MILLION);
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    /**
     * Build request request by tenant and byte streams.
     *
     * @param streams  the byte streams
     * @param tenantId the tenant id (add hera prefix)
     * @return the request
     */
    private Request buildRequest(byte[] streams, String tenantId) {
        if (this.tenantId != null && !this.tenantId.isEmpty()) {
            tenantId = this.tenantId;
        }

        // add hera prefix for tenant
        tenantId = LokiConfig.LOKI_TENANT_PREFIX + tenantId;

        return new Request.Builder()
                .url(conf.pushUrl)
                .addHeader(LokiConfig.CONTENT_TYPE, LokiConfig.CONTENT_TYPE_HTTP)
                .addHeader(LokiConfig.X_SCOPE_ORGID, tenantId)
                .post(RequestBody.create(streams, json))
                .build();
    }

    private OkHttpClient customOkHttpClient(LokiConfig conf) {
        if (conf == null) {
            return new OkHttpClient();
        }
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (conf.httpConnectTimeoutSecond > 0) {
            builder.connectTimeout(conf.httpConnectTimeoutSecond, TimeUnit.SECONDS);
        }
        if (conf.httpReadTimeoutSecond > 0) {
            builder.readTimeout(conf.httpReadTimeoutSecond, TimeUnit.SECONDS);
        }
        if (conf.httpWriteTimeoutSecond > 0) {
            builder.writeTimeout(conf.httpWriteTimeoutSecond, TimeUnit.SECONDS);
        }
        return builder.build();
    }
}
