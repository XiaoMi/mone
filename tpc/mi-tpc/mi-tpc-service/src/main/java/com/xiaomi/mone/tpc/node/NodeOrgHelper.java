package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.param.BaseParam;
import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.param.OrgInfoParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 17:08
 */
public interface NodeOrgHelper {

    ResultVo<PageDataVo<OrgInfoVo>> list(NodeOrgQryParam param);

    ResultVo updateNodeOrg(BaseParam param, OrgInfoParam orgParam, Long nodeId);
}
