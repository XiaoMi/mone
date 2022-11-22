package com.xiaomi.mone.http.docs.providers;

import com.xiaomi.mone.http.docs.core.HttpApiDocsCache;
import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;

import java.util.List;

public class HttpDocProviderImpl implements IHttpDocProvider{
    @Override
    public List<HttpModuleCacheItem> httpApiModuleListAndApiInfo() {
        return HttpApiDocsCache.getAllApiModuleInfo();
    }

    @Override
    public String httpApiModuleInfo(String apiInterfaceClassName) {
        return HttpApiDocsCache.getApiModuleStr(apiInterfaceClassName);
    }

    @Override
    public String httpApiParamsResponseInfo(String apiInterfaceClassNameMethodName) {
        return HttpApiDocsCache.getApiParamsAndRespStr(apiInterfaceClassNameMethodName);
    }
}
