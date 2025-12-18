package run.mone.hive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 调用请求 DTO
 *
 * @author goodjava@qq.com
 * @date 2025/12/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketCallRequest {

    /**
     * 路径参数
     */
    private String path;

    /**
     * 是否递归（用于列出文件等操作）
     */
    private Boolean recursive;

    /**
     * 正则表达式（用于搜索文件等操作）
     */
    private String regex;

    /**
     * 文件模式（用于搜索文件等操作）
     */
    private String filePattern;

    /**
     * 文件内容（用于写入文件等操作）
     */
    private String content;

    /**
     * 命令（用于执行命令等操作）
     */
    private String command;

    /**
     * 目录（用于执行命令等操作）
     */
    private String directory;

    /**
     * 超时时间（秒）
     */
    private Integer timeout;
}