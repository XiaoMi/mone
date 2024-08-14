package run.mone.z.desensitization.api.service;

import com.xiaomi.youpin.infra.rpc.Result;
import run.mone.z.desensitization.api.bo.DesensitizeReq;

/**
 * @author wmin
 * @date 2023/6/5
 */
public interface CodeDesensitizeService {

    /**
     * @param codeSnippet 纯代码片段
     * @return
     */
    Result<String> codeDesensitize(String codeSnippet);

    /**
     * @param text 包含代码片段的文本
     * @return
     */
    Result<String> textDesensitize(String text);

    Result<String> textDesensitizeWithAi(DesensitizeReq req);
}
