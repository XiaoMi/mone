package run.mone.m78.ip.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import lombok.Data;
import run.mone.openai.StreamListener;

/**
 * @author goodjava@qq.com
 * @date 2023/5/19 14:25
 */
@Data
public abstract class OpenAiListener implements StreamListener {

    private Project project;

    private Editor editor;

}
