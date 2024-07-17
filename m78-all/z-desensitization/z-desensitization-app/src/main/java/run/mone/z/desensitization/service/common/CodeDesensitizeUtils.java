package run.mone.z.desensitization.service.common;

import com.google.common.collect.Sets;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.z.desensitization.api.common.CodeTypeEnum;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static run.mone.z.desensitization.service.common.Consts.*;

/**
 * @author wmin
 * @date 2023/6/5
 * 代码脱敏
 */
@Slf4j
public class CodeDesensitizeUtils {

    /**
     * 敏感词key
     */
    private static Set<String> filedOrMethodNames = Sets.newHashSet("password", "pwd", "secret", "token", "ip", "host");

    private static Set<String> ipKeys = Sets.newHashSet( "ip", "host");
    /**
     * IP白名单
     */
    private static Set<String> ipWhite = Sets.newHashSet("127.0.0.1", "0.0.0.0");

    private static TreeScanner<Void, Void> scanner;

    public static String codeDesensitizeForClass(String sourceCode, String className) throws Exception {
        CompilationUnitTree compilationUnit = ClassUtils.getUnitTreeWithClassName(sourceCode, className);
        TreeScanner scanner = getTreeScanner();
        scanner.scan(compilationUnit, null);
        String codeAfterDesensitized = compilationUnit.toString();
        if (tmpClassName.equals(className)){
            codeAfterDesensitized = codeAfterDesensitized.substring(tmpClassNamePrefix.length()+1, codeAfterDesensitized.length()-1).replaceAll("^\\n+|\\n+$", "");
        }
        log.info("codeAfterDesensitized ======{}======", codeAfterDesensitized);
        return codeAfterDesensitized;
    }

    public static String codeDesensitizeForClass(String sourceCode) throws Exception {
        Pair<String,String> rst = CodeParseUtils.codeParse(sourceCode);
        if (rst!=null){
            if (CodeTypeEnum.CLASS.getType().equals(rst.getKey())){
                return codeDesensitizeForClass(sourceCode, rst.getValue());
            }
            if (CodeTypeEnum.METHOD.getType().equals(rst.getKey())){
                return codeDesensitizeForClass(tmpClassNamePrefix + sourceCode + tmpClassNameSuffix, rst.getValue());
            }
        }
        return "";
    }

    private static synchronized TreeScanner getTreeScanner(){
        if (scanner!=null){
            return scanner;
        }
        Context context = new Context();
        JavacFileManager.preRegister(context);
        TreeMaker treeMaker = TreeMaker.instance(context);
        scanner = new TreeScanner<Void, Void>() {
            @Override
            public Void visitVariable(VariableTree variableTree, Void aVoid) {
                String key = variableTree.getName().toString();

                //已初始化
                if (variableTree.getInitializer()!=null) {
                    ExpressionTree initializer = variableTree.getInitializer();
                    if (initializer instanceof LiteralTree) {
                        String value = ((LiteralTree) initializer).getValue().toString();
                        String valueDesensitized = value;
                        if (isSensitiveKey(key.toLowerCase())){
                            valueDesensitized = maskValue(value, ipKeys.contains(key.toLowerCase())?"ip":"content");
                        }
                        Pair<Boolean, String> valuePair = isSensitiveValue(value);
                        if (valuePair.getKey()){
                            valueDesensitized = valuePair.getValue();
                        }
                        JCTree.JCLiteral newLiteral = treeMaker.Literal(valueDesensitized);
                        ((JCTree.JCVariableDecl) variableTree).init = newLiteral;
                    }
                }
                return super.visitVariable(variableTree, aVoid);
            }

            @Override
            public Void visitMethod(MethodTree methodTreeTree, Void aVoid) {
                BlockTree body = methodTreeTree.getBody();
                if (null != body) {
                    List<? extends StatementTree> stats = body.getStatements();
                    stats.forEach(it -> {
                        if (it instanceof JCTree.JCExpressionStatement) {
                            JCTree.JCExpressionStatement statement = (JCTree.JCExpressionStatement) it;
                            JCTree.JCExpression expression = statement.getExpression();
                            if (expression instanceof JCTree.JCMethodInvocation) {
                                JCTree.JCMethodInvocation invocation = (JCTree.JCMethodInvocation) expression;
                                com.sun.tools.javac.util.List<JCTree.JCExpression> arguments = invocation.getArguments();
                                if (arguments.head!=null){
                                    com.sun.tools.javac.util.List<JCTree.JCExpression> newArguments = null;
                                    for (int i =0;i<arguments.size();i++){
                                        JCTree.JCExpression arg = arguments.get(i);
                                        if (arg instanceof JCTree.JCLiteral) {
                                            JCTree.JCLiteral literal = (JCTree.JCLiteral) arg;
                                            String value = literal.value.toString();
                                            Pair<Boolean, String> pair = isSensitiveValue(value);
                                            if (pair.getKey()){
                                                arg = treeMaker.Literal(pair.getValue());
                                            }
                                            if (newArguments==null){
                                                newArguments = com.sun.tools.javac.util.List.of(arg);
                                            } else {
                                                newArguments = newArguments.append(arg);
                                            }
                                        }
                                        if (arg instanceof JCTree.JCIdent) {
                                            if (newArguments==null){
                                                newArguments = com.sun.tools.javac.util.List.of(arg);
                                            } else {
                                                newArguments = newArguments.append(arg);
                                            }
                                        }
                                    }
                                    invocation.args = newArguments;
                                }
                            }
                        }
                    });
                }
                return super.visitMethod(methodTreeTree, aVoid);
            }
        };
        return scanner;
    }


    private static boolean isSensitiveKey(String key){
        //todo 只要包含就算，可能误判
        for (String keyword : filedOrMethodNames) {
            if (key.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static Pair<Boolean, String> isSensitiveValue(String value){
        Pair<Boolean, String> ipRst = hasSensitiveIP(value);
        if (ipRst.getKey()){
            return ipRst;
        }
        Pair<Boolean, String> pwdRst = hasSensitivePwd(value);
        return pwdRst;
    }

    private static Pair<Boolean, String> hasSensitivePwd(String value){
        if (value.length() < 8) {
            return Pair.of(false, value);
        }
        String specialCharacters = "!@#$%^&*()_-";
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : value.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                if (specialCharacters.contains(String.valueOf(c))) {
                    hasSpecialChar = true;
                }
            }
        }
        //同时包含字母/数字/特殊字符
        if (hasLetter && hasDigit && hasSpecialChar){
            return Pair.of(true, maskValue(value, "value"));
        }

        return Pair.of(false, value);
    }

    private static Pair<Boolean, String> hasSensitiveIP(String value){
        String ipPattern = "^((\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])$";
        if (!ipWhite.contains(value) && Pattern.matches(ipPattern, value)){
            return Pair.of(true, maskValue(value, "ip"));
        }
        return Pair.of(false, value);
    }

    //对敏感信息进行脱敏
    private static String maskValue(String value, String type) {
        if ("ip".equals(type)){
            if (ipWhite.contains(value)){
                return value;
            }
            return "*.*.*.*";
        }
        return value.replaceAll(".", "*");
    }

}
