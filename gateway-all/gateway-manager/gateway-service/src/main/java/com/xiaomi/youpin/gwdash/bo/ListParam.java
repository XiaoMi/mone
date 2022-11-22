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

import lombok.Data;

import java.util.Objects;

/**
 * @author goodjava@qq.com
 */
@Data
public class ListParam {

    private int pageNo;

    private int pageSize;

    private String urlString;

    private String name;

    private String pathString;

    private String serviceName;

    private int groupType;

    private Integer groupId;

    private String httpMethod;

    private Integer routeType;


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathString() {
        return pathString;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    @Override
    public String toString() {
        return "ListParam{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", urlString='" + urlString + '\'' +
                ", name='" + name + '\'' +
                ", pathString='" + pathString + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", groupType=" + groupType +
                ", groupId=" + groupId +
                ", httpMethod='" + httpMethod + '\'' +
                ", routeType=" + routeType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListParam listParam = (ListParam) o;
        return pageNo == listParam.pageNo &&
                pageSize == listParam.pageSize &&
                groupType == listParam.groupType &&
                Objects.equals(urlString, listParam.urlString) &&
                Objects.equals(name, listParam.name) &&
                Objects.equals(pathString, listParam.pathString) &&
                Objects.equals(serviceName, listParam.serviceName) &&
                Objects.equals(groupId, listParam.groupId) &&
                Objects.equals(httpMethod, listParam.httpMethod) &&
                Objects.equals(routeType, listParam.routeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNo, pageSize, urlString, name, pathString, serviceName, groupType, groupId, httpMethod, routeType);
    }
}
