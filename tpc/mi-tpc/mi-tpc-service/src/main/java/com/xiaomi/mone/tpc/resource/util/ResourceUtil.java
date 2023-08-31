package com.xiaomi.mone.tpc.resource.util;

import com.xiaomi.mone.tpc.dao.entity.ResourceEntity;
import com.xiaomi.mone.tpc.common.vo.ResourceVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class ResourceUtil {

    public static List<ResourceVo> toVoList(List<ResourceEntity> entitys, boolean content) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<ResourceVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e, content)));
        return voList;
    }

    public static ResourceVo toVo(ResourceEntity entity, boolean content) {
        if (entity == null) {
            return null;
        }
        ResourceVo vo = new ResourceVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        if (!content) {
            vo.setContent(null);
        }
        return vo;
    }
}
