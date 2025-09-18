package run.mone.mcp.codecheck.tool;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.JsonObject;
import lombok.Data;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 代码安全检查工具
 * 分析代码的安全风险、循环复杂度、日志打印等问题
 * 
 * @author goodjava@qq.com
 */
public class CodeSecurityCheckTool implements ITool {

    private static final List<String> SECURITY_RISK_PATTERNS = Arrays.asList(
        "Runtime.getRuntime().exec",
        "ProcessBuilder",
        "System.exit",
        "Class.forName",
        "URLClassLoader",
        "ScriptEngine",
        "eval(",
        "executeQuery",
        "createStatement",
        "prepareStatement",
        "File(",
        "FileInputStream",
        "FileOutputStream",
        "RandomAccessFile",
        "Socket(",
        "ServerSocket",
        "URL(",
        "HttpURLConnection",
        "reflection",
        "setAccessible(true)",
        "getDeclaredField",
        "getDeclaredMethod",
        "newInstance()"
    );

    private static final List<String> LOG_PATTERNS = Arrays.asList(
        "System.out.print",
        "System.err.print",
        "printStackTrace()",
        "log.debug",
        "log.info",
        "log.warn",
        "log.error",
        "logger.debug",
        "logger.info",
        "logger.warn",
        "logger.error",
        "Logger.getLogger",
        "LoggerFactory.getLogger"
    );

    @Override
    public String getName() {
        return "code_security_check";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public String description() {
        return """
                网关Filter代码安全检查工具，用于分析Java代码的安全风险、循环复杂度和日志打印情况。
                
                **功能特性：**
                - 检测潜在的安全风险（如反射、文件操作、网络连接、SQL注入等）
                - 分析循环复杂度（for、while、do-while循环）
                - 检测日志打印语句
                - 基于10分制评分系统，发现问题会相应减分
                
                **使用场景：**
                - 网关Filter代码审查
                - 代码质量评估
                - 安全风险评估
                - 性能问题识别
                """;
    }

    @Override
    public String parameters() {
        return """
                - code: (必需) 要检查的Java代码字符串
                - check_type: (可选) 检查类型，可选值：
                  - 'all': 全面检查（默认）
                  - 'security': 仅安全检查
                  - 'loops': 仅循环检查
                  - 'logs': 仅日志检查
                - strict_mode: (可选) 严格模式，true/false，默认false
                """;
    }

    @Override
    public String usage() {
        return """
                使用示例：
                
                1. 全面代码检查：
                {
                  "code": "public class TestFilter { public void doFilter() { while(true) { System.out.println(\"test\"); } } }",
                  "check_type": "all"
                }
                
                2. 仅安全检查：
                {
                  "code": "public class Test { public void test() { Runtime.getRuntime().exec(\"ls\"); } }",
                  "check_type": "security"
                }
                
                返回格式：
                {
                  "score": 7,
                  "total_score": 10,
                  "issues": [
                    {
                      "type": "SECURITY_RISK",
                      "description": "发现潜在安全风险：Runtime.getRuntime().exec",
                      "severity": "HIGH",
                      "deduction": 2
                    }
                  ],
                  "summary": "代码检查完成，发现1个安全风险问题"
                }
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            String code = inputJson.has("code") ? inputJson.get("code").getAsString() : "";
            String checkType = inputJson.has("check_type") ? inputJson.get("check_type").getAsString() : "all";
            boolean strictMode = inputJson.has("strict_mode") && inputJson.get("strict_mode").getAsBoolean();
            
            if (code.trim().isEmpty()) {
                result.addProperty("error", "代码内容不能为空");
                return result;
            }
            
            CodeCheckResult checkResult = analyzeCode(code, checkType, strictMode);
            
            result.addProperty("score", checkResult.getScore());
            result.addProperty("total_score", 10);
            result.addProperty("summary", checkResult.getSummary());
            
            // 转换问题列表为JSON
            JsonObject issuesJson = new JsonObject();
            for (int i = 0; i < checkResult.getIssues().size(); i++) {
                CodeIssue issue = checkResult.getIssues().get(i);
                JsonObject issueJson = new JsonObject();
                issueJson.addProperty("type", issue.getType());
                issueJson.addProperty("description", issue.getDescription());
                issueJson.addProperty("severity", issue.getSeverity());
                issueJson.addProperty("deduction", issue.getDeduction());
                issuesJson.add("issue_" + (i + 1), issueJson);
            }
            result.add("issues", issuesJson);
            
            return result;
            
        } catch (Exception e) {
            result.addProperty("error", "代码分析失败: " + e.getMessage());
            return result;
        }
    }

    private CodeCheckResult analyzeCode(String code, String checkType, boolean strictMode) {
        CodeCheckResult result = new CodeCheckResult();
        result.setScore(10); // 满分开始
        List<CodeIssue> issues = new ArrayList<>();
        
        try {
            // 尝试解析Java代码
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(code).getResult().orElse(null);
            
            if (cu != null) {
                // 使用AST分析
                analyzeWithAST(cu, issues, checkType, strictMode);
            } else {
                // 如果AST解析失败，使用字符串匹配
                analyzeWithStringMatching(code, issues, checkType, strictMode);
            }
        } catch (Exception e) {
            // AST解析失败，使用字符串匹配作为备选方案
            analyzeWithStringMatching(code, issues, checkType, strictMode);
        }
        
        // 计算最终分数
        int totalDeduction = issues.stream().mapToInt(CodeIssue::getDeduction).sum();
        result.setScore(Math.max(0, 10 - totalDeduction));
        result.setIssues(issues);
        result.setSummary(generateSummary(issues, result.getScore()));
        
        return result;
    }

    private void analyzeWithAST(CompilationUnit cu, List<CodeIssue> issues, String checkType, boolean strictMode) {
        if ("all".equals(checkType) || "security".equals(checkType)) {
            checkSecurityRisksAST(cu, issues, strictMode);
        }
        
        if ("all".equals(checkType) || "loops".equals(checkType)) {
            checkLoopsAST(cu, issues, strictMode);
        }
        
        if ("all".equals(checkType) || "logs".equals(checkType)) {
            checkLogsAST(cu, issues, strictMode);
        }
    }

    private void analyzeWithStringMatching(String code, List<CodeIssue> issues, String checkType, boolean strictMode) {
        if ("all".equals(checkType) || "security".equals(checkType)) {
            checkSecurityRisksString(code, issues, strictMode);
        }
        
        if ("all".equals(checkType) || "loops".equals(checkType)) {
            checkLoopsString(code, issues, strictMode);
        }
        
        if ("all".equals(checkType) || "logs".equals(checkType)) {
            checkLogsString(code, issues, strictMode);
        }
    }

    private void checkSecurityRisksAST(CompilationUnit cu, List<CodeIssue> issues, boolean strictMode) {
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr n, Void arg) {
                String methodCall = n.toString();
                for (String pattern : SECURITY_RISK_PATTERNS) {
                    if (methodCall.contains(pattern)) {
                        int deduction = getSecurityRiskDeduction(pattern, strictMode);
                        issues.add(new CodeIssue(
                            "SECURITY_RISK",
                            "发现潜在安全风险：" + pattern + " 在方法调用 " + methodCall,
                            getSeverity(pattern),
                            deduction
                        ));
                        break;
                    }
                }
                super.visit(n, arg);
            }
        }, null);
    }

    private void checkLoopsAST(CompilationUnit cu, List<CodeIssue> issues, boolean strictMode) {
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ForStmt n, Void arg) {
                int deduction = strictMode ? 2 : 1;
                issues.add(new CodeIssue(
                    "LOOP_COMPLEXITY",
                    "发现for循环，可能影响网关性能",
                    "MEDIUM",
                    deduction
                ));
                super.visit(n, arg);
            }

            @Override
            public void visit(WhileStmt n, Void arg) {
                int deduction = strictMode ? 3 : 2;
                issues.add(new CodeIssue(
                    "LOOP_COMPLEXITY",
                    "发现while循环，需要注意死循环风险",
                    "HIGH",
                    deduction
                ));
                super.visit(n, arg);
            }

            @Override
            public void visit(DoStmt n, Void arg) {
                int deduction = strictMode ? 3 : 2;
                issues.add(new CodeIssue(
                    "LOOP_COMPLEXITY",
                    "发现do-while循环，需要注意死循环风险",
                    "HIGH",
                    deduction
                ));
                super.visit(n, arg);
            }
        }, null);
    }

    private void checkLogsAST(CompilationUnit cu, List<CodeIssue> issues, boolean strictMode) {
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr n, Void arg) {
                String methodCall = n.toString();
                for (String pattern : LOG_PATTERNS) {
                    if (methodCall.contains(pattern)) {
                        int deduction = getLogDeduction(pattern, strictMode);
                        issues.add(new CodeIssue(
                            "LOG_OUTPUT",
                            "发现日志输出：" + pattern + "，可能影响网关性能",
                            getLogSeverity(pattern),
                            deduction
                        ));
                        break;
                    }
                }
                super.visit(n, arg);
            }
        }, null);
    }

    private void checkSecurityRisksString(String code, List<CodeIssue> issues, boolean strictMode) {
        for (String pattern : SECURITY_RISK_PATTERNS) {
            if (code.contains(pattern)) {
                int deduction = getSecurityRiskDeduction(pattern, strictMode);
                issues.add(new CodeIssue(
                    "SECURITY_RISK",
                    "发现潜在安全风险：" + pattern,
                    getSeverity(pattern),
                    deduction
                ));
            }
        }
    }

    private void checkLoopsString(String code, List<CodeIssue> issues, boolean strictMode) {
        if (code.contains("for(") || code.contains("for (")) {
            int deduction = strictMode ? 2 : 1;
            issues.add(new CodeIssue(
                "LOOP_COMPLEXITY",
                "发现for循环，可能影响网关性能",
                "MEDIUM",
                deduction
            ));
        }
        
        if (code.contains("while(") || code.contains("while (")) {
            int deduction = strictMode ? 3 : 2;
            issues.add(new CodeIssue(
                "LOOP_COMPLEXITY",
                "发现while循环，需要注意死循环风险",
                "HIGH",
                deduction
            ));
        }
        
        if (code.contains("do{") || code.contains("do {")) {
            int deduction = strictMode ? 3 : 2;
            issues.add(new CodeIssue(
                "LOOP_COMPLEXITY",
                "发现do-while循环，需要注意死循环风险",
                "HIGH",
                deduction
            ));
        }
    }

    private void checkLogsString(String code, List<CodeIssue> issues, boolean strictMode) {
        for (String pattern : LOG_PATTERNS) {
            if (code.contains(pattern)) {
                int deduction = getLogDeduction(pattern, strictMode);
                issues.add(new CodeIssue(
                    "LOG_OUTPUT",
                    "发现日志输出：" + pattern + "，可能影响网关性能",
                    getLogSeverity(pattern),
                    deduction
                ));
            }
        }
    }

    private int getSecurityRiskDeduction(String pattern, boolean strictMode) {
        // 高风险操作
        if (pattern.contains("Runtime.getRuntime().exec") || 
            pattern.contains("ProcessBuilder") || 
            pattern.contains("System.exit")) {
            return strictMode ? 4 : 3;
        }
        // 中等风险操作
        if (pattern.contains("Class.forName") || 
            pattern.contains("reflection") || 
            pattern.contains("setAccessible")) {
            return strictMode ? 3 : 2;
        }
        // 低风险操作
        return strictMode ? 2 : 1;
    }

    private int getLogDeduction(String pattern, boolean strictMode) {
        // System.out/err 输出
        if (pattern.contains("System.out") || pattern.contains("System.err")) {
            return strictMode ? 2 : 1;
        }
        // 日志框架输出
        return strictMode ? 1 : 1;
    }

    private String getSeverity(String pattern) {
        if (pattern.contains("Runtime.getRuntime().exec") || 
            pattern.contains("ProcessBuilder") || 
            pattern.contains("System.exit")) {
            return "HIGH";
        }
        if (pattern.contains("Class.forName") || 
            pattern.contains("reflection") || 
            pattern.contains("setAccessible")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String getLogSeverity(String pattern) {
        if (pattern.contains("System.out") || pattern.contains("System.err")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String generateSummary(List<CodeIssue> issues, int score) {
        if (issues.isEmpty()) {
            return "代码检查完成，未发现问题，得分：" + score + "/10";
        }
        
        Map<String, Long> issueCount = new HashMap<>();
        issues.forEach(issue -> 
            issueCount.merge(issue.getType(), 1L, Long::sum)
        );
        
        StringBuilder summary = new StringBuilder("代码检查完成，得分：" + score + "/10。发现问题：");
        issueCount.forEach((type, count) -> {
            String typeName = switch (type) {
                case "SECURITY_RISK" -> "安全风险";
                case "LOOP_COMPLEXITY" -> "循环复杂度";
                case "LOG_OUTPUT" -> "日志输出";
                default -> type;
            };
            summary.append(typeName).append("(").append(count).append("个) ");
        });
        
        return summary.toString();
    }

    @Data
    private static class CodeCheckResult {
        private int score;
        private List<CodeIssue> issues;
        private String summary;
    }

    @Data
    private static class CodeIssue {
        private String type;
        private String description;
        private String severity;
        private int deduction;

        public CodeIssue(String type, String description, String severity, int deduction) {
            this.type = type;
            this.description = description;
            this.severity = severity;
            this.deduction = deduction;
        }
    }
}
