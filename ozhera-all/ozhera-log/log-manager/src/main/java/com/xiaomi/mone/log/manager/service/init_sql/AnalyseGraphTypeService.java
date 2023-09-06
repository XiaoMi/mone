/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.init_sql;

import com.google.common.collect.Maps;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseGraphTypeMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogAnalyseGraphTypeDO;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COLON;

/**
 * @author wtt
 * @version 1.0
 * @description Log analysis graph type initialization
 * @date 2023/3/31 10:32
 */
@Service
public class AnalyseGraphTypeService {

    @Resource
    private MilogAnalyseGraphTypeMapper graphTypeMapper;

    private static Map<Integer, String> namesMap = Maps.newHashMap();

    static {
        namesMap.put(1, String.format("%s%s%s", "饼图", SYMBOL_COLON, "比例"));
        namesMap.put(2, String.format("%s%s%s", "折线图", SYMBOL_COLON, "折线图和面积图"));
        namesMap.put(3, String.format("%s%s%s", "垂直条形图", SYMBOL_COLON, "条形图"));

        namesMap.put(4, String.format("%s%s%s", "水平条形图", SYMBOL_COLON, "条形图"));
        namesMap.put(5, String.format("%s%s%s", "垂直条形图", SYMBOL_COLON, "折线图和面积图"));
        namesMap.put(8, String.format("%s%s%s", "圆环图", SYMBOL_COLON, "比例"));

        namesMap.put(9, String.format("%s%s%s", "南丁格尔玫瑰图", SYMBOL_COLON, "比例"));
        //没上线
//        namesMap.put(10, String.format("%s%s%s", "分时柱状图", SYMBOL_COLON, "分时柱状图"));
    }

    public void init() {
        List<MilogAnalyseGraphTypeDO> graphTypeDOList = graphTypeMapper.selectList(null);
        if (CollectionUtils.isEmpty(graphTypeDOList)) {
            for (Map.Entry<Integer, String> entry : namesMap.entrySet()) {
                MilogAnalyseGraphTypeDO logAnalyseGraphType = new MilogAnalyseGraphTypeDO();
                logAnalyseGraphType.setType(entry.getKey());
                String[] nameClassifiers = entry.getValue().split(SYMBOL_COLON);
                logAnalyseGraphType.setName(nameClassifiers[0]);
                logAnalyseGraphType.setCalculate(nameClassifiers[1]);
                graphTypeMapper.insert(logAnalyseGraphType);
            }
        }
    }
}
