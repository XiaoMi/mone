package run.mone.m78.ip.ui;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import run.mone.m78.ip.bo.CreateClassRes;
import run.mone.m78.ip.util.PsiClassUtils;
import run.mone.m78.ip.util.ScreenSizeUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class CreateClassUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JRadioButton buildRadioButton;
    private JRadioButton dataRadioButton;
    private JButton buttonCancel;
    private Project project;

    @Getter
    private CreateClassRes res = new CreateClassRes();


    public CreateClassUi(Project project, List<String> moduleList, String currentModule, String packageStr) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        ComboBoxModel<String> model = new DefaultComboBoxModel<>(moduleList.toArray(String[]::new));
        this.comboBox1.setModel(model);
        if (StringUtils.isNotEmpty(currentModule)) {
            this.comboBox1.setSelectedItem(currentModule);
        }


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

        this.textField1.setText(packageStr);

        //控制大小
        this.setSize(ScreenSizeUtils.size());
        //居中显示
        this.setLocationRelativeTo(null);
    }

    private void onOK() {
        String module = this.comboBox1.getSelectedItem().toString();
        String packageName = this.textField1.getText().trim();
        String className = this.textField2.getText().trim();
        res.setModuleName(module);
        res.setPackageStr(packageName);
        res.setClassName(className);
        res.setCode(DialogWrapper.OK_EXIT_CODE);
        PsiClassUtils.createEmptyClass(project, module, packageName, className, false, false, null, false);
        dispose();
    }

    private void onCancel() {
        res.setCode(DialogWrapper.CANCEL_EXIT_CODE);
        dispose();
    }

    public static void main(String[] args) {
        CreateClassUi dialog = new CreateClassUi(null, Lists.newArrayList("a", "b"), "b", "");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
