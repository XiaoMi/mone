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

import com.google.gson.annotations.Expose;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.summingInt;

/**
 * @description Loki log record stream for serialization, refer to https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push
 * @author feig
 * @date 2022/01/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LokiLogStream {
    /**
     * The Stream. mean tags for Loki(like elasticsearch keyword, eg,ip:127.0.0.1)
     */
    @Expose
    private Map<String, Object> stream;

    /**
     * The Values. eg,[[timestampNano,logRaw]]
     */
    @Expose
    private List<List<String>> values;

    private int messageUtf8SizeBytes;
    private int messageLines;

    public LokiLogStream(Map<String, Object> stream, List<List<String>> values) {
        this.stream = stream;
        this.values = values;
        this.messageUtf8SizeBytes = utf8Length(stream.toString() + values.toString());
        this.messageLines = values.size();
    }

    /**
     * Calculate the number of bytes required to store given string
     * in UTF-8 encoding.
     */
    public static int utf8Length(CharSequence input) {
        int count = 0;
        for (int i = 0, len = input.length(); i < len; i++) {
            char ch = input.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }
}
