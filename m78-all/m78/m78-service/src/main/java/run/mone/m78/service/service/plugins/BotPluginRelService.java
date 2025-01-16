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
package run.mone.m78.service.service.plugins;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.plugins.BotPluginOrgDTO;
import run.mone.m78.service.dao.entity.M78BotPluginRel;
import run.mone.m78.service.dao.entity.M78PluginRelCount;
import run.mone.m78.service.dao.mapper.M78BotPluginRelMapper;

import java.util.List;

@Service
@Slf4j

/**
 * BotPluginRelService类负责处理与M78BotPluginRel实体相关的业务逻辑。
 * 该类继承自ServiceImpl，并使用M78BotPluginRelMapper进行数据库操作。
 * 主要功能包括设置组织关联计数和生成SQL中的in子句。
 *
 * 注解：
 * - @Service：标识该类为Spring的服务组件。
 * - @Slf4j：提供日志记录功能。
 *
 * 依赖：
 * - M78BotPluginRelMapper：用于执行数据库操作的Mapper接口。
 *
 * 方法：
 * - setOrgRelCount(List<BotPluginOrgDTO> dtos)：设置组织关联计数。
 * - getIds(List<BotPluginOrgDTO> dtos)：生成SQL中的in子句。
 */

public class BotPluginRelService extends ServiceImpl<M78BotPluginRelMapper, M78BotPluginRel> {

    @Autowired
    private M78BotPluginRelMapper mapper;

    /**
     * 设置组织关联计数
     *
     * @param dtos 包含组织信息的列表
     */
    public void setOrgRelCount(List<BotPluginOrgDTO> dtos) {
        String ids = getIds(dtos);
        List<M78PluginRelCount> orgRelCount = mapper.getOrgRelCount(ids);
        for (M78PluginRelCount relCount : orgRelCount) {
            for (BotPluginOrgDTO dto : dtos) {
                if (dto.getId().equals(relCount.getOrg_id())) {
                    dto.setBotRefCnt(relCount.getCount());
                }
            }
        }
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
