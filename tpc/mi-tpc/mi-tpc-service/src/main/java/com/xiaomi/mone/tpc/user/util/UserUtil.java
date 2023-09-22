package com.xiaomi.mone.tpc.user.util;

import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class UserUtil {

    public static List<UserVo> toVoList(List<UserEntity> entitys, boolean isFront) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<UserVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e, isFront)));
        return voList;
    }

    public static UserVo toVo(UserEntity entity, boolean isFront) {
        if (entity == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        StringBuilder showAccount = new StringBuilder();
        showAccount.append(entity.getAccount());
        UserTypeEnum typeEnum = UserTypeEnum.getEnum(entity.getType());
        if (UserTypeEnum.CAS_TYPE.equals(typeEnum)
                || UserTypeEnum.EMAIL.equals(typeEnum)
                || UserTypeEnum.SERVICE_TYPE.equals(typeEnum)
                || UserTypeEnum.GITLAB_TYPE.equals(typeEnum)
                || UserTypeEnum.GITEE_TYPE.equals(typeEnum)
                || UserTypeEnum.GITHUB_TYPE.equals(typeEnum)) {
            showAccount.append("(").append(typeEnum.getDesc()).append(")");
        } else if (UserTypeEnum.FEISHU_TYPE.equals(typeEnum) || UserTypeEnum.DINGDING_TYPE.equals(typeEnum)) {
            AuthUserVo userVo = GsonUtil.gsonToBean(entity.getContent(), AuthUserVo.class);
            if (userVo != null && !StringUtils.isEmpty(userVo.getName())) {
                showAccount.append("[").append(userVo.getName()).append("]");
            }
            showAccount.append("(").append(typeEnum.getDesc()).append(")");
        } else {
            showAccount.append(entity.getAccount()).append("(unknown)");
        }
        vo.setShowAccount(showAccount.toString());
        /**
         * 前端请求，隐藏用户私密数据
         */
        if (isFront) {
            vo.setContent(null);
        }
        return vo;
    }

    public static UserEntity toEntity(UserVo vo) {
        if (vo == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}
