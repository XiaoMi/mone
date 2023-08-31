package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.common.param.UserGroupMemberAddParam;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberDeleteParam;
import com.xiaomi.mone.tpc.common.param.UserGroupMemberQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserGroupRelVo;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupRelEntity;
import com.xiaomi.mone.tpc.dao.impl.UserDao;
import com.xiaomi.mone.tpc.dao.impl.UserGroupDao;
import com.xiaomi.mone.tpc.dao.impl.UserGroupRelDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.user.util.UserGroupRelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class UserGroupMemberService {

    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserGroupRelDao userGroupRelDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private NodeHelper nodeHelper;

    public ResultVo<UserGroupRelVo> add(UserGroupMemberAddParam param) {
        //是否成员
        UserGroupRelEntity relEntity = userGroupRelDao.getByGroupIdAndUserId(param.getGroupId(), param.getUserId());
        if (relEntity == null) {
            //是否超管
            if (!nodeHelper.isTopMgr(param.getUserId())) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        UserEntity member = userDao.getById(param.getMemberId(), UserEntity.class);
        if (member == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        UserGroupEntity group = userGroupDao.getById(param.getGroupId(), UserGroupEntity.class);
        if (group == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        relEntity = userGroupRelDao.getByGroupIdAndUserId(group.getId(), member.getId());
        if (relEntity != null) {
            return ResponseCode.OPER_FAIL.build("重复添加");
        }
        UserGroupRelEntity entity = new UserGroupRelEntity();
        entity.setUserId(member.getId());
        entity.setAccount(member.getAccount());
        entity.setUserType(member.getType());
        entity.setGroupId(param.getGroupId());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        boolean result = userGroupRelDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(UserGroupRelUtil.toVo(entity));
    }

    public ResultVo delete(UserGroupMemberDeleteParam param) {
        UserGroupRelEntity relEntity = userGroupRelDao.getById(param.getId(), UserGroupRelEntity.class);
        if (relEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //是否成员
        UserGroupRelEntity operUserRelEntity = userGroupRelDao.getByGroupIdAndUserId(relEntity.getGroupId(), param.getUserId());
        if (operUserRelEntity == null) {
            //是否超管
            if (!nodeHelper.isTopMgr(param.getUserId())) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        relEntity.setUpdaterId(param.getUserId());
        relEntity.setUpdaterAcc(param.getAccount());
        relEntity.setUpdaterType(param.getUserType());
        boolean result = userGroupRelDao.deleteById(relEntity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo<PageDataVo<UserGroupRelVo>> list(UserGroupMemberQryParam param) {
        //是否成员
        UserGroupRelEntity relEntity = userGroupRelDao.getByGroupIdAndUserId(param.getGroupId(), param.getUserId());
        if (relEntity == null) {
            //是否超管
            if (!nodeHelper.isTopMgr(param.getUserId())) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        PageDataVo<UserGroupRelVo> pageData = param.buildPageDataVo();
        List<UserGroupRelEntity> relEntityList = userGroupRelDao.getListByPage(param.getGroupId(), param.getMemberId(), pageData);
        List<UserGroupRelVo> userGroupRelVos = UserGroupRelUtil.toVoList(relEntityList);
        pageData.setList(userGroupRelVos);
        return ResponseCode.SUCCESS.build(pageData);
    }

}
