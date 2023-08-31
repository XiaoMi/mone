package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.param.BaseParam;
import com.xiaomi.mone.tpc.common.param.NodeUserAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserEditParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
public interface NodeUserHelper {

    ResultVo add(boolean force, NodeUserAddParam param);

    ResultVo<List<NodeUserRelVo>> updateProjectMember(BaseParam param, List<NodeUserRelVo> relVos, NodeVo nodeVo);

    ResultVo edit(boolean force, NodeUserEditParam param);

    List<NodeUserRelVo> list(NodeVo nodeVo);

}