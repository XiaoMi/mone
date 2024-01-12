package run.mone.mimeter.dashboard.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserStatusEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.NodeUserQryParam;
import com.xiaomi.mone.tpc.common.param.UserQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.gateway.manager.bo.user.UserInfoVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import run.mone.mimeter.dashboard.bo.UserInfo;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmin
 * @date 2023/5/10
 */
@Component
@Slf4j
public class UserV1Service {

    @DubboReference(group = "${ref.tpc.service.group}", interfaceClass = UserFacade.class, version = "1.0")
    private UserFacade userFacade;

    @DubboReference(group = "${ref.tpc.service.group}", interfaceClass = NodeUserFacade.class, version = "1.0")
    private NodeUserFacade nodeUserFacade;

    @NacosValue(value = "${tpc.gatewayManager.id:10}", autoRefreshed = true)
    private long tpcNodeId;

    /**
     * 获取用户登录信息
     * @return
     */
    public UserInfo getUserInfo(){
        AuthUserVo userVo = UserUtil.getUser();
        // 适配private/api类型的请求
        if(userVo == null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setDepartmentName(userVo.getDepartmentName());
        userInfo.setDisplayName(userVo.getName());
        userInfo.setEmail(userVo.getEmail());
        userInfo.setName(userVo.getName());
        userInfo.setUsername(userVo.getAccount());
        userInfo.setFullAccount(userVo.genFullAccount());
        userInfo.setAvatar(userVo.getAvatarUrl());
        userInfo.setAdmin(true);
        return userInfo;
    }


    public boolean isAdmin(String username, AuthUserVo userVo){
        if(true)return true;
        if (StringUtils.isBlank(username)){
            return false;
        }
        if (userVo==null){
            throw new CommonException(CommonError.InvalidUserInfoError);
        }
        boolean isAdmin = false;
        NodeUserQryParam param = new NodeUserQryParam();
        param.setNodeId(tpcNodeId);
        param.setType(NodeUserRelTypeEnum.MANAGER.getCode());
        param.setAccount(userVo.getAccount());
        param.setUserType(userVo.getUserType());
        Result<PageDataVo<NodeUserRelVo>> rst = nodeUserFacade.list(param);
        log.debug("nodeUserFacade.list rst:{}", rst);
        if(rst != null && rst.getData() != null && CollectionUtils.isNotEmpty(rst.getData().getList())){
            for (NodeUserRelVo vo : rst.getData().getList()){
                if (username.equals(vo.getAccount())){
                    isAdmin = true;
                    break;
                }
            }
        }
        return isAdmin;
    }


    /**
     * 用户列表搜索
     */
    public List<UserInfoVo> qryUserList(String username, Integer userType, AuthUserVo userVo) {
        List<UserInfoVo> list = new ArrayList<>();
        UserQryParam qryParam = new UserQryParam();
        if (userType!=null){
            qryParam.setType(userType);
        }
        qryParam.setUserAcc(username);
        qryParam.setStatus(UserStatusEnum.ENABLE.getCode());
        qryParam.setAccount(userVo.getAccount());
        qryParam.setUserType(userVo.getUserType());
        Result<PageDataVo<UserVo>> rst = userFacade.list(qryParam);
        if(rst != null && rst.getData() != null && CollectionUtils.isNotEmpty(rst.getData().getList())){
            List<UserVo> nodeUserRelVos = rst.getData().getList();
            nodeUserRelVos.forEach(nodeUser->{
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setUserName(nodeUser.getAccount());
                userInfoVo.setUserType(nodeUser.getType());
                userInfoVo.setFullAccount(UserUtil.getFullAccount(nodeUser.getAccount(), nodeUser.getType()));
                UserTypeEnum anEnum = UserTypeEnum.getEnum(nodeUser.getType());
                if(anEnum != null){
                    userInfoVo.setFullUserName(nodeUser.getAccount()+"("+ anEnum.getDesc() +")");
                }
                list.add(userInfoVo);
            });
        }
        return list;
    }
}

