package com.xiaomi.mone.tpc.apply.util;

import com.xiaomi.mone.tpc.common.vo.ApplyApprovalVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class ApprovalUtil {

    public static List<ApplyApprovalVo> toVoList(List<ApplyApprovalEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<ApplyApprovalVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static ApplyApprovalVo toVo(ApplyApprovalEntity entity) {
        if (entity == null) {
            return null;
        }
        ApplyApprovalVo vo = new ApplyApprovalVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }
}
