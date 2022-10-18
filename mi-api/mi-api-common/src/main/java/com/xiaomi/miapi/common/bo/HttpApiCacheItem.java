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
package com.xiaomi.miapi.common.bo;

import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * api cache item.
 */
@Data
public class HttpApiCacheItem implements Serializable {

    private String apiName;

    private String apiPath;

    private String apiMethod;

    private String description;

    private String apiRespDec;

    private String apiTag;

    private String paramsDesc;

    private List<HttpLayerItem> paramsLayerList;

    private String response;

    private HttpLayerItem responseLayer;

}
