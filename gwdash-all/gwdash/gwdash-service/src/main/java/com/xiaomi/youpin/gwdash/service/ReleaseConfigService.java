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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.dao.model.ReleaseConfigBo;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReleaseConfigService {

    @Autowired
    private Dao dao;

    public ReleaseConfigBo getConfig(int type, long projectId) {
        return dao.fetch(ReleaseConfigBo.class, Cnd.where("type", "=", type).and("project_id", "=", projectId));
    }

    public void updateConfig(int type, long projectId, int count) {
        long now = System.currentTimeMillis();
        ReleaseConfigBo releaseConfigBo = getConfig(type, projectId);
        if (null != releaseConfigBo) {
            releaseConfigBo.setCount(count);
            releaseConfigBo.setUtime(now);
        } else {
            releaseConfigBo = new ReleaseConfigBo();
            releaseConfigBo.setType(type);
            releaseConfigBo.setProjectId(projectId);
            releaseConfigBo.setCount(count);
            releaseConfigBo.setCtime(now);
            releaseConfigBo.setUtime(now);
        }
        dao.insertOrUpdate(releaseConfigBo);
    }
}
