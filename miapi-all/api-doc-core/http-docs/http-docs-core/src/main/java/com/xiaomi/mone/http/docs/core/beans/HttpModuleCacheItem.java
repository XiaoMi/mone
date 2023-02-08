/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.http.docs.core.beans;

import java.io.Serializable;
import java.util.List;

public class HttpModuleCacheItem implements Serializable {
    private String httpModuleDocName;

    private String httpModuleClassName;

    private List<HttpApiCacheItem> httpModuleApiList;

    public String getHttpModuleDocName() {
        return httpModuleDocName;
    }

    public void setHttpModuleDocName(String httpModuleDocName) {
        this.httpModuleDocName = httpModuleDocName;
    }

    public String getHttpModuleClassName() {
        return httpModuleClassName;
    }

    public void setHttpModuleClassName(String httpModuleClassName) {
        this.httpModuleClassName = httpModuleClassName;
    }

    public List<HttpApiCacheItem> getHttpModuleApiList() {
        return httpModuleApiList;
    }

    public void setHttpModuleApiList(List<HttpApiCacheItem> httpModuleApiList) {
        this.httpModuleApiList = httpModuleApiList;
    }
}
