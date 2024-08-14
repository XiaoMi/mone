package run.mone.z.desensitization.service.common;

import com.alibaba.nacos.common.utils.MapUtils;
import com.google.common.collect.Sets;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;
import run.mone.antlr.golang.Field;
import run.mone.antlr.golang.GoCode;
import run.mone.antlr.golang.ParseResult;
import run.mone.z.desensitization.api.bo.DesensitizeReq;
import run.mone.z.desensitization.api.bo.SensitiveWordConfigBo;
import run.mone.z.desensitization.api.common.CodeTypeEnum;
import run.mone.z.desensitization.api.common.SensitiveWordTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static run.mone.z.desensitization.service.common.Consts.*;

/**
 * @author wmin
 * @date 2023/6/5
 * 可根据指定的敏感词进行代码脱敏
 */
@Slf4j
public class NewCodeDesensitizeUtils {

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

    public static String codeDesensitizeForClass(String sourceCode, String className, List<SensitiveWordConfigBo> sensitiveWordConfigs) throws Exception {
        CompilationUnitTree compilationUnit = ClassUtils.getUnitTreeWithClassName(sourceCode, className);
        TreeScanner scanner = getTreeScanner(sensitiveWordConfigs);
        scanner.scan(compilationUnit, null);
        String codeAfterDesensitized = compilationUnit.toString();
        if (tmpClassName.equals(className)){
            codeAfterDesensitized = codeAfterDesensitized.substring(tmpClassNamePrefix.length()+1, codeAfterDesensitized.length()-1).replaceAll("^\\n+|\\n+$", "");
        }
        log.info("codeAfterDesensitized ======{}======", codeAfterDesensitized);
        return codeAfterDesensitized;
    }

    public static String codeDesensitizeForClass(DesensitizeReq req) throws Exception {
        String sourceCode = req.getText();
        Pair<String,String> rst = CodeParseUtils.codeParse(sourceCode);
        if (rst!=null){
            if (CodeTypeEnum.CLASS.getType().equals(rst.getKey())){
                return codeDesensitizeForClass(sourceCode, rst.getValue(), req.getSensitiveWordConfigBo());
            }
            if (CodeTypeEnum.METHOD.getType().equals(rst.getKey())){
                return codeDesensitizeForClass(tmpClassNamePrefix + sourceCode + tmpClassNameSuffix, rst.getValue(), req.getSensitiveWordConfigBo());
            }
        }
        return "";
    }

    private static synchronized TreeScanner getTreeScanner(List<SensitiveWordConfigBo> sensitiveWordConfigs){
        if (scanner!=null){
            return scanner;
        }
        final List<SensitiveWordConfigBo> sensitiveWordConfigList = CollectionUtils.isEmpty(sensitiveWordConfigs)?new ArrayList<>():sensitiveWordConfigs;
        Context context = new Context();
        JavacFileManager.preRegister(context);
        TreeMaker treeMaker = TreeMaker.instance(context);
        scanner = new TreeScanner<Void, Void>() {
            List<SensitiveWordConfigBo> keys = getConfiguredSensitiveKeys(sensitiveWordConfigList);
            List<SensitiveWordConfigBo> values = getConfiguredSensitiveValues(sensitiveWordConfigList);
            @Override
            public Void visitVariable(VariableTree variableTree, Void aVoid) {
                String key = variableTree.getName().toString();

                //已初始化
                if (variableTree.getInitializer()!=null) {
                    ExpressionTree initializer = variableTree.getInitializer();
                    if (initializer instanceof LiteralTree) {
                        String value = ((LiteralTree) initializer).getValue().toString();
                        String valueDesensitized = value;
                        if (isSensitiveKey(key, keys)){
                            valueDesensitized = maskValue(value, ipKeys.contains(key.toLowerCase())?"ip":"content");
                        }
                        Pair<Boolean, String> valuePair = isSensitiveValue(value, values);
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
                                            Pair<Boolean, String> pair = isSensitiveValue(value, values);
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


    private static boolean isSensitiveKey(String key, List<SensitiveWordConfigBo> sensitiveWordConfigs){
        //todo 只要包含就算，可能误判
        ZNormalizer.normailize(key);
        if (CollectionUtils.isEmpty(sensitiveWordConfigs)){
            for (String keyword : filedOrMethodNames) {
                if (key.toLowerCase().contains(keyword.toLowerCase())) {
                    return true;
                }
            }
        }
        for (SensitiveWordConfigBo config : sensitiveWordConfigs){
            if (config.getRegexMatch()){
                Pattern pattern = Pattern.compile(config.getContent());
                if (pattern.matcher(key).matches()){
                    return true;
                }
            } else if (config.getCaseSensitive()){
                if (key.contains(config.getContent())) {
                    return true;
                }
            } else {
                if (key.toLowerCase().contains(config.getContent().toLowerCase())) {
                    return true;
                }
            }
        }
        return checkSensitivityFromZ(key);
    }

    private static boolean checkSensitivityFromZ(String key) {
        // TODO 从z获取敏感词信息
        return false;
    }

    private static Pair<Boolean, String> isSensitiveValue(String value, List<SensitiveWordConfigBo> sensitiveWordConfigs){
        if (CollectionUtils.isEmpty(sensitiveWordConfigs)){
            Pair<Boolean, String> ipRst = hasSensitiveIP(value);
            if (ipRst.getKey()){
                return ipRst;
            }
            Pair<Boolean, String> pwdRst = hasSensitivePwd(value);
            return pwdRst;
        }
        for (SensitiveWordConfigBo config : sensitiveWordConfigs){
            if (config.getRegexMatch()){
                Pattern pattern = Pattern.compile(config.getContent());
                if (pattern.matcher(value).matches()){
                    return Pair.of(true, maskValue(value, ""));
                }
            } else if (config.getCaseSensitive()){
                if (value.contains(config.getContent())) {
                    return Pair.of(true, maskValue(value, ""));
                }
            } else {
                if (value.toLowerCase().contains(config.getContent().toLowerCase())) {
                    return Pair.of(true, maskValue(value, ""));
                }
            }
        }
        return Pair.of(false, value);
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

    public static String codeDesensitizeForGo(DesensitizeReq req) {
        String sourceCode = req.getText();
        try {
            ParseResult parsedSource = GoCode.parse(sourceCode);
            sourceCode = doDesensitize(sourceCode, parsedSource, req.getSensitiveWordConfigBo());
        } catch (Exception e) {
            log.error("Error while try to parse and desensitize go code, req:{}, nested exception is:", req, e);
        }
        return sourceCode;
    }

    public static String doDesensitize(String sourceCode, ParseResult parsedSource, List<SensitiveWordConfigBo> sensitiveWordConfigBo) {
        List<Field> fieldList = parsedSource.getFieldList();
        for (Field f : fieldList) {
            // TODO: 当前处理方式将非method调用的语法元素统一处理
            if (!"method".equals(f.getType()) && isSensitiveKey(f.getK().toLowerCase(), getConfiguredSensitiveKeys(sensitiveWordConfigBo))) {
                String v = f.getV();
                int lengthOfAsterisk = v.length(); // 设定 "*" 的长度
                // 使用正则表达式替换所有的模式串为 "*"
                // TODO：当前为全文替换，后续可改为精准替换
                sourceCode = sourceCode.replaceAll(v, StringUtils.repeat("*", lengthOfAsterisk));
            } else {
                Map<String, List<String>> methodMap = parsedSource.getMethodMap();
                if (MapUtils.isNotEmpty(methodMap)) {
                    List<String> callParams = methodMap.get(f.getK());
                    List<Integer> markIndex = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(callParams)) {
                        for (int i = 0; i < callParams.size(); i++) {
                            if (isSensitiveKey(callParams.get(i), sensitiveWordConfigBo)) {
                                markIndex.add(i);
                            }
                        }
                    }
                    if (!CollectionUtils.isEmpty(markIndex)) {
                        String v = f.getV();
                        String[] callHolders = v.split(",");
                        if (callHolders != null && callHolders.length > 0) {
                            for (int index : markIndex) {
                                String callHolder = callHolders[index];
                                int lengthOfAsterisk = callHolder.length();
                                sourceCode = sourceCode.replaceAll(callHolder, StringUtils.repeat("*", lengthOfAsterisk));
                            }
                        }
                    }
                }
            }
        }
        return sourceCode;
    }

    private static List<SensitiveWordConfigBo> getConfiguredSensitiveKeys(List<SensitiveWordConfigBo> sensitiveWordConfigList) {
        return sensitiveWordConfigList.stream().filter(i ->
                null!=i && (SensitiveWordTypeEnum.All.code==i.getType() || SensitiveWordTypeEnum.FiledKey.code==i.getType())
        ).collect(Collectors.toList());
    }

    private static List<SensitiveWordConfigBo> getConfiguredSensitiveValues(List<SensitiveWordConfigBo> sensitiveWordConfigList) {
        return sensitiveWordConfigList.stream().filter(i ->
                null!=i && (SensitiveWordTypeEnum.All.code==i.getType() || SensitiveWordTypeEnum.FiledValue.code==i.getType())
        ).collect(Collectors.toList());
    }

}
