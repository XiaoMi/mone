package run.mone.m78.gateway;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import run.mone.m78.gateway.bo.RequestBO;
import run.mone.m78.gateway.constant.KnowledgeEnum;

@Service

/**
 * KnowledgeGatewayService类是一个服务类，负责处理知识查询的请求。
 * 它通过Spring的ApplicationContext获取不同类型的知识服务，并调用相应的服务进行知识查询。
 * 该类的主要职责是根据传入的查询类型和请求参数，选择合适的知识服务并执行查询操作。
 *
 * 依赖注入:
 * - ApplicationContext: 用于获取知识服务的Spring上下文。
 *
 * 方法:
 * - queryKnowledge(RequestBO requestBO, String type, String sessionId): 根据查询类型和请求参数，调用相应的知识服务进行查询。
 *
 * 注: 该类使用了Spring的@Service注解，表明它是一个服务组件。
 */

public class KnowledgeGatewayService {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 查询知识信息
     *
     * @param requestBO 请求对象，包含查询所需的参数
     * @param type      查询类型，用于确定使用的知识服务
     * @param sessionId 会话ID，用于标识当前会话
     * @return 无返回值
     */
    public void queryKnowledge(RequestBO requestBO, String type, String sessionId) {
        IKnowledgeService knowledgeService = null;
        if (StringUtils.isEmpty(type)) {
            knowledgeService = (IKnowledgeService) applicationContext.getBean(KnowledgeEnum.JSW.getType());
        } else if (KnowledgeEnum.KBS.getType().equals(type)) {
            knowledgeService = (IKnowledgeService) applicationContext.getBean(KnowledgeEnum.KBS.getType());
        } else if (KnowledgeEnum.JSW.getType().equals(type)) {
            knowledgeService = (IKnowledgeService) applicationContext.getBean(KnowledgeEnum.JSW.getType());
        } else if (KnowledgeEnum.KNOWLEDGE.getType().equals(type)) {
            knowledgeService = (IKnowledgeService) applicationContext.getBean(KnowledgeEnum.KNOWLEDGE.getType());
        }
        knowledgeService.queryKnowledge(requestBO, sessionId);
    }


}
