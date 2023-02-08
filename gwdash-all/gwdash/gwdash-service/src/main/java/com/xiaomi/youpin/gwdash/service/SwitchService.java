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

import com.xiaomi.youpin.gwdash.bo.SwitchBo;
import com.xiaomi.youpin.gwdash.dao.model.SwitchEntity;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwitchService {

    @Autowired
    private Dao dao;

    public SwitchEntity update(SwitchBo switchBo) {
        return dao.insertOrUpdate(toEntity(switchBo));
    }

    public SwitchBo getConfig() {
        return toSwitchBo(dao.fetch(SwitchEntity.class));
    }

    private SwitchBo toSwitchBo(SwitchEntity se) {
        SwitchBo switchBo = new SwitchBo();
        if (null != se) {
            switchBo.setId(se.getId());
            switchBo.setRelease(se.isRelease());
        }
        return switchBo;
    }

    private SwitchEntity toEntity(SwitchBo switchBo) {
        long now = System.currentTimeMillis();
        SwitchEntity se = new SwitchEntity();
        se.setId(switchBo.getId());
        se.setRelease(switchBo.isRelease());
        se.setCtime(now);
        se.setUtime(now);
        return se;
    }
}
