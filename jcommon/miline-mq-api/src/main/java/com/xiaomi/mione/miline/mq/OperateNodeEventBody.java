package com.xiaomi.mione.miline.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点操作事件消息体
 *
 * @author qoder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperateNodeEventBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用变更id
     */
    private String changeId;

    /**
     * 应用变更名称
     */
    private String changeName;

    /**
     * 环境
     */
    private String env;

    /**
     * meego工作项ID，如需求ID
     */
    private Integer workItemId;

    /**
     * meego工作项标题/需求名称
     */
    private String workItemTitle;

    /**
     * meego空间id或者空间域名
     */
    private String projectSpace;

    /**
     * 操作用户(即触发节点流转的用户)，格式：邮箱前缀
     */
    private String operator;

    /**
     * 操作时间, Unix时间戳(单位：秒)
     */
    private Long time;

    /**
     * 扩展字段
     */
    private Object extend;

    /**
     * Git 开发分支
     */
    private String gitDevBranch;

    /**
     * Git 源分支
     */
    private String gitSourceBranch;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 服务器区域
     */
    private String serverZone;

    /**
     * 流水线ID
     */
    private Long pipelineId;

    /**
     * 提交ID
     */
    private String commitId;

    /**
     * 评审人
     */
    private String reviewer;

    /**
     * 环境组
     */
    private String envGroup;
}
