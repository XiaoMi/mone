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

package com.xiaomi.mone.log.common;

import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shanwb
 * @date 2021-07-19
 */
@ToString
public class LineMessage implements Serializable {

    public static transient String KEY_IP = "ip";
    public static transient String KEY_COLLECT_TIMESTAMP = "ct";
    public static transient String KEY_MQ_TOPIC = "t";
    public static transient String KEY_MQ_TOPIC_TAG = "tag";

    private Long lineNumber;

    private String fileName;

    private Long pointer;

    private Integer msgLength;

    private String msgBody;

    private Map<String, String> extMap;

    public LineMessage() {
    }

    public long getTimestamp() {
        String value = extMap.get(KEY_COLLECT_TIMESTAMP);
        if (StringUtils.isEmpty(value)) {
            return 0;
        } else {
            return Long.parseLong(value);
        }
    }

    public void setTimeStamp(long time) {
        extMap.put(KEY_COLLECT_TIMESTAMP, String.valueOf(time));
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Long getPointer() {
        return pointer;
    }

    public void setPointer(Long pointer) {
        this.pointer = pointer;
    }

    public Integer getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(Integer msgLength) {
        this.msgLength = msgLength;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public Map<String, String> getExtMap() {
        return extMap;
    }

    public void setExtMap(Map<String, String> extMap) {
        this.extMap = extMap;
    }

    public synchronized void setProperties(String key, String value) {
        if (null == extMap) {
            extMap = new HashMap<>();
        }
        extMap.put(key, value);
    }

    public String getProperties(String key) {
        return this.getProperties(key, null);
    }

    public String getProperties(String key, String defaultValue) {
        if (null == this.extMap) {
            return defaultValue;
        }

        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }

        String value = this.extMap.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        return value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
