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

package com.xiaomi.youpin.mischedule.api.service.bo;

import com.xiaomi.youpin.mischedule.enums.XmlSettingEnums;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class CompileParam {

    private String gitUrl;
    private String branch;
    private String profile;
    private Long timeOut;
    private String tags;
    private String gitName;
    private String gitToken;
    private String buildPath;
    private String jarPath;
    /**
     * @see XmlSettingEnums
     */
    private int repoType;

    /**
     * gwdash 那边的唯一id
     */
    private Long id;

    /**
     * 二进制文件名称
     */
    private String binName;

    /**
     * 语言
     */
    private String language;

    /**
     * mvn 命令
     */
    private String customParams;


    /**
     * 别名
     */
    private String alias;

}
