package run.mone.m78.ip.service;

import com.intellij.openapi.actionSystem.AnActionEvent;
import run.mone.m78.ip.common.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2023/3/11 21:36
 * <p>
 * 在editor中生成代码的都走这里了
 */
@Slf4j
public class ChatGptService extends AbstractService {


    @Override
    public void execute(Context context, AnActionEvent e) {
        String content = context.getContent();
        if (null != e) {
            context.setProject(e.getProject());
        }
        if (!(content.startsWith("//"))) {
            next(context, e);
            return;
        }
        PromptService.generateMethod(e.getProject(), content);
    }


}
