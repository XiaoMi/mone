package run.mone.m78.ip.component.project;

import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import run.mone.m78.ip.bo.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.bo.PackageInfo;
import run.mone.ultraman.event.AthenaEventBus;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author goodjava@qq.com
 * @date 2023/6/24 21:15
 */
public class AthenaTreeSelectionListener implements TreeSelectionListener {


    public static void selectTreeNode(TreePath path) {
        if (null != path.getLastPathComponent() && path.getLastPathComponent() instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (null != userObject) {
                if (userObject instanceof PsiDirectoryNode pdn) {
                    PsiDirectory pd = pdn.getValue();
                    if (null != pd) {
                        @Nullable PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(pd);
                        if (null != psiPackage) {
                            String name = psiPackage.getQualifiedName();
                            if (StringUtils.isNotEmpty(name) && AthenaEventBus.ins().getListener().getPackageConsumer() != null) {
                                PackageInfo pi = new PackageInfo();
                                pi.setName(name);
                                int result = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
                                if (result == JOptionPane.YES_OPTION) {
                                    AthenaEventBus.ins().post(pi);
                                }
                            }
                        }
                    }
                }

                if (userObject instanceof ClassTreeNode ctn) {
                    PsiClass pc = ctn.getValue();
                    if (null != pc) {
                        String name = pc.getQualifiedName();
                        if (StringUtils.isNotEmpty(name) && AthenaEventBus.ins().getListener().getClassConsumer() != null) {
                            ClassInfo ci = ClassInfo.builder().className(pc.getQualifiedName()).build();
                            AthenaEventBus.ins().post(ci);
                        }
                    }
                }
            }

        }
    }


    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        if (false) {
            TreePath path = treeSelectionEvent.getPath();
            selectTreeNode(path);
        }
    }
}
