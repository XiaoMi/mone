package com.xiaomi.mone.tpc.node.util;

import com.xiaomi.mone.tpc.common.vo.NodeUserGroupRelVo;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.dao.entity.NodeUserGroupRelEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class NodeUserGroupRelUtil {

    public static List<NodeUserGroupRelVo> toVoList(List<NodeUserGroupRelEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<NodeUserGroupRelVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static NodeUserGroupRelVo toVo(NodeUserGroupRelEntity entity) {
        if (entity == null) {
            return null;
        }
        NodeUserGroupRelVo vo = new NodeUserGroupRelVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }

    public static List<NodeUserGroupRelEntity> toEntityList(List<NodeUserGroupRelVo> vos) {
        if ( CollectionUtils.isEmpty(vos)) {
            return null;
        }
        List<NodeUserGroupRelEntity> entityList = new ArrayList<>(vos.size());
        vos.stream().forEach(vo->entityList.add(toEntity(vo)));
        return entityList;
    }

    public static NodeUserGroupRelEntity toEntity(NodeUserGroupRelVo vo) {
        if (vo == null) {
            return null;
        }
        NodeUserGroupRelEntity entity = new NodeUserGroupRelEntity();
        BeanUtils.copyProperties(vo, entity);
        if (vo.getCreateTime() != null) {
            entity.setCreateTime(new Date(vo.getCreateTime()));
        }
        if (vo.getUpdateTime() != null) {
            entity.setUpdateTime(new Date(vo.getUpdateTime()));
        }
        return entity;
    }
}
