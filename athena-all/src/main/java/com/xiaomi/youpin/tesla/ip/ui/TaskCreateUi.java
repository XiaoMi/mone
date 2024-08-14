package com.xiaomi.youpin.tesla.ip.ui;

import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.ip.bo.UserBo;
import com.xiaomi.youpin.tesla.ip.common.ApiCall;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.service.UserService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class TaskCreateUi extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea contentTextArea;
    private JComboBox comboBox1;

    private Gson gson = new Gson();

    public TaskCreateUi() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiCall call = new ApiCall();
                Map<String, String> m = new HashMap<>();
                m.put("cmd", "createTask");
                m.put("user", ConfigUtils.user());
                m.put("content", contentTextArea.getText());
                UserBo ub = (UserBo) comboBox1.getSelectedItem();
                m.put("executorName", ub.getEmail().split("@")[0]);
                call.postCall(ApiCall.TASK_API, new Gson().toJson(m), 3000);
                onCancel();
            }
        });

        UserService us = new UserService();
        us.userBoList().forEach(it -> {
            this.comboBox1.addItem(it);
        });


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TaskCreateUi dialog = new TaskCreateUi();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
