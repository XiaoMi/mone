package run.mone.ultraman.manager;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/9 11:36
 */
public class ConsoleViewManager {


    public static String getSelectedText(Project project) {
        try {
            ExecutionManager executionManager = ExecutionManager.getInstance(project);
            RunContentDescriptor currentDescriptor = executionManager.getContentManager().getSelectedContent();
            ConsoleView consoleView = (ConsoleView) currentDescriptor.getExecutionConsole();
            if (consoleView instanceof BaseTestsOutputConsoleView) {
                BaseTestsOutputConsoleView btocv = (BaseTestsOutputConsoleView) consoleView;
                @NotNull ConsoleView console = btocv.getConsole();
                if (console instanceof ConsoleViewImpl) {
                    return getText((ConsoleViewImpl) console);
                }
            }

            if (consoleView instanceof ConsoleViewImpl) {
                return getText((ConsoleViewImpl) consoleView);
            }
        } catch (Throwable ignore) {

        }
        return null;
    }


    private static String getText(ConsoleViewImpl cv) {
        @Nullable @NlsSafe String text = cv.getEditor().getSelectionModel().getSelectedText();
        System.out.println(text);
        return text;
    }
}
