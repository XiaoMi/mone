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

/**
 * api cache item.
 */
public class HttpApiCacheItem implements Serializable {

    private String apiName;

    private String apiMethodName;

    private String apiPath;

    private String apiMethod;

    private String description;

    private String apiRespDec;

    private String apiTag;

    private String paramsDesc;

    private List<HttpLayerItem> paramsLayerList;

    private String response;

    private HttpLayerItem responseLayer;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiMethodName() {
        return apiMethodName;
    }

    public void setApiMethodName(String apiMethodName) {
        this.apiMethodName = apiMethodName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }


    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiRespDec() {
        return apiRespDec;
    }

    public void setApiRespDec(String apiRespDec) {
        this.apiRespDec = apiRespDec;
    }

    public String getApiTag() {
        return apiTag;
    }

    public void setApiTag(String apiTag) {
        this.apiTag = apiTag;
    }

    public String getParamsDesc() {
        return paramsDesc;
    }

    public void setParamsDesc(String paramsDesc) {
        this.paramsDesc = paramsDesc;
    }

    public List<HttpLayerItem> getParamsLayerList() {
        return paramsLayerList;
    }

    public void setParamsLayerList(List<HttpLayerItem> paramsLayerList) {
        this.paramsLayerList = paramsLayerList;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public HttpLayerItem getResponseLayer() {
        return responseLayer;
    }

    public void setResponseLayer(HttpLayerItem responseLayer) {
        this.responseLayer = responseLayer;
    }
}
