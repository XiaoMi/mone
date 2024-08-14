package run.mone.ultraman.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.xiaomi.youpin.tesla.ip.util.FixCodeUtils;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.bo.AthenaFieldInfo;
import run.mone.ultraman.bo.AthenaMethodInfo;
import run.mone.ultraman.bo.AthenaPair;
import run.mone.ultraman.listener.CaretHoverPlugin;
import run.mone.ultraman.service.AthenaCodeService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/6 22:46
 */
public class AthenaCodeServiceTest {

    @Test
    public void testParseMethod() {
        List<AthenaMethodInfo> list = AthenaCodeService.parseMethodCode("public class A { public void t(){} public int sum(int a, int b) {return a+b;}}");
        System.out.println(list);
    }


    @SneakyThrows
    @Test
    public void testClassInfo() {
        String code = Files.readString(Paths.get("src/main/java/run/mone/ultraman/AthenaContext.java"));
        AthenaClassInfo info = AthenaCodeService.classInfo(code);
        System.out.println(info);
        System.out.println(info.getClassCode().length());

        System.out.println("--------");
        info.getPublicMethodList().forEach(it -> {
            System.out.println(it);
        });
        System.out.println("-------");
        System.out.println(info.getClassCode());
    }

    @SneakyThrows
    @Test
    public void testAddDataAnnotationForClass() {
        String code = Files.readString(Paths.get("/your path/PageBase.java"));
        AthenaClassInfo athenaClassInfo = AthenaCodeService.classInfoWithDetail(code, true);
        String newCode = FixCodeUtils.addDataAnnotationForClass(code, athenaClassInfo);

        System.out.println(newCode);
    }

    @SneakyThrows
    @Test
    public void testAddBasicImportForClass() {
        String code = Files.readString(Paths.get("/your path/SlaServiceImpl.java"));
        AthenaClassInfo athenaClassInfo = AthenaCodeService.classInfoWithDetail(code, true);
        String newCode = FixCodeUtils.addBasicImportForClass(code, athenaClassInfo);

        System.out.println(newCode);
    }


    @SneakyThrows
    @Test
    public void testParseCode() {

        String code = Files.readString(Paths.get("/your path/PageBase.java"));
//        String code = Files.readString(Paths.get("/tmp/k"));
//        String code = Files.readString(Paths.get("/tmp/o.java"));
        List<AthenaPair<AthenaClassInfo, String>> info = AthenaCodeService.parseCode(code);

        System.out.println(info);
    }


    @SneakyThrows
    @Test
    public void testRemove() {
        String code = Files.readString(Paths.get("/your path/T.java"));
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> pr = javaParser.parse(code);
        if (!pr.isSuccessful()) {
            System.out.println(pr.getProblems());
        }
        CompilationUnit cu = pr.getResult().get();
        cu.findAll(MethodDeclaration.class).stream().filter(it -> it.isPrivate()).forEach(it -> it.removeForced());
        cu.findAll(ConstructorDeclaration.class).stream().forEach(it -> it.getBody().removeForced());
        cu.findAll(InitializerDeclaration.class, init -> init.isStatic()).forEach(it -> it.removeForced());

        cu.findAll(FieldDeclaration.class).stream().forEach(it -> it.removeForced());
        System.out.println(cu.toString());
    }


    @SneakyThrows
    @Test
    public void testParseField() {
        String code = Files.readString(Paths.get("/your path/AthenaClassInfo.java"));
        List<AthenaFieldInfo> fieldList = AthenaCodeService.parseFieldCode(code);
        System.out.println(fieldList);
    }


    @Test
    public void test1() {
        String line = "for(int i=0;i<3;i++) {}";
        System.out.println(CaretHoverPlugin.isCompleteStatement("for(int i=0;i<3;i++) {}"));
    }




    private boolean containsUnclosedStructures(String lineText) {
        int openBraces = 0;
        int closeBraces = 0;
        for (char c : lineText.toCharArray()) {
            if (c == '{') {
                openBraces++;
            } else if (c == '}') {
                closeBraces++;
            }
        }
        return openBraces > closeBraces;
    }


}
