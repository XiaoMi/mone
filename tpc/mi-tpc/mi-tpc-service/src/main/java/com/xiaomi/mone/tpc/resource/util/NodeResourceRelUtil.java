package com.xiaomi.mone.tpc.resource.util;

import com.xiaomi.mone.tpc.common.vo.NodeResourceRelVo;
import com.xiaomi.mone.tpc.common.vo.ResourceVo;
import com.xiaomi.mone.tpc.dao.entity.NodeResourceRelEntity;
import com.xiaomi.mone.tpc.dao.entity.ResourceEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei
 * @date: 2022/3/25
 */
public class NodeResourceRelUtil {

    public static List<NodeResourceRelVo> toVoList(List<NodeResourceRelEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<NodeResourceRelVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static NodeResourceRelVo toVo(NodeResourceRelEntity entity) {
        if (entity == null) {
            return null;
        }
        NodeResourceRelVo vo = new NodeResourceRelVo();
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
