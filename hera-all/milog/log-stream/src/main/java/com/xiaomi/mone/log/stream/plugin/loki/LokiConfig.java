/*
 * Copyright (C) 2022 REPLACE_WITH_NAME
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

import com.xiaomi.mone.log.common.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description Loki config of configuration properties, eg. url/retention..
 * @author feig
 * @date 2022/01/12
 */
@Slf4j
@Data
public class LokiConfig {
    public static final String STREAM_KEY_SERVICE_TYPE = "type";
    /**
     * The constant service type must refer to https://xiaomi.f.mioffice.cn/docs/dock4O6E3Mx6JfpR5fs5MKQ68de#sWlBTE
     */
    public static final String STREAM_VALUE_SERVICE_TYPE_MIONE = "MIONE";
    public static final String STREAM_KEY_TAIL_ID = "tailId";
    public static final String STREAM_KEY_STORE_ID = "storeId";
    public static final String STREAM_KEY_SPACE_ID = "spaceId";
    public static final int STREAM_MILLION_TO_NANO = 1000000;
    public static final int STREAM_SECOND_TO_MILLION = 1000;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_HTTP = "application/json";
    public static final String X_SCOPE_ORGID = "X-Scope-OrgID";
    public static final String LOKI_TENANT_PREFIX_SPLIT = "-";
    public static final String LOKI_TENANT_PREFIX = "hera" + LOKI_TENANT_PREFIX_SPLIT;
    public static final String AUTHORIZATION = "Authorization";

    private final int defaultRetentionSecond = 300;
    private final int defaultSendMaxBatch = 1000;
    private final int defaultSendMaxSizeBytes = 1024 * 1024 * 40;
    private final int defaultSendMaxTimeoutMs = 5000;
    private final int defaultHttpConnectTimeoutSecond = 3;
    private final int defaultHttpReadTimeoutSecond = 5;
    private final int defaultHttpWriteTimeoutSecond = 5;
    private final String configPropertiesStreamTagsParseSplit = ",";
    private final String configPropertiesEnabledKey = "loki.enabled";
    private final String configPropertiesPushUrlKey = "loki.push.url";
    private final String configPropertiesRetentionKey = "loki.retention";
    private final String configPropertiesStreamTagsParseKey = "loki.tag.parse";
    private final String configPropertiesPushSendMaxBatch = "loki.push.max.batch";
    private final String configPropertiesPushSendMaxSizeBytes = "loki.push.max.size.bytes";
    private final String configPropertiesPushSendMaxTimeoutMs = "loki.push.max.flush.ms";

    public boolean enabled;
    public String pushUrl;
    public int streamRetentionSecond;
    public int httpConnectTimeoutSecond;
    public int httpReadTimeoutSecond;
    public int httpWriteTimeoutSecond;
    public List<String> customStreamParseTags;
    public int streamPushSendMaxBatch;
    public int streamPushSendMaxSizeBytes;
    public long streamPushSendMaxTimeoutMs;

    /**
     * Instantiates a new Loki config.
     */
    public LokiConfig() {
        log.info("LokiConfig init start");
        // validate config
        try {
            this.enabled = Boolean.parseBoolean(Config.ins().get(configPropertiesEnabledKey, "false"));
        } catch (Exception ignore) {
            this.enabled = false;
        }

        try {
            this.streamRetentionSecond = Integer.parseInt(Config.ins().get(configPropertiesRetentionKey, String.valueOf(defaultRetentionSecond)));
        } catch (NumberFormatException ignore) {
            this.streamRetentionSecond = defaultRetentionSecond;
        }

        try {
            this.streamPushSendMaxBatch = Integer.parseInt(Config.ins().get(configPropertiesPushSendMaxBatch, String.valueOf(defaultSendMaxBatch)));
        } catch (NumberFormatException ignore) {
            this.streamPushSendMaxBatch = defaultSendMaxBatch;
        }

        try {
            this.streamPushSendMaxSizeBytes = Integer.parseInt(Config.ins().get(configPropertiesPushSendMaxSizeBytes, String.valueOf(defaultSendMaxSizeBytes)));
        } catch (NumberFormatException ignore) {
            this.streamPushSendMaxSizeBytes = defaultSendMaxSizeBytes;
        }

        try {
            this.streamPushSendMaxTimeoutMs = Long.parseLong(Config.ins().get(configPropertiesPushSendMaxTimeoutMs, String.valueOf(defaultSendMaxTimeoutMs)));
        } catch (NumberFormatException ignore) {
            this.streamPushSendMaxTimeoutMs = defaultSendMaxTimeoutMs;
        }

        this.pushUrl = Config.ins().get(configPropertiesPushUrlKey, "http://loki-staging.log.xiaomi.net/api/v1/loki/api/v1/push");
        this.httpConnectTimeoutSecond = defaultHttpConnectTimeoutSecond;
        this.httpReadTimeoutSecond = defaultHttpReadTimeoutSecond;
        this.httpWriteTimeoutSecond = defaultHttpWriteTimeoutSecond;

        this.customStreamParseTags = new ArrayList<>(Arrays.asList(Config.ins().
                get(configPropertiesStreamTagsParseKey, "logstore,tail,logip").split(configPropertiesStreamTagsParseSplit)));
        log.info("LokiConfig init end");
    }
}