package com.xiaomi.mone.tpc.permission.util;

import com.xiaomi.mone.tpc.dao.entity.PermissionEntity;
import com.xiaomi.mone.tpc.common.vo.PermissionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class PermissionUtil {

    public static List<PermissionVo> toVoList(List<PermissionEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<PermissionVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static PermissionVo toVo(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        PermissionVo vo = new PermissionVo();
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
