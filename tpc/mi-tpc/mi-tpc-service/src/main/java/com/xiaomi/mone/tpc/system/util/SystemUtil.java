package com.xiaomi.mone.tpc.system.util;

import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.common.vo.SystemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class SystemUtil {

    public static List<SystemVo> toVoList(List<SystemEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<SystemVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static SystemVo toVo(SystemEntity entity) {
        if (entity == null) {
            return null;
        }
        SystemVo vo = new SystemVo();
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
