package run.mone.z.desensitization.service.common;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.z.desensitization.api.common.CodeTypeEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author wmin
 * @date 2023/6/5
 * 代码解析
 */
@Slf4j
public class CodeParseUtils {

    public static Pair<String,String> codeParse(String sourceCode){
        Pair<String,String> rst = null;
        try {
            if (isClass(sourceCode)){
                JavaParser javaParser = new JavaParser();
                CompilationUnit compilationUnit = javaParser.parse(sourceCode).getResult().get();
                String className = compilationUnit.getType(0).getName().getIdentifier();
                if (compilationUnit.getPackageDeclaration().isPresent()){
                    PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().get();
                    className = packageDeclaration.getName()+"."+className;
                }
                rst = Pair.of(CodeTypeEnum.CLASS.getType(), className);
            } else if (isMethod(sourceCode)){
                rst = methodCodeParse(sourceCode);
            }
        } catch (Exception e){
            log.error("codeParse error", e);
        }
        log.info("codeParse end.rst:{}", rst);
        return rst;
    }

    public static boolean isClass(String sourceCode){
        Pattern pattern = Pattern.compile(".*(public|protected|private)\\s+(class|interface)\\s.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sourceCode);
        return matcher.matches();
    }

    public static boolean isMethod(String sourceCode){
        String regex = "\\s*(public|protected|private)?\\s+(static\\s+)?[\\w\\<\\>\\[\\]]+\\s+[\\w\\$]+\\(.*\\)\\s*\\{.*";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sourceCode);
        return matcher.matches();
    }

    public static Pair<String,String> methodCodeParse(String sourceCode){
        Pair<String,String> rst = null;
        JavaParser javaParser = new JavaParser();
        ParseResult<MethodDeclaration> cu = javaParser.parseMethodDeclaration(sourceCode);
        if (cu.isSuccessful()){
            rst = Pair.of(CodeTypeEnum.METHOD.getType(), Consts.tmpClassName);
        }
        return rst;
    }

}
