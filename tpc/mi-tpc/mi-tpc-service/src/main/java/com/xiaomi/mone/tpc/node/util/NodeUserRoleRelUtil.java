package com.xiaomi.mone.tpc.node.util;

import com.xiaomi.mone.tpc.dao.entity.UserNodeRoleRelEntity;
import com.xiaomi.mone.tpc.common.vo.UserNodeRoleRelVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class NodeUserRoleRelUtil {

    public static List<UserNodeRoleRelVo> toVoList(List<UserNodeRoleRelEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<UserNodeRoleRelVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static UserNodeRoleRelVo toVo(UserNodeRoleRelEntity entity) {
        if (entity == null) {
            return null;
        }
        UserNodeRoleRelVo vo = new UserNodeRoleRelVo();
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
