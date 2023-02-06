package com.xiaomi.mone.http.docs.providers;

import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;

import java.util.List;

public interface IHttpDocProvider {

    /**
     * Get all information of all modules , including API parameter information.
     * @return java.lang.String
     */
    List<HttpModuleCacheItem> httpApiModuleListAndApiInfo();

    /**
     * Get module information according to the complete class name of Dubbo provider interface.
     * @param apiInterfaceClassName
     * @return java.lang.String
     */
    String httpApiModuleInfo(String apiInterfaceClassName);

    /**
     * Get method parameters and return information according to the complete class name and method name of Dubbo provider interface.
     * @param apiInterfaceClassNameMethodName
     * @return java.lang.String
     */
    String httpApiParamsResponseInfo(String apiInterfaceClassNameMethodName);
}
