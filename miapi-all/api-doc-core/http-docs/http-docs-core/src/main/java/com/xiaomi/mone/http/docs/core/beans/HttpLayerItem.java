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

import com.xiaomi.mone.http.docs.util.ClassTypeUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * http api params cache item.
 */
public class HttpLayerItem implements Serializable {
    private transient Class itemClass;
    private transient Type itemType;

    private String paramKey;
    private String paramName;
    private String paramType;
    private boolean paramNotNull;
    private String paramNote = "";
    private String paramValue = "";
    private List<HttpLayerItem> childList;

    public HttpLayerItem(String paramKey,Class itemClass, Type itemType) {
        this.itemClass = itemClass;
        this.itemType = itemType;
        this.paramKey = paramKey;
        this.paramType = ClassTypeUtil.typeStr2TypeNo(itemClass);
    }

    public Class getItemClass() {
        return itemClass;
    }

    public void setItemClass(Class itemClass) {
        this.itemClass = itemClass;
    }

    public Type getItemType() {
        return itemType;
    }

    public void setItemType(Type itemType) {
        this.itemType = itemType;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public boolean isParamNotNull() {
        return paramNotNull;
    }

    public void setParamNotNull(boolean paramNotNull) {
        this.paramNotNull = paramNotNull;
    }

    public String getParamNote() {
        return paramNote;
    }

    public void setParamNote(String paramNote) {
        this.paramNote = paramNote;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public List<HttpLayerItem> getChildList() {
        return childList;
    }

    public void setChildList(List<HttpLayerItem> childList) {
        this.childList = childList;
    }
}
