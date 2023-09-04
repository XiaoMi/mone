package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.tpc.common.param.NodeQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeVo;

import java.util.List;

public interface IAMService {

    List<NodeVo> list(NodeQryParam nodeQryParam);
}
