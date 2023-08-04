/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.log.manager.user.MoneUser;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.api.service.NodeUserFacade;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;

@Service
@Slf4j
public class Tpc {
    @Resource
    private MilogSpaceDao milogSpaceDao;

    @Reference(interfaceClass = NodeFacade.class, group = "$tpc_dubbo_group", check = false, version = "1.0", timeout = 10000)
    private NodeFacade tpcService;

    @Reference(interfaceClass = NodeUserFacade.class, group = "$tpc_dubbo_group", check = false, version = "1.0", timeout = 10000)
    private NodeUserFacade tpcUserService;

    @Reference(interfaceClass = UserOrgFacade.class, group = "$tpc_dubbo_group", check = false, version = "1.0", timeout = 10000)
    private UserOrgFacade userOrgFacade;

    @Value("${tpc_node_code}")
    private String tpcNodeCode;

    private Long tpcPId;

    public boolean isAdmin(String account, Integer userType) {
        handleRemoteTpcId(tpcNodeCode, account, userType);
        NodeQryParam param = new NodeQryParam();
        param.setAccount(account);
        param.setUserType(userType);
        param.setId(tpcPId);
        param.setType(NodeTypeEnum.PRO_TYPE.getCode());
        Result<NodeVo> nodeVoResult = tpcService.get(param);
        NodeVo node = nodeVoResult.getData();
        log.debug("get user isAdmin,param:{},result:{}", GSON.toJson(param), GSON.toJson(node));
        return node.isCurrentMgr() || node.isTopMgr() || node.isParentMgr();
    }

    public List<Long> getUserPermSpaceId() {
        Result<PageDataVo<NodeVo>> res = getUserPermSpace(null, 1, 100);
        if (res == null || res.getData() == null || res.getData().getList() == null) {
            return null;
        }
        return res.getData().getList().stream().map(NodeVo::getOutId).collect(Collectors.toList());
    }

    public Result<PageDataVo<NodeVo>> getUserPermSpace(String spaceName, Integer page, Integer pageSize) {
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        if (null == currentUser) {
            log.info("user not login");
            throw new MilogManageException("please go to login");
        }
        handleRemoteTpcId(tpcNodeCode, currentUser.getUser(), currentUser.getUserType());
        NodeQryParam param = new NodeQryParam();
        param.setPager(true);
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setParentId(tpcPId);
        param.setAccount(currentUser.getUser());
        param.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        param.setUserType(currentUser.getUserType());
        if (StringUtils.isNotEmpty(spaceName)) {
            param.setNodeName(spaceName);
        }
        param.setStatus(NodeStatusEnum.ENABLE.getCode());
        // 管理员用户查询所有
        param.setMyNode(currentUser.getIsAdmin() ? false : true);
        return tpcService.orgNodelist(param);
    }

    /**
     * set tpc id from tpc server
     */
    public void handleRemoteTpcId(String tpcNodeCode, String account, Integer userType) {
        if (StringUtils.isEmpty(tpcNodeCode)) {
            throw new MilogManageException("tpc_node_code is empty,please check config file");
        }
        if (null == tpcPId) {
            Result<NodeVo> nodeVoResult = tpcService.getByNodeCode(getNodeParam(account, userType));
            tpcPId = nodeVoResult.getData().getId();
        }
        if (null == tpcPId) {
            throw new MilogManageException("query tpc id by tpc server error,tpc code:" + tpcNodeCode);
        }
    }

    private NodeQryParam getNodeParam(String account, Integer userType) {
        NodeQryParam nodeParam = new NodeQryParam();
        nodeParam.setAccount(account);
        nodeParam.setUserType(userType);
        nodeParam.setType(NodeTypeEnum.PRO_TYPE.getCode());
        nodeParam.setNodeCode(tpcNodeCode);
        return nodeParam;
    }

    public boolean hasPerm(MoneUser user, Long spaceId) {
        NodeQryParam param = new NodeQryParam();
        param.setAccount(user.getUser());
        param.setUserType(user.getUserType());
        param.setOutId(spaceId);
        param.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        Result<NodeVo> nodeVoResult = tpcService.getByOutId(param);
        NodeVo node = nodeVoResult.getData();
        return node.isTopMgr() || node.isParentMgr() || node.isCurrentMgr();
    }

    public Result deleteSpaceTpc(Long id, String account, Integer userType) {
        NodeDeleteParam delete = new NodeDeleteParam();
        delete.setOutId(id);
        delete.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        delete.setAccount(account);
        delete.setUserType(userType);
        return tpcService.delete(delete);
    }

    public Result saveSpacePerm(MilogSpaceDO spaceDO, String account) {
        NodeAddParam nodeAddParam = new NodeAddParam();
        handleRemoteTpcId(tpcNodeCode, account, UserTypeEnum.CAS_TYPE.getCode());
        nodeAddParam.setParentNodeId(tpcPId);
        nodeAddParam.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        nodeAddParam.setNodeName(spaceDO.getSpaceName());
        nodeAddParam.setDesc(spaceDO.getDescription());
        nodeAddParam.setOutId(spaceDO.getId());
        nodeAddParam.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        nodeAddParam.setAccount(account);
        nodeAddParam.setUserType(UserTypeEnum.CAS_TYPE.getCode());

        return tpcService.add(nodeAddParam);
    }

    public Result updateSpaceTpc(MilogSpaceParam param, String accout) {
        NodeEditParam edit = new NodeEditParam();
        edit.setNodeName(param.getSpaceName());
        edit.setDesc(param.getDescription());
        edit.setOutId(param.getId());
        edit.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        edit.setAccount(accout);
        edit.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        return tpcService.edit(edit);
    }

    /**
     * add space member
     *
     * @param spaceId
     * @param userAccount
     * @param userType
     * @param memberCode
     */
    public void addSpaceMember(Long spaceId, String userAccount, Integer userType, Integer memberCode) {
        NodeUserAddParam add = new NodeUserAddParam();
        add.setOutId(spaceId);
        add.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        add.setMemberAcc(userAccount);
        add.setMemberAccType(UserTypeEnum.CAS_TYPE.getCode());
        add.setType(memberCode);
        add.setAccount(userAccount);
        add.setUserType(userType);
        tpcUserService.add(add);
    }

    public NodeVo getByOuterId(Long id, Integer outType) {
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        NodeQryParam param = new NodeQryParam();
        param.setPager(false);
        param.setAccount(currentUser == null ? "wangtao29" : currentUser.getUser());
        param.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        if (null != currentUser) {
            param.setUserType(currentUser.getUserType());
        }
        param.setStatus(NodeStatusEnum.ENABLE.getCode());
        param.setOutIdType(OutIdTypeEnum.SPACE.getCode());
        param.setOutId(id);
        param.setMyNode(false);
        Result<NodeVo> nodeVoResult = tpcService.getByOutId(param);
        NodeVo data = nodeVoResult.getData();
        if (data == null) {
            return null;
        }
        return data;
    }

    public NodeVo getSpaceByOuterId(Long id) {
        return getByOuterId(id, OutIdTypeEnum.SPACE.getCode());
    }

    public String getSpaceLastOrg(Long id) {
        NodeVo spaceNode = this.getSpaceByOuterId(id);
        if (spaceNode == null || spaceNode.getOrgInfoVo() == null) {
            return "";
        }
        String spaceOrg = spaceNode.getOrgInfoVo().getNamePath();
        // system显示本组名称
        return spaceOrg.lastIndexOf('/') == -1 ? "公用space" : spaceOrg.substring(spaceOrg.lastIndexOf('/') + 1, spaceOrg.length());
    }

    public OrgInfoVo getOrg(String account, Integer userType) {
        NullParam param = new NullParam();
        param.setAccount(account);
        param.setUserType(userType);
        Result<OrgInfoVo> res = userOrgFacade.getOrgByAccount(param);
        if (res == null || res.getCode() != 0) {
            log.warn("查找用户部门失败,account:[{}], userType:[{}], res:[{}]", account, userType, res);
            return new OrgInfoVo();
        }
        return res.getData();
    }

}
