package run.mone.hive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * WebSocket 调用响应 DTO
 *
 * @author goodjava@qq.com
 * @date 2025/12/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketCallResponse {

    /**
     * 调用是否成功
     */
    private boolean success;

    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 调用的动作类型
     */
    private String action;

    /**
     * 调用耗时（毫秒）
     */
    private Long duration;

    /**
     * WebSocket 响应数据
     */
    private Map<String, Object> response;

    /**
     * 错误信息（如果有）
     */
    private String error;

    /**
     * 目录路径
     */
    private String directoryPath;

    /**
     * 是否递归
     */
    private Boolean recursive;

    /**
     * 文件列表结果（JSON 字符串）
     */
    private String result;

    /**
     * 文件操作模式
     */
    private String mode;
}