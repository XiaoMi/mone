package com.xiaomi.mone.tpc.node.util;

import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
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
public class NodeUserRelUtil {

    public static List<NodeUserRelVo> toVoList(List<NodeUserRelEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<NodeUserRelVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static NodeUserRelVo toVo(NodeUserRelEntity entity) {
        if (entity == null) {
            return null;
        }
        NodeUserRelVo vo = new NodeUserRelVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }

    public static List<NodeUserRelEntity> toEntityList(List<NodeUserRelVo> vos) {
        if ( CollectionUtils.isEmpty(vos)) {
            return null;
        }
        List<NodeUserRelEntity> entityList = new ArrayList<>(vos.size());
        vos.stream().forEach(vo->entityList.add(toEntity(vo)));
        return entityList;
    }

    public static NodeUserRelEntity toEntity(NodeUserRelVo vo) {
        if (vo == null) {
            return null;
        }
        NodeUserRelEntity entity = new NodeUserRelEntity();
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
