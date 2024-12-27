package run.mone.ultraman.ai;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.xiaomi.youpin.tesla.ip.common.ProjectCache;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * goodjava@qq.com
 * 目前不好用...后边想想办法
 */
public class MyCompletionContributor extends CompletionContributor {

    public MyCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        Editor editor = parameters.getEditor();
                        Project project = editor.getProject();

                        try {
                            PsiElement position = parameters.getPosition();
                            PsiElement prevSibling = position.getPrevSibling();

                            if (prevSibling instanceof PsiReferenceParameterList) {
                                prevSibling = prevSibling.getPrevSibling();
                                if (prevSibling instanceof PsiJavaToken && ((PsiJavaToken) prevSibling).getTokenType().equals(JavaTokenType.DOT)) {
                                    prevSibling = prevSibling.getPrevSibling();
                                    if (prevSibling.getReference().resolve() instanceof PsiField) {
                                        PsiField psiField = (PsiField) prevSibling.getReference().resolve();
                                        String str = psiField.getType().getCanonicalText();
                                        if (str.equals("com.xiaomi.sautumn.serverless.api.Context")) {
                                            System.out.println(1);
                                            generateCodeSuggestion(project, resultSet, "System", str);
                                        }
                                    }
                                    if (prevSibling.getReference().resolve() instanceof PsiVariable) {
                                        PsiVariable psiVariable = (PsiVariable) prevSibling.getReference().resolve();
                                        String str = psiVariable.getType().getCanonicalText();
                                        if (str.equals("com.xiaomi.sautumn.serverless.api.Context")) {
                                            System.out.println(2);
                                            generateCodeSuggestion(project, resultSet, "System", str);
                                        }

                                    }
                                }
                            }

                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            throw ex;
                        }

                    }
                }
        );
    }

    private void generateCodeSuggestion(Project project, CompletionResultSet resultSet, String str, String clazz) {
        List<String> list = null;
        Object v = ProjectCache.get(project, str);
        if (null != v) {
            list = (List<String>) v;
        } else {
            list = CodeService.listMethodInfo(clazz);
            ProjectCache.put(project, str, list);
        }
        list.forEach(it -> {
            resultSet.addElement(LookupElementBuilder.create(it).withTypeText("mone support").withBoldness(true));
        });
    }
}
