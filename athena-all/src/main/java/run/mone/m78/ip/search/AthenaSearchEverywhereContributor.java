package run.mone.m78.ip.search;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Processor;
import run.mone.m78.ip.bo.ElementInfo;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.common.PromptType;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.service.PromptService;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.util.LabelUtils;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.openai.common.MutableObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 12:08
 */
public class AthenaSearchEverywhereContributor implements SearchEverywhereContributor<AthenaSearchInfo> {

    private Project project;

    private Module module;

    public AthenaSearchEverywhereContributor(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @NotNull
    @Override
    public String getSearchProviderId() {
        return "AthenaTab";
    }

    @NotNull
    @Override
    public String getGroupName() {
        return "Athena";
    }


    @Override
    public boolean showInFindResults() {
        return true;
    }


    /**
     * 真正执行prompt的地方
     *
     * @param selectedItem item chosen by user
     * @param text         text from search field
     * @return
     */
    @Override
    public boolean processSelectedItem(@NotNull AthenaSearchInfo selectedItem, int modifiers, @NotNull String text) {
        String promptName = selectedItem.getValue();
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PromptType promptType = Prompt.getPromptType(promptInfo);
        PromptService.dynamicInvoke(GenerateCodeReq.builder().project(this.project).module(module).promptName(promptInfo.getPromptName()).promptInfo(promptInfo).promptType(promptType).build());
        return true;
    }

    @Override
    public @NotNull ListCellRenderer<? super Object> getElementsRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AthenaSearchInfo) {
                    AthenaSearchInfo si = (AthenaSearchInfo) value;
                    setText(si.getDesc() + ":" + si.getMethodName());
                }
                return this;
            }
        };
    }

    @Nullable
    @Override
    public Object getDataForItem(@NotNull AthenaSearchInfo element, @NotNull String dataId) {
        return null;
    }

    @Override
    public int getSortWeight() {
        return 0;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }


    @Override
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super AthenaSearchInfo> consumer) {
        if (LabelUtils.open(this.project, Const.DISABLE_SEARCH)) {
            return;
        }
        List<PromptInfo> promptList = Prompt.promptList(Const.SEARCH);
        List<AthenaSearchInfo> list = promptList.stream().map(prompt -> {
            ElementInfo ei = getElementInfo(prompt);
            return AthenaSearchInfo.builder().methodName(ei.getName()).value(prompt.getPromptName()).desc(prompt.getDesc()).build();
        }).collect(Collectors.toList());

        // 根据输入的 pattern 进行过滤
        List<AthenaSearchInfo> filteredList = pattern.isBlank() ? list : list.stream()
                .filter(info -> null != info.getMethodName() && (info.getDesc().contains(pattern) || info.getMethodName().contains(pattern)))
                .toList();

        for (AthenaSearchInfo item : filteredList) {
            if (progressIndicator.isCanceled()) {
                return;
            }
            consumer.process(item);
        }
    }


    @SneakyThrows
    private ElementInfo getElementInfo(PromptInfo promptInfo) {
        MutableObject<ElementInfo> mo = new MutableObject<>();
        mo.setData(ElementInfo.builder().name("").build());
        PromptType promptType = Prompt.getPromptType(promptInfo);
        SwingUtilities.invokeAndWait(() -> {
            switch (promptType) {
                case comment, lineByLineComment, modifyMethod, createMethod2, removeComment, createMethod -> {
                    PsiMethod method = (PsiMethod) CodeService.getParentOfType(this.project, PsiMethod.class);
                    if (null != method) {
                        ElementInfo ei = ElementInfo.builder().name(method.getName()).build();
                        mo.setData(ei);
                    }
                }
                case modifyClass, createClass, createFile -> {
                    PsiClass psiClass = (PsiClass) CodeService.getParentOfType(this.project, PsiClass.class);
                    if (null != psiClass) {
                        ElementInfo ei = ElementInfo.builder().name(psiClass.getName()).build();
                        mo.setData(ei);
                    }
                }
                default -> {
                    PsiMethod method = (PsiMethod) CodeService.getParentOfType(this.project, PsiMethod.class);
                    if (null == method) {
                        ElementInfo ei = ElementInfo.builder().name("").build();
                        mo.setData(ei);
                    } else {
                        ElementInfo ei = ElementInfo.builder().name(method.getName()).build();
                        mo.setData(ei);
                    }
                }
            }
        });
        return mo.getData();
    }


}
