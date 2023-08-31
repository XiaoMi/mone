package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.param.BaseParam;
import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.param.OrgInfoParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.impl.FlagDao;
import com.xiaomi.mone.tpc.org.OrgHelper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 16:19
 */
@Slf4j
@Service
public class NodeOrgService implements NodeOrgHelper {

    @Autowired
    private OrgHelper orgHelper;
    @Autowired
    private FlagDao flagDao;

    @Override
    public ResultVo<PageDataVo<OrgInfoVo>> list(NodeOrgQryParam param) {
        return orgHelper.list(param);
    }

    @Override
    public ResultVo updateNodeOrg(BaseParam param, OrgInfoParam orgParam, Long nodeId) {
        //需要更新组织信息
        List<FlagEntity> flagEntities = orgHelper.buildNodeOrgs(param, orgParam);
        if (!CollectionUtils.isEmpty(flagEntities)) {
            flagEntities.stream().forEach(flagEntity -> flagEntity.setParentId(nodeId));
            try {
                Trans.exec(new Atom() {
                    @Override
                    public void run() {
                        boolean result = flagDao.deleteByNodeId(nodeId, FlagTypeEnum.ORG.getCode());
                        if (!result) {
                            throw new RuntimeException("节点编辑删除部门信息失败");
                        }
                        result = flagDao.deleteByNodeId(nodeId, FlagTypeEnum.FULL_ORG.getCode());
                        if (!result) {
                            throw new RuntimeException("节点编辑删除部门信息失败");
                        }
                        flagDao.batchInsertWithException(flagEntities);
                    }
                });
            } catch (Throwable e) {
                log.error("节点{}删除组织信息失败", nodeId, e);
                return ResponseCode.OPER_FAIL.build();
            }
        }
        return ResponseCode.SUCCESS.build();
    }

}
