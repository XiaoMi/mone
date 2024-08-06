package com.xiaomi.youpin.tesla.ip.dialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.xiaomi.youpin.tesla.ip.bo.ParamDialogReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.ValueInfo;
import com.xiaomi.youpin.tesla.ip.bo.prompt.PromptParam;
import com.xiaomi.youpin.tesla.ip.bo.prompt.PromptParamType;
import com.xiaomi.youpin.tesla.ip.util.EditorUtils;
import com.xiaomi.youpin.tesla.ip.util.FileUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import com.xiaomi.youpin.tesla.ip.util.ScreenSizeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.event.AthenaEventBus;
import run.mone.ultraman.event.ConsumerBo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/5/30 09:30
 * 用来输入参数的一个泛化Table
 */
@Slf4j
public class ParamTableDialog extends DialogWrapper {

    private JBTable table;

    private Map<String, ? extends Object> initialData;

    /**
     * 会有多个选择的(选择的就放到这里)
     */
    @Getter
    private Map<String, Object> valuesMap = new HashMap<>();

    private Project project;

    private PromptInfo promptInfo;

    private ParamDialogReq req;

    @Setter
    private Consumer<Map<String, Object>> consumer;

    @Setter
    private boolean edit;

    public ParamTableDialog(ParamDialogReq req, @Nullable Project project, Map<String, ? extends Object> initialData, Map<String, List<String>> listMap, PromptInfo promptInfo) {
        super(project);
        this.req = req;
        this.project = project;
        this.promptInfo = promptInfo;
        this.initialData = initialData;
        init();
        setTitle(req.getTitle());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        //key不允许被编辑
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 2;
            }
        };
        tableModel.addColumn("Key");
        tableModel.addColumn("Value");

        // 设置初始值
        if (initialData != null) {
            for (Map.Entry<String, ? extends Object> entry : initialData.entrySet()) {
                PromptParam pp = new PromptParam();
                pp.init(entry.getKey(), this.project, this.promptInfo);
                tableModel.addRow(new Object[]{entry.getKey(), StringUtils.isNotEmpty(pp.getType()) ? pp : entry.getValue()});
            }
        }
        table = new JBTable(tableModel);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.getSelectedColumn();
                int row = table.getSelectedRow();
                Object v = table.getValueAt(row, col);
                if (v instanceof PromptParam pp) {
                    String type = pp.getType();
                    if (type.equals("editor")) {
                        ConsumerBo bo = new ConsumerBo();
                        bo.setType("value");
                        bo.setConsumer((Consumer<ValueInfo>) (p) -> SwingUtilities.invokeLater(() -> {
                            pp.setValue(p.getValue());
                            table.getModel().setValueAt(pp, row, 1);
                            ParamTableDialog.this.getRootPane().getParent().setVisible(true);
                        }));
                        AthenaEventBus.ins().post(bo);
                        ParamTableDialog.this.getRootPane().getParent().setVisible(false);
                        EditorUtils.openEditor(project, pp.getValue(), "param.md");
                    }
                }
            }
        });


        AthenaTableCellEditor atc = new AthenaTableCellEditor(project, this.promptInfo);
        atc.setTableModel(tableModel);
        atc.setDialogWrapper(this);
        table.getColumnModel().getColumn(1).setCellEditor(atc);

        JBScrollPane scrollPane = new JBScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(ScreenSizeUtils.size());
        scrollPane.setMinimumSize(ScreenSizeUtils.size());
        return scrollPane;
    }

    @Override
    public Dimension getPreferredSize() {
        return ScreenSizeUtils.size();
    }

    @Override
    protected void doOKAction() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String key = (String) tableModel.getValueAt(row, 0);
            Object value = tableModel.getValueAt(row, 1);
            String v = "";
            if (value instanceof PromptParam pp) {
                if (pp.getType().equals(PromptParamType.params.name())) {
                    String classList = pp.getValue();
                    Type typeOfT = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> names = new Gson().fromJson(classList, typeOfT);
                    String s = names.stream().map(it -> {
                        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(it, GlobalSearchScope.allScope(project));
                        if (null == psiClass) {
                            return "";
                        }
                        return psiClass.getText();
                    }).collect(Collectors.joining("\n\n"));
                    v = classList + "\n\n" + s;
                } else if (pp.getType().equals(PromptParamType.code.name())) {//如果是code,需要替换为代码
                    String classPath = pp.getValue();
                    PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(classPath, GlobalSearchScope.allScope(project));
                    v = psiClass.getText();
                } else {
                    v = pp.getValue();
                }
            } else {
                v = (String) tableModel.getValueAt(row, 1);
            }
            valuesMap.put(key, v);
        }
        super.doOKAction();
        if (null != consumer) {
            consumer.accept(valuesMap);
        }
        save();
    }

    //把配置保存到配置文件+刷新
    private void save() {
        String title = req.getTitle();
        if (title.equals("config")) {
            log.info("save config");
            String content = new GsonBuilder().setPrettyPrinting().create().toJson(this.valuesMap);
            FileUtils.writeConfig(content, ResourceUtils.USER_HOME_ATHENA_FILE_NAME);
            //刷新配置
            ResourceUtils.getAthenaConfig(this.project, true);
        }
    }

}
