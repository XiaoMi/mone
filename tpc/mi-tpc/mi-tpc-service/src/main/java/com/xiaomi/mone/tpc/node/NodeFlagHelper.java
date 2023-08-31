package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.param.FlagAddParam;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.ResultVo;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
public interface NodeFlagHelper {

    ResultVo<FlagVo> realAdd(FlagAddParam param, NodeVo parentNode);

    FlagVo getFirstOneByParentId(Long parentId, Integer type);

}
