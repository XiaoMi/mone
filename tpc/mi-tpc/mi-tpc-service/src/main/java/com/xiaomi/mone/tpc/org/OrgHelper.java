package com.xiaomi.mone.tpc.org;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.BaseParam;
import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.param.OrgInfoParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.impl.FlagDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
@Slf4j
public abstract class OrgHelper {

    @Autowired
    private FlagDao flagDao;

    /**
     * 部门信息查询
     * @param param
     * @return
     */
    public abstract ResultVo<PageDataVo<OrgInfoVo>> list(NodeOrgQryParam param);

    /**
     * 部门信息查询
     * @param account
     * @return
     */
    public abstract OrgInfoVo get(String account);

    /**
     * 构建组织信息
     * @param param
     * @return
     */
    public List<FlagEntity> buildNodeOrgs(BaseParam param, OrgInfoParam orgParam) {
        String[] ids = null;
        String[] names = null;
        if (orgParam != null) {
            ids = orgParam.getIds();
            names = orgParam.getNames();
        } else {
            if (!UserTypeEnum.CAS_TYPE.getCode().equals(param.getUserType())) {
                return null;
            }
            OrgInfoVo orgVo = this.get(param.getAccount());
            if (orgVo == null) {
                return null;
            }
            ids = orgVo.getIdPath().split("\\/");
            names = orgVo.getNamePath().split("\\/");
        }
        if (ids.length != names.length) {
            return null;
        }
        List<FlagEntity> flagEntities = Lists.newArrayList();
        int idx = 0;
        StringBuilder fullId = new StringBuilder();
        StringBuilder fullName = new StringBuilder();
        for (String id : ids) {
            FlagEntity flagEntity = new FlagEntity();
            flagEntity.setType(FlagTypeEnum.ORG.getCode());
            flagEntity.setCreaterId(param.getUserId());
            flagEntity.setCreaterAcc(param.getAccount());
            flagEntity.setCreaterType(param.getUserType());
            flagEntity.setUpdaterId(param.getUserId());
            flagEntity.setUpdaterAcc(param.getAccount());
            flagEntity.setUpdaterType(param.getUserType());
            //ID
            flagEntity.setFlagKey(id);
            //名称
            flagEntity.setFlagName(names[idx]);
            //等级
            flagEntity.setFlagVal(String.valueOf(idx));
            flagEntities.add(flagEntity);
            fullId.append(id).append("/");
            fullName.append(names[idx]).append("/");
            idx++;
        }
        FlagEntity flagEntity = new FlagEntity();
        flagEntity.setType(FlagTypeEnum.FULL_ORG.getCode());
        flagEntity.setCreaterId(param.getUserId());
        flagEntity.setCreaterAcc(param.getAccount());
        flagEntity.setCreaterType(param.getUserType());
        flagEntity.setUpdaterId(param.getUserId());
        flagEntity.setUpdaterAcc(param.getAccount());
        flagEntity.setUpdaterType(param.getUserType());
        //ID
        flagEntity.setFlagKey(fullId.substring(0, fullId.length() - 1));
        //名称
        flagEntity.setFlagName(fullName.substring(0, fullName.length() - 1));
        //等级
        flagEntity.setFlagVal(String.valueOf(0));
        flagEntities.add(flagEntity);
        return flagEntities;
    }


    /**
     * 通过父节点组织信息构建组织信息
     * @param param
     * @param parentId
     * @return
     */
    public List<FlagEntity> buildNodeOrgs(BaseParam param, Long parentId) {
        List<FlagEntity> flagList  = flagDao.getListByNodeId(parentId, FlagTypeEnum.ORG.getCode());
        if (CollectionUtils.isEmpty(flagList)) {
            return null;
        }
        for (FlagEntity flag : flagList) {
            flag.setId(null);
            flag.setCreaterId(param.getUserId());
            flag.setCreaterAcc(param.getAccount());
            flag.setCreaterType(param.getUserType());
            flag.setUpdaterId(param.getUserId());
            flag.setUpdaterAcc(param.getAccount());
            flag.setUpdaterType(param.getUserType());
            flag.setParentId(null);
            flag.setCreateTime(null);
            flag.setUpdateTime(null);
            flag.setDeleted(null);
        }
        return flagList;
    }

}
