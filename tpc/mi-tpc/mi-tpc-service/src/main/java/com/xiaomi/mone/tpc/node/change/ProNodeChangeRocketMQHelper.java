package com.xiaomi.mone.tpc.node.change;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.common.enums.NodeChangeEnum;
import com.xiaomi.mone.tpc.common.vo.NodeChangeVo;
import com.xiaomi.mone.tpc.common.vo.NodeUserRelVo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.node.NodeUserHelper;
import com.xiaomi.mone.tpc.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
@Slf4j
@Component("proNodeChangeRocketMQHelper")
@ConditionalOnExpression("'${project.node.change}'.equals('rocketmq')")
public class ProNodeChangeRocketMQHelper extends ProNodeChangeHelper {

    @Autowired
    private NodeUserHelper nodeUserHelper;
    private DefaultMQProducer producer;
    @NacosValue("${project.node.change.rocketmq.addr}")
    private String rocketAddr;
    @NacosValue("${project.node.change.rocketmq.ak:null}")
    private String rocketAk;
    @NacosValue("${project.node.change.rocketmq.sk:null}")
    private String rocketSk;
    @NacosValue("${project.node.change.rocketmq.producer.group:mi-tpc-producer}")
    private String producerGroup;
    @NacosValue("${project.node.change.rocketmq.instance.name:mi-tpc-producer}")
    private String instanceName;
    @NacosValue("${project.node.change.rocketmq.producer.topic:hera_app_operate}")
    private String producerTopic;
    @NacosValue("${project.node.change.rocketmq.producer.topic.tag:app_modify}")
    private String producerTopicTag;

    @PostConstruct
    public void init() throws MQClientException {
        if (!StringUtils.isEmpty(rocketSk) && !StringUtils.isEmpty(rocketSk)) {
            producer = new DefaultMQProducer(producerGroup, new AclClientRPCHook(new SessionCredentials(rocketAk, rocketSk)));
        } else {
            producer = new DefaultMQProducer(producerGroup);
        }
        producer.setNamesrvAddr(rocketAddr);
        producer.setInstanceName(instanceName);
        producer.start();
        System.out.println("Producer Started...");
    }

    @Override
    void realChange(NodeChangeEnum nodeChange, NodeVo nodeVo) throws Throwable {
        NodeChangeVo eventVo = new NodeChangeVo();
        eventVo.setEnv(nodeVo.getEnv());
        eventVo.setIamTreeType(1);//0-米IAM;1-TPC
        eventVo.setIamTreeId(nodeVo.getId().intValue());
        eventVo.setId(nodeVo.getOutId() != null && nodeVo.getOutId() > 0L ? nodeVo.getOutId() : nodeVo.getId());
        eventVo.setAppName(nodeVo.getNodeName());
        eventVo.setDelete(NodeChangeEnum.DEL.equals(nodeChange) ? 1 : 0);
        List<NodeUserRelVo> nodeUserRelVos = nodeUserHelper.list(nodeVo);
        if (!CollectionUtils.isEmpty(nodeUserRelVos)) {
            List<String> accounts = nodeUserRelVos.stream().map( vo -> UserUtil.getFullAccount(vo.getAccount(), vo.getUserType())).collect(Collectors.toList());
            eventVo.setJoinedMembers(accounts);
        }
        String eventJson = JacksonUtil.bean2Json(eventVo);
        Message msg = new Message(producerTopic,producerTopicTag, eventJson.getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = producer.send(msg);
        log.info("项目节点变更rocketmq同步: eventJson={}, sendResult={}", eventJson, sendResult);
    }

    @Override
    protected boolean isDefault() {
        return false;
    }
}
