package run.mone.processor.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.processor.bo.MethodInfo;
import run.mone.processor.common.CodeUtils;
import run.mone.processor.common.MethodCode;
import run.mone.processor.common.Pair;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2023/2/20 18:29
 */
public class JavaPoetTest {

    @SneakyThrows
    private void out(MethodSpec methodSpec) {
        TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder("run.mone.test", typeSpec).build();
        javaFile.writeTo(System.out);
    }


    @Test
    public void test4() {
        String path = "/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/test/java/run/mone/processor/test/Run.java";
        MethodCode addMethod = MethodCode.builder()
                .name("setEnv")
                .paramList(Lists.newArrayList(Pair.of(String.class, "key"), Pair.of(String.class, "value")))
                .returnType(void.class)
                .code("try {\n" +
                        "            Map<String, String> env = System.getenv();\n" +
                        "            Class<?> cl = env.getClass();\n" +
                        "            Field field = cl.getDeclaredField(\"m\");\n" +
                        "            field.setAccessible(true);\n" +
                        "            Map<String, String> writableEnv = (Map<String, String>) field.get(env);\n" +
                        "            writableEnv.put(key, value);\n" +
                        "        } catch (Exception e) {\n" +
                        "            throw new IllegalStateException(\"Failed to set environment variable\", e);\n" +
                        "        }")
                .build();
        MethodCode modifyMethod = MethodCode.builder().name("main").code("setEnv(\"a\",\"b\")").build();
        String code = CodeUtils.modifyCode(path, "Run", Lists.newArrayList(addMethod), Lists.newArrayList(modifyMethod), Lists.newArrayList("java.util.Map", "java.lang.reflect.Field"));
        System.out.println(code);
    }


    @SneakyThrows
    @Test
    public void testParse() {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(new File("/tmp/e")).getResult().get();
        PackageDeclaration a = cu.getPackageDeclaration().get();
        System.out.println(a.getName());
        System.out.println(cu.getType(0).getName().getIdentifier());
    }


    @Test
    public void test0() {
        File file = new File("/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/test/java/run/mone/processor/test/Run.java");
        try {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(file).getResult().get();
            ClassOrInterfaceDeclaration classOrInterface = cu.getClassByName("Run")
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            MethodSpec newMethod = MethodSpec.methodBuilder("setEnv")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "key")
                    .addParameter(String.class, "value")
                    .returns(void.class)
                    .addCode("try {\n" +
                            "            Map<String, String> env = System.getenv();\n" +
                            "            Class<?> cl = env.getClass();\n" +
                            "            Field field = cl.getDeclaredField(\"m\");\n" +
                            "            field.setAccessible(true);\n" +
                            "            Map<String, String> writableEnv = (Map<String, String>) field.get(env);\n" +
                            "            writableEnv.put(key, value);\n" +
                            "        } catch (Exception e) {\n" +
                            "            throw new IllegalStateException(\"Failed to set environment variable\", e);\n" +
                            "        }")
                    .build();
            classOrInterface.addMember(javaParser.parseBodyDeclaration(newMethod.toString()).getResult().get());

            cu.addImport("java.util.Map");
            cu.addImport("java.lang.reflect.Field");

            Optional<MethodDeclaration> methodOptional = classOrInterface.getMethodsByName("main").stream().findFirst();
            if (methodOptional.isPresent()) {
                MethodDeclaration method = methodOptional.get();

                CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                codeBlockBuilder.addStatement("setEnv(\"a\",\"b\")");
                CodeBlock newCode = codeBlockBuilder.build();
                NodeList<Statement> newStatements = javaParser.parseBlock("{" + newCode.toString() + "}").getResult().get().getStatements();

                // 在方法的开头插入新代码
                method.getBody().ifPresent(body -> {
                    NodeList<Statement> statements = body.getStatements();
                    statements.addAll(0, newStatements);
                });
            } else {
                throw new RuntimeException("Method not found");
            }
            System.out.println(cu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows
    @Test
    public void test1() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello World!")
                .build();

        out(methodSpec);
    }


    @Test
    public void test2() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("sum")
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode("int total = 0;\n" +
                        "for (int i=0;i<10;i++;){\n" +
                        "    total+=i;\n" + "" +
                        "}\n" +
                        "return total;").build();

        out(methodSpec);
    }

    @Test
    public void test3() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("sum")
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("int total = 0")
                .beginControlFlow("for (int i=0;i<10;i++)")
                .addStatement("total+=i")
                .endControlFlow()
                .build();

        System.out.println(methodSpec.toString());

        out(methodSpec);
    }

    @SneakyThrows
    @Test
    public void testReadImports() {
        CodeUtils.readImports(new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/main/resources/code3.txt")))).forEach(it -> {
            System.out.println(it);
        });
    }


    @SneakyThrows
    @Test
    public void testReadMethod() {
        String m = CodeUtils.readMethod(CodeUtils.removeImports(new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/main/resources/code.txt")))));
        System.out.println(m);
    }

    @SneakyThrows
    @Test
    public void test11() {
        String str = new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/main/resources/code.txt")));
        CharSource charSource = CharSource.wrap(str);
        charSource.lines().forEach(it -> {
            String i = CodeUtils.readImports2(it);
            if (!i.equals("Error")) {
                System.out.println(i);
            }
        });

    }

    @Test
    public void testMethod() {
        // 准备要解析的 Java 代码（这里以方法为例）
        String sourceCode = "public static void main(String[] args) {\n" +
                "    System.out.println(\"Hello, world!\");\n" +
                "}";

        ParseResult<MethodDeclaration> cu = new JavaParser().parseMethodDeclaration(sourceCode);
        List<MethodDeclaration> methods = cu.getResult().get().findAll(MethodDeclaration.class);

        for (MethodDeclaration method : methods) {
            // 输出方法名和参数列表
            System.out.println("Method name: " + method.getNameAsString());
            System.out.println("Method parameters: " + method.getParameters());

        }
    }

    @Test
    public void test10() {
        MethodInfo mi = CodeUtils.getMethod("public class A { public void hi(){System.out.println(123);}}");
        System.out.println(mi);
        mi = CodeUtils.getMethod("public void hi2(){System.out.println(123);}");
        System.out.println(mi);
    }





}
