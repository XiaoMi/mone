package run.mone.m78.ip.component.project;


import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.common.NotificationCenter;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/6/23 21:13
 */
public class AthenaProjectComment implements ProjectComponent {


    private Project project;

    public AthenaProjectComment(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        ProjectView projectView = ProjectView.getInstance(project);
        new Thread(() -> {
            int i = 200;
            for (; i > 0; i--) {
                ProjectViewPane projectViewPane = (ProjectViewPane) projectView.getCurrentProjectViewPane();
                if (projectViewPane != null) {
                    ProjectViewTree projectViewTree = (ProjectViewTree) projectViewPane.getTree();
                    projectViewTree.addTreeSelectionListener(new AthenaTreeSelectionListener());
                    projectViewTree.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                ProjectViewTree t = projectViewTree;
                                TreePath treePath = t.getSelectionPath();
                                AthenaTreeSelectionListener.selectTreeNode(treePath);
                            }
                        }
                    });
                    NotificationCenter.notice(this.project, "tree listener init finish", true);
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public void projectClosed() {
    }

    @Override
    public @NotNull String getComponentName() {
        return "AthenaProjectComment";
    }
}
