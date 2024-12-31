package com.xiaomi.youpin.tesla.ip.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.ScreenSizeUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;


/**
 * @author goodjava@qq.com
 * @author caobaoyu
 * @date 2023/7/13
 * 弹出一个包路径树
 */
public class DirectoryTreeDialog extends DialogWrapper {

    private final ComboBox<String> moduleComboBox;
    private final Tree tree;
    private final JBScrollPane scrollPane;

    @Getter
    private final DialogResult result = new DialogResult();

    public DirectoryTreeDialog(Project project) {
        super(project, false);
        List<String> moduleList = ProjectUtils.listAllModules(project);
        moduleComboBox = new ComboBox<>(moduleList.toArray(new String[0]));

        VirtualFile sourceRoot = ProjectUtils.getSourceRoot(project, moduleComboBox.getSelectedItem().toString());
        DefaultMutableTreeNode root = buildTreeNodes(sourceRoot);
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        this.tree = new Tree(treeModel);
        tree.setCellRenderer(new NodeRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
                super.customizeCellRenderer(jTree, o, b, b1, b2, i, b3);
                if (o instanceof DefaultMutableTreeNode) {
                    setIcon(AllIcons.Nodes.Folder);
                }
            }
        });

        moduleComboBox.addActionListener(e -> {
            String moduleName = (String) moduleComboBox.getSelectedItem();
            VirtualFile sourceRoot1 = ProjectUtils.getSourceRoot(project, moduleName);
            DefaultMutableTreeNode root1 = buildTreeNodes(sourceRoot1);
            DefaultTreeModel treeModel1 = new DefaultTreeModel(root1);
            tree.setModel(treeModel1);
        });

        setTitle("Select a Package");
        scrollPane = new JBScrollPane(tree);
        scrollPane.setPreferredSize(ScreenSizeUtils.size());
        scrollPane.setMinimumSize(ScreenSizeUtils.size());
        init();
    }

    private DefaultMutableTreeNode buildTreeNodes(VirtualFile directory) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(directory.getName());
        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory() && !child.getName().startsWith(".")) {
                DefaultMutableTreeNode childNode = buildTreeNodes(child);
                node.add(childNode);
            }
        }

        return node;
    }

    @Override
    protected void doOKAction() {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null && selectionPaths.length > 0) {
            for (TreePath path : selectionPaths) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                String selectedPackage = getPathFromRoot(selectedNode);
                this.result.getData().put("package", selectedPackage);
            }
        }
        String selectedModule = (String) moduleComboBox.getSelectedItem();
        this.result.getData().put("module", selectedModule);
        super.doOKAction();
    }

    private String getPathFromRoot(DefaultMutableTreeNode node) {
        StringBuilder path = new StringBuilder();
        boolean isFirst = true;
        while (node != null) {
            if (node.getParent() != null) {
                if (!isFirst) {
                    path.insert(0, ".");
                } else {
                    isFirst = false;
                }
                path.insert(0, node.getUserObject());
            }
            node = (DefaultMutableTreeNode) node.getParent();
        }
        return path.toString();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(moduleComboBox);
        panel.add(scrollPane);
        return panel;
    }
}

