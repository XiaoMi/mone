package run.mone.mcp.idea.composer.handler;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:41
 */
@Data
public class PromptResult {

    private String content;
    private boolean success;
    private String error;

    public PromptResult(String content) {
        this.content = content;
        this.success = true;
    }

    public PromptResult(String error, boolean success) {
        this.error = error;
        this.success = false;
    }

    public String getContent() {
        return content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

}
