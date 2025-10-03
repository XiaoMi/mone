package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Markdown文档数据模型
 * 支持指定格式的markdown文档，包含以下字段：
 * - name: 名称
 * - profile: 简介
 * - goal: 目标
 * - constraints: 约束条件
 * - workflow: 工作流程
 * - agentPrompt: 代理提示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkdownDocument {
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 简介
     */
    private String profile;
    
    /**
     * 目标
     */
    private String goal;
    
    /**
     * 约束条件
     */
    private String constraints;
    
    /**
     * 工作流程
     */
    private String workflow;
    
    /**
     * 代理提示
     */
    private String agentPrompt;

    private String fileName;
    
    /**
     * 验证文档是否有效
     * @return 如果所有必需字段都不为空则返回true
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               profile != null && !profile.trim().isEmpty() &&
               goal != null && !goal.trim().isEmpty();
    }
    
    /**
     * 获取文档摘要
     * @return 文档的简短描述
     */
    public String getSummary() {
        return String.format("MarkdownDocument[name=%s, profile=%s]", 
                           name != null ? name : "未设置", 
                           profile != null ? (profile.length() > 50 ? profile.substring(0, 50) + "..." : profile) : "未设置");
    }
}
