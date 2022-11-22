/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.apidocs.core.providers;

import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;

import java.util.List;

/**
 * The api used by Dubbo doc, get the parsed API information.
 */
public interface IDubboDocProvider {

    /**
     * Get basic information of all modules, excluding API parameter information.
     * @return java.lang.String
     */
    String apiModuleList();

    /**
     * Get all information of all modules , including API parameter information.
     * @return java.lang.String
     */
    List<ModuleCacheItem> apiModuleListAndApiInfo();

    /**
     * Get module information according to the complete class name of Dubbo provider interface.
     * @param apiInterfaceClassName
     * @return java.lang.String
     */
    String apiModuleInfo(String apiInterfaceClassName);

    /**
     * Get method parameters and return information according to the complete class name and method name of Dubbo provider interface.
     * @param apiInterfaceClassNameMethodName
     * @return java.lang.String
     */
    String apiParamsResponseInfo(String apiInterfaceClassNameMethodName);

}
