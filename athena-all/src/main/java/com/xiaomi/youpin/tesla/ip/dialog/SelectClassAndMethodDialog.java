package com.xiaomi.youpin.tesla.ip.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBList;
import com.xiaomi.youpin.tesla.ip.bo.ClassInfo;
import com.xiaomi.youpin.tesla.ip.bo.MethodInfo;
import com.xiaomi.youpin.tesla.ip.bo.ModuleInfo;
import com.xiaomi.youpin.tesla.ip.bo.PsiInfo;
import com.xiaomi.youpin.tesla.ip.service.ClassFinder;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.ScreenSizeUtils;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author caobaoyu
 * @author goodjava@qq.com
 * @description: 对话框的一些操作
 * @date 2023-04-25 14:42
 */
public class SelectClassAndMethodDialog extends DialogWrapper {

    public JPanel rootPanel;

    private JTabbedPane tabbedPane;

    private JTextField searchField;

    private Map<String, JBList<PsiInfo>> listMap = new HashMap<>();

    private Project project;

    /**
     * 调用后传出去的结果
     */
    @Getter
    private DialogResult result = new DialogResult();

    private DialogContext context = new DialogContext();

    private String moduleName;

    private String selectModuleName;

    /**
     * 调用这个dialog传进来的参数
     */
    private DialogReq req;

    private boolean newClass = false;

    public SelectClassAndMethodDialog(Project project, DialogReq req) {
        super(project);
        this.project = project;
        this.moduleName = req.getModule().getName();
        this.req = req;
        this.context.setModule(req.getModule());
        init();
        setTitle("Athena(" + moduleName + ")");
        setOKButtonText("Next");
    }


    private void initList(List<? extends PsiInfo> list, String name, int selectIndex) {
        JPanel controllerPanel = new JPanel(new BorderLayout());
        JBList<PsiInfo> jbList = new JBList<>(list);

        jbList.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.toString());
            if (value.isHidden()) {
                label.setText("");
            }
            return label;
        });


        jbList.setSelectedIndex(selectIndex);
        controllerPanel.add(jbList, BorderLayout.CENTER);
        tabbedPane.addTab(name, controllerPanel);
        listMap.put(name, jbList);
    }


    private void changeListModel(List<? extends PsiInfo> list, String name) {
        DefaultListModel<PsiInfo> listModel = new DefaultListModel<>();
        listModel.addAll(list);
        listMap.get(name).setModel(listModel);
        listMap.get(name).setSelectedIndex(0);
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        searchField = new JTextField();
        searchField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = searchField.getText();
                String selectedTab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
                JBList<PsiInfo> tab = listMap.get(selectedTab);
                ListModel<PsiInfo> listModel = tab.getModel();
                if (StringUtils.isNotEmpty(text)) {
                    for (int i = 0; i < listModel.getSize(); i++) {
                        PsiInfo value = listModel.getElementAt(i);
                        if (!value.getName().contains(text)) {
                            listModel.getElementAt(i).hidden(true);
                        }
                    }
                } else {
                    for (int i = 0; i < listModel.getSize(); i++) {
                        listModel.getElementAt(i).hidden(false);
                    }
                }
                notifyChange(tab, listModel);
            }
        });
        // 创建一个选项卡面板
        tabbedPane = new JTabbedPane();

        @NotNull List<PsiInfo> moduleList = getModuleList();
        initList(moduleList, "Module", getModuleIndex(moduleList));
        initList(Lists.newArrayList(), "Class", 0);
        initList(Lists.newArrayList(), "Method", 0);

        JScrollPane scrollPane = new JScrollPane(tabbedPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchField, scrollPane);
        splitPane.setDividerLocation(searchField.getPreferredSize().height);

        rootPanel.add(splitPane, BorderLayout.CENTER);
        rootPanel.setPreferredSize(ScreenSizeUtils.size());
        rootPanel.setMinimumSize(ScreenSizeUtils.size());

        if (req.getCmd().equals("test")) {
            tabbedPane.setSelectedIndex(0);
        }

        return rootPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return ScreenSizeUtils.size();
    }

    private int getModuleIndex(List<PsiInfo> moduleList) {
        return IntStream.range(0, moduleList.size()).filter(i -> moduleList.get(i).getName().equals(this.moduleName)).findAny().getAsInt();
    }

    private static void notifyChange(JBList<PsiInfo> tab, ListModel<PsiInfo> listModel) {
        ListDataListener[] listeners = ((DefaultListModel) listModel).getListDataListeners();
        for (ListDataListener listener : listeners) {
            listener.contentsChanged(new ListDataEvent(tab, ListDataEvent.CONTENTS_CHANGED, 0, listModel.getSize()));
        }
    }

    @NotNull
    private List<PsiInfo> getModuleList() {
        return ProjectUtils.listAllModules(this.project).stream().map(it ->
                ModuleInfo.builder().name(it).
                        build()).collect(Collectors.toList());
    }

    @Override
    protected void doOKAction() {
        // 获取用户选择的选项卡和列表项
        String selectedTab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        String selectedItem = null;

        if (selectedTab.equals("Module")) {
            String moduleName = this.listMap.get("Module").getSelectedValue().getName();
            this.selectModuleName = moduleName;
            String name = req.getName();
            String type = req.getType();
            List<ClassInfo> classList = ClassFinder.findClassList(project, type, name, moduleName);
            if (this.newClass) {
                //class 支持new出来
                classList.add(ClassInfo.builder().className("New").build());
            }
            changeListModel(classList, "Class");
            tabbedPane.setSelectedIndex(1);
        }

        if (selectedTab.equals("Class")) {
            if (req.getCmd().equals("test") || req.getCmd().equals("docean_controller")) {
                JBList<PsiInfo> list = this.listMap.get("Class");
                if (null == list || list.isEmpty()) {
                    return;
                }
                String clazz = list.getSelectedValue().getName();
                this.result.getData().put("class", clazz);
                //得用选中的selectModule,不然找不到class
                PsiClass psiClass = CodeService.getPsiClass(project, ProjectUtils.getModuleWithName(project, selectModuleName), clazz);
                List<PsiInfo> methodList = new ArrayList<>();
                if (null != psiClass) {
                    //查找所有public方法
                    methodList.addAll(CodeService.methods(psiClass).stream().map(it -> {
                        MethodInfo mi = new MethodInfo(it);
                        return mi;
                    }).collect(Collectors.toList()));
                    this.result.getData().put("new_class", "false");
                } else {
                    //新的类的话,根本就不用选择方法
                    this.result.getData().put("new_class", "true");
                    this.result.getData().put("method", "New");
                    if (clazz.equals("New")) {
                        String v = JOptionPane.showInputDialog("Enter class Name");
                        if (StringUtils.isEmpty(v)) {
                            return;
                        }
                        this.result.getData().put("class", v);
                    }
                }

                if (!req.isCreateMethod()) {
                    super.doOKAction();
                }

                methodList.add(0, new MethodInfo("New"));
                changeListModel(methodList, "Method");
                this.result.setCmd(req.getCmd());
                tabbedPane.setSelectedIndex(2);
            }
        }

        if (selectedTab.equals("Method")) {
            if (req.getCmd().equals("test") || req.getCmd().equals("docean_controller")) {
                String method = this.listMap.get("Method").getSelectedValue().getName();
                this.result.getData().put("method", method);
                super.doOKAction();
            }
        }

        if (selectedTab.equals("Controller")) {
            selectedItem = this.listMap.get("Controller").getSelectedValue().getName();
        }
    }


}
