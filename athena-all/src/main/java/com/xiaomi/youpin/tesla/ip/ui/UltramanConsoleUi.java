package com.xiaomi.youpin.tesla.ip.ui;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UltramanConsoleUi extends JDialog {
    private JPanel contentPane;
    private JTextArea textArea1;
    private JScrollPane jScrollPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public UltramanConsoleUi() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });


        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textArea1.setEditable(false);
        textArea1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                textArea1.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                textArea1.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });

        textArea1.getCaret().addChangeListener(e -> textArea1.getCaret().setVisible(true));


        textArea1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 3) {
                    JBList<String> list = new JBList<>();
                    String[] title = new String[2];
                    title[0] = "    Select All";
                    title[1] = "    Clear All";
                    list.setListData(title);
                    JBPopup popup = new PopupChooserBuilder(list)
                            .setItemChoosenCallback(() -> {
                                String value = list.getSelectedValue();
                                if ("    Clear All".equals(value)) {
                                    textArea1.setText("");
                                } else if ("    Select All".equals(value)) {
                                    textArea1.selectAll();
                                }
                            }).createPopup();
                    Dimension dimension = popup.getContent().getPreferredSize();
                    popup.setSize(new Dimension(150, dimension.height));
                    popup.show(new RelativePoint(mouseEvent));
                }
            }
        });



    }


    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        UltramanConsoleUi dialog = new UltramanConsoleUi();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public JPanel jpanel() {
        return this.contentPane;
    }
}
