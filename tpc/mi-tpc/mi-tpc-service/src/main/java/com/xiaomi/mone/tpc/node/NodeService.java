package com.xiaomi.mone.tpc.node;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.impl.*;
import com.xiaomi.mone.tpc.node.change.ProNodeChangeHelper;
import com.xiaomi.mone.tpc.node.util.NodeUtil;
import com.xiaomi.mone.tpc.org.OrgHelper;
import com.xiaomi.mone.tpc.user.UserGroupHelper;
import com.xiaomi.mone.tpc.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 16:19
 */
@Slf4j
@Service
public class NodeService implements NodeHelper{

    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private UserNodeRoleRelDao userNodeRoleRelDao;
    @Autowired
    private FlagDao flagDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private NodeResourceRelDao nodeResourceRelDao;
    @Autowired
    private Cache cache;
    @Autowired
    private NodeOrgHelper nodeOrgHelper;
    @Autowired
    private OrgHelper orgHelper;
    @Resource(name = "nodeLinkExecutor")
    private ThreadPoolExecutor nodeLinkExecutor;

    @Autowired
    private UserGroupHelper userGroupHelper;

    @Resource
    private ProNodeChangeHelper proNodeChangeHelper;

    public NodeVo getById(Long nodeId) {
        NodeEntity nodeEntity = nodeDao.getById(nodeId, NodeEntity.class);
        return NodeUtil.toVo(nodeEntity);
    }

    /**
     * 快速进入
     * @param param
     * @return
     */
    public ResultVo<NodeVo> fast(NullParam param) {
        NodeQryParam nodeQryParam = new NodeQryParam();
        BeanUtils.copyProperties(param, nodeQryParam);
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getMaxLevelNode(param.getUserId());
        // 进入自己管理的节点
        if (nodeUserRelEntity != null) {
            nodeQryParam.setId(nodeUserRelEntity.getNodeId());
        } else {
            //没有自己管理的节点，进入顶级节点
            NodeEntity nodeEntity = nodeDao.getOneByType(NodeTypeEnum.TOP_TYPE.getCode());
            if (nodeEntity == null) {
                ResponseCode.UNKNOWN_ERROR.build();
            }
            nodeQryParam.setId(nodeEntity.getId());
        }
        return get(nodeQryParam);
    }

    /**
     * 枚举列表
     * @return
     */
    public ResultVo<Map<String,List<EnumData>>> enumList() {
        Map<String,List<EnumData>> map = EnumUtil.getMapList();
        return ResponseCode.SUCCESS.build(map);
    }


    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeVo>> list(NodeQryParam param) {
        PageDataVo<NodeVo> pageData = param.buildPageDataVo();
        if (param.getParentOutId() != null && param.getParentOutIdType() != null) {
            NodeEntity parentNodeEntity = nodeDao.getOneByOutId(param.getParentOutIdType(), param.getParentOutId());
            if (parentNodeEntity == null) {
                return ResponseCode.SUCCESS.build(pageData);
            }
            param.setParentId(parentNodeEntity.getId());
        }
        Long userId = param.isMyNode() ? param.getUserId() : null;
        List<NodeEntity> entityList = nodeDao.getListByPageByOrgIdAndUserId(param.getOrgId(), userId, param.getParentId(), param.getNodeName(), param.getType(), param.getRelType(), param.getStatus(), pageData);
        List<NodeVo> voList = NodeUtil.toVoList(entityList);
        pageData.setList(voList);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeVo>> innerListByFlagKey(NodeQryByFlagParam param) {
        PageDataVo<NodeVo> pageData = param.buildPageDataVo();
        List<NodeEntity> entityList = nodeDao.getListByPageByFlagKey(param.getFlagKey(), param.getType(), param.getStatus(), pageData);
        List<NodeVo> voList = NodeUtil.toVoList(entityList);
        pageData.setList(voList);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeVo>> orgNodelist(NodeQryParam param) {
        PageDataVo<NodeVo> pageData = param.buildPageDataVo();
        if (param.getParentOutId() != null && param.getParentOutIdType() != null) {
            NodeEntity parentNodeEntity = nodeDao.getOneByOutId(param.getParentOutIdType(), param.getParentOutId());
            if (parentNodeEntity == null) {
                return ResponseCode.SUCCESS.build(pageData);
            }
            param.setParentId(parentNodeEntity.getId());
        }
        String[] orgIds = null;
        Long userId = null;
        if (param.isMyNode()) {
            OrgInfoVo orgInfoVo = orgHelper.get(param.getAccount());
            orgIds = (orgInfoVo == null || StringUtils.isEmpty(orgInfoVo.getIdPath())) ? null : orgInfoVo.getIdPath().split("\\/");
            userId = param.getUserId();
        }
        List<NodeEntity> entityList = nodeDao.getListByPageByOrgIdsAndUserId(orgIds, userId, param.getParentId(), param.getNodeName(), param.getType(), param.getStatus(), pageData);
        List<NodeVo> voList = NodeUtil.toVoList(entityList);
        pageData.setList(voList);
        return ResponseCode.SUCCESS.build(pageData);
    }


    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeVo>> userGroupNodelist(NodeQryParam param) {
        PageDataVo<NodeVo> pageData = param.buildPageDataVo();
        if (param.getParentOutId() != null && param.getParentOutIdType() != null) {
            NodeEntity parentNodeEntity = nodeDao.getOneByOutId(param.getParentOutIdType(), param.getParentOutId());
            if (parentNodeEntity == null) {
                return ResponseCode.SUCCESS.build(pageData);
            }
            param.setParentId(parentNodeEntity.getId());
        }
        List<Long> userGroupIds = null;
        Long userId = null;
        if (param.isMyNode()) {
            userGroupIds = userGroupHelper.getMyUserGroupIds(param.getUserId());
            userId = param.getUserId();
        }
        List<NodeEntity> entityList = nodeDao.getListByPageByUserGroupIdsAndUserId(userGroupIds, userId, param.getParentId(), param.getNodeName(), param.getType(), param.getStatus(), pageData);
        List<NodeVo> voList = NodeUtil.toVoList(entityList);
        pageData.setList(voList);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<NodeVo> getByOutId(NodeQryParam param) {
        NodeEntity nodeEntity = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
        if (nodeEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        param.setId(nodeEntity.getId());
        return get(param, nodeEntity, param.isNeedParent());
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<NodeVo> getByNodeCode(NodeQryParam param) {
        NodeEntity nodeEntity = nodeDao.getOneByCode(param.getNodeCode(), param.getType());
        if (nodeEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        param.setId(nodeEntity.getId());
        return get(param, nodeEntity, param.isNeedParent());
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo exists(NodeQryParam param) {
        NodeEntity nodeEntity = null;
        if (param.getId() != null) {
            nodeEntity = nodeDao.getById(param.getId(), NodeEntity.class);
        } else if (param.getOutId() != null && param.getOutIdType() != null){
            nodeEntity = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
        }
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build("节点不存在");
        }
        if (!NodeStatusEnum.ENABLE.getCode().equals(nodeEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("节点不可用");
        }
        if (!param.isMyNode()) {
            return ResponseCode.SUCCESS.build();
        }
        NodeUserRelEntity relEntity = nodeUserRelDao.getOneByNodeIdAndUserId(nodeEntity.getId(), param.getUserId(), null);
        if (relEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build("非节点成员");
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<NodeVo> get(NodeQryParam param) {
        NodeEntity nodeEntity = nodeDao.getById(param.getId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        return get(param, nodeEntity, param.isNeedParent());
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    @Override
    public ResultVo<NodeVo> get(BaseParam param, NodeEntity nodeEntity, boolean needParent) {
        NodeVo nodeVo = NodeUtil.toVo(nodeEntity);
        NodeTypeEnum nodeType = NodeTypeEnum.getEnum(nodeVo.getType());
        //支持的子节点
        nodeVo.setSupportNodeTypes(nodeType.getSubNodeTypes());
        //支持组织的节点列表(创建的时候使用)
        nodeVo.setSupportOrgNodeTypes(NodeTypeEnum.getSupportOrgNode());
        //支持成员的节点列表(创建时候使用)
        nodeVo.setSupportMemberNodeTypes(NodeTypeEnum.getSupportMemberNode());
        //支持的成员类型(创建的时候，判断是否需要管理员)
        nodeVo.setSupportMemberTypes(nodeType.getNodeUserTypes());
        //支持的工单类型
        nodeVo.setSupportApplyTypes(nodeType.getApplyTypes());
        //收集当前及上级节点的ID集合
        List<Long> parentIds = getparentNodeIdList(nodeVo.getContent());
        parentIds.add(nodeEntity.getId());
        List<NodeUserRelEntity> nodeUserRelEntitys = nodeUserRelDao.getByNodeIdsAndUserId(parentIds, param.getUserId(), null);
        if (!CollectionUtils.isEmpty(nodeUserRelEntitys)) {
            nodeUserRelEntitys.stream().forEach(e -> {
                //当前节点
                if (e.getNodeId().equals(nodeEntity.getId())) {
                    //当前节点管理员
                    if (NodeUserRelTypeEnum.MANAGER.getCode().equals(e.getType())) {
                        nodeVo.setCurrentMgr(true);
                        nodeVo.setCurrentMember(true);
                    } else {
                        nodeVo.setCurrentMember(true);
                    }
                    //上级节点
                } else {
                    //上级节点管理员
                    if (NodeUserRelTypeEnum.MANAGER.getCode().equals(e.getType())) {
                        nodeVo.setParentMgr(true);
                        nodeVo.setParentMember(true);
                    } else {
                        nodeVo.setParentMember(true);
                    }
                }
                if (NodeTypeEnum.TOP_TYPE.getCode().equals(e.getNodeType())) {
                    if (NodeUserRelTypeEnum.MANAGER.getCode().equals(e.getType())) {
                        nodeVo.setTopMgr(true);
                        nodeVo.setTopMember(true);
                        nodeVo.setParentMgr(true);
                        nodeVo.setParentMember(true);
                    } else {
                        nodeVo.setTopMember(true);
                        nodeVo.setParentMember(true);
                    }
                } else if (NodeTypeEnum.PRO_TYPE.getCode().equals(e.getNodeType())) {
                    //是不是项目测试人员
                    nodeVo.setProjectTester(Integer.valueOf(1).equals(e.getTester()));
                }
            });
        }
        //查询节点组织信息
        if (NodeTypeEnum.supportOrgNode(nodeEntity.getType())) {
            List<FlagEntity> flagEntities = flagDao.getListByNodeId(nodeEntity.getId(), FlagTypeEnum.ORG.getCode());
            nodeVo.setOrgInfoVo(buildOrgInfoVo(flagEntities));
        }
        boolean isMgr = isMgr(nodeVo);
        //是否支持工单申请
        nodeVo.setSupportApply(!CollectionUtils.isEmpty(nodeVo.getSupportApplyTypes()));
        //是否支持移动-仅支持项目节点移动
        nodeVo.setSupportMove(NodeTypeEnum.PRO_TYPE.getCode().equals(nodeVo.getType()) && isMgr);
        //支持编辑组织信息
        nodeVo.setSupportEditOrg(NodeTypeEnum.supportOrgNode(nodeVo.getType()) && isMgr);
        //是否存在成员列表
        nodeVo.setHasMemberList(!CollectionUtils.isEmpty(nodeType.getNodeUserTypes()));
        nodeVo.setMemberListOper(nodeVo.isHasMemberList() && isMgr);
        //用户组关系列表
        nodeVo.setHasUserGroupList(nodeType.supportNodeUserGroupTypes());
        nodeVo.setUserGroupListOper(nodeVo.isHasUserGroupList() && isMgr);
        //是否存在标签列表，目前全部节点都可以添加标签
        nodeVo.setHasFlagList(true);
        nodeVo.setFlagListOper(isMgr);
        //是否存在成员授权列表
        nodeVo.setHasUserNodeRoleList(NodeTypeEnum.supportGrantUserNode(nodeVo.getType()));
        nodeVo.setUserNodeRoleListOper(nodeVo.isHasUserNodeRoleList() && isMgr);
        //是否存在资源池列表
        nodeVo.setHasPoolResList(NodeTypeEnum.supportResPoolNode(nodeVo.getType()));
        nodeVo.setPoolResListOper(nodeVo.isHasPoolResList() && isMgr);
        //是否存在关联资源
        nodeVo.setHasRelResList(NodeTypeEnum.supportResNodeType(nodeVo.getType()));
        nodeVo.setRelResListOper(nodeVo.isHasRelResList() && isMgr);
        //是否存在子节点列表
        nodeVo.setHasSubNodeList(!NodeTypeEnum.RES_GROUP_TYPE.getCode().equals(nodeVo.getType()));
        nodeVo.setSubNodeListOper(nodeVo.isHasSubNodeList() && isMgr);
        //是否存在iam列表
        nodeVo.setHasIamList(NodeTypeEnum.supportIamNode(nodeVo.getType()));
        nodeVo.setIamListOper(isMgr);
        /**
         * 加载父亲节点
         */
        if (needParent) {
            NodeEntity parentNode = nodeDao.getById(nodeVo.getParentId());
            nodeVo.setParentNode(NodeUtil.toVo(parentNode));
        }
        return ResponseCode.SUCCESS.build(nodeVo);
    }

    private boolean isMember(NodeVo nodeVo) {
        return nodeVo.isTopMember() || nodeVo.isParentMember()
                || nodeVo.isCurrentMember();
    }

    private boolean isMgr(NodeVo nodeVo) {
        return nodeVo.isTopMgr() || nodeVo.isParentMgr()
                || nodeVo.isCurrentMgr();
    }

    private OrgInfoVo buildOrgInfoVo(List<FlagEntity> flagEntities) {
        if (CollectionUtils.isEmpty(flagEntities)) {
            return null;
        }
        StringBuilder name = new StringBuilder();
        StringBuilder id = new StringBuilder();
        flagEntities.stream().sorted((o1, o2) -> o1.getFlagVal().compareTo(o2.getFlagVal())).forEach(e -> {
            name.append(e.getFlagName()).append("/");
            id.append(e.getFlagKey()).append("/");
        });
        OrgInfoVo orgVo = new OrgInfoVo();
        orgVo.setNamePath(name.substring(0, name.length() - 1));
        orgVo.setIdPath(id.substring(0, id.length() - 1));
        return orgVo;
    }

    /**
     * 判断是否是顶级节点管理员
     * @param userId
     * @return
     */
    @Override
    public boolean isTopMgr(Long userId) {
        return null != nodeUserRelDao.getOneByNodeTypeAndUserId(NodeTypeEnum.TOP_TYPE.getCode(), userId, NodeUserRelTypeEnum.MANAGER.getCode());
    }

    /**
     * 添加
     * @param force
     * @param param
     * @return
     */
    @Override
    public ResultVo<NodeVo> add(boolean force, NodeAddParam param) {
        NodeEntity parentNode = null;
        if (param.getParentNodeId() != null) {
            parentNode = nodeDao.getById(param.getParentNodeId(), NodeEntity.class);
            if (parentNode == null) {
                return ResponseCode.OPER_FAIL.build();
            }
        } else {
            parentNode = nodeDao.getOneByOutId(param.getParentOutIdType(), param.getParentOutId());
            if (parentNode == null) {
                return ResponseCode.NO_OPER_PERMISSION.build("项目未绑定");
            }
        }
        NodeTypeEnum parentNodeType = NodeTypeEnum.getEnum(parentNode.getType());
        if (!parentNodeType.supportSubNodeType(param.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!force && !isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        return realAdd(param, parentNode);
    }

    @Override
    public ResultVo<NodeVo> realAdd(NodeAddParam param, NodeEntity parentNode) {
        List<Long> allParentIds = Lists.newArrayList();
        allParentIds.addAll(getparentNodeIdList(parentNode.getContent()));
        allParentIds.add(parentNode.getId());
        //给节点添加管理员-资源组节点除外
        NodeUserRelEntity nodeUserRelEntity = new NodeUserRelEntity();
        boolean addUserMgr = NodeTypeEnum.supportMemberNode(param.getType());
        if (addUserMgr) {
            Long mgrUserId = Optional.ofNullable(param.getMgrUserId()).orElse(param.getUserId());
            UserEntity userEntity = userDao.getById(mgrUserId, UserEntity.class);
            if (userEntity == null) {
                log.error("添加节点mgrUserId={}不存在", mgrUserId);
                return ResponseCode.OPER_ILLEGAL.build();
            }
            buildNodeUser(param, userEntity, nodeUserRelEntity);
        }
        List<FlagEntity> allFlagEntities = Lists.newArrayList();
        List<FlagEntity> flagEntities = orgHelper.buildNodeOrgs(param, param.getOrgParam());
        //兜底方案，取父节点组织信息
        if (CollectionUtils.isEmpty(flagEntities)) {
            flagEntities = orgHelper.buildNodeOrgs(param, parentNode.getId());
        }
        if (!CollectionUtils.isEmpty(flagEntities)) {
            allFlagEntities.addAll(flagEntities);
        }
        NodeEntity entity = buildCurNode(param, parentNode, allParentIds);
        Key key = null;
        try {
            if (StringUtils.isNotEmpty(param.getCode())) {
                key = Key.build(ModuleEnum.NODE_CODE_LOCK).keys(param.getCode());
                if (!cache.get().lock(key)) {
                    return ResponseCode.OPER_FAIL.build("节点编码重复");
                }
                NodeEntity nodeEntity = nodeDao.getOneByCode(param.getCode());
                if (nodeEntity != null) {
                    return ResponseCode.OPER_FAIL.build("节点编码重复");
                }
            }
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    nodeDao.insertWithException(entity);
                    if (!CollectionUtils.isEmpty(allFlagEntities)) {
                        allFlagEntities.stream().forEach(flagEntity -> flagEntity.setParentId(entity.getId()));
                        flagDao.batchInsertWithException(allFlagEntities);
                    }
                    if (addUserMgr) {
                        nodeUserRelEntity.setNodeId(entity.getId());
                        nodeUserRelEntity.setNodeType(entity.getType());
                        nodeUserRelDao.insertWithException(nodeUserRelEntity);
                    }
                }
            });
            proNodeChangeHelper.change(NodeChangeEnum.ADD, NodeUtil.toVo(entity));
        } catch (Throwable e) {
            log.error("添加节点失败 param={}", param, e);
            return ResponseCode.OPER_FAIL.build();
        } finally{
            if (key != null) {
                cache.get().unlock(key);
            }
        }
        //创建默认资源组
        if (param.isCreateDefEnv() && NodeTypeEnum.getEnum(entity.getType()).supportSubNodeType(NodeTypeEnum.RES_GROUP_TYPE.getCode())) {
            List<NodeEntity> nodeEntities = buildSubNodeList(param, entity, allParentIds);
            nodeDao.batchInsert(nodeEntities);
        }
        return ResponseCode.SUCCESS.build(NodeUtil.toVo(entity));
    }

    /**
     * 构建分区节点对象
     * @param param
     * @param entity
     * @param allParentIds
     * @return
     */
    private List<NodeEntity> buildSubNodeList(NodeAddParam param, NodeEntity entity, List<Long> allParentIds) {
        List<Long> curAllParentIds = new ArrayList<>();
        curAllParentIds.addAll(allParentIds);
        curAllParentIds.add(entity.getId());
        List<NodeEntity> subEntitys = Lists.newArrayList();
        for (NodeEnvFlagEnum envEnum : NodeEnvFlagEnum.values()) {
            NodeEntity subEntity = new NodeEntity();
            subEntity.setParentId(entity.getId());
            subEntity.setParentType(entity.getType());
            subEntity.setTopId(entity.getTopId());
            subEntity.setTopType(entity.getTopType());
            subEntity.setType(NodeTypeEnum.RES_GROUP_TYPE.getCode());
            subEntity.setNodeName(envEnum.getDesc());
            subEntity.setDesc(envEnum.getDesc());
            subEntity.setCreaterId(param.getUserId());
            subEntity.setCreaterAcc(param.getAccount());
            subEntity.setCreaterType(param.getUserType());
            subEntity.setUpdaterId(param.getUserId());
            subEntity.setUpdaterAcc(param.getAccount());
            subEntity.setUpdaterType(param.getUserType());
            subEntity.setContent(rebuildContentForPids(null, curAllParentIds));
            subEntity.setEnvFlag(envEnum.getCode());
            subEntitys.add(subEntity);
        }
        return subEntitys;
    }

    private void buildNodeUser(NodeAddParam param, UserEntity userEntity, NodeUserRelEntity nodeUserRelEntity) {
        nodeUserRelEntity.setType(NodeUserRelTypeEnum.MANAGER.getCode());
        nodeUserRelEntity.setCreaterId(param.getUserId());
        nodeUserRelEntity.setCreaterAcc(param.getAccount());
        nodeUserRelEntity.setCreaterType(param.getUserType());
        nodeUserRelEntity.setUpdaterId(param.getUserId());
        nodeUserRelEntity.setUpdaterAcc(param.getAccount());
        nodeUserRelEntity.setUpdaterType(param.getUserType());
        nodeUserRelEntity.setUserId(userEntity.getId());
        nodeUserRelEntity.setUserType(userEntity.getType());
        nodeUserRelEntity.setAccount(userEntity.getAccount());
    }

    /**
     * 构建当前节点对象
     * @param param
     * @param parentNode
     * @param allParentIds
     * @return
     */
    @Override
    public NodeEntity buildCurNode(NodeAddParam param, NodeEntity parentNode, List<Long> allParentIds) {
        NodeEntity entity = new NodeEntity();
        entity.setEnvFlag(param.getEnvFlag());
        entity.setOutId(param.getOutId() == null ? 0L : param.getOutId());
        entity.setOutIdType(param.getOutIdType() == null ? 0 : param.getOutIdType());
        entity.setParentId(parentNode.getId());
        entity.setParentType(parentNode.getType());
        entity.setTopId(parentNode.getTopId());
        entity.setTopType(parentNode.getTopType());
        entity.setType(param.getType());
        entity.setNodeName(param.getNodeName());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        rebuildContentForPids(null, allParentIds);
        entity.setContent(rebuildContentForPids(null, allParentIds));
        if (StringUtils.isNotBlank(param.getCode())) {
            entity.setCode(param.getCode());
        } else {
            entity.setCode("");
        }
        return entity;
    }

    /**
     * 编辑
     * @param param
     * @return
     */
    public ResultVo<NodeVo> edit(boolean force, NodeEditParam param) {
        NodeEntity curNode = null;
        if (param.getId() != null) {
            curNode = nodeDao.getById(param.getId(), NodeEntity.class);
        } else {
            curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
        }
        if (curNode == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!force && curNode.getOutId() != null && curNode.getOutId() > 0L && !isTopMgr(param.getUserId())) {
            return ResponseCode.OPER_FAIL.build("外部同步节点不允许编辑");
        }
        NodeEntity parentNode = nodeDao.getById(curNode.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!force && !isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        return realEdit(param, curNode);
    }

    @Override
    public ResultVo<NodeVo> realEdit(NodeEditParam param, NodeEntity curNode) {
        curNode.setNodeName(param.getNodeName());
        curNode.setDesc(param.getDesc());
        curNode.setUpdaterId(param.getUserId());
        curNode.setUpdaterAcc(param.getAccount());
        curNode.setUpdaterType(param.getUserType());
        curNode.setCode(param.getCode());
        Key key = null;
        try {
            if (StringUtils.isNotEmpty(param.getCode())) {
                key = Key.build(ModuleEnum.NODE_CODE_LOCK).keys(param.getCode());
                if (!cache.get().lock(key)) {
                    return ResponseCode.OPER_FAIL.build("节点编码重复");
                }
                NodeEntity nodeEntity = nodeDao.getOneByCode(param.getCode());
                if (nodeEntity != null && !nodeEntity.getId().equals(param.getId())) {
                    return ResponseCode.OPER_FAIL.build("节点编码重复");
                }
            }
            ResultVo orgResult = nodeOrgHelper.updateNodeOrg(param, param.getOrgParam(), curNode.getId());
            if (!orgResult.success()) {
                return orgResult;
            }
            boolean result = nodeDao.updateById(curNode);
            if (!result) {
                return ResponseCode.OPER_FAIL.build();
            }
            proNodeChangeHelper.change(NodeChangeEnum.UPDATE, NodeUtil.toVo(curNode));
            return ResponseCode.SUCCESS.build(NodeUtil.toVo(curNode));
        } finally{
            if (key != null) {
                cache.get().unlock(key);
            }
        }
    }

    /**
     * 编辑部门信息
     * @param param
     * @return
     */
    public ResultVo orgEdit(NodeOrgEditParam param) {
        NodeEntity  curNode = nodeDao.getById(param.getId(), NodeEntity.class);
        if (curNode == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!isMgrOrSuperMgr(param.getUserId(), curNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        return nodeOrgHelper.updateNodeOrg(param, param.getOrgParam(), curNode.getId());
    }

    /**
     * 状态变更
     * @param param
     * @return
     */
    public ResultVo status(NodeStatusParam param) {
        NodeEntity curNode = nodeDao.getById(param.getId(), NodeEntity.class);
        if (curNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        NodeEntity parentNode = nodeDao.getById(curNode.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build("没有管理员权限");
        }
        NodeEntity entity = new NodeEntity();
        entity.setId(param.getId());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(param.getStatus());
        boolean result = nodeDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 节点移动，当前用户必须是两个节点的管理员或长辈管理员，且to父节点里面不能包含from
     * @param param
     * @return
     */
    @Override
    public ResultVo move(boolean force, NodeMoveParam param) {
        if (param.getFromId().equals(param.getToId())) {
            return ResponseCode.OPER_ILLEGAL.build("源节点和目标节点不能相同");
        }
        NodeEntity fromNode = nodeDao.getById(param.getFromId(), NodeEntity.class);
        if (fromNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (!NodeTypeEnum.PRO_TYPE.getCode().equals(fromNode.getType())) {
            log.info("节点移动;用户{},仅支持项目节点移动;fromId={}", param.getAccount(), param.getUserId(), param.getFromId());
            return ResponseCode.OPER_ILLEGAL.build("源节点类型不支持");
        }
        NodeEntity toNode = nodeDao.getById(param.getToId(), NodeEntity.class);
        if (toNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (!NodeTypeEnum.PRO_GROUP_TYPE.getCode().equals(toNode.getType())) {
            log.info("节点移动;用户{},仅支持移动到项目组节点; toId={}", param.getUserId(), param.getToId());
            return ResponseCode.OPER_ILLEGAL.build("目标节点类型不支持");
        }
        if (toNode.getId().equals(fromNode.getParentId())) {
            return ResponseCode.OPER_ILLEGAL.build("源节点的父节点已经是目的节点");
        }
        List<Long> parentNodeIdList = getparentNodeIdList(toNode.getContent());
        if (parentNodeIdList.contains(param.getFromId())) {
            log.info("节点移动; 用户{}, from节点:{}不能是to节点:{}的长辈节点", param.getUserId(), param.getFromId(), param.getToId());
            return ResponseCode.OPER_ILLEGAL.build("源节点不能是目标节点的上级节点");
        }
        if (!force && (!isMgrOrSuperMgr(param.getUserId(), fromNode)
                || !isMgrOrSuperMgr(param.getUserId(), toNode))) {
            log.info("节点移动;用户{}不是节点fromId={} 和 toId={}的管理员", param.getUserId(), param.getFromId(), param.getToId());
            return ResponseCode.NO_OPER_PERMISSION.build("必须是目标节点的管理员或上级节点管理员");
        }
        fromNode.setParentId(toNode.getId());
        fromNode.setParentType(toNode.getTopType());
        parentNodeIdList.add(toNode.getId());
        fromNode.setContent(rebuildContentForPids(fromNode.getContent(), parentNodeIdList));
        boolean result = nodeDao.updateById(fromNode);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        updateNodeIdLink(fromNode.getId(), parentNodeIdList);
        return ResponseCode.SUCCESS.build();
    }

    private void updateNodeIdLink(Long parentId, List<Long> parentNodeIdList) {
        try {
            int pageSize = 300;
            parentNodeIdList.add(parentId);
            PageDataVo pageVo = new PageDataVo();
            pageVo.setPageSize(pageSize);
            for (int page = 1; page < 10; page++) {
                pageVo.setPage(page);
                List<NodeEntity> nodeEntities = nodeDao.getListByPage(parentId, null, null, null, pageVo);
                if (CollectionUtils.isEmpty(nodeEntities)) {
                    break;
                }
                for (NodeEntity nodeEntity : nodeEntities) {
                    nodeEntity.setContent(rebuildContentForPids(nodeEntity.getContent(), parentNodeIdList));
                    nodeLinkExecutor.execute(() -> {
                        if (nodeDao.updateById(nodeEntity)) {
                            updateNodeIdLink(nodeEntity.getId(), new ArrayList<>(parentNodeIdList));
                        }
                    });
                }
                if (page * pageSize >= pageVo.getTotal()) {
                    break;
                }
            }
        } catch (Throwable e) {
            log.error("NodeService.updateNodeIdLink 执行异常; parentId={}", parentId, e);
        }
    }

    /**
     * 查询是否是该节点或长辈节点管理员
     * @param userId
     * @param node
     * @return
     */
    @Override
    public boolean isMgrOrSuperMgr(Long userId, NodeEntity node) {
        List<Long> parentIds = getparentNodeIdList(node.getContent());
        parentIds.add(node.getId());
        return null != nodeUserRelDao.getOneByNodeIdsAndUserId(parentIds, userId, NodeUserRelTypeEnum.MANAGER.getCode());
    }

    /**
     * 查询是否是该节点成员或长辈节成员
     * @param userId
     * @param node
     * @return
     */
    @Override
    public boolean isMgrOrSuperMember(Long userId, NodeEntity node) {
        if (isMgr(userId, node.getId())) {
            return true;
        }
        List<Long> parentIds = getparentNodeIdList(node.getContent());
        return null != nodeUserRelDao.getOneByNodeIdsAndUserId(parentIds, userId, null);
    }


    /**
     * 查询是否是该节点成员或长辈节成员
     * @param userId
     * @param node
     * @return
     */
    @Override
    public boolean isMemberOrSuperMember(Long userId, NodeEntity node) {
        List<Long> parentIds = getparentNodeIdList(node.getContent());
        parentIds.add(node.getId());
        return null != nodeUserRelDao.getOneByNodeIdsAndUserId(parentIds, userId, null);
    }

    /**
     * 查询是否是该节点管理员
     * @param userId
     * @return
     */
    @Override
    public boolean isMgr(Long userId, Long nodeId) {
        return null != nodeUserRelDao.getOneByNodeIdAndUserId(nodeId, userId, NodeUserRelTypeEnum.MANAGER.getCode());
    }

    /**
     * 重新存储pids字段
     * @param content
     * @param parentNodeIdList
     * @return
     */
    @Override
    public String rebuildContentForPids(String content, List<Long> parentNodeIdList) {
        Map<String, String> map = GsonUtil.gsonToBean(content, new TypeToken<Map<String, String>>(){});
        if (map == null) {
            map = Maps.newHashMap();
        }
        map.put("pids", GsonUtil.gsonString(parentNodeIdList));
        return GsonUtil.gsonString(map);
    }

    /**
     * 获取当前节点的父节点集合
     * @param content
     * @return
     */
    @Override
    public List<Long> getparentNodeIdList(String content) {
        List<Long> parentIds =null;
        if (StringUtils.isNotBlank(content)) {
            Map<String, String> map = GsonUtil.gsonToBean(content, new TypeToken<Map<String, String>>(){});
            if (map.containsKey("pids")) {
                parentIds = GsonUtil.gsonToBean(map.get("pids"), new TypeToken<List<Long>>() {});
            }
        }
        if (parentIds == null) {
            parentIds = Lists.newArrayList();
        }
        return parentIds;
    }

    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo delete(boolean force, NodeDeleteParam param) {
        NodeEntity curNode = null;
        if (param.getId() != null) {
            curNode = nodeDao.getById(param.getId(), NodeEntity.class);
        } else {
            curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
        }
        if (curNode == null || NodeTypeEnum.TOP_TYPE.getCode().equals(curNode.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity parentNode = nodeDao.getById(curNode.getParentId(), NodeEntity.class);
        if (parentNode == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!force && !isMgrOrSuperMgr(param.getUserId(), parentNode)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        ResultVo resultVo = cascadeDelete(force, param, curNode);
        return resultVo;
    }

    @Override
    public ResultVo cascadeDelete(boolean force, BaseParam param, NodeEntity curNode) {
        //获取当前节点的子节点
        List<NodeEntity> nodeEntities = nodeDao.getByParentId(curNode.getId());
        if (!CollectionUtils.isEmpty(nodeEntities)) {
            nodeEntities.stream().sorted((o1, o2) -> o1.getId().compareTo(o2.getId()));
            for (NodeEntity entity : nodeEntities) {
                ResultVo resultVo = cascadeDelete(force, param, entity);
                if (!resultVo.success()) {
                    return resultVo;
                }
            }
        }
        //顶级节点管理员有强制删除权限
        if (!force && curNode.getOutId() != null && curNode.getOutId() > 0L && !isTopMgr(param.getUserId())) {
            return ResponseCode.OPER_FAIL.build("外部同步节点不允许删除");
        }
        if (!force && NodeTypeEnum.PRO_GROUP_TYPE.getCode().equals(curNode.getType()) && resourceDao.getOneByPoolNodeId(curNode.getId()) != null) {
            return ResponseCode.OPER_FAIL.build("项目组下有资源存在");
        }
        if (!force && NodeTypeEnum.RES_GROUP_TYPE.getCode().equals(curNode.getType()) && nodeResourceRelDao.getOneByNodeId(curNode.getId()) != null) {
            return ResponseCode.OPER_FAIL.build("环境下有资源存在");
        }
        if (!flagDao.deleteByNodeId(curNode.getId(), null)) {
            return ResponseCode.OPER_FAIL.build("节点标签或部门信息删除失败");
        }
        if (NodeTypeEnum.supportMemberNode(curNode.getType()) && !nodeUserRelDao.deleteByNodeId(curNode.getId())) {
            return ResponseCode.OPER_FAIL.build("节点成员删除失败");
        }
        if (NodeTypeEnum.supportGrantUserNode(curNode.getType()) && !userNodeRoleRelDao.deleteByNodeId(curNode.getId())) {
            return ResponseCode.OPER_FAIL.build("节点用户授权删除失败");
        }
        curNode.setUpdaterAcc(param.getAccount());
        curNode.setUpdaterId(param.getUserId());
        curNode.setUpdaterType(param.getUserType());
        boolean result = nodeDao.deleteById(curNode);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        proNodeChangeHelper.change(NodeChangeEnum.DEL, NodeUtil.toVo(curNode));
        return ResponseCode.SUCCESS.build();
    }

}
