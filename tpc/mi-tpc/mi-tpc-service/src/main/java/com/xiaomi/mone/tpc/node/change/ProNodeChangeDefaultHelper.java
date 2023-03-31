package com.xiaomi.mone.tpc.node.change;

import com.xiaomi.mone.tpc.common.enums.NodeChangeEnum;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
@Slf4j
@Component("proNodeChangeDefaultHelper")
@ConditionalOnExpression("'${project.node.change}'.equals('default')")
public class ProNodeChangeDefaultHelper extends ProNodeChangeHelper {

    @Override
    void realChange(NodeChangeEnum nodeChange, NodeVo nodeVo) throws Throwable {
        /**
         * 不需要实现，默认实现类，不会调用到
         */
    }
}
