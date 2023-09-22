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
package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDO;
import com.xiaomi.youpin.docean.anno.Service;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MilogLogProcessDao {
    @Resource
    private NutDao dao;

    /**
     * Get the log details monitored by the agent
     *
     * @param agentId
     * @return
     */
    public List<MilogLogProcessDO> queryByAgentId(long agentId) {
        return dao.query(MilogLogProcessDO.class, Cnd.where("agent_id", "IN", agentId));
    }

    public List<MilogLogProcessDO> queryByIp(String ip) {
        return dao.query(MilogLogProcessDO.class, Cnd.where("ip", "=", ip));
    }

    /**
     * Save in batches
     *
     * @param doList
     */
    public List<MilogLogProcessDO> bulkSave(List<MilogLogProcessDO> doList) {
        return dao.insert(doList);
    }

    /**
     * delete log collection progress
     *
     * @param tailId
     */
    public void deleteByTailId(Long tailId) {
        dao.clear(MilogLogProcessDO.class, Cnd.where("tailId", "=", tailId));
    }

}
