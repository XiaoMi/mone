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

package com.xiaomi.miapi.common;

import java.util.Map;
import java.util.Objects;

public class HttpResult {

    private int status;

    private Map<String, String> headers;

    private String content;

    private long timestamp;

    private long cost;

    private long size;

    public HttpResult() {
    }

    public HttpResult(int status) {
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public HttpResult(int status, Map<String, String> headers) {
        this.status = status;
        this.headers = headers;
        this.timestamp =  System.currentTimeMillis();
    }

    public HttpResult(int status, String content) {
        this.status = status;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public HttpResult(int status, Map<String, String> headers, String content) {
        this.status = status;
        this.headers = headers;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public static HttpResult fail(int status) {
        return new HttpResult(status);
    }

    public static HttpResult success(int status, String content) {
        return new HttpResult(status, content);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "status=" + status +
                ", headers=" + headers +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResult that = (HttpResult) o;
        return status == that.status &&
                timestamp == that.timestamp &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, headers, content, timestamp);
    }
}
