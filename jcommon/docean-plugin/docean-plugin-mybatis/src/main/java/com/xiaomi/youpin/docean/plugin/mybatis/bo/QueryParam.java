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

package com.xiaomi.youpin.docean.plugin.mybatis.bo;

import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.mapping.ResultMapping;

import java.io.Serializable;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 15:32
 */
@Data
@Builder
public class QueryParam implements Serializable {

    private String sql;

    private String[] params;

    private List<ResultMapping> mappings;

    private  Class<?> type;

    private String dsName;

}
