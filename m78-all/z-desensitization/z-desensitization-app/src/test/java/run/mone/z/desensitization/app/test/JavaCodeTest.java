package run.mone.z.desensitization.app.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2024/2/2 11:28
 */
public class JavaCodeTest {

    @Test
    public void test1() {
        try {
            String code = "abc public class A { int a=1; int b =2;} def";
            JavaParser javaParser = new JavaParser();
            CompilationUnit compilationUnit = javaParser.parse(code).getResult().get();

            // 遍历所有的类型声明（包括类和接口）
            for (ClassOrInterfaceDeclaration type : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
                if (type.isPublic() && !type.isInterface()) { // 检查是否为公共类
                    System.out.println("Found class: " + type.getName());
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void test2() {
        String code = "abc public class DubboHealthServiceImpl implements DubboHealthService { @Override public Result<String> health() { return Result.success(\"ok\"); } //计算两数之和 } def";

        String code2 = "public class NacosService { private Map<String, NacosConf> nacosNamingMap; @PostConstruct public void init() { } @Data public static class TokenResult { private String token; } @Data @Builder public static class NacosConf { private NacosNaming nacosNaming; } }";

        String classPattern = "public\\s+class\\s+(\\w+)\\s*\\{.*?\\}";

        Pattern pattern = Pattern.compile(classPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code2);

        if (matcher.find()) {
            String classCode = matcher.group(0); // 获取整个类定义
            System.out.println("Extracted Java class:");
            System.out.println(classCode);
        } else {
            System.out.println("No Java class found in the string.");
        }
    }

    @Test
    public void test3() {
        String code = "ddd private int myMethod(String param1, int param2) { return param1.length() + param2; } kkk";
        String methodPattern = "(?:public|protected|private)?\\s+[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{.*?\\}|;)";

        Pattern pattern = Pattern.compile(methodPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String methodCode = matcher.group(0); // 获取整个方法定义
            System.out.println("Extracted Java method:");
            System.out.println(methodCode);
        }
    }

    @Test
    public void testMultiple(){
        String code = MyTest.codeSnippet;
        String classPattern = "(?:public|protected|private)?\\s+class\\s+(\\w+)\\s*\\{.*?\\}";

        Pattern pattern = Pattern.compile(classPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);

        int count = 1;

        while (matcher.find()) {
            String className = matcher.group(1); // 获取类名
            String classCode = matcher.group(0); // 获取整个类定义
            int startIndex = matcher.start(); // 获取匹配块的开始索引
            int endIndex = matcher.end(); // 获取匹配块的结束索引

            System.out.println("Extracted Java class " + count + ": " + className);
            System.out.println("Start Index: " + startIndex);
            System.out.println("End Index: " + endIndex);
            System.out.println(classCode);
            System.out.println();

            count++;
        }
    }
}
