package com.xiaomi.mone.tpc.node;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.param.NodeAddParam;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeUserRelDao;
import com.xiaomi.mone.tpc.dao.impl.SystemDao;
import com.xiaomi.mone.tpc.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/31 16:14
 */
@Slf4j
@Service
public class NodeInitService implements CommandLineRunner {

    @Autowired
    private UserDao userDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;
    @Autowired
    private NodeHelper nodeHelper;

    @Autowired
    private SystemDao systemDao;

    @NacosValue("${tpc.super.account:tpc@tpc.com}")
    private String tpcSuperAccount;

    @Override
    public void run(String... args) throws Exception {
        nodeInit();
    }

    /**
     * @return
     */
    public ResultVo nodeInit() {
        UserTypeEnum userType = UserTypeEnum.EMAIL;
        log.info("tpc init call.........");
        UserEntity user = userDao.getOneByAccount(tpcSuperAccount, userType.getCode());
        log.info("tpc init user={}", user);
        if (user == null) {
            user = new UserEntity();
            user.setAccount(tpcSuperAccount);
            user.setType(userType.getCode());
            user.setStatus(UserStatusEnum.ENABLE.getCode());
            user.setCreaterId(0L);
            user.setCreaterAcc(tpcSuperAccount);
            user.setCreaterType(userType.getCode());
            user.setUpdaterId(0L);
            user.setUpdaterAcc(tpcSuperAccount);
            user.setUpdaterType(userType.getCode());
            userDao.insert(user);
            user.setCreaterId(user.getId());
            user.setUpdaterId(user.getId());
            userDao.updateById(user);
            log.info("tpc init user.result={}", user);
        }
        NodeEntity node = nodeDao.getOneByType(NodeTypeEnum.TOP_TYPE.getCode());
        log.info("tpc init topNode={}", node);
        if (node == null) {
            node = new NodeEntity();
            node.setType(NodeTypeEnum.TOP_TYPE.getCode());
            node.setStatus(NodeStatusEnum.ENABLE.getCode());
            node.setParentType(0);
            node.setParentId(0L);
            node.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            node.setTopId(0L);
            node.setNodeName("顶级节点");
            node.setDesc("顶级节点");
            node.setCreaterAcc(user.getAccount());
            node.setCreaterId(user.getId());
            node.setCreaterType(user.getType());
            node.setUpdaterAcc(user.getAccount());
            node.setUpdaterId(user.getId());
            node.setUpdaterType(user.getType());
            nodeDao.insert(node);
            node.setTopId(node.getId());
            nodeDao.updateById(node);
            log.info("tpc init topNode.result={}", node);
        }
        heraNodeInit(user, node);
        NodeUserRelEntity nodeUserRel = nodeUserRelDao.getOneByNodeIdAndUserId(node.getId(), user.getId(), NodeUserRelTypeEnum.MANAGER.getCode());
        log.info("tpc init nodeUserRel={}", nodeUserRel);
        if (nodeUserRel == null) {
            nodeUserRel = new NodeUserRelEntity();
            nodeUserRel.setNodeId(node.getId());
            nodeUserRel.setNodeType(node.getType());
            nodeUserRel.setUserId(user.getId());
            nodeUserRel.setUserType(user.getType());
            nodeUserRel.setCreaterAcc(user.getAccount());
            nodeUserRel.setCreaterId(user.getId());
            nodeUserRel.setCreaterType(user.getType());
            nodeUserRel.setUpdaterAcc(user.getAccount());
            nodeUserRel.setUpdaterId(user.getId());
            nodeUserRel.setUpdaterType(user.getType());
            nodeUserRel.setAccount(user.getAccount());
            nodeUserRel.setType(NodeUserRelTypeEnum.MANAGER.getCode());
            nodeUserRelDao.insert(nodeUserRel);
            log.info("tpc init nodeUserRel.result={}", nodeUserRel);
        }
        SystemEntity system = systemDao.getOneByName("grafana");
        if (system == null) {
            system = new SystemEntity();
            system = new SystemEntity();
            system.setSystemName("grafana");
            system.setDesc("grafana-default");
            system.setSystemToken("bd2de9d16abbc5d400277349dd3db5f0");
            system.setCreaterId(user.getId());
            system.setCreaterAcc(user.getAccount());
            system.setCreaterType(user.getType());
            system.setUpdaterId(user.getId());
            system.setUpdaterAcc(user.getAccount());
            system.setUpdaterType(user.getType());
            system.setStatus(SystemStatusEnum.ENABLE.getCode());
            systemDao.insert(system);
        }
        return ResponseCode.SUCCESS.build();
    }

    private void heraNodeInit(UserEntity user, NodeEntity top) {
        List<Long> allParentIds = Lists.newArrayList();
        allParentIds.add(top.getId());
        //hera项目组创建
        NodeEntity heraGroupNode = nodeDao.getOneByCode("hera", NodeTypeEnum.PRO_GROUP_TYPE.getCode());
        if (heraGroupNode == null) {
            heraGroupNode = new NodeEntity();
            heraGroupNode.setType(NodeTypeEnum.PRO_GROUP_TYPE.getCode());
            heraGroupNode.setStatus(NodeStatusEnum.ENABLE.getCode());
            heraGroupNode.setParentType(top.getType());
            heraGroupNode.setParentId(top.getId());
            heraGroupNode.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            heraGroupNode.setTopId(top.getId());
            heraGroupNode.setNodeName("hera");
            heraGroupNode.setCode("hera");
            heraGroupNode.setDesc("hera项目组初始化");
            heraGroupNode.setCreaterAcc(user.getAccount());
            heraGroupNode.setCreaterId(user.getId());
            heraGroupNode.setCreaterType(user.getType());
            heraGroupNode.setUpdaterAcc(user.getAccount());
            heraGroupNode.setUpdaterId(user.getId());
            heraGroupNode.setUpdaterType(user.getType());
            heraGroupNode.setContent(nodeHelper.rebuildContentForPids(null, allParentIds));
            nodeDao.insert(heraGroupNode);
            log.info("tpc init heraNode.result={}", heraGroupNode);
        }
        NodeEntity logProjectNode = nodeDao.getOneByCode("logger", NodeTypeEnum.PRO_TYPE.getCode());
        if (logProjectNode == null) {
            logProjectNode = new NodeEntity();
            logProjectNode.setType(NodeTypeEnum.PRO_TYPE.getCode());
            logProjectNode.setStatus(NodeStatusEnum.ENABLE.getCode());
            logProjectNode.setParentType(heraGroupNode.getType());
            logProjectNode.setParentId(heraGroupNode.getId());
            logProjectNode.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            logProjectNode.setTopId(top.getId());
            logProjectNode.setNodeName("logger");
            logProjectNode.setCode("logger");
            logProjectNode.setDesc("logger项目初始化");
            logProjectNode.setCreaterAcc(user.getAccount());
            logProjectNode.setCreaterId(user.getId());
            logProjectNode.setCreaterType(user.getType());
            logProjectNode.setUpdaterAcc(user.getAccount());
            logProjectNode.setUpdaterId(user.getId());
            logProjectNode.setUpdaterType(user.getType());
            allParentIds.add(heraGroupNode.getId());
            logProjectNode.setContent(nodeHelper.rebuildContentForPids(null, allParentIds));
            nodeDao.insert(logProjectNode);
            log.info("tpc init loggerNode.result={}", logProjectNode);
        }
        NodeEntity logAgentProjectNode = nodeDao.getOneByCode("log-agent", NodeTypeEnum.PRO_TYPE.getCode());
        if (logAgentProjectNode == null) {
            NodeAddParam param = new NodeAddParam();
            param.setUserId(user.getId());
            param.setAccount(user.getAccount());
            param.setUserType(user.getType());
            param.setParentNodeId(heraGroupNode.getId());
            param.setType(NodeTypeEnum.PRO_TYPE.getCode());
            param.setNodeName("log-agent");
            param.setCode("log-agent");
            param.setDesc("不要轻易修改");
            param.setOutId(10010L);
            param.setOutIdType(OutIdTypeEnum.PROJECT.getCode());
            Object result = nodeHelper.realAdd(param, heraGroupNode);
            log.info("tpc init logAgentNode.result={}", result);
        }
    }

    private void gatewayNodeInit(UserEntity user, NodeEntity top) {
        List<Long> allParentIds = Lists.newArrayList();
        allParentIds.add(top.getId());
        //hera项目组创建
        NodeEntity gatewayGroupNode = nodeDao.getOneByCode("gateway", NodeTypeEnum.PRO_GROUP_TYPE.getCode());
        if (gatewayGroupNode == null) {
            gatewayGroupNode = new NodeEntity();
            gatewayGroupNode.setType(NodeTypeEnum.PRO_GROUP_TYPE.getCode());
            gatewayGroupNode.setStatus(NodeStatusEnum.ENABLE.getCode());
            gatewayGroupNode.setParentType(top.getType());
            gatewayGroupNode.setParentId(top.getId());
            gatewayGroupNode.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            gatewayGroupNode.setTopId(top.getId());
            gatewayGroupNode.setNodeName("gateway");
            gatewayGroupNode.setCode("gateway");
            gatewayGroupNode.setDesc("gateway项目组初始化");
            gatewayGroupNode.setCreaterAcc(user.getAccount());
            gatewayGroupNode.setCreaterId(user.getId());
            gatewayGroupNode.setCreaterType(user.getType());
            gatewayGroupNode.setUpdaterAcc(user.getAccount());
            gatewayGroupNode.setUpdaterId(user.getId());
            gatewayGroupNode.setUpdaterType(user.getType());
            gatewayGroupNode.setContent(nodeHelper.rebuildContentForPids(null, allParentIds));
            nodeDao.insert(gatewayGroupNode);
            log.info("tpc init gatewayNode.result={}", gatewayGroupNode);
        }
        NodeEntity gatewayMgrProjectNode = nodeDao.getOneByCode("gateway-manager", NodeTypeEnum.PRO_TYPE.getCode());
        if (gatewayMgrProjectNode == null) {
            gatewayMgrProjectNode = new NodeEntity();
            gatewayMgrProjectNode.setType(NodeTypeEnum.PRO_TYPE.getCode());
            gatewayMgrProjectNode.setStatus(NodeStatusEnum.ENABLE.getCode());
            gatewayMgrProjectNode.setParentType(gatewayGroupNode.getType());
            gatewayMgrProjectNode.setParentId(gatewayGroupNode.getId());
            gatewayMgrProjectNode.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            gatewayMgrProjectNode.setTopId(top.getId());
            gatewayMgrProjectNode.setNodeName("gateway-manager");
            gatewayMgrProjectNode.setCode("gateway-manager");
            gatewayMgrProjectNode.setDesc("gateway-manager项目初始化");
            gatewayMgrProjectNode.setCreaterAcc(user.getAccount());
            gatewayMgrProjectNode.setCreaterId(user.getId());
            gatewayMgrProjectNode.setCreaterType(user.getType());
            gatewayMgrProjectNode.setUpdaterAcc(user.getAccount());
            gatewayMgrProjectNode.setUpdaterId(user.getId());
            gatewayMgrProjectNode.setUpdaterType(user.getType());
            allParentIds.add(gatewayGroupNode.getId());
            gatewayMgrProjectNode.setContent(nodeHelper.rebuildContentForPids(null, allParentIds));
            nodeDao.insert(gatewayMgrProjectNode);
            log.info("tpc init gatewayMgrNode.result={}", gatewayMgrProjectNode);
        }
        NodeEntity cnzoneBizNode = nodeDao.getOneByCode("cnzone", NodeTypeEnum.PART_TYPE.getCode());
        if (cnzoneBizNode == null) {
            cnzoneBizNode = new NodeEntity();
            cnzoneBizNode.setType(NodeTypeEnum.PART_TYPE.getCode());
            cnzoneBizNode.setStatus(NodeStatusEnum.ENABLE.getCode());
            cnzoneBizNode.setParentType(gatewayMgrProjectNode.getType());
            cnzoneBizNode.setParentId(gatewayMgrProjectNode.getId());
            cnzoneBizNode.setTopType(NodeTypeEnum.TOP_TYPE.getCode());
            cnzoneBizNode.setTopId(top.getId());
            cnzoneBizNode.setNodeName("中国区");
            cnzoneBizNode.setCode("cnzone");
            cnzoneBizNode.setOutId(1L);
            cnzoneBizNode.setDesc("cnzone初始化");
            cnzoneBizNode.setCreaterAcc(user.getAccount());
            cnzoneBizNode.setCreaterId(user.getId());
            cnzoneBizNode.setCreaterType(user.getType());
            cnzoneBizNode.setUpdaterAcc(user.getAccount());
            cnzoneBizNode.setUpdaterId(user.getId());
            cnzoneBizNode.setUpdaterType(user.getType());
            allParentIds.add(gatewayMgrProjectNode.getId());
            cnzoneBizNode.setContent(nodeHelper.rebuildContentForPids(null, allParentIds));
            nodeDao.insert(cnzoneBizNode);
            log.info("tpc init gatewayMgrNode.result={}", cnzoneBizNode);
        }
    }

}
