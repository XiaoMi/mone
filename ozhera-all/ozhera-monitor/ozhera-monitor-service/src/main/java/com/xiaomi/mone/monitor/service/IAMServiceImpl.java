package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.service.api.IAMService;
import com.xiaomi.mone.tpc.common.param.NodeQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 6:44 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class IAMServiceImpl implements IAMService {
    @Override
    public List<NodeVo> list(NodeQryParam nodeQryParam) {
        return null;
    }
}
