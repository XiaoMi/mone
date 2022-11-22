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
