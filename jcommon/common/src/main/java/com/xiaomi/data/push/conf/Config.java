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

package com.xiaomi.data.push.conf;


//import com.xiaomi.data.push.annotation.Cache;
import com.xiaomi.data.push.dao.mapper.ConfMapper;
import com.xiaomi.data.push.dao.model.Conf;
import com.xiaomi.data.push.dao.model.ConfExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Component
public class Config {

    @Autowired
    private ConfMapper confMapper;

    /**
     * 根据key 获取 value 配置
     *
     * @param key
     * @return
     */
//    @Cache(paramIndex = {"0"})
    public String getValue(String key,String defaultValue) {
        ConfExample example = new ConfExample();
        example.createCriteria().andConfKeyEqualTo(key);
        List<Conf> list = confMapper.selectByExampleWithBLOBs(example);
        if (list.size() > 0) {
            return list.get(0).getConfValue();
        }
        return defaultValue;
    }

}
