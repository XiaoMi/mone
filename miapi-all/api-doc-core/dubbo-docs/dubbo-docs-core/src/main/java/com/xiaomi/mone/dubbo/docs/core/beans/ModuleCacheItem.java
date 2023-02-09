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
package com.xiaomi.mone.dubbo.docs.core.beans;

import java.util.List;

/**
 * api module cache item.
 */
public class ModuleCacheItem {

    private String moduleDocName;

    private String moduleClassName;

    private String moduleVersion;

    private String moduleGroup;

    private List<ApiCacheItem> moduleApiList;

    public String getModuleDocName() {
        return moduleDocName;
    }

    public void setModuleDocName(String moduleDocName) {
        this.moduleDocName = moduleDocName;
    }

    public String getModuleClassName() {
        return moduleClassName;
    }

    public void setModuleClassName(String moduleClassName) {
        this.moduleClassName = moduleClassName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public List<ApiCacheItem> getModuleApiList() {
        return moduleApiList;
    }

    public void setModuleApiList(List<ApiCacheItem> moduleApiList) {
        this.moduleApiList = moduleApiList;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }
}
