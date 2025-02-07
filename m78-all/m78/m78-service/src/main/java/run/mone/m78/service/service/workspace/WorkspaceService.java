package run.mone.m78.service.service.workspace;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.workspace.WorkspaceInfoResp;
import run.mone.m78.api.bo.workspace.QueryWorkspace;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78UserWorkspaceRole;
import run.mone.m78.service.dao.entity.M78Workspace;
import run.mone.m78.service.dao.mapper.M78UserWorkspaceRoleMapper;
import run.mone.m78.service.dao.mapper.M78WorkspaceMapper;
import run.mone.m78.service.dto.UserWorkSpaceDto;
import run.mone.m78.service.service.gray.GrayService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caobaoyu
 * @description: m78 workspace service
 * @date 2024-03-01 14:55
 */
@Service
@Slf4j
public class WorkspaceService extends ServiceImpl<M78WorkspaceMapper, M78Workspace> {

    @Resource
    private M78UserWorkspaceRoleMapper workspaceRoleMapper;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = NodeFacade.class, version = "1.0")
    private NodeFacade nodeFacade;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = UserFacade.class, version = "1.0")
    private UserFacade userFacade;

    @DubboReference(check = false, group = "${ref.tpc.service.group}", interfaceClass = NodeUserFacade.class, version = "1.0")
    private NodeUserFacade nodeUserFacade;

    @Autowired
    private GrayService grayService;

    @Value("${tpc.parent.node.id}")
    private Long tpcParentNodeId;

    private static Gson gson = new Gson();

    /**
     * 创建一个新的工作区，并在TPC中创建相应节点
     *
     * @param account       当前会话的账户信息
     * @param workspaceName 工作区名称
     * @param remark        工作区备注
     * @param avatarUrl     工作区头像URL
     * @return 新创建的工作区的ID
     */
    @Transactional
    public Long createWorkspace(SessionAccount account, String workspaceName, String remark, String avatarUrl) {
        M78Workspace m78Workspace = M78Workspace.builder()
                .name(workspaceName)
                .avatarUrl(avatarUrl)
                .remark(remark)
                .owner(account.getUsername())
                .creator(account.getUsername())
                .createTime(LocalDateTime.now())
                .deleted(0)
                .build();

        log.info("createWorkspace isGray={}， workspaceName={}", grayService.isGray(), workspaceName);
        if (grayService.isGray()) {
            m78Workspace.setVersion(GrayService.KNOWLEDGE_VERSION_V2);
        }

        super.save(m78Workspace);


//        return workspaceRoleMapper.insertSelective(userWorkspaceRole) == 1;
        // 在tpc创建相应节点
        addTpcNode(account, m78Workspace.getId(), workspaceName);
        return m78Workspace.getId();
    }

    /**
     * 更新工作区信息
     *
     * @param username    更新者的用户名
     * @param workspaceId 工作区的ID
     * @param name        工作区的名称
     * @param remark      工作区的备注
     * @param avatarUrl   工作区的头像URL
     * @return 更新操作是否成功
     */
    public boolean updateWorkspace(String username, Long workspaceId, String name, String remark, String avatarUrl) {
        M78Workspace byId = super.getById(workspaceId);
        Preconditions.checkArgument(byId != null, "workspace can not be updated");
        M78Workspace m78Workspace = M78Workspace.builder()
                .id(workspaceId)
                .name(name)
                .avatarUrl(avatarUrl)
                .updater(username)
                .updateTime(LocalDateTime.now())
                .remark(remark).build();
        return super.updateById(m78Workspace);
    }

    /**
     * 根据创建者列出未删除的工作区列表
     *
     * @param creator 创建者的名称
     * @return 未删除的工作区列表
     */
    public List<M78Workspace> listWorkspace(String creator) {
        return super.list(QueryWrapper.create().eq("creator", creator).eq("deleted", 0));
    }

    /**
     * 查找指定创建者的“我的空间”工作区列表
     *
     * @param creator 创建者的名称
     * @return 创建者的“我的空间”工作区列表
     */
    //查找我的空间，入参是String creator
    public List<M78Workspace> findMyWorkspaces(String creator) {
        return super.list(QueryWrapper.create().eq("creator", creator).eq("name", "我的空间").eq("deleted", 0));
    }

    /**
     * 删除指定的工作区
     *
     * @param account     当前会话的账户信息
     * @param workspaceId 要删除的工作区ID
     * @return 如果删除成功返回true，否则返回false
     * @throws IllegalArgumentException 如果工作区不存在或当前用户不是创建者，抛出此异常
     */
    @Transactional
    public boolean deleteWorkspace(SessionAccount account, Long workspaceId) {
        M78Workspace byId = super.getById(workspaceId);
        // todo 是否需要owner才能del
        Preconditions.checkArgument(byId != null && byId.getCreator().equals(account.getUsername()), "workspace can not be deleted");
        M78Workspace m78Workspace = M78Workspace.builder()
                .id(workspaceId)
                .deleted(1)
                .build();

        NodeDeleteParam param = new NodeDeleteParam();
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setOutId(workspaceId);
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        Result delete = nodeFacade.delete(param);
        Preconditions.checkArgument(delete.getCode() == 0, "delete tpc node error,error msg: %s", delete.getMessage());
        return super.updateById(m78Workspace);
    }


    /**
     * 添加工作区用户
     *
     * @param account     当前会话的账户信息
     * @param workspaceId 工作区的唯一标识
     * @param username    用户名
     * @param role        用户角色的整数代码
     * @return 包含操作结果代码和消息的Pair对象
     */
    public Pair<Integer, String> addWorkspaceUser(SessionAccount account, Long workspaceId, String username, Integer role) {
        return addWorkspaceUser(account, workspaceId, username, UserRoleEnum.valueOfCode(role));
    }

    public Pair<Integer, String> addWorkspaceUser(SessionAccount account, Long workspaceId, String username) {
        return addWorkspaceUser(account, workspaceId, username, UserRoleEnum.USER);
    }

    public Pair<Integer, String> addWorkspaceUser(SessionAccount account, Long workspaceId, String username, UserRoleEnum role) {
        if (role == null) {
            role = UserRoleEnum.USER;
        }
        NodeUserAddParam param = new NodeUserAddParam();
        Integer roleCode = getTpcRoleFromUserRole(role).getCode();
        param.setOutId(workspaceId);
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setMemberAcc(username);
        // todo 暂且认为管理员要添加的用户是同一来源
        param.setMemberAccType(account.getUserType());
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        param.setType(roleCode);
        Result<NodeUserRelVo> add = nodeUserFacade.add(param);
        if (add.getCode() != 0) {
            log.error("nodeUserFacade.add error:{}", gson.toJson(add));
        }
        return add.getCode() == 0 ? Pair.of(0, "") : Pair.of(add.getCode(), add.getMessage());

    }

    /**
     * 更新用户权限（先删除tpc上用户，再创建一个新的用户）
     *
     * @param account
     * @param workspaceId
     * @param username
     * @param role
     * @return
     */
    public Pair<Integer, String> updateUserRole(SessionAccount account, Long workspaceId, String username, UserRoleEnum role) {
        // 先删除
        Pair<Integer, String> deleteRes = deleteUser(account, workspaceId, username);
        if (deleteRes.getKey() != 0) {
            log.error("delete user fail ,error code:{},error:{}", deleteRes.getKey(), deleteRes.getValue());
            return deleteRes;
        }
        return addWorkspaceUser(account, workspaceId, username, role);
    }

    /**
     * 删除指定用户
     *
     * @param account     当前会话的账户信息
     * @param workspaceId 工作空间的ID
     * @param username    要删除的用户名
     * @return 包含删除操作结果代码和消息的Pair对象，成功时返回(0, "")，失败时返回错误代码和错误消息
     */
    public Pair<Integer, String> deleteUser(SessionAccount account, Long workspaceId, String username) {
        NodeUserDeleteParam param = new NodeUserDeleteParam();
        param.setOutId(workspaceId);
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        param.setDelAcc(username);
        param.setDelUserType(account.getUserType());

        Result delete = nodeUserFacade.delete(param);
        return delete.getCode() == 0 ? Pair.of(0, "") : Pair.of(delete.getCode(), delete.getMessage());
    }


    /**
     * 获取用户在指定工作区的角色
     *
     * @param account     用户的会话账户信息
     * @param workspaceId 工作区的ID
     * @return 用户在工作区的角色代码，如果用户不在工作区中则返回未知角色代码
     */
    public Integer getWorkspaceRole(SessionAccount account, Long workspaceId) {
        List<UserWorkSpaceDto> userList = getUserList(account, workspaceId);
        if (CollectionUtils.isEmpty(userList)) {
            return UserRoleEnum.UN_KNOW.getCode();
        }
        Optional<UserWorkSpaceDto> matchingUser = userList.stream()
                .filter(user -> user.getUsername().equals(account.getUsername()))
                .findFirst();
        return matchingUser.map(UserWorkSpaceDto::getRole).orElse(UserRoleEnum.UN_KNOW.getCode());
    }


    /**
     * 获取个人的空间，过滤出有没有叫“我的空间”的节点，如果没有就创建“我的空间”，并返回空间id，如果有就直接返回
     *
     * @param account 用户的会话账户信息
     * @return 空间的ID
     */
    // 获取个人的空间，过滤出有没有叫“我的空间”的节点，如果没有就创建“我的空间”，并返回空间id，如果有就直接返回
    public Long getOrCreateMyWorkspace(SessionAccount account) {
        List<M78Workspace> workspaces = orgNodeList(account, null);
        Optional<M78Workspace> myWorkspaceOpt = workspaces.stream()
                .filter(workspace -> "我的空间".equals(workspace.getName()))
                .findFirst();

        if (myWorkspaceOpt.isPresent()) {
            return myWorkspaceOpt.get().getId();
        } else {
            Random random = new Random();
            int randomNumber = random.nextInt(10) + 1; // 生成1到10之间的随机整数
            M78Workspace myWorkspace = M78Workspace.builder()
                    .name("我的空间")
                    .owner(account.getUsername())
                    .avatarUrl(randomNumber + "")
                    .creator(account.getUsername())
                    .createTime(LocalDateTime.now())
                    .deleted(0)
                    .build();
            super.save(myWorkspace);
            addTpcNode(account, myWorkspace.getId(), "我的空间");
            return myWorkspace.getId();
        }
    }

    /**
     * 获取命名空间下所有的用户列表
     *
     * @param account
     * @param workspaceId
     * @return
     */
    public List<UserWorkSpaceDto> getUserList(SessionAccount account, Long workspaceId) {
        NodeUserQryParam param = buildNodeUserQryParam(account, workspaceId);
        Result<PageDataVo<NodeUserRelVo>> list = nodeUserFacade.list(param);
        if (list.getCode() != 0) {
            log.error("nodeUserFacade.list error:{}", gson.toJson(list));
            return Collections.emptyList();
        }
        List<NodeUserRelVo> userRelVos = list.getData().getList();
        if (CollectionUtils.isEmpty(list.getData().getList())) {
            if (account.isAdmin()) {
                userRelVos = new ArrayList<>();
                NodeUserRelVo nodeUserRelVo = new NodeUserRelVo();
                nodeUserRelVo.setAccount(account.getUsername());
                nodeUserRelVo.setType(NodeUserRelTypeEnum.MANAGER.getCode());
                userRelVos.add(nodeUserRelVo);
            } else {
                return Collections.emptyList();
            }
        }


        return userRelVos.stream().map(nodeUser -> {
            UserRoleEnum m78RoleFromTpcRole = getM78RoleFromTpcRole(nodeUser.getType());
            return UserWorkSpaceDto.builder()
                    .workspaceId(workspaceId)
                    .username(nodeUser.getAccount())
                    .role(m78RoleFromTpcRole.getCode())
                    .roleDesc(m78RoleFromTpcRole.getDesc())
                    .build();
        }).toList();


    }


    public boolean deleteUser(String operatorUser, Long workspaceId, String user) {
        M78UserWorkspaceRole userWorkspaceRole = workspaceRoleMapper
                .selectOneByQuery(QueryWrapper.create().eq("workspace_id", workspaceId).eq("deleted", 0).eq("username", user));
        Preconditions.checkArgument(userWorkspaceRole != null, "user not exist");

        M78UserWorkspaceRole operatorRole = workspaceRoleMapper
                .selectOneByQuery(QueryWrapper.create().eq("workspace_id", workspaceId).eq("deleted", 0).eq("username", operatorUser));

        Preconditions.checkArgument(operatorRole != null && operatorRole.getRole() > UserRoleEnum.USER.getCode(), "Insufficient permissions");

        userWorkspaceRole.setDeleted(1);
        return workspaceRoleMapper.update(userWorkspaceRole) > 0;
    }

    /**
     * 转移工作区的所有权
     *
     * @param operatorUser  操作用户的用户名
     * @param workspaceId   工作区的ID
     * @param ownerUsername 新所有者的用户名
     * @return 如果转移成功返回true，否则返回false
     */
    @Transactional
    public Boolean transferWorkspace(SessionAccount operatorUser, Long workspaceId, String ownerUsername) {
        Integer userWorkspaceRole = getWorkspaceRole(operatorUser, workspaceId);
        Preconditions.checkArgument(userWorkspaceRole != null && userWorkspaceRole > UserRoleEnum.USER.getCode(), "Insufficient permissions");
        M78Workspace byId = super.getById(workspaceId);
        byId.setOwner(ownerUsername);
        byId.setUpdater(operatorUser.getUsername());
        byId.setUpdateTime(LocalDateTime.now());
        return super.updateById(byId);
    }

    /**
     * 获取用户在指定工作区的角色
     *
     * @param workspaceId 工作区ID
     * @param username    用户名
     * @return 用户在工作区的角色代码，如果用户角色不存在则返回未知角色代码
     */
    public Integer getUserWorkspaceRole(Long workspaceId, String username) {
        M78UserWorkspaceRole userWorkspaceRole = workspaceRoleMapper.selectOneByQuery(QueryWrapper.create().eq("workspace_id", workspaceId)
                .eq("username", username)
                .eq("deleted", 0));
        if (userWorkspaceRole == null) {
            return UserRoleEnum.UN_KNOW.getCode();
        }
        return UserRoleEnum.valueOfCode(userWorkspaceRole.getRole()).getCode();

    }

    /**
     * 添加TPC节点
     *
     * @param account       用户会话信息
     * @param workspaceId   工作空间ID
     * @param workspaceName 工作空间名称
     * @return 添加操作是否成功
     */
    public boolean addTpcNode(SessionAccount account, Long workspaceId, String workspaceName) {
        NodeAddParam addParam = buildNodeAddParam(account.getUsername(), workspaceId, workspaceName, account.getUserId(), account.getUserType());
        Result<NodeVo> result = nodeFacade.add(addParam);
        log.info("[WorkspaceService.addTpcNode], res is: {}", gson.toJson(result));
        Preconditions.checkArgument(result != null && result.getCode() == 0, "add tpc node failed");

        return true;

    }

    /**
     * 获取组织节点列表，并将名称为“我的空间”的元素提到最前面
     *
     * @param account 用户会话信息
     * @param outId   节点ID，如果为null则获取所有节点
     * @return 组织节点列表，按特定顺序排序
     */
    public List<M78Workspace> orgNodeList(SessionAccount account, Long outId) {
        NodeQryParam param = buildNodeQueryParam(account, outId);
        Set<Long> list = new HashSet<>();
        if (null != outId) {
            // 用来判断该用户是否有该节点权限
            Result existResult = nodeFacade.exists(param);
            if (existResult.getCode() != 0) {
                log.error(" nodeFacade.orgNode exists outId:{},res:{}", outId, existResult);
                return Collections.emptyList();
            }
            list.add(outId);
        } else {
            Result<PageDataVo<NodeVo>> nodeInfo = nodeFacade.list(param);
            if (nodeInfo.getCode() != 0) {
                log.error(" nodeFacade.orgNode list error,res:{}", gson.toJson(nodeInfo));
                return Collections.emptyList();
            }
            if (CollectionUtils.isEmpty(nodeInfo.getData().getList())) {
                return Collections.emptyList();
            }
            list = nodeInfo.getData().getList().stream().map(NodeVo::getOutId).collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 获取 M78Workspace 列表
        List<M78Workspace> workspaces = super.listByIds(list);

        // 使用 Stream API 查找名称为“我的空间”的元素并提到最前面

        return workspaces.stream()
                .sorted((w1, w2) -> {
                    if ("我的空间".equals(w1.getName())) {
                        return -1;
                    } else if ("我的空间".equals(w2.getName())) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取工作区列表
     *
     * @param account 当前会话的账户信息
     * @param outId   外部标识ID
     * @return 工作区信息响应列表
     */
    public List<WorkspaceInfoResp> workspaceList(SessionAccount account, Long outId) {
        List<M78Workspace> workspaces = orgNodeList(account, outId);
        List<WorkspaceInfoResp> workspaceInfoRespList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workspaces)) {
            workspaceInfoRespList = workspaces.stream().map(basePo -> poToInfo(basePo, false)).collect(Collectors.toList());
        }
        return workspaceInfoRespList;
    }


    /**
     * 根据会话账户查询用户名称列表
     *
     * @param account 会话账户信息
     * @return 用户名称列表，如果没有找到用户则返回空列表
     */
    public List<UserVo> queryUserNames(SessionAccount account) {
        UserQryParam param = new UserQryParam();
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setStatus(UserStatusEnum.ENABLE.getCode());
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<UserVo>> list = userFacade.list(param);
        if (list != null && list.getData() != null && CollectionUtils.isNotEmpty(list.getData().getList())) {
            return list.getData().getList();
        }
        return Collections.emptyList();
    }

    private NodeAddParam buildNodeAddParam(String username, Long workspaceId, String workspaceName, Long userId, Integer userType) {
        NodeAddParam addParam = new NodeAddParam();
        addParam.setAccount(username);
        addParam.setUserType(userType);
        addParam.setParentNodeId(tpcParentNodeId);
        addParam.setOutId(workspaceId);
        addParam.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        addParam.setNodeName(workspaceName);
        // 默认管理员
        addParam.setMgrUserId(userId);
        addParam.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        return addParam;
    }

    private NodeQryParam buildNodeQueryParam(SessionAccount account, Long outId) {
        NodeQryParam param = new NodeQryParam();
        param.setParentId(tpcParentNodeId);
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setMyNode(true);
        param.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        param.setStatus(NodeStatusEnum.ENABLE.getCode());
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        param.setOutId(outId);
        return param;
    }

    private NodeUserQryParam buildNodeUserQryParam(SessionAccount account, Long workspaceId) {
        NodeUserQryParam param = new NodeUserQryParam();
        param.setAccount(account.getUsername());
        param.setUserType(account.getUserType());
        param.setOutId(workspaceId);
        param.setOutIdType(OutIdTypeEnum.BOT_SAPCE.getCode());
        param.setPage(1);
        param.setPageSize(1000);
        return param;
    }


    private NodeUserRelTypeEnum getTpcRoleFromUserRole(UserRoleEnum userRoleEnum) {
        if (userRoleEnum == null) {
            return NodeUserRelTypeEnum.MEMBER;
        }
        if (userRoleEnum.getCode() == UserRoleEnum.ADMIN.getCode()) {
            return NodeUserRelTypeEnum.MANAGER;
        }
        return NodeUserRelTypeEnum.MEMBER;
    }

    private UserRoleEnum getM78RoleFromTpcRole(Integer nodeUserRelType) {
        if (nodeUserRelType == null) {
            return UserRoleEnum.UN_KNOW;
        }
        if (Objects.equals(nodeUserRelType, NodeUserRelTypeEnum.MANAGER.getCode())) {
            return UserRoleEnum.ADMIN;
        }
        return UserRoleEnum.USER;

    }


    /**
     * 根据分页查询M78Workspace信息
     *
     * @param queryWorkspace 查询条件，包括页码和每页大小等信息
     * @return 分页后的Workspace信息响应对象
     */
    //根据分页查询M78Workspace
    public Page<WorkspaceInfoResp> superAdminWorkspace(QueryWorkspace queryWorkspace) {
        Page<M78Workspace> page = new Page<>(queryWorkspace.getPageNum(), queryWorkspace.getPageSize());
        QueryWrapper queryWrapper = new QueryWrapper().eq("deleted", 0);
        if (StringUtils.isNotBlank(queryWorkspace.getOwner())) {
            queryWrapper.eq("owner", queryWorkspace.getOwner());
        }
        if (StringUtils.isNotBlank(queryWorkspace.getName())) {
            queryWrapper.like("name", queryWorkspace.getName());
        }
        Page<M78Workspace> pageData = super.page(page, queryWrapper);
        List<M78Workspace> records = pageData.getRecords();
        Page<WorkspaceInfoResp> resPage = new Page<>();
        if (CollectionUtils.isNotEmpty(records)) {
            resPage.setRecords(records.stream().map(basePo -> poToInfo(basePo, true)).collect(Collectors.toList()));
        }
        resPage.setPageNumber(page.getPageNumber());
        resPage.setPageSize(page.getPageSize());
        resPage.setTotalPage(page.getTotalPage());
        resPage.setTotalRow(page.getTotalRow());
        return resPage;
    }


    //将M78Workspace转换为WorkspaceInfoResp

    private WorkspaceInfoResp poToInfo(M78Workspace workspace, Boolean superAdmin) {
        WorkspaceInfoResp workspaceInfoResp = WorkspaceInfoResp.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .avatarUrl(workspace.getAvatarUrl())
                .remark(workspace.getRemark())
                .owner(workspace.getOwner())
                .creator(workspace.getCreator())
                .deleted(workspace.getDeleted())
                .createTime(workspace.getCreateTime())
                .updater(workspace.getUpdater())
                .updateTime(workspace.getUpdateTime())
                .version(workspace.getVersion())
                .canOperate(superAdmin || !"我的空间".equals(workspace.getName()))
                .build();
        return workspaceInfoResp;
    }

}
