package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.convert.SpaceConvert;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogSpaceDO;
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
public class SpaceDao {

    @Resource
    private NutDao dao;

    /**
     * 新建
     *
     * @param ms
     * @return
     */
    public LogSpaceDO newMilogSpace(LogSpaceDO ms) {
        LogSpaceDO ret = dao.insert(ms);
        return ret;
    }

    public LogSpaceDO insert(LogSpaceDO spaceDO) {
        return dao.insert(spaceDO);
    }

    public LogSpaceDO queryById(Long id) {
        return dao.fetch(LogSpaceDO.class, id);
    }

    public List<LogSpaceDO> queryBySpaceName(String spaceName) {
        return dao.query(LogSpaceDO.class, Cnd.where("space_name", EQUAL_OPERATE, spaceName));
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
        int ret = dao.update(LogSpaceDO.class, chain, Cnd.where("id", "=", id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean update(LogSpaceDO milogSpace) {
        return dao.update(milogSpace) == 1;
    }

    /**
     * 删除
     *
     * @param id
     */
    public boolean deleteMilogSpace(Long id) {
        int ret = dao.clear(LogSpaceDO.class, Cnd.where("id", "=", id));
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
    public List<LogSpaceDO> getSpaceByIdList(List<Long> ids) {
        return dao.query(LogSpaceDO.class, Cnd.where("id", "in", ids));
    }

    public List<LogSpaceDO> getMilogSpaces(String limitDeptId) {
        Cnd cnd = Cnd.NEW();
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        if (!currentUser.getIsAdmin() && StringUtils.isNotEmpty(currentUser.getZone())) {
            cnd.and("perm_dept_id", "like", "%" + limitDeptId + "%").or("creator", "=", "system");
        }
        cnd.orderBy("ctime", "desc");
        return dao.query(LogSpaceDO.class, cnd);
    }

    public LogSpaceDO getMilogSpaceById(Long id) {
        List<LogSpaceDO> milogSpaces = dao.query(LogSpaceDO.class, Cnd.where("id", "=", id));
        if (CollectionUtils.isNotEmpty(milogSpaces)) {
            return milogSpaces.get(milogSpaces.size() - 1);
        }
        return null;
    }

    public boolean verifyExistByName(String spaceName) {
        int count = dao.count(LogSpaceDO.class, Cnd.where("space_name", "=", spaceName));
        return count > 0;
    }

    public boolean verifyExistByName(String spaceName, Long id) {
        List<LogSpaceDO> ret = dao.query(LogSpaceDO.class, Cnd.where("space_name", "=", spaceName));
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
        List<LogSpaceDO> ret = dao.query(LogSpaceDO.class, cnd.orderBy("ctime", "desc"), new Pager(page, pagesize));
        List<MilogSpaceDTO> dtoList = SpaceConvert.INSTANCE.fromDOList(ret);
        Map<String, Object> result = new HashMap<>();
        result.put("list", dtoList);
        result.put("total", dao.count(LogSpaceDO.class, cnd));
        result.put("page", page);
        result.put("pageSize", pagesize);
        return result;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<LogSpaceDO> getAll() {
        return dao.query(LogSpaceDO.class, null);
    }

    public List<LogSpaceDO> queryByIds(List<Long> spaceIds) {
        Cnd cnd = Cnd.where("id", "in", spaceIds);
        return dao.query(LogSpaceDO.class, cnd);
    }
}
