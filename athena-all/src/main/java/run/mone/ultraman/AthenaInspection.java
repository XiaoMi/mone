package run.mone.ultraman;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.common.PromptType;
import run.mone.m78.ip.service.PromptService;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 对方法进行重命名
 * @date 2022/5/2 09:19
 */
public class AthenaInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {


            @Override
            public void visitClass(PsiClass aClass) {

                holder.registerProblem(aClass, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return "Athena(类分析)";
                    }

                    //类分析
                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = "analyse_class";
                        invokePrompt(project,promptName);
                    }
                });

            }

            // rename_method(方法重命名) comment_2(方法添加注释) suggest_sidecar(方法review+建议)
            @Override
            public void visitMethod(PsiMethod method) {
                List<Pair<String, String>> list = Prompt.getPromptInfoByTag("method_inspection").stream()
                        .filter(it -> it.isCollected() || Prompt.containsTag(it.getTags(), "system"))
                        .map(it -> Pair.of(it.getDesc(), it.getPromptName()))
                        .collect(Collectors.toList());
                list.forEach(it -> holder.registerProblem(method, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return it.getKey();
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = it.getValue();
                        invokePrompt(project,promptName);
                    }
                }));

            }


            @Override
            //访问注释
            public void visitComment(@NotNull PsiComment comment) {
                //直接生成代码
                if (comment.getText().startsWith("//")) {
                    final String content = comment.getText().substring(2);
                    holder.registerProblem(comment, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                        @Override
                        //获取修复的家族名称
                        public @IntentionFamilyName @NotNull String getFamilyName() {
                            return "Athena(生成方法)";
                        }

                        @Override
                        //应用修复
                        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                            //根据注释生成代码
                            PromptService.generateMethod(project, content);
                        }
                    });
                }
            }


            /**
             * 给字段起新名字
             */
            @Override
            public void visitField(PsiField field) {
                holder.registerProblem(field, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return "Athena(字段重命名)";
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = "rename_field";
                        invokePrompt(project, promptName);
                    }
                });
            }
        };
    }


    public static void invokePrompt(Project project, String promptName) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PromptType promptType = Prompt.getPromptType(promptInfo);
        PromptService.dynamicInvoke(GenerateCodeReq.builder()
                .project(project)
                .promptType(promptType)
                .promptName(promptName)
                .promptInfo(promptInfo)
                .build());
    }


}
