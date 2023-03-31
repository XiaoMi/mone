package com.xiaomi.mone.tpc.notify;

import com.xiaomi.mone.tpc.common.enums.ApplyStatusEnum;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/6 17:39
 */
@Slf4j
@Component("notifyHelper")
@ConditionalOnExpression("'${notify.type}'.equals('default')")
public class DefaultNotifyHelper extends NotifyHelper {

    @Override
    @Async("notifyExecutor")
    public void sendApply(NodeUserRelEntity relEntity, String nodeName, ApplyEntity applyEntity) {
            StringBuilder content = new StringBuilder();
            content.append("工单名称: ").append(applyEntity.getApplyName()).append("\n\n");
            content.append("当前节点: ").append(nodeName).append("\n\n");
            if (StringUtils.isNotBlank(applyEntity.getDesc())) {
                content.append("描述: ").append(applyEntity.getDesc()).append("\n\n");
            }
            content.append("申请人: ").append(applyEntity.getApplyAccount());
            log.info("申请工单消息: {}", content.toString());
    }

    @Override
    @Async("notifyExecutor")
    public void sendApplyResult(ApplyEntity applyEntity) {
        StringBuilder content = new StringBuilder();
        content.append("工单名称: ").append(applyEntity.getApplyName()).append("\n\n");
        if (StringUtils.isNotBlank(applyEntity.getDesc())) {
            content.append("描述: ").append(applyEntity.getDesc()).append("\n\n");
        }
        content.append("申请人: ").append(applyEntity.getApplyAccount()).append("\n\n");
        if (ApplyStatusEnum.FINSH.getCode().equals(applyEntity.getStatus())) {
            content.append("状态: ").append("审核通过");
        } else if (ApplyStatusEnum.GOING.getCode().equals(applyEntity.getStatus())) {
            content.append("状态: ").append("审核中，请留意结果通知");
        } else {
            content.append("状态: ").append("驳回");
        }
        log.info("审批工单消息: {}", content.toString());
    }

}
