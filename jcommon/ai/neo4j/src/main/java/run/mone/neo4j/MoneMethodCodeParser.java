package run.mone.neo4j;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;

import java.io.File;
import java.util.*;

/**
 * @author goodjava@qq.com
 * <p>
 * 建立方法调用之间的关系(call),且只建立业务调用的关系
 */
@Slf4j
public class MoneMethodCodeParser {

    private String NEO4J_URI = "bolt://localhost:7687";

    private String NEO4J_USER = "neo4j";

    private String password = "";

    public MoneMethodCodeParser setPassword(String password) {
        this.password = password;
        return this;
    }


    public void writeJavaFilesToNeo4j(String directoryPath) {
        if (new File(directoryPath).isFile()) {
            writeToNeo4j(directoryPath);
            return;
        }

        getJavaFilesInDirectory(directoryPath).forEach(it -> {
            log.info("parse it:{}", it);
            writeToNeo4j(it);
        });
    }

    //给一个文件夹,获取里边是.java文件的列表,注意你需要递归获取(class)
    public static List<String> getJavaFilesInDirectory(String directoryPath) {
        List<String> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        javaFiles.addAll(getJavaFilesInDirectory(file.getAbsolutePath()));
                    } else if (file.getName().endsWith(".java")) {
                        javaFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return javaFiles;
    }


    @SneakyThrows
    private void writeToNeo4j(String filePath) {
        //写入到neo4j中
        // 替换成你的 Java 文件路径
        String projectName = "MyProject";

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            // 解析 Java 文件
            CompilationUnit cu = new JavaParser().parse(new File(filePath)).getResult().get();

            // 遍历类、接口、方法等
            cu.accept(new MoneMethodCodeParser.Visitor(session, projectName, filePath), null);
        }
    }


    private static class Visitor extends com.github.javaparser.ast.visitor.VoidVisitorAdapter<Void> {

        private final Session session;

        public Visitor(Session session, String projectName, String filePath) {
            this.session = session;
        }


        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            super.visit(n, arg);
        }


        private String getFullMethodName(MethodDeclaration method) {
            String packageName = method.findCompilationUnit()
                    .flatMap(cu -> cu.getPackageDeclaration())
                    .map(pd -> pd.getNameAsString())
                    .orElse("");
            String className = method.findAncestor(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)
                    .map(c -> c.getNameAsString())
                    .orElse("");
            String methodName = method.getNameAsString();
            return packageName + "." + className + "." + methodName;
        }

        /**
         * 获取方法调用的完整路径，包括包名、类名和方法名
         *
         * @param methodCall 方法调用表达式
         * @return 方法调用的完整路径
         */
        public String getFullMethodPath(MethodCallExpr methodCall) {
            StringBuilder fullPath = new StringBuilder();

            // 获取包名
            Optional<CompilationUnit> cu = methodCall.findCompilationUnit();
            if (cu.isPresent()) {
                cu.get().getPackageDeclaration().ifPresent(pkg ->
                        fullPath.append(pkg.getNameAsString()).append(".")
                );
            }

            // 获取类名
            String className = methodCall.findAncestor(ClassOrInterfaceDeclaration.class)
                    .map(ClassOrInterfaceDeclaration::getNameAsString)
                    .orElse("");

            // 获取方法调用的对象
            String objectName = methodCall.getScope()
                    .map(scope -> scope.toString())
                    .orElse("");


            //静态调用
            if (methodCall.getScope().isPresent() && methodCall.getScope().get() instanceof FieldAccessExpr) {
                return objectName + "." + methodCall.getNameAsString();
            }

            //lombok 的log
            if (isLogCall(methodCall)) {
                return objectName + "." + methodCall.getNameAsString();
            }

            // 如果对象名不为空，尝试找到它的类型
            if (!objectName.isEmpty()) {
                Optional<FieldDeclaration> field = methodCall.findAncestor(ClassOrInterfaceDeclaration.class)
                        .flatMap(classDecl -> classDecl.getFieldByName(objectName));

                if (field.isPresent()) {
                    ClassOrInterfaceType type = field.get().getVariable(0).getType().asClassOrInterfaceType();
                    String v = resolveTypePath(type);
                    return v + "." + methodCall.getNameAsString();
                }
            }


            // 构建完整路径
            fullPath.append(className).append(".");
            fullPath.append(methodCall.getNameAsString());

            return fullPath.toString();
        }

        public static String resolveTypePath(ClassOrInterfaceType type) {
            String typeName = type.getNameAsString();

            Optional<CompilationUnit> cu = type.findAncestor(CompilationUnit.class);
            if (cu.isPresent()) {
                // 尝试从导入声明中查找匹配
                Optional<String> importedPath = findMatchingImport(cu.get(), typeName);
                if (importedPath.isPresent()) {
                    return importedPath.get();
                }

                // 如果没有找到匹配的导入，检查是否在同一包中
                Optional<String> currentPackage = getCurrentPackage(cu.get());
                if (currentPackage.isPresent()) {
                    return currentPackage.get() + "." + typeName;
                }
            }

            // 如果无法解析，返回原始类型名称
            return typeName;
        }

        private static Optional<String> findMatchingImport(CompilationUnit cu, String typeName) {
            return cu.getImports().stream()
                    .filter(importDecl -> !importDecl.isAsterisk() && importDecl.getNameAsString().endsWith("." + typeName))
                    .map(ImportDeclaration::getNameAsString)
                    .findFirst();
        }

        private static Optional<String> getCurrentPackage(CompilationUnit cu) {
            return cu.getPackageDeclaration().map(pd -> pd.getNameAsString());
        }

        /**
         * 判断方法调用是否为日志调用
         *
         * @param n 方法调用表达式
         * @return 如果方法调用是日志调用则返回true，否则返回false
         */
        private boolean isLogCall(MethodCallExpr n) {
            if (!n.getScope().isPresent()) {
                return false;
            }
            String scope = n.getScope().get().toString();
            String method = n.getNameAsString();
            return scope.equals("log") &&
                    (method.equals("trace") || method.equals("debug") || method.equals("info") ||
                            method.equals("warn") || method.equals("error"));
        }


        @Override
        public void visit(MethodDeclaration n, Void arg) {
            super.visit(n, arg);
            if (n.findAncestor(ClassOrInterfaceDeclaration.class).isEmpty()) {
                return;
            }
            // 处理方法调用
            processMethodCalls(n);

        }


        // 处理方法调用
        private void processMethodCalls(MethodDeclaration n) {
            n.findAll(MethodCallExpr.class).forEach(methodCall -> {
                Map<String, Object> callParams = new HashMap<>();
                callParams.put("callerName", getFullMethodName(n));
                callParams.put("calleeName", getFullMethodPath(methodCall));

                // 检查 callerName 和 calleeName 是否都存在
                Result result = session.run("MATCH (caller:Method {name: $callerName}), (callee:Method {name: $calleeName}) " +
                        "RETURN caller, callee", callParams);

                //只有两个业务method,才有必要创建这个边
                if (result.hasNext()) {
                    // 创建 CALLS 关系
                    session.run("MATCH (caller:Method {name: $callerName}) " +
                                    "MATCH (callee:Method {name: $calleeName}) " +
                                    "MERGE (caller)-[:CALLS]->(callee)",
                            callParams);
                }
            });
        }


        // 处理注释
        private void processComments(MethodDeclaration n) {
            for (Comment comment : n.getAllContainedComments()) {
                createCommentNode(comment, n);
            }

            Optional<JavadocComment> optional = n.getJavadocComment();
            if (optional.isPresent()) {
                createCommentNode(optional.get(), n);
            }

            Optional<Comment> commentOptional = n.getComment();
            if (commentOptional.isPresent()) {
                createCommentNode(commentOptional.get(), n);
            }
        }

        private void createCommentNode(Comment comment, MethodDeclaration n) {
            Map<String, Object> commentParams = new HashMap<>();
            commentParams.put("text", comment.getContent());
            commentParams.put("text_vector", new float[]{}); // 替换为实际的文本向量

            session.run("MERGE (comment:Comment {text: $text, text_vector: $text_vector})", commentParams);

            // 创建 DOCUMENTS 关系 (Comment -[:DOCUMENTS]-> Method)
            Map<String, Object> documentsParams = new HashMap<>();
            documentsParams.put("commentText", comment.getContent());
            documentsParams.put("methodName", getFullMethodName(n));
            documentsParams.put("methodSignature", n.getSignature().asString());
            session.run("MATCH (comment:Comment {text: $commentText}) " +
                            "MATCH (m:Method {name: $methodName, signature: $methodSignature}) " +
                            "MERGE (comment)-[:DOCUMENTS]->(m)",
                    documentsParams);
        }

    }

}
