package com.xiaomi.youpin.tesla.ip.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-20 10:03
 */
public class SelectModuleDialog extends DialogWrapper {

    private List<String> moduleList;
    private ComboBox<String> moduleComboBox;

    @Getter
    private String selectedModule;

    public SelectModuleDialog(Project project, List<String> moduleList) {
        super(project);
        this.moduleList = moduleList;
        init();
        setTitle("Athena");
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        moduleComboBox = new ComboBox<>(moduleList.toArray(new String[0]));
        panel.add(moduleComboBox);
        return panel;
    }

    @Override
    protected void doOKAction() {
        selectedModule = (String) moduleComboBox.getSelectedItem();
        super.doOKAction();
    }

}
