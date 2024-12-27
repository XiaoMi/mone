package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * goodjava@qq.com
 * minzai
 */
public class PsiClassFixer {

    /**
     * 修复类中引用的不存在方法
     *
     * @param psiClass 要修复的类
     * @param project  当前项目
     */
    public static void fixUndefinedMethods(PsiClass psiClass, Project project) {
        // 在写操作中执行修复
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiMethod method : psiClass.getMethods()) {
                PsiCodeBlock body = method.getBody();
                if (body != null) {
                    body.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                            super.visitMethodCallExpression(expression);

                            // 获取方法调用表达式
                            PsiReferenceExpression methodExpression = expression.getMethodExpression();
                            if (methodExpression != null) {
                                // 解析方法引用
                                PsiElement referencedMethod = methodExpression.resolve();

                                if (referencedMethod == null) {
                                    String methodName = methodExpression.getReferenceName();

                                    if (methodName != null) {
                                        Pair<Boolean, PsiClass> isReferencedClassPair = isReferencedClass(methodExpression);
                                        if (isReferencedClassPair.getKey()){
                                            // 添加空方法到当前类
                                            addEmptyMethod(psiClass, methodName, expression);
                                        } else {
                                            // 添加空方法到外部类
                                            addEmptyMethod(isReferencedClassPair.getValue(), methodName, expression);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 查找引用的外部类
     *
     * @param methodExpression 方法引用表达式
     * @return 引用的外部类
     */
    private static Pair<Boolean, PsiClass> isReferencedClass(PsiReferenceExpression methodExpression) {
        boolean isCurrentClassFlag = false;
        PsiClass referencedClass = null;

        // 获取方法调用的限定表达式，例如 "this.method()" 或 "SomeClass.method()"
        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();

        if (qualifierExpression instanceof PsiThisExpression || qualifierExpression == null) {
            // 如果限定表达式是 "this" 或者没有限定表达式，那么它是当前类的方法
            isCurrentClassFlag = true;
        } else if (qualifierExpression instanceof PsiReferenceExpression) {
            // 如果限定表达式是引用表达式，可能是外部类的方法
            PsiElement qualifier = ((PsiReferenceExpression) qualifierExpression).resolve();
            if (qualifier instanceof PsiClass) {
                // 如果解析的限定符是一个类，那么它是外部类的方法
                referencedClass = (PsiClass) qualifier;
            }
        }
        return Pair.of(isCurrentClassFlag, referencedClass);
    }

    /**
     * 添加空方法到指定类中
     *
     * @param psiClass   指定类
     * @param methodName 方法名
     * @param expression 方法调用表达式（用于获取参数和返回类型）
     */
    private static void addEmptyMethod(PsiClass psiClass, String methodName, PsiMethodCallExpression expression) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        // 获取方法参数和返回类型
        PsiExpressionList argumentList = expression.getArgumentList();
        PsiType returnType = getMethodCallReturnType(expression);

        // 构建方法签名
        StringBuilder methodSignature = new StringBuilder("public static ");
        if (returnType != null) {
            methodSignature.append(returnType.getCanonicalText()).append(" ");
        } else {
            methodSignature.append("void ");
        }
        methodSignature.append(methodName).append("(");

        if (argumentList != null) {
            PsiExpression[] arguments = argumentList.getExpressions();
            for (int i = 0; i < arguments.length; i++) {
                PsiExpression argument = arguments[i];
                methodSignature.append(argument.getType().getCanonicalText()).append(" arg").append(i);
                if (i < arguments.length - 1) {
                    methodSignature.append(", ");
                }
            }
        }
        // 创建空方法并添加返回语句
        String returnStatement = getReturnStatement(returnType);
        methodSignature.append(") { " + returnStatement + " }");

        // 创建新的方法并添加到类中
        PsiMethod newMethod = elementFactory.createMethodFromText(methodSignature.toString(), psiClass);
        psiClass.add(newMethod);
    }


    public static void a(Project project) {
        InspectionManager inspectionManager = InspectionManager.getInstance(project);
        DaemonCodeAnalyzer daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(project);
    }

    public static void addFields0(PsiClass psiClass) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiField ageField = elementFactory.createField("age", PsiTypes.intType());
        PsiField nameField = elementFactory.createField("name", PsiType.getJavaLangString(psiClass.getManager(), psiClass.getResolveScope()));

        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> {
            // 添加字段前检查是否已存在同名字段
            if (psiClass.findFieldByName("age", false) == null) {
                psiClass.add(ageField);
            }
            if (psiClass.findFieldByName("name", false) == null) {
                psiClass.add(nameField);
            }
        });
    }

    public static void addFields(PsiClass psiClass, List<PsiField> fieldList) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> {
            fieldList.forEach(it -> psiClass.add(it));
        });

    }
    private static PsiType getMethodCallReturnType(PsiMethodCallExpression expression) {
        PsiElement parent = expression.getParent();
        if (parent instanceof PsiAssignmentExpression) {
            PsiAssignmentExpression assignment = (PsiAssignmentExpression) parent;
            PsiExpression lExpression = assignment.getLExpression();
            if (lExpression instanceof PsiReferenceExpression) {
                PsiElement resolved = ((PsiReferenceExpression) lExpression).resolve();
                if (resolved instanceof PsiVariable) {
                    return ((PsiVariable) resolved).getType();
                }
            }
        } else if (parent instanceof PsiVariable) {
            return ((PsiVariable) parent).getType();
        } else if (parent instanceof PsiReturnStatement) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(expression, PsiMethod.class);
            if (containingMethod != null) {
                return containingMethod.getReturnType();
            }
        }
        // 其他情况，可能需要更复杂的逻辑来确定返回类型
        return null;
    }

    /**
     * 获取返回语句
     *
     * @param returnType 返回类型
     * @return 返回语句
     */
    private static String getReturnStatement(PsiType returnType) {
        if (PsiType.VOID.equals(returnType)) {
            return "";
        } else if (PsiType.BOOLEAN.equals(returnType)) {
            return "return false;";
        } else if (PsiType.INT.equals(returnType) || PsiType.LONG.equals(returnType) || PsiType.FLOAT.equals(returnType) || PsiType.DOUBLE.equals(returnType)) {
            return "return 0;";
        } else if (PsiType.CHAR.equals(returnType)) {
            return "return '\0';";
        } else {
            return "return null;";
        }
    }
}

