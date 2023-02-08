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
package com.xiaomi.mone.dubbo.docs.core.beans;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * api cache item.
 */
public class LayerItem implements Serializable {

    private Class itemClass;
    private Type itemType;

    private String itemName;
    private String itemClassStr;
    private String itemTypeStr;
    private boolean required;
    private String desc;
    private String defaultValue;
    private String exampleValue;
    private String[] allowableValues;

    private List<LayerItem> itemValue;

    public LayerItem(String itemName, Class itemClass, Type itemType) {
        this.itemClass = itemClass;
        this.itemClassStr = itemClass.getTypeName();
        this.itemType = itemType;
        this.itemTypeStr = itemType.getTypeName();
        this.itemName = itemName;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemClassStr() {
        return itemClassStr;
    }

    public void setItemClassStr(String itemClassStr) {
        this.itemClassStr = itemClassStr;
    }

    public String getItemTypeStr() {
        return itemTypeStr;
    }

    public void setItemTypeStr(String itemTypeStr) {
        this.itemTypeStr = itemTypeStr;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExampleValue() {
        return exampleValue;
    }

    public void setExampleValue(String exampleValue) {
        this.exampleValue = exampleValue;
    }

    public String[] getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(String[] allowableValues) {
        this.allowableValues = allowableValues;
    }

    public List<LayerItem> getItemValue() {
        return itemValue;
    }

    public void setItemValue(List<LayerItem> itemValue) {
        this.itemValue = itemValue;
    }
}
