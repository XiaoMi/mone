package run.mone.mcp.git.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Git操作响应模型
 *
 * @author generated
 * @date 2025-11-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitResponse {

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 附加数据
     */
    private Object data;

    /**
     * 创建成功响应
     */
    public static GitResponse success(String message) {
        return GitResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * 创建成功响应，带数据
     */
    public static GitResponse success(String message, Object data) {
        return GitResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 创建失败响应
     */
    public static GitResponse error(String error) {
        return GitResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}
