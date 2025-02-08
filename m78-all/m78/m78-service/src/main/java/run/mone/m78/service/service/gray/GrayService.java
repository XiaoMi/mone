package run.mone.m78.service.service.gray;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78KnowledgeBase;
import run.mone.m78.service.dao.entity.M78Workspace;
import run.mone.m78.service.dao.mapper.M78KnowledgeBaseMapper;
import run.mone.m78.service.dao.mapper.M78WorkspaceMapper;

import java.util.Objects;

/**
 * 灰度切流相关
 *
 * @author zhengjinqiu
 */
@Slf4j
@Service
public class GrayService {

    @Autowired
    private M78KnowledgeBaseMapper m78KnowledgeBaseMapper;

    @Autowired
    private M78WorkspaceMapper m78WorkspaceMapper;

    //切流量到knowledge
    public static final Integer KNOWLEDGE_VERSION_V2 = 2;

    @NacosValue(value = "${knowledge.gray.switch:false}", autoRefreshed = true)
    private String graySwitch;

    /**
     * 是否灰度到新知识库
     *
     * @param knowledgeBaseId
     * @return
     */
    public boolean gray2Knowledge(Long knowledgeBaseId) {
        M78KnowledgeBase knowledgeBase = m78KnowledgeBaseMapper.selectOneByQuery(new QueryWrapper().eq("knowledge_base_id", knowledgeBaseId));
        if (knowledgeBase == null) {
            return false;
        }
        boolean isGray = Objects.equals(knowledgeBase.getVersion(), KNOWLEDGE_VERSION_V2);
        if (isGray) {
            log.info("灰度切流到新版知识库， knowledgeBaseId={}", knowledgeBase);
        }
        return isGray;
    }

    /**
     * 创建知识灰度版本2.0
     *
     * @param workspaceId 工作空间的唯一标识
     * @return 如果工作空间存在且版本为知识版本2.0，则返回true；否则返回false
     */
    public boolean createKnowledgeGray2V2(Long workspaceId) {
        M78Workspace m78Workspace = m78WorkspaceMapper.selectOneById(workspaceId);
        return m78Workspace != null && Objects.equals(m78Workspace.getVersion(), KNOWLEDGE_VERSION_V2);
    }

    /**
     * 检查当前状态是否为灰色模式
     *
     * @return 如果灰色开关为真，则返回true；否则返回false
     */
    public boolean isGray() {
        return Boolean.parseBoolean(graySwitch);
    }


}
