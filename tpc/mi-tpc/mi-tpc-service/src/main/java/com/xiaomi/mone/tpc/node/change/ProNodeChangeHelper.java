package com.xiaomi.mone.tpc.node.change;

import com.xiaomi.mone.tpc.common.enums.NodeChangeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 项目节点变更
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
@Slf4j
public abstract class ProNodeChangeHelper {

    @Resource(name = "nodeChangeExecutor")
    private ThreadPoolExecutor nodeChangeExecutor;

    public void change(NodeChangeEnum nodeChange, NodeVo nodeVo) {
        if (!NodeTypeEnum.PRO_TYPE.getCode().equals(nodeVo.getType())) {
            return;
        }
        if (isDefault()) {
            return;
        }
        nodeChangeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    realChange(nodeChange, nodeVo);
                } catch (Throwable e) {
                    log.error("节点变更通知异常; nodeVo={}", nodeVo, e);
                }
            }
        });
    }

    protected boolean isDefault() {
        return true;
    }

    abstract void realChange(NodeChangeEnum nodeChange, NodeVo nodeVo) throws Throwable;


}
