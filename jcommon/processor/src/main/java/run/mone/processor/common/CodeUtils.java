package run.mone.processor.common;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.io.CharSource;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.SneakyThrows;
import run.mone.processor.bo.MethodInfo;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/3/29 16:52
 */
public abstract class CodeUtils {

    @SneakyThrows
    public static String getClassName(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        PackageDeclaration a = cu.getPackageDeclaration().get();
        return (cu.getType(0).getName().getIdentifier());
    }

    /**
     * 可以根据已有的代码生成新的代码
     *
     * @param filePath         已有代码路径
     * @param className        要修改的类
     * @param addMethodList    添加的代码片段
     * @param modifyMethodList 修改已有的代码
     * @param importList       需要import的类
     * @return
     */
    public static String modifyCode(String filePath, String className, List<MethodCode> addMethodList, List<MethodCode> modifyMethodList, List<String> importList) {
        File file = new File(filePath);
        try {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(file).getResult().get();
            ClassOrInterfaceDeclaration classOrInterface = cu.getClassByName(className)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            //add method
            addMethodList.forEach(it -> {
                MethodSpec.Builder builder = MethodSpec.methodBuilder(it.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(it.getReturnType())
                        .addCode(it.getCode());
                it.getParamList().forEach(p -> {
                    builder.addParameter(p.getKey(), p.getValue());
                });
                MethodSpec newMethod = builder.build();
                classOrInterface.addMember(javaParser.parseBodyDeclaration(newMethod.toString()).getResult().get());
            });
            //add import
            importList.forEach(i -> cu.addImport(i));
            //modify method
            modifyMethodList.forEach(mm -> {
                Optional<MethodDeclaration> methodOptional = classOrInterface.getMethodsByName(mm.getName()).stream().findFirst();
                if (methodOptional.isPresent()) {
                    MethodDeclaration method = methodOptional.get();
                    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                    codeBlockBuilder.addStatement(mm.getCode());
                    CodeBlock newCode = codeBlockBuilder.build();
                    NodeList<Statement> newStatements = javaParser.parseBlock("{" + newCode.toString() + "}").getResult().get().getStatements();
                    //Insert new code at the beginning of the method.
                    method.getBody().ifPresent(body -> {
                        NodeList<Statement> statements = body.getStatements();
                        statements.addAll(0, newStatements);
                    });
                }
            });
            return cu.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String> readImports(String code) {
        CharSource source = CharSource.wrap(code);
        String output = null;
        try {
            output = source.lines()
                    .filter(line -> line.contains("import "))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> cu = parser.parse(output);
        CompilationUnit cu2 = cu.getResult().get();
        return cu2.getImports().stream()
                .map(ImportDeclaration::getNameAsString)
                .collect(Collectors.toList());
    }

    public static String readImports2(String code) {
        JavaParser parser = new JavaParser();
        ParseResult<ImportDeclaration> cu = parser.parseImport(code);
        if (!cu.isSuccessful()) {
            return "Error";
        }
        ImportDeclaration cu2 = cu.getResult().get();
        return cu2.getName().toString();
    }

    public static String readMethod(String code) {
        JavaParser parser = new JavaParser();
        ParseResult<MethodDeclaration> cu = parser.parseMethodDeclaration(code);
        if (!cu.isSuccessful()) {
            return "Error";
        }
        return cu.getResult().get().toString();
    }


    public static String removeImports(String code) {
        CharSource source = CharSource.wrap(code);
        String output = null;
        try {
            output = source.lines()
                    .filter(line -> !line.contains("import "))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     * 是否是class
     *
     * @param code
     * @return
     */
    public static boolean isClass(String code) {
        boolean isPublicClass = code.matches("^\\s*public\\s+class.*");
        return isPublicClass;
    }


    @SneakyThrows
    public static MethodInfo getMethod(String code) {
        JavaParser parser = new JavaParser();
        if (isClass(code)) {
            ParseResult<CompilationUnit> result = parser.parse(code);
            CompilationUnit cu = result.getResult().get();
            MethodDeclaration md = cu.getType(0).getMethods().stream().findAny().get();
            return MethodInfo.builder().code(md.toString()).name(md.getNameAsString()).build();
        }
        String methodCode = removeImports(code);
        ParseResult<MethodDeclaration> cu = new JavaParser().parseMethodDeclaration(methodCode);
        List<MethodDeclaration> methods = cu.getResult().get().findAll(MethodDeclaration.class);
        MethodDeclaration md = methods.get(0);
        return MethodInfo.builder().code(md.toString()).name(md.getNameAsString()).build();
    }


}
