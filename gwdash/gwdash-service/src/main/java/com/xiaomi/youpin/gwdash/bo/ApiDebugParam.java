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

package com.xiaomi.youpin.gwdash.bo;

import java.util.Objects;

public class ApiDebugParam {

    private Integer aid;

    private String url;

    private String httpMethod;

    private String headers;

    private Integer timeout;

    private String params;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ApiDebugParam{" +
                "aid=" + aid +
                ", url='" + url + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", headers='" + headers + '\'' +
                ", timeout=" + timeout +
                ", params='" + params + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDebugParam that = (ApiDebugParam) o;
        return Objects.equals(aid, that.aid) &&
                Objects.equals(url, that.url) &&
                Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(timeout, that.timeout) &&
                Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aid, url, httpMethod, headers, timeout, params);
    }
}
