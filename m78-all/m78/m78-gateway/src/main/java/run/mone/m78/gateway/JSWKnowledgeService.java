package run.mone.m78.gateway;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.gateway.bo.LoginBO;
import run.mone.m78.gateway.bo.LoginData;
import run.mone.m78.gateway.bo.RequestBO;
import run.mone.m78.service.common.GsonUtils;

import java.util.UUID;

@Service("jsw")
@Slf4j

/**
 * JSWKnowledgeService类是一个实现IKnowledgeService接口的服务类。
 * 该类主要负责处理知识查询请求，并将请求发送到指定的知识库地址。
 *
 * 类中包含了多个配置属性，如知识库地址、机器人ID、组名和访问令牌，这些属性通过Spring的@Value注解进行注入。
 *
 * 主要方法：
 * - queryKnowledge(RequestBO requestBO, String sessionId): 处理知识查询请求，构建登录数据并发送请求。
 *
 * 该类使用了Spring的@Service注解进行标注，表明它是一个服务类，并且使用了@Slf4j注解来启用日志记录功能。
 */

public class JSWKnowledgeService implements IKnowledgeService {

    @Value("${knowledge.jishuwei.address}")
    private String jswAddress;

    @Value("${knowledge.jishuwei.robotId}")
    private String robotId;

    //@Value("${knowledge.jishuwei.group:汽车知识问答}")
    private String group = "汽车知识问答";

    @Value("${knowledge.jishuwei.token}")
    private String token;

    /**
     * 查询知识并发送请求
     *
     * @param requestBO 请求对象，包含用户信息和查询内容
     * @param sessionId 会话ID，用于标识当前会话
     * @return 无返回值
     */
    @Override
    public void queryKnowledge(RequestBO requestBO, String sessionId) {
        LoginData data = LoginData.builder().userId(requestBO.getUserId()).userName(requestBO.getUserName()).robotId(robotId).group(group).channel("web").token(token).build();
        LoginBO loginBO = LoginBO.builder().data(data).action("login").group(group).requestId(UUID.randomUUID().toString()).messageId(UUID.randomUUID().toString()).sessionId(UUID.randomUUID().toString()).build();
        sendWithSessionId(jswAddress, sessionId, group, GsonUtils.gson.toJson(loginBO), requestBO.getQuery(), requestBO.getRequestId(), requestBO.getHistory());
    }

}
