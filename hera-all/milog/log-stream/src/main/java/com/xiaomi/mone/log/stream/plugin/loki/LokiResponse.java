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

/**
 * @description Loki's response. when send ok with 204 code
 * @author feig
 * @date 2022/01/12
 */
public class LokiResponse {
    public final int status;

    public final String body;

    public LokiResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }
}
