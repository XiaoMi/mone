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

package com.youpin.xiaomi.tesla.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class ScriptInfo implements Serializable {

    private String scriptId;

    private String methodName;

    private int version;

    private String script;

    private List<String> params;

    /**
     * 是否是jar包形式
     */
    private boolean jar;

    /**
     * 存放jar包的地址
     */
    private String jarUrl;


    private String entryClassName;


    private String gitName;


    private String gitGroup;


    private String commitId;


    /**
     * 0 执行方法
     * 1 before
     * 2 after
     * 3 around
     * 4 jar 包形式加载
     * ScriptType
     */
    private int scriptType;

    public ScriptInfo() {
    }

    public ScriptInfo(String scriptId, String methodName, String script, List<String> params) {
        this.scriptId = scriptId;
        this.methodName = methodName;
        this.script = script;
        this.params = params;
    }

    public ScriptInfo(String scriptId, String methodName, String script, List<String> params, int scriptType) {
        this.scriptId = scriptId;
        this.methodName = methodName;
        this.script = script;
        this.params = params;
        this.scriptType = scriptType;
    }
}
