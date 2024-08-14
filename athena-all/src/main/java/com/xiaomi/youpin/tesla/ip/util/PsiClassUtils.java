package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.InheritanceUtil;
import com.xiaomi.youpin.tesla.ip.bo.AddMethodConfig;
import com.xiaomi.youpin.tesla.ip.bo.Message;
import com.xiaomi.youpin.tesla.ip.bo.ParamInfo;
import com.xiaomi.youpin.tesla.ip.common.JavaClassUtils;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.service.AthenaCodeService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/5/13 10:18
 */
@Slf4j
public class PsiClassUtils {


    /**
     * 获取方法列表
     *
     * @param psiClass
     * @return
     */
    public static List<String> getMethodList(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods()).map(it -> it.getName()).collect(Collectors.toList());
    }

    /**
     * 根据类名查找这个类
     *
     * @param project
     * @param className
     * @return
     */
    public static PsiClass findClassByName(Project project, String className) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = javaPsiFacade.findClass(className, scope);
        return psiClass;
    }

    /**
     * 添加字段
     * 如果里边有同名字段则不添加
     *
     * @param project
     * @param psiClass
     * @param name
     * @param type
     */
    public static void addField(Project project, PsiClass psiClass, String name, PsiType type) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        boolean find = Arrays.stream(psiClass.getFields()).filter(it -> it.getName().equals(name)).findAny().isPresent();
        if (!find) {
            PsiField field = elementFactory.createField(name, type);
            psiClass.add(field);
        }
    }

    public static PsiClass getInterface(PsiClass psiClass) {
        return Arrays.stream(psiClass.getInterfaces()).findAny().orElse(null);
    }

    /**
     * 添加方法(支持向类中或者interface中添加)
     *
     * @param project
     * @param psiClass
     * @param paramInfoList
     */
    public static void addMethod(Project project, PsiClass psiClass, AddMethodConfig config, List<ParamInfo> paramInfoList) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        String name = config.getName();
        PsiType returnType = config.getReturnType();
        @NotNull PsiMethod method = elementFactory.createMethod(name, returnType, psiClass);
        paramInfoList.stream().forEach(it -> {
            PsiParameter param = elementFactory.createParameter(it.getName(), it.getPsiType());
            method.getParameterList().add(param);
        });
        if (config.isInterface()) {
            PsiElement replacement = elementFactory.createStatementFromText(";", psiClass);
            method.getBody().replace(replacement);
        }
        psiClass.add(method);
    }

    public static void addImport(PsiClass psiClass, String className) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiClass pc = CodeService.createPsiClass(factory, className);
        PsiImportStatement importStatement = factory.createImportStatement(pc);
        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        boolean has = Arrays.stream(javaFile.getImportList().getImportStatements()).filter(it -> it.getQualifiedName().equals(className)).findAny().isPresent();
        if (!has) {
            javaFile.getImportList().add(importStatement);
        }
    }

    public static PsiClass findApplicationRunClass(Project project) {
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiClassType mainClassType = JavaPsiFacade.getInstance(project).getElementFactory().createTypeByFQClassName("java.lang.Object", GlobalSearchScope.allScope(project));
        PsiClass resolvedMainClassType = Objects.requireNonNull(mainClassType.resolve());
        PsiMethod[] methods = shortNamesCache.getMethodsByName("main", GlobalSearchScope.allScope(project));
        for (PsiMethod method : methods) {
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null && containingClass.isInheritor(resolvedMainClassType, true)) {
                PsiParameterList parameterList = method.getParameterList();
                if (parameterList.getParametersCount() == 1) {
                    PsiParameter[] parameters = parameterList.getParameters();
                    PsiType parameterType = parameters[0].getType();
                    boolean isStringArrayType = parameterType instanceof PsiArrayType && ((PsiArrayType) parameterType).getComponentType().equalsToText("java.lang.String");
                    PsiModifierList modifierList = containingClass.getModifierList();
                    if (isStringArrayType && (modifierList != null && modifierList.hasAnnotation("org.springframework.boot.autoconfigure.SpringBootApplication"))) {
                        return containingClass;
                    }
                    PsiAnnotation[] annotations = containingClass.getAnnotations();
                    for (PsiAnnotation annotation : annotations) {
                        if ("org.springframework.boot.autoconfigure.SpringBootApplication".equals(annotation.getQualifiedName())) {
                            return containingClass;
                        }
                        if ("org.springframework.boot.autoconfigure.EnableAutoConfiguration".equals(annotation.getQualifiedName())) {
                            return containingClass;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static PsiDirectory getSourceDirectory(Project project, String moduleName, boolean testPath) {
        return getSourceDirectory(project, moduleName, testPath, "java");
    }

    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath) {
        createEmptyClass(project, moduleName, packageName, className, testPath, true, null, false);
    }

    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath, List<String> annoList) {
        createEmptyClass(project, moduleName, packageName, className, testPath, true, annoList, false);

    }

    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath, boolean isInterface) {
        createEmptyClass(project, moduleName, packageName, className, testPath, false, null, isInterface);
    }

    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath, boolean openClass, List<String> annoList, boolean isInterface) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiDirectory psiDirectory = getSourceDirectory(project, moduleName, testPath);
            if (null == psiDirectory) {
                HintUtils.show(CodeService.getEditor(project), Message.cannotFindTestDirectory, true);
                return;
            }
            String[] packageParts = packageName.split("\\.");
            for (String packagePart : packageParts) {
                PsiDirectory subDirectory = psiDirectory.findSubdirectory(packagePart);
                if (subDirectory == null) {
                    subDirectory = psiDirectory.createSubdirectory(packagePart);
                }
                psiDirectory = subDirectory;
            }
            PsiDirectory tmp = psiDirectory;
            String classText = isInterface ? buildEmptyInterfaceInfo(className) : buildEmptyClassInfo(project, className, testPath, annoList);
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFile = psiFileFactory.createFileFromText(className + ".java", classText);
            tmp.add(psiFile.getContainingFile());
            if (openClass) {
                JavaClassUtils.openClass(project, className);
            }
        });
    }

    //创建一个类,并打开
    public static void createClass(Project project, String packageStr, String className, String code) {
        createClass(project, packageStr, className, code, true);
    }

    public static void createClass(Project project, String packageStr, String className, String code, boolean format) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFile = psiFileFactory.createFileFromText(className + ".java", code);
            PsiDirectory directory = getDirectoryFromPackage(project, packageStr);
            directory.add(psiFile);
            if (format) {
                //打开这个类
                JavaClassUtils.openClass(project, className);
                //格式化代码
                CodeService.formatCode(project);
            }
        });
    }

    public static void createClass(Project project, String className, String code, boolean format, PsiDirectory directory) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFile = psiFileFactory.createFileFromText(className + ".java", code);
            directory.add(psiFile);
            if (format) {
                //打开这个类
                JavaClassUtils.openClass(project, className);
                //格式化代码
                CodeService.formatCode(project);
            }
        });
    }

    public static PsiDirectory getDirectoryFromPackage(Project project, String packagePath) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiPackage psiPackage = javaPsiFacade.findPackage(packagePath);
        if (psiPackage != null) {
            PsiDirectory[] directories = psiPackage.getDirectories();
            if (directories.length > 0) {
                return directories[0];
            }
        }
        return null;
    }

    private static String buildEmptyClassInfo(Project project, String className, boolean testPath, List<String> annoList) {
        String classText = testPath ?
                String.format("public class %s{\n\n}", className)
                : String.format("import java.io.Serializable;\nimport lombok.Builder;\nimport lombok.Data;\n\n@Data\n@Builder\npublic class %s implements Serializable{\n\n}", className);
        if (CollectionUtils.isNotEmpty(annoList)) {
            StringBuilder sb = new StringBuilder();
            annoList.forEach(a -> {
                if ("@SpringBootTest".equals(a)) {
                    PsiClass applicationRunClass = findApplicationRunClass(project);
                    sb.append(String.format("import %s;", applicationRunClass.getQualifiedName()));
                    a = String.format(a + "(classes = %s.class)", applicationRunClass.getName());
                }
                sb.append(a).append("\n");
            });
            sb.append(classText);
            return sb.toString();
        }
        return classText;
    }

    public static String buildEmptyInterfaceInfo(String className) {
        return String.format("public interface %s{\n\n}", className);
    }

    public static PsiDirectory getSourceDirectory(Project project, String moduleName, boolean testPath, String sourceFolderPath) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            if (!module.getName().equals(moduleName)) {
                continue;
            }
            for (VirtualFile sourceFolder : moduleRootManager.getSourceRoots(testPath)) {
                //包含/test/目录才是测试代码路径
                if (testPath && !sourceFolder.getPath().contains("/src/test/java")) {
                    continue;
                }
                if (sourceFolder.getName().equals(sourceFolderPath)) {
                    return PsiManager.getInstance(project).findDirectory(sourceFolder);
                }
            }
        }
        return null;
    }

    public static PsiDirectory getSourceDirectory(Project project, String moduleName) {
        return getSourceDirectory(project, moduleName, "/src/main/java");
    }

    public static PsiDirectory getSourceDirectoryWithModule(Project project, String moduleName) {
        return getSourceDirectory(project, moduleName, "/src/main/java", false, false);
    }

    public static PsiDirectory getTestSourceDirectory(Project project, String moduleName) {
        return getSourceDirectory(project, moduleName, "/src/test/java", true, false);
    }


    public static PsiDirectory getSourceDirectory(Project project, String moduleName, String containsPath) {
        return getSourceDirectory(project, moduleName, containsPath, false, true);
    }

    public static PsiDirectory createPackageDirectories(PsiDirectory directory, String packageName) {
        String[] names = packageName.split("\\.");
        for (String name : names) {
            PsiDirectory subdirectory = directory.findSubdirectory(name);
            if (subdirectory == null) {
                directory = directory.createSubdirectory(name);
            } else {
                directory = subdirectory;
            }
        }
        return directory;
    }

    public static PsiDirectory getSourceDirectory(Project project, String moduleName, String containsPath, boolean includingTests, boolean getParent) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            if (!module.getName().equals(moduleName)) {
                continue;
            }
            for (VirtualFile sourceFolder : moduleRootManager.getSourceRoots(includingTests)) {
                //包含/test/目录才是测试代码路径
                if (sourceFolder.getPath().contains(containsPath)) {
                    PsiDirectory res = PsiManager.getInstance(project).findDirectory(sourceFolder);
                    if (getParent) {
                        return res.getParent();
                    }
                    return res;
                }
            }
        }
        return null;
    }

    public static PsiDirectory getDirectory(Project project, String moduleName, String containsPath) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            if (!module.getName().equals(moduleName)) {
                continue;
            }
            for (VirtualFile sourceFolder : moduleRootManager.getSourceRoots(false)) {
                //包含/test/目录才是测试代码路径
                if (sourceFolder.getPath().contains(containsPath)) {
                    return PsiManager.getInstance(project).findDirectory(sourceFolder);
                }
            }
        }
        return null;
    }

    public static PsiDirectory getSourceDirectory(Project project, boolean testPath, String packagePath) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            for (VirtualFile sourceFolder : moduleRootManager.getSourceRoots(testPath)) {
                //包含/test/目录才是测试代码路径
                if (testPath && !sourceFolder.getPath().contains("/src/test/java")) {
                    continue;
                }

                if (!module.getName().contains("demo-server")) {
                    continue;
                }

                if (sourceFolder.getName().equals("java")) {
                    sourceFolder = sourceFolder.findFileByRelativePath(packagePath.replace('.', '/'));
                    return PsiManager.getInstance(project).findDirectory(sourceFolder);
                }
            }
        }
        return null;
    }

    public static Module getModule(Project project, PsiClass psiClass) {
        VirtualFile virtualFile = psiClass.getContainingFile().getVirtualFile();
        Module module = ModuleUtilCore.findModuleForFile(virtualFile, project);
        return module;
    }

    //需要分析的引入类
    private static List<String> list = ImmutableList.of("javax.annotation.Resource",
            "jakarta.annotation.Resource",
            "org.springframework.beans.factory.annotation.Autowired",
            "org.apache.dubbo.config.annotation.Reference",
            "org.apache.dubbo.config.annotation.DubboReference"
    );

    public static List<PsiField> findFieldsWithResourceAnnotation(@NotNull PsiClass psiClass) {
        @Nullable PsiClass superClass = psiClass.getSuperClass();
        List<PsiField> superClassFields = new ArrayList<>();
        //父类里有,也会被拉取出来
        if (null != superClass) {
            superClassFields.addAll(Arrays.stream(superClass.getFields()).filter(it -> hasAnno(it, list)).collect(Collectors.toList()));
        }
        List<PsiField> fields = Arrays.stream(psiClass.getFields()).filter(it -> hasAnno(it, list)).collect(Collectors.toList());
        fields.addAll(superClassFields);
        return fields;
    }

    private static boolean hasAnno(PsiField field, List<String> annoList) {
        if (null == field || null == annoList) {
            return false;
        }
        try {
            return annoList.stream().filter(it -> field.getAnnotation(it) != null).findAny().isPresent();
        } catch (Throwable ex) {
            return false;
        }
    }

    public static List<String> getClassText(Project project, List<PsiField> fields) {
        return fields.stream().map(it -> {
            PsiClass psiClass = PsiClassUtils.findClassByName(project, it.getType().getCanonicalText());
            if (null != psiClass) {
                if (psiClass.isInterface()) {
                    return psiClass.getText();
                } else {
                    return PsiClassUtils.getInterfaceText(project, psiClass);
                }
            }
            return null;
        }).filter(it -> null != it).collect(Collectors.toList());
    }

    public static String getClassText(Project project, String text) {
        PsiClass psiClass = PsiClassUtils.findClassByName(project, text);
        if (null != psiClass) {
            if (psiClass.isInterface()) {
                return psiClass.getText();
            } else {
                return PsiClassUtils.getInterfaceText(project, psiClass);
            }
        }
        return "";
    }

    public static String getInterfaceText(Project project, PsiClass originalClass) {
        //直接先分析下代码,如果加了@Data注解,则直接自己分析了
        try {
            String code = originalClass.getText();
            AthenaClassInfo info = AthenaCodeService.classInfo(code);
            return info.getClassCode();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }


        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiClass newInterface = factory.createInterface(originalClass.getName());
        PsiMethod[] methods = originalClass.getMethods();
        for (PsiMethod method : methods) {
            try {
                if (method.hasModifierProperty(PsiModifier.PUBLIC)) {

                    if (method.getClass().getName().equals("de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder")) {
                        continue;
                    }

                    PsiMethod newMethod = factory.createMethod(method.getName(), method.getReturnType());
                    for (PsiParameter parameter : method.getParameterList().getParameters()) {
                        newMethod.getParameterList().add(parameter);
                    }

                    for (PsiElement child : method.getChildren()) {
                        if (child instanceof PsiComment) {
                            newMethod.addBefore(child, newMethod.getFirstChild());
                        }
                    }

                    @NotNull PsiElement newLine = PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n\n");
                    newMethod.addBefore(newLine, newMethod.getFirstChild());

                    newInterface.add(newLine);
                    newInterface.add(newMethod);
                }
            } catch (Throwable ignore) {

            }

        }
        return newInterface.getText();

    }

    /**
     * 查找指定类继承的非私有非静态方法集合.
     * 遍历给定类的所有父类，忽略Object类，筛选出所有非私有非静态方法.
     * 如果父类是接口，则只考虑默认方法.
     *
     * @param psiClass 需要查找方法的类
     * @return 继承的非私有非静态方法集合
     */
    public static Set<PsiMethod> findInheritedNonPrivateNonStaticMethods(PsiClass psiClass) {
        Set<PsiMethod> inheritedMethods = new HashSet<>();
        LinkedHashSet<PsiClass> superClasses = InheritanceUtil.getSuperClasses(psiClass);
        for (PsiClass superClass : superClasses) {
            if (superClass.getName().equals("Object")) {
                continue;
            }
            // 获取当前父类的所有方法
            PsiMethod[] methods = superClass.getMethods();
            for (PsiMethod method : methods) {
                if (superClass.isInterface() && !method.hasModifierProperty(PsiModifier.DEFAULT)) {
                    continue;
                }
                // 检查方法是否是继承来的
                if (!method.hasModifierProperty(PsiModifier.PRIVATE) && !method.hasModifierProperty(PsiModifier.STATIC)) {
                    // 如果方法不是 private 或 static，那么它可能是被继承的
                    inheritedMethods.add(method);
                }
            }
        }
        return inheritedMethods;
    }

    public static String generateMethodSignature(PsiMethod psiMethod) {
        // 获取方法的访问修饰符（public, private, 等等）
        if (psiMethod.isConstructor() || psiMethod.getReturnType() == null) {
            return "";
        }
        String modifier = psiMethod.getModifierList().getText();
        String returnType = psiMethod.getReturnType().getPresentableText();
        String methodName = psiMethod.getName();
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        StringBuilder paramsBuilder = new StringBuilder();
        // 获取方法的文档注释（如果存在）
        PsiDocComment docComment = psiMethod.getDocComment();
        StringBuilder commentBuilder = new StringBuilder();
        // 如果存在文档注释，则获取注释的文本并添加到commentBuilder
        if (docComment != null) {
            commentBuilder.append(docComment.getText());
        }

        String comment = getSingleLineCommentAboveMethod(psiMethod);
        if (StringUtils.isNotEmpty(comment)) {
            commentBuilder.append(comment);
        }

        for (PsiParameter param : parameters) {
            if (paramsBuilder.length() > 0) {
                paramsBuilder.append(", ");
            }
            paramsBuilder.append(param.getType().getPresentableText()).append(" ").append(param.getName());
        }
        String methodDefinition = modifier + " " + returnType + " " + methodName + "(" + paramsBuilder.toString() + ")";
        return commentBuilder + "\n" + methodDefinition;
    }

    /**
     * 获取给定方法上方的单行注释文本.
     * 遍历方法节点的前一个兄弟节点，直到找到单行注释或者不再有前一个兄弟节点为止.
     * 如果找到单行注释，则返回该注释的文本内容；否则返回null.
     */
    public static String getSingleLineCommentAboveMethod(PsiMethod psiMethod) {
        for (PsiElement child : psiMethod.getChildren()) {
            if (child instanceof PsiComment) {
                PsiComment comment = (PsiComment) child;
                IElementType type = comment.getTokenType();
                if (JavaTokenType.END_OF_LINE_COMMENT.equals(type)) {
                    return comment.getText();
                }
            } else {
                break;
            }
        }
        return null; // No single line comment found
    }

}
