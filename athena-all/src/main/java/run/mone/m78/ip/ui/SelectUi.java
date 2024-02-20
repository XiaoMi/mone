package run.mone.m78.ip.ui;

import com.intellij.openapi.ui.Messages;
import run.mone.m78.ip.util.ProjectUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList list1;

    private DefaultListModel<String> listModel = new DefaultListModel<>();


    public SelectUi(List<String> list) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        list.forEach(it->{
            listModel.addElement(it);
        });
        list1.setModel(listModel);
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = list1.locationToIndex(e.getPoint());
                    String selectedItem = list1.getModel().getElementAt(selectedIndex).toString();
                    dispose();
                    Messages.showMessageDialog(ProjectUtils.projectFromManager(), "mone", selectedItem, Messages.getInformationIcon());
                }
            }
        });
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 计算对话框大小
        int width = screenSize.width / 3;
        int height = screenSize.height / 3;

        // 设置对话框大小
        setSize(width, height);

        // 将对话框定位在屏幕中央
        setLocationRelativeTo(null);


    }

    public static void main(String[] args) {
        SelectUi dialog = new SelectUi(new ArrayList<>());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
