package com.xiaomi.youpin.tesla.ip.ui;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.TbTask;
import com.xiaomi.youpin.tesla.ip.common.ApiCall;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.listener.UltrmanTreeKeyAdapter;
import com.xiaomi.youpin.tesla.ip.util.ScreenSizeUtils;
import run.mone.ultraman.AthenaContext;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class UltramanTreeUi extends JDialog {

    private JPanel contentPane;
    private JTree tree1;
    private JTextField textField1 = new JTextField();
    private JScrollPane scrollPane;
    private JPanel webPannel;
    private JPanel treePannel;

    private Project project;


    private Gson gson = new Gson();

    public UltramanTreeUi(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        tree1.setModel(new DefaultTreeModel(null));
        tree1.setRootVisible(false);

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem("done");
        popupMenu.add(item);

        item.addActionListener(e -> {
                    //完成任务
                    TreePath path = tree1.getSelectionPath();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    TbTask task = (TbTask) node.getUserObject();
                    ApiCall call = new ApiCall();
                    Map<String, String> m = new HashMap<>();
                    m.put("user", ConfigUtils.user());
                    m.put("cmd", "closeTask");
                    m.put("taskId", task.getTaskId());
                    String res = call.postCall(ApiCall.TASK_API, gson.toJson(m), 3000);
                    System.out.println("done:" + res);
                    DefaultTreeModel dt = (DefaultTreeModel) tree1.getModel();
                    dt.removeNodeFromParent(node);
                }
        );

        JMenuItem item1 = new JMenuItem("new");
        popupMenu.add(item1);

        item1.addActionListener(e -> {
            TaskCreateUi ui = new TaskCreateUi();
            ui.setSize(ScreenSizeUtils.size());
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
            ui.setResizable(false);
            ui.requestFocus();
        });

        UltrmanTreeKeyAdapter adapter = new UltrmanTreeKeyAdapter(this.project, textField1, tree1, this.webPannel, this.treePannel);
        AthenaContext.ins().setAthenaTreeKeyAdapter(adapter);
        textField1.addKeyListener(adapter);
        this.webPannel.setVisible(false);
        textField1.setText("mone");
        String apiUrl = ConfigUtils.getConfig().getDashServer();
        adapter.loadMone(apiUrl, "mone");
    }


    public static void main(String[] args) {
        UltramanTreeUi dialog = new UltramanTreeUi(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public JPanel jpanel() {
        return this.contentPane;
    }

    private void onCancel() {
        dispose();
    }

    public JPanel webPannel() {
        return this.webPannel;
    }
}
