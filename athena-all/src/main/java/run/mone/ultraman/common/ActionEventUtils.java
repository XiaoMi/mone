package run.mone.ultraman.common;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2023/4/20 17:22
 */
public class ActionEventUtils {


    public static AnActionEvent createAnAction(Project project, String data) {
        // 创建一个新的 Presentation 对象
        Presentation presentation = new Presentation();
        // 获取 ActionManager 实例
        ActionManager actionManager = ActionManager.getInstance();

        // 创建一个新的 DataContext 对象
        DataContext dataContext = new MyDataContext(project, data);

        // 创建一个新的 AnActionEvent 对象
        AnActionEvent anActionEvent = new AnActionEvent(
                null,
                dataContext,
                "",
                presentation,
                actionManager,
                0
        );
        return anActionEvent;
    }

    private static class MyDataContext implements DataContext {
        private final Project project;

        private String data;

        public MyDataContext(Project project, String data) {
            this.project = project;
            this.data = data;
        }

        @Override
        public Object getData(String dataId) {
            if (CommonDataKeys.PROJECT.is(dataId)) {
                return project;
            }
            if (dataId.equals(SpeedSearchSupply.SPEED_SEARCH_CURRENT_QUERY.getName())) {
                return this.data;
            }
            // 如果需要支持其他 dataId，可以在此处添加相应的条件
            return null;
        }

    }
}