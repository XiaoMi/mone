package com.xiaomi.mone.tpc.node.util;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class NodeUtil {

    public static List<NodeVo> toVoList(List<NodeEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<NodeVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static NodeVo toVo(NodeEntity entity) {
        if (entity == null) {
            return null;
        }
        NodeVo vo = new NodeVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        if (StringUtils.isNotBlank(entity.getEnv())) {
            vo.setEnv(GsonUtil.gsonToBean(entity.getEnv(), new TypeToken<Map<String,String>>(){}));
        }
        return vo;
    }

    public static NodeEntity toEntity(NodeVo vo) {
        if (vo == null) {
            return null;
        }
        NodeEntity entity = new NodeEntity();
        BeanUtils.copyProperties(vo, entity);
        if (vo.getCreateTime() != null) {
            entity.setCreateTime(new Date(vo.getCreateTime()));
        }
        if (vo.getUpdateTime() != null) {
            entity.setUpdateTime(new Date(vo.getUpdateTime()));
        }
        if (vo.getEnv() != null) {
            entity.setEnv(GsonUtil.gsonString(vo.getEnv()));
        }
        return entity;
    }
}
