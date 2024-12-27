package com.xiaomi.youpin.tesla.ip.dialog;

import com.google.common.base.Joiner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.xiaomi.youpin.tesla.ip.bo.ClassInfo;
import com.xiaomi.youpin.tesla.ip.bo.CreateClassRes;
import com.xiaomi.youpin.tesla.ip.bo.Message;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.prompt.PromptParam;
import com.xiaomi.youpin.tesla.ip.bo.prompt.PromptParamType;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.ui.CreateClassUi;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.bo.PackageInfo;
import run.mone.ultraman.bo.ParamsInfo;
import run.mone.ultraman.event.AthenaEventBus;
import run.mone.ultraman.event.ConsumerBo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @author caobaoyu
 * @date 2023/6/15 14:02
 */
public class AthenaTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JComboBox comboBox;

    private JTextField textField;

    private JButton button;

    private JLabel label;

    private PromptInfo promptInfo;

    @Setter
    private DialogWrapper dialogWrapper;

    @Setter
    private DefaultTableModel tableModel;

    private Object value;

    /**
     * 有若干列是combox
     *
     * @param project
     * @param promptInfo
     */
    public AthenaTableCellEditor(Project project, PromptInfo promptInfo) {
        this.promptInfo = promptInfo;
        this.textField = new JTextField();
        this.button = new JButton();
        this.label = new JLabel();
        this.comboBox = new JComboBox();
        addListenerToJComboBox(this.comboBox, project);
    }


    private void addListenerToJComboBox(JComboBox c, Project project) {
        c.addActionListener(e -> {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String selectedOption = (String) comboBox.getSelectedItem();

            //创建类
            if (selectedOption.equals("New")) {
                String packageStr = LabelUtils.getLabelValue(project, this.promptInfo, Const.REQ_PACKAGE, "");
                CreateClassUi createClassUi = new CreateClassUi(project, ProjectUtils.listAllModules(project), ProjectUtils.getCurrentModudleName(project), packageStr);
                createClassUi.show();
                CreateClassRes res = createClassUi.getRes();
                if (res.getCode() != DialogWrapper.OK_EXIT_CODE) {
                    return;
                }
                String value = Joiner.on(".").join(res.getPackageStr(), res.getClassName());
                comboBox.addItem(value);
                comboBox.setSelectedItem(value);
            }

            if (selectedOption.equals("Params")) {
                ConsumerBo bo = new ConsumerBo();
                bo.setType("params");
                this.dialogWrapper.getRootPane().getParent().setVisible(false);
                bo.setConsumer((Consumer<ParamsInfo>) (p) -> SwingUtilities.invokeLater(() -> {
                    if (this.value instanceof PromptParam pp) {
                        pp.getList().add(p.getParams());
                        pp.setValue(p.getParams());
                    }
                    this.dialogWrapper.getRootPane().getParent().setVisible(true);
                }));
                AthenaEventBus.ins().post(bo);
            }

            //选择包路径
            if (selectedOption.equals("Select")) {
                int result = JOptionPane.showConfirmDialog(null, Message.selectPackage, "Confirmation", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
                this.dialogWrapper.getRootPane().getParent().setVisible(false);
                ConsumerBo bo = new ConsumerBo();

                String subType = "package";
                if (this.value instanceof PromptParam pp) {
                    if (StringUtils.isNotEmpty(pp.getSubType())) {
                        subType = pp.getSubType();
                    }
                }
                if (subType.equals("package")) {
                    bo.setType("package");
                    bo.setConsumer((Consumer<PackageInfo>) (p) -> SwingUtilities.invokeLater(() -> {
                        if (this.value instanceof PromptParam pp) {
                            pp.getList().add(p.getName());
                            pp.setValue(p.getName());
                        }
                        this.dialogWrapper.getRootPane().getParent().setVisible(true);
                    }));
                }
                if (subType.equals("class")) {
                    bo.setType("class");
                    bo.setConsumer((Consumer<ClassInfo>) (p) -> SwingUtilities.invokeLater(() -> {
                        if (this.value instanceof PromptParam pp) {
                            pp.getList().add(p.getName());
                            pp.setValue(p.getName());
                        }
                        this.dialogWrapper.getRootPane().getParent().setVisible(true);
                    }));
                }
                AthenaEventBus.ins().post(bo);
            }

            if (selectedOption.equals("Select2")) {
                DirectoryTreeDialog dialog = new DirectoryTreeDialog(project);
                dialog.show();
                String selectedPath = dialog.getResult().getData().get("package");
                comboBox.addItem(selectedPath);
                comboBox.setSelectedItem(selectedPath);
                String modulePath = dialog.getResult().getData().get("module");
                addModuleRow(modulePath);
            }
        });
    }


    @Override
    public Object getCellEditorValue() {
        if (this.value instanceof PromptParam pp) {
            if (pp.getType().equals(PromptParamType.comboBox.name()) || pp.getType().equals(PromptParamType.code.name()) || pp.getType().equals(PromptParamType.params.name())) {
                pp.setValue(this.comboBox.getSelectedItem().toString());
            }
            if (pp.getType().equals(PromptParamType.textField.name())) {
                pp.setValue(this.textField.getText().trim());
            }
            return this.value;
        }
        return this.textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        if (this.value instanceof PromptParam pp) {
            String type = pp.getType();
            if (type.equals(PromptParamType.button.name())) {
                this.button.setText(pp.getValue());
                return this.button;
            }
            if (type.equals(PromptParamType.textField.name())) {
                this.textField.setText(pp.getValue());
                return this.textField;
            }
            if (type.equals(PromptParamType.comboBox.name()) || type.equals(PromptParamType.code.name()) || type.equals(PromptParamType.params.name())) {
                this.comboBox.setModel(new DefaultComboBoxModel(pp.getList().toArray(new String[0])));
                return this.comboBox;
            }
            if (type.equals(PromptParamType.label.name()) || type.equals(PromptParamType.editor.name())) {
                this.label.setText(pp.getValue());
                return this.label;
            }
        }
        this.textField.setText(this.value.toString());
        return this.textField;

    }

    private void addModuleRow(String modulePath) {
        int rowCount = tableModel.getRowCount();
        boolean moduleExists = false;
        int moduleRow = -1;

        // 遍历表格中的每一行，查找是否已经存在该module
        for (int i = 0; i < rowCount; i++) {
            String currentModule = (String) tableModel.getValueAt(i, 0);
            if (currentModule.equals("module")) {
                moduleExists = true;
                moduleRow = i;
                break;
            }
        }

        // 如果存在，则更新对应的modulePath
        if (moduleExists) {
            tableModel.setValueAt(modulePath, moduleRow, 1);
        } else {
            // 否则添加新的一行
            tableModel.addRow(new Object[]{"module", modulePath});
        }
    }

}
