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

import java.io.Serializable;
import java.util.List;

/**
 * api cache item.
 */
public class ApiCacheItem implements Serializable {

    private Boolean async;

    private String apiName;

    private String apiDocName;

    private String apiVersion;

    private String apiGroup;

    private String description;

    private String mavenAddr;

    private String apiRespDec;

    private String apiModelClass;

    private List<LayerItem> paramsLayerList;

    private String request;

    private String response;

    private LayerItem responseLayer;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiDocName() {
        return apiDocName;
    }

    public void setApiDocName(String apiDocName) {
        this.apiDocName = apiDocName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMavenAddr() {
        return mavenAddr;
    }

    public void setMavenAddr(String mavenAddr) {
        this.mavenAddr = mavenAddr;
    }

    public String getApiRespDec() {
        return apiRespDec;
    }

    public void setApiRespDec(String apiRespDec) {
        this.apiRespDec = apiRespDec;
    }

    public String getApiModelClass() {
        return apiModelClass;
    }

    public void setApiModelClass(String apiModelClass) {
        this.apiModelClass = apiModelClass;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public List<LayerItem> getParamsLayerList() {
        return paramsLayerList;
    }

    public void setParamsLayerList(List<LayerItem> paramsLayerList) {
        this.paramsLayerList = paramsLayerList;
    }

    public LayerItem getResponseLayer() {
        return responseLayer;
    }

    public void setResponseLayer(LayerItem responseLayer) {
        this.responseLayer = responseLayer;
    }
}
