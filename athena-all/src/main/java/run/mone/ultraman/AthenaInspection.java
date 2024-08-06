package run.mone.ultraman;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.EditorUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class AthenaInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitParameter(@NotNull PsiParameter parameter) {
                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;

                // 获取参数名
                String paramName = parameter.getName();
                // 获取参数类型
                PsiElement parent = parameter.getParent();
                if (parent instanceof PsiParameterList) {
                    PsiElement grandparent = parent.getParent();
                    if (ResourceUtils.checkDisableCodeCompletionStatus()) return;

                    if (grandparent instanceof PsiMethod) {
                        holder.registerProblem(parameter, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                            @Override
                            public @IntentionFamilyName @NotNull String getFamilyName() {
                                return Const.PLUGIN_NAME + "(参数命名)";
                            }


                            //参数重命名
                            @Override
                            public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                                HashMap<String, String> param = Maps.newHashMap();
                                param.put("paramName", paramName);
                                invokePrompt(project, "rename_param_name", param, ImmutableMap.of("parameter", parameter));
                            }
                        });

                    }
                }


            }

            //analyse_class
            @Override
            public void visitClass(PsiClass aClass) {
                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;

                List<Pair<String, String>> list = Prompt.getPromptInfoByTag("class_inspection").stream()
                        .filter(it -> it.isCollected() || Prompt.containsTag(it.getTags(), "system"))
                        .map(it -> Pair.of(it.getDesc(), it.getPromptName()))
                        .collect(Collectors.toList());

                list.forEach(it -> holder.registerProblem(aClass, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return it.getKey();
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = it.getValue();
                        invokePrompt(project, promptName, Maps.newHashMap());
                    }
                }));


            }

            // rename_method(方法重命名) comment_2(方法添加注释) suggest_sidecar(方法review+建议) test_code(单元测试)
            @Override
            public void visitMethod(PsiMethod method) {
                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;

                List<Pair<String, String>> list = getPromptMethodMenu();
                list.forEach(it -> holder.registerProblem(method, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return it.getKey();
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = it.getValue();
                        invokePrompt(project, promptName, Maps.newHashMap());
                    }

                    @Override
                    public boolean startInWriteAction() {
                        return false;
                    }
                }));

            }


            @Override
            //访问注释
            public void visitComment(@NotNull PsiComment comment) {
                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;
                //单行注释 直接生成代码
                if (comment.getText().startsWith("//")) {
                    //注释范围在 (method, class]
                    if (EditorUtils.isCommentInsideClass(comment)
                            && EditorUtils.isNotClassHeaderComment(comment)
                            && isNotMethodComment(comment)) {
                        final String content = comment.getText().substring(2);
                        holder.registerProblem(comment, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                            @Override
                            //获取修复的家族名称
                            public @IntentionFamilyName @NotNull String getFamilyName() {
                                return Const.PLUGIN_NAME + "(生成代码)";
                            }

                            @Override
                            //应用修复
                            public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                                PromptInfo promptInfo = Prompt.getPromptInfo(Const.GENERATE_CODE);
                                if (isBotUsageConfigured(promptInfo)) {
                                    //调用bot生成代码
                                    invokePrompt(project, promptInfo.getPromptName(), Maps.newHashMap());
                                } else {
                                    //根据注释生成代码
                                    PromptService.generateMethod(project, content);
                                }
                            }
                        });
                    }
                }

                // 处理多行注释
                if (comment.getText().startsWith("/*")) {
                    if (EditorUtils.isCommentInsideClass(comment)
                            && EditorUtils.isNotClassHeaderComment(comment)
                            && isNotMethodComment(comment)) {
                        final String content = comment.getText().trim();
                        holder.registerProblem(comment, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                            @Override
                            public @IntentionFamilyName @NotNull String getFamilyName() {
                                return Const.PLUGIN_NAME + "(生成代码)";
                            }

                            @Override
                            public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                                // 移动光标到注释末尾
                                PsiElement element = descriptor.getPsiElement();
                                if (element instanceof PsiComment) {
                                    PsiComment comment = (PsiComment) element;
                                    EditorUtils.moveCaretToCommentEnd(project, comment);
                                }

                                PromptInfo promptInfo = Prompt.getPromptInfo(Const.GENERATE_CODE);
                                if (isBotUsageConfigured(promptInfo)) {
                                    Map<String, String> parms = Maps.newHashMap();
                                    parms.put(Const.GENERATE_CODE_COMMENT, content);
                                    invokePrompt(project, promptInfo.getPromptName(), parms);
                                } else {
                                    PromptService.generateMethod(project, content);
                                }
                            }
                        });
                    }
                }
            }


            /**
             * 给字段起新名字
             */
            @Override
            public void visitField(PsiField field) {
                if (ResourceUtils.checkDisableCodeCompletionStatus()) return;
                holder.registerProblem(field, "", ProblemHighlightType.INFORMATION, new LocalQuickFix() {
                    @Override
                    public @IntentionFamilyName @NotNull String getFamilyName() {
                        return Const.PLUGIN_NAME + "(字段重命名)";
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        String promptName = "rename_field";
                        invokePrompt(project, promptName, Maps.newHashMap());
                    }
                });
            }
        };
    }

    //查询方法级菜单列表
    private List<Pair<String, String>> getPromptMethodMenu() {
        List<Pair<String, String>> list = Prompt.getPromptInfoByTag("method_inspection").stream()
                .filter(it -> it.isCollected() || Prompt.containsTag(it.getTags(), "popup"))
                .sorted(Comparator.comparingInt(it -> Integer.parseInt(it.getLabels().getOrDefault("order", "1"))))
                .map(it -> Pair.of(it.getDesc(), it.getPromptName()))
                .collect(Collectors.toList());
        return list;
    }

    private static boolean isNotMethodComment(@NotNull PsiComment comment) {
        return !(comment.getParent() instanceof PsiMethod);
    }


    public static void invokePrompt(Project project, String promptName, Map<String, String> param) {
        invokePrompt(project, promptName, param, Maps.newHashMap());
    }

    public static void invokePrompt(Project project, String promptName, Map<String, String> param, Map<String, Object> objMap) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PromptType promptType = Prompt.getPromptType(promptInfo);
        //直接使用bot
        if (isBotUsageConfigured(promptInfo)) {
            promptType = PromptType.executeBot;
        }
        PromptService.dynamicInvoke(GenerateCodeReq.builder()
                .project(project)
                .promptType(promptType)
                .promptName(promptName)
                .promptInfo(promptInfo)
                .param(param)
                .objMap(objMap)
                .build());
    }

    //是否是调用bot来执行
    public static boolean isBotUsageConfigured(PromptInfo promptInfo) {
        return ResourceUtils.has(Const.USE_BOT) && promptInfo.has(Const.BOT_ID) && !(promptInfo.getLabels().get(Const.BOT_ID).equals("0"));
    }

}
