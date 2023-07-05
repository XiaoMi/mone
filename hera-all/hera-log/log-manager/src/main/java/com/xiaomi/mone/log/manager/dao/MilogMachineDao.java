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

import com.xiaomi.mone.log.api.enums.AppTypeEnum;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.model.bo.MachineQueryParam;
import com.xiaomi.mone.log.manager.model.pojo.MiLogMachine;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.pager.Pager;

import javax.annotation.Resource;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 11:45
 */
@Service
public class MilogMachineDao {

    @Resource
    private NutDao dao;

    public List<MiLogMachine> queryMachineByType(AppTypeEnum appTypeEnum) {
        return dao.query(MiLogMachine.class, Cnd.where("type", EQUAL_OPERATE, appTypeEnum.getType()));
    }

    public MiLogMachine insert(MiLogMachine miLogMachine) {
        return dao.insert(miLogMachine);
    }

    public void deleteMachineInfo(Long id) {
        dao.delete(MiLogMachine.class, id);
    }

    public List<MiLogMachine> queryMachinePage(MachineQueryParam param) {
        return dao.query(MiLogMachine.class, assembleParam(param).orderBy("utime", "desc"),
                new Pager(param.getPageNum(), param.getPageSize()));
    }

    public Integer queryMachinePageCount(MachineQueryParam param) {

        return dao.count(MilogSpaceDO.class, assembleParam(param));
    }

    private Cnd assembleParam(MachineQueryParam param) {
        Cnd cnd = Cnd.NEW();
        if (null != param.getId()) {
            cnd.where().and("space_name", EQUAL_OPERATE, param.getId());
        }
        if (null != param.getType()) {
            cnd.where().and("type", EQUAL_OPERATE, param.getType());
        }
        if (StringUtils.isNotEmpty(param.getIp())) {
            cnd.where().and("ip", EQUAL_OPERATE, param.getIp());
        }
        return cnd;
    }

    public MiLogMachine queryById(Long id) {
        Cnd cnd = Cnd.where("id", Constant.EQUAL_OPERATE, id);
        List<MiLogMachine> miLogMachines = dao.query(MiLogMachine.class, cnd);
        if (CollectionUtils.isNotEmpty(miLogMachines)) {
            return miLogMachines.get(miLogMachines.size() - 1);
        }
        return null;
    }

    public void executeSql(String sql) {
        dao.execute(new NutSql(sql));
    }
}
