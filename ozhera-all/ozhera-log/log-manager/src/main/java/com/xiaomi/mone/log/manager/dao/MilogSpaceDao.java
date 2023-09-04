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

import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.convert.MilogSpaceConvert;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.log.manager.user.MoneUser;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

@Service
public class MilogSpaceDao {

    @Resource
    private NutDao dao;

    /**
     * 新建
     *
     * @param ms
     * @return
     */
    public MilogSpaceDO newMilogSpace(MilogSpaceDO ms) {
        MilogSpaceDO ret = dao.insert(ms);
        return ret;
    }

    public MilogSpaceDO insert(MilogSpaceDO spaceDO) {
        return dao.insert(spaceDO);
    }

    public MilogSpaceDO queryById(Long id) {
        return dao.fetch(MilogSpaceDO.class, id);
    }

    public List<MilogSpaceDO> queryBySpaceName(String spaceName) {
        return dao.query(MilogSpaceDO.class, Cnd.where("space_name", EQUAL_OPERATE, spaceName));
    }

    /**
     * 更新
     *
     * @param id
     * @param tenantId
     * @param spaceName
     * @param description
     * @return
     */
    public boolean updateMilogSPace(Long id, Long tenantId, String spaceName, String description) {
        Chain chain = Chain.make("tenant_id", tenantId).add("space_name", spaceName).add("description", description);
        chain.add("utime", Instant.now().toEpochMilli());
        int ret = dao.update(MilogSpaceDO.class, chain, Cnd.where("id", "=", id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean update(MilogSpaceDO milogSpace) {
        return dao.update(milogSpace) == 1;
    }

    /**
     * 删除
     *
     * @param id
     */
    public boolean deleteMilogSpace(Long id) {
        int ret = dao.clear(MilogSpaceDO.class, Cnd.where("id", "=", id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 查询
     *
     * @param ids
     * @return
     */
    public List<MilogSpaceDO> getSpaceByIdList(List<Long> ids) {
        return dao.query(MilogSpaceDO.class, Cnd.where("id", "in", ids));
    }

    public List<MilogSpaceDO> getMilogSpaces(String limitDeptId) {
        Cnd cnd = Cnd.NEW();
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        if (!currentUser.getIsAdmin() && StringUtils.isNotEmpty(currentUser.getZone())) {
            cnd.and("perm_dept_id", "like", "%" + limitDeptId + "%").or("creator", "=", "system");
        }
        cnd.orderBy("ctime", "desc");
        return dao.query(MilogSpaceDO.class, cnd);
    }

    public MilogSpaceDO getMilogSpaceById(Long id) {
        List<MilogSpaceDO> milogSpaces = dao.query(MilogSpaceDO.class, Cnd.where("id", "=", id));
        if (CollectionUtils.isNotEmpty(milogSpaces)) {
            return milogSpaces.get(milogSpaces.size() - 1);
        }
        return null;
    }

    public boolean verifyExistByName(String spaceName) {
        int count = dao.count(MilogSpaceDO.class, Cnd.where("space_name", "=", spaceName));
        return count > 0;
    }

    public boolean verifyExistByName(String spaceName, Long id) {
        List<MilogSpaceDO> ret = dao.query(MilogSpaceDO.class, Cnd.where("space_name", "=", spaceName));
        for (int i = 0; i < ret.size(); i++) {
            if (!ret.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getMilogSpaceByPage(String spaceName, List<Long> permSpaceIdList, int page, int pagesize) {
        Cnd cnd;
        if (StringUtils.isNotEmpty(spaceName)) {
            cnd = Cnd.where("space_name", "like", "%" + spaceName + "%");
        } else {
            cnd = Cnd.NEW();
        }
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        if (!currentUser.getIsAdmin() && StringUtils.isNotEmpty(currentUser.getZone())) {
            cnd.and("id", "in", permSpaceIdList).or("creator", "=", "system");
        }
        List<MilogSpaceDO> ret = dao.query(MilogSpaceDO.class, cnd.orderBy("ctime", "desc"), new Pager(page, pagesize));
        List<MilogSpaceDTO> dtoList = MilogSpaceConvert.INSTANCE.fromDOList(ret);
        Map<String, Object> result = new HashMap<>();
        result.put("list", dtoList);
        result.put("total", dao.count(MilogSpaceDO.class, cnd));
        result.put("page", page);
        result.put("pageSize", pagesize);
        return result;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<MilogSpaceDO> getAll() {
        return dao.query(MilogSpaceDO.class, null);
    }

    public List<MilogSpaceDO> queryByIds(List<Long> spaceIds) {
        Cnd cnd = Cnd.where("id", "in", spaceIds);
        return dao.query(MilogSpaceDO.class, cnd);
    }
}
