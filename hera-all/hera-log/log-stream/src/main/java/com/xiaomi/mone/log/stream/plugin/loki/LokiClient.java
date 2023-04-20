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

import java.util.Map;
import java.util.Optional;

/**
 * @description Basic interface that all Loki senders must implement. // TODO add grpc logproto client
 * @author feig
 * @date 2022/01/12
 */
public interface LokiClient extends AutoCloseable {
    /**
     * Get Loki configuration for this client
     */
    public LokiConfig getConfig();

    /**
     * Send a message to Loki with batch cache
     * @param  msg send data from MQ message
     * @param  tags for loki stream meta tags
     * @param  tenantId for loki stream X_SCOPE_ORGID (option)
     * @return A LokiResponse from Loki.
     */
    public LokiResponse send(Map<String, Object> msg, Map<String, Object> tags, String tenantId);

    /**
     * Send a batch directly to Loki
     * @param  batch send byte data
     * @param  tenantId for loki stream X_SCOPE_ORGID (option)
     * @return A LokiResponse from Loki
     */
    public LokiResponse send(byte[] batch, String tenantId);


    /**
     * Build fixed tags map by RmqSinkJob variables
     *
     * @param logTailId the log tail id
     * @param logStoreId the log store id
     * @param logSpaceId the log space id
     * @return the loki fixed map
     */
    public Map<String, Object> buildFixedTags(Long logTailId, Long logStoreId, Long logSpaceId);
}
