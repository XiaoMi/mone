package run.mone.m78.ip.util;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import run.mone.m78.ip.bo.AddMethodConfig;
import run.mone.m78.ip.bo.ParamInfo;
import run.mone.m78.ip.common.JavaClassUtils;
import run.mone.m78.ip.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.service.AthenaCodeService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    }

    //创建一个类,并打开
    public static void createClass(Project project, String packageStr, String className, String code) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFile = psiFileFactory.createFileFromText(className + ".java", code);
            PsiDirectory directory = getDirectoryFromPackage(project, packageStr);
            directory.add(psiFile);
            //打开这个类
            JavaClassUtils.openClass(project, className);
            //格式化代码
            CodeService.formatCode(project);
        });
    }

    public static PsiDirectory getDirectoryFromPackage(Project project, String packagePath) {
        PsiManager psiManager = PsiManager.getInstance(project);
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

    public static PsiDirectory getSourceDirectory(Project project, String moduleName, String containsPath) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            if (!module.getName().equals(moduleName)) {
                continue;
            }
            for (VirtualFile sourceFolder : moduleRootManager.getSourceRoots(false)) {
                //包含/test/目录才是测试代码路径
                if (sourceFolder.getPath().contains(containsPath)) {
                    return PsiManager.getInstance(project).findDirectory(sourceFolder).getParent();
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
        return null;
    }

    public static Module getModule(Project project, PsiClass psiClass) {
        VirtualFile virtualFile = psiClass.getContainingFile().getVirtualFile();
        Module module = ModuleUtilCore.findModuleForFile(virtualFile, project);
        return module;
    }

    private static List<String> list = ImmutableList.of("javax.annotation.Resource", "jakarta.annotation.Resource"
            , "org.springframework.beans.factory.annotation.Autowired", "org.apache.dubbo.config.annotation.Reference"
            , "org.apache.dubbo.config.annotation.DubboReference"
    );


    public static List<PsiField> findFieldsWithResourceAnnotation(@NotNull PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields()).filter(it -> hasAnno(it, list)).collect(Collectors.toList());
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


}
