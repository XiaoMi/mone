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

package com.xiaomi.mone.log.stream.sink;

import com.xiaomi.youpin.docean.anno.Component;
import lombok.Data;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/20 10:09
 */
@Component
@Data
public class SinkChain {

    @Resource
    private TeslaSink teslaSink;


    public boolean execute(Map<String, Object> map) {
        return teslaSink.execute(map);
    }

}
