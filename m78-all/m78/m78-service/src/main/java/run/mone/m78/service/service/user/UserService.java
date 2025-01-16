package run.mone.m78.service.service.user;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserStatusEnum;
import com.xiaomi.mone.tpc.common.param.NodeUserQryParam;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.param.UserQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.nr.eiam.api.dto.user.GetPsUserInfoRequest;
import com.xiaomi.nr.eiam.api.service.PsUserService;
import com.xiaomi.nr.eiam.api.vo.user.GetPsUserInfoResponse;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.ai.z.service.ZDubboService;
import run.mone.m78.api.bo.model.ModelConfig;
import run.mone.m78.api.bo.user.UserConfig;
import run.mone.m78.api.bo.user.UserDTO;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.bo.user.UserInfoVo;
import run.mone.m78.service.dao.mapper.UserConfigMapper;
import run.mone.m78.service.dao.entity.UserConfigPo;
import run.mone.m78.service.dao.entity.table.UserConfigPoTableDef;
import run.mone.m78.service.exceptions.InternalException;
import run.mone.m78.service.service.cache.CacheService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2023/5/10
 */
@Component
@Slf4j
public class UserService {

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = NodeUserFacade.class, version = "1.0")
    private NodeUserFacade nodeUserFacade;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = UserOrgFacade.class, version = "1.0")
    private UserOrgFacade userOrgFacade;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = UserFacade.class, version = "1.0")
    private UserFacade userFacade;

    @DubboReference(check = false, group = "${ref.ai.z.service.group}", interfaceClass = ZDubboService.class, version = "${ref.ai.z.service.version}")
    private ZDubboService zDubboService;

    @DubboReference(check = false,  group = "${ref.psUserService.group}", interfaceClass = PsUserService.class)
    private PsUserService psUserService;

    @NacosValue(value = "${server.cas.logoutUrl:}", autoRefreshed = true)
    private String logoutUrl;

    @Value("${tpc.m78.id}")
    private long tpcNodeId;

    @Resource
    private UserConfigMapper userConfigMapper;

    @Resource
    private CacheService cacheService;

    @Value("${default.models}")
    private String defaultModels;

    private ModelConfig defaultModelConfig;

    @PostConstruct
    public void init() {
        defaultModelConfig = new Gson().fromJson(defaultModels, ModelConfig.class);
    }

    /**
     * 获取用户登录信息
     *
     * @return
     */
    public UserInfoVo getUserInfo() {
        AuthUserVo userVo = UserUtil.getUser();
        if (userVo == null) {
            return null;
        }
        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setDisplayName(userVo.getName());
        userInfo.setEmail(userVo.getEmail());
        userInfo.setName(userVo.getName());
        userInfo.setUsername(userVo.getAccount());
        userInfo.setFullAccount(userVo.genFullAccount());
        userInfo.setAvatar(userVo.getAvatarUrl());
        boolean isAdministrator = isAdministrator(userVo);
        log.info("[UserService.getUserInfo], user: {}, isAdministrator: {}", userVo, isAdministrator);
        userInfo.setAdmin(isAdministrator);
        userInfo.setLogoutUrl(logoutUrl);
        Optional<OrgInfoVo> userOrgInfo = getUserOrgInfo(userVo.getAccount());
        userInfo.setTenant(!userOrgInfo.isPresent() ? "" : userOrgInfo.get().getIdPath());
        Optional<String> userZToken = getUserZToken(userInfo.getUsername());
        userInfo.setZToken(!userZToken.isPresent() ? "" : userZToken.get());
        userInfo.setUserType(userVo.getUserType());
        return userInfo;
    }

    /**
     * 根据用户名获取用户组织信息，若成功则返回包含OrgInfoVo的Optional，失败则记录警告并返回空的Optional。
     */
    public Optional<OrgInfoVo> getUserOrgInfo(String userName) {
        NullParam param = new NullParam();
        param.setAccount(userName);
        param.setUserType(0);
        try {
            return Optional.ofNullable(userOrgFacade.getOrgByAccount(param).getData());
        } catch (Exception e) {
            log.warn("[LoginService.getAccountFromSession], failed to getOrgByAccount, msg: {} ", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<String> getUserZToken(String userName) {
        try {
            return Optional.ofNullable(zDubboService.getOrGenToken(userName, 0).getData());
        } catch (Exception e) {
            log.warn("[LoginService.getZAiInfo], failed to getZAiInfo, msg: {} ", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 检查传入的AuthUserVo对象是否代表一个管理员用户，如果用户未登录或用户名为空则返回false，否则根据账号和用户类型获取管理者列表并判断用户是否在列表中。
     */
    public boolean isAdministrator(AuthUserVo userVo) {
        if (userVo == null) {
            throw new RuntimeException("User not logged in.");
        }
        String userName = userVo.getAccount();
        if (StringUtils.isBlank(userName)) {
            return false;
        }
        List<NodeUserRelVo> managers = getManagers(userVo.getAccount(), userVo.getUserType());
        log.info("[UserService.isAdministrator], user: {}, managers: {}", userName, managers);
        if (CollectionUtils.isNotEmpty(managers)) {
            return managers.stream().anyMatch(vo -> Objects.equals(userName, vo.getAccount()));
        }
        return false;
    }

    /**
     * 根据用户名和用户类型查询并返回用户的管理者信息列表。如果查询结果为空，则返回一个空的列表。
     */
    private List<NodeUserRelVo> getManagers(String userName, Integer userType) {
        NodeUserQryParam param = new NodeUserQryParam();
        param.setNodeId(tpcNodeId);
        param.setType(NodeUserRelTypeEnum.MANAGER.getCode());
        param.setAccount(userName);
        param.setUserType(userType);
        Result<PageDataVo<NodeUserRelVo>> rst = nodeUserFacade.list(param);
        if (rst != null && rst.getData() != null && CollectionUtils.isNotEmpty(rst.getData().getList())) {
            return rst.getData().getList();
        }
        return new ArrayList<>();
    }

    //获取用户配置
    public UserConfig getUserConfig(String userName) {
        return getUserConfig(userName, false);
    }

    public UserConfig getUserConfig(String userName, boolean useCache) {
        if (useCache) {
            UserConfig uc = cacheService.get(userName);
            if (null != uc) {
                return uc;
            }
        }
        UserConfigPo res = userConfigMapper.selectOneByCondition(UserConfigPoTableDef.USER_CONFIG_PO.USER_NAME.eq(userName));
        if (null != res) {
            UserConfig userConfig = UserConfig.builder().modelConfig(res.getModelConfig()).id(res.getId()).build();
            cacheService.set(userName, userConfig);
            return userConfig;
        }
        UserConfig configRes = UserConfig.builder().modelConfig(defaultModelConfig).build();

        cacheService.set(userName, configRes);
        return configRes;
    }

    //更新用户配置
    public void updateOrInsertUserConfig(UserConfigPo userConfigPo) {
        long now = System.currentTimeMillis();
        userConfigPo.setUtime(now);
        userConfigPo.setState(1);
        UserConfigPo userConfig = userConfigMapper.selectOneByCondition(UserConfigPoTableDef.USER_CONFIG_PO.USER_NAME.eq(userConfigPo.getUserName()));
        if (null == userConfig) {
            userConfigPo.setCtime(now);
            userConfigMapper.insert(userConfigPo);
        } else {
            userConfigPo.setId(userConfig.getId());
            userConfigMapper.update(userConfigPo);
        }
        cacheService.set(userConfigPo.getUserName(), UserConfig.builder().modelConfig(userConfigPo.getModelConfig()).build());
    }

    public List<UserDTO> queryUserNames(SessionAccount account, String keyword) {
        List<UserDTO> result = new ArrayList<>();
        UserQryParam param = new UserQryParam();
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setStatus(UserStatusEnum.ENABLE.getCode());
        param.setUserAcc(keyword);
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<UserVo>> list = userFacade.list(param);
        if (list != null && list.getData() != null && CollectionUtils.isNotEmpty(list.getData().getList())) {
            List<UserVo> nodeUserRelVos = list.getData().getList();
            nodeUserRelVos.forEach(userVo -> {
                UserTypeEnum anEnum = UserTypeEnum.getEnum(userVo.getType());
                String user = userVo.getAccount() + "(" + (anEnum == null ? "" : anEnum.getDesc()) + ")";
                result.add(new UserDTO(user, userVo.getAccount(), userVo.getAccount() + "@xiaomi.com", null));
            });
        }
        return result;
    }


    public Long getMiId(SessionAccount account) {
        String username = account.getUsername();
        GetPsUserInfoRequest getPsUserInfoRequest = new GetPsUserInfoRequest();
        String userEmail = username + "@xiaomi.com";
        getPsUserInfoRequest.setEmail(userEmail);
        GetPsUserInfoResponse data = psUserService.getPsUserInfo(getPsUserInfoRequest).getData();
        //新来的同学可能会查无此人(灬ꈍ ꈍ灬)
        if (data == null){
            throw new InternalException("Your user information cannot be found!");
        }
        return data.getMiId();
    }
}
