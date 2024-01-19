package run.mone.ultraman.common;

import com.intellij.ide.actions.GotoActionAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * @author goodjava@qq.com
 * @date 2023/4/20 16:54
 */
public class SearchUtils {

    public static void search(Project project,String searchData) {
        GotoActionAction gotoActionAction = new MyGotoActionAction();
        AnActionEvent anAction = ActionEventUtils.createAnAction(project,searchData);
        gotoActionAction.actionPerformed(anAction);
    }


}
