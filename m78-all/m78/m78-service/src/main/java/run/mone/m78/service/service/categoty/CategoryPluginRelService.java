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
package run.mone.m78.service.service.categoty;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.plugins.BotPluginOrgDTO;
import run.mone.m78.service.dao.entity.M78CategoryPluginRel;
import run.mone.m78.service.dao.entity.M78CategoryPluginRelName;
import run.mone.m78.service.dao.mapper.M78CategoryPluginRelMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

/**
 * CategoryPluginRelService类负责管理插件与类别之间的关系。
 * 该类继承自ServiceImpl，并提供了以下主要功能：
 * 1. 设置插件类别关系：根据传入的插件信息列表，设置每个插件对应的类别。
 * 2. 根据分类ID获取未删除的插件类别关系列表。
 * 3. 辅助方法用于生成SQL查询中的ID列表字符串。
 *
 * 该类使用了Spring的@Service注解和Lombok的@Slf4j注解。
 */

public class CategoryPluginRelService extends ServiceImpl<M78CategoryPluginRelMapper, M78CategoryPluginRel> {

    /**
     * 设置插件类别关系
     *
     * @param dtos 包含插件信息的列表
     */
    public void setCategoryPluginRel(List<BotPluginOrgDTO> dtos) {
        String ids = getIds(dtos);
        List<M78CategoryPluginRelName> categoryByPlugins = mapper.getCategoryByPlugins(ids);
        for (BotPluginOrgDTO org : dtos) {
            for (M78CategoryPluginRelName rel : categoryByPlugins) {
                if (org.getId() == rel.getPluginId()) {
                    List<String> categories = org.getPluginCategory();
                    if (categories == null) {
                        categories = new ArrayList<>();
                        categories.add(rel.getName());
                        org.setPluginCategory(categories);
                    } else {
                        categories.add(rel.getName());
                    }
                }
            }
        }
    }

    /**
     * 根据分类ID获取未删除的M78CategoryPluginRel列表
     *
     * @param catId 分类ID
     * @return 未删除的M78CategoryPluginRel列表
     */
    public List<M78CategoryPluginRel> getByCatId(Long catId) {
        return mapper.selectListByQuery(QueryWrapper.create().eq("cat_id", catId).eq("deleted", 0));

    }

    /**
     * 拼成SQL中的in，最终是(1,2,3)格式
     *
     * @param dtos
     * @return
     */
    private String getIds(List<BotPluginOrgDTO> dtos) {
        StringBuilder sb = new StringBuilder("(");
        for (BotPluginOrgDTO dot : dtos) {
            sb.append(dot.getId()).append(",");
        }
        String result = sb.substring(0, sb.length() - 1) + ")";
        return result;
    }


}
