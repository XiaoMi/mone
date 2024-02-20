/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.m78.ip.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import run.mone.m78.ip.common.ApiCall;
import run.mone.m78.ip.common.Context;
import run.mone.m78.ip.common.NotificationCenter;
import run.mone.m78.ip.util.EditorUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 */
public class AthenaAction extends AnAction {

    private static final Logger log = Logger.getInstance(AthenaAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String content = getText(e);
        log.info("Ultraman req:" + content);
        ActionChain chain = new ActionChain();
        chain.init();
        Context context = new Context();
        context.setContent(content);
        chain.execute(context, e);
        if (content.equals(ActionEnum.background.name())) {
            NotificationCenter.notice("background");
            log.info("background");
            String image = new ApiCall().callOne(ApiCall.IMAGE_API);
            PropertiesComponent prop = PropertiesComponent.getInstance();
            prop.setValue(IdeBackgroundUtil.FRAME_PROP, null);
            prop.setValue(IdeBackgroundUtil.EDITOR_PROP, image);
            IdeBackgroundUtil.repaintAllWindows();
            return;
        }
    }

    private String getText(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        return EditorUtils.getSelectContent(editor);
    }

}
