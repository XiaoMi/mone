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

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class NodeInfo {

    private Integer id;

    private Long apiInfoId;

    /**
     * ?name=$name&age=$age
     */
    private String url;

    /**
     * {'name':$name}
     */
    private String body;

    private String header;

    /**
     * 参数的提取规则
     * 0_map_result{key}
     * 前边是抽取的key,后边是抽取规则
     */
    private Map<String, String> paramExtract;

    /**
     * 抽取出来的参数
     * 这个抽取出来的参数,最后会用来填充 url body 或者header
     */
    private Map<String, String> paramMap;

    /**
     * GET POST
     */
    private String httpMethod;


    /**
     * 是否作为结果返回给调用方
     */
    private boolean result;

}
