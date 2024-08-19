package run.mone.neo4j;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Data
public class MoneCodeParser {


    private String NEO4J_URI = "bolt://localhost:7687";

    private String NEO4J_USER = "neo4j";

    private String NEO4J_PASSWORD = "";

    private String embeddingUrl = "";


    //查询所有Comment的信息(使用neo4j),返回是个List(class)
    public List<Map<String, Object>> getAllComments() {
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
             Session session = driver.session()) {
            List<Map<String, Object>> comments = new ArrayList<>();
            Result result = session.run("MATCH (comment:Comment) RETURN comment, id(comment) as commentId");
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> commentMap = new HashMap<>(record.get("comment").asMap());
                commentMap.put("commentId", record.get("commentId").asLong());
                comments.add(commentMap);
            }
            return comments;
        }
    }


    /**
     * 根据文本向量查询评论
     *
     * @param text 输入的文本，用于生成查询向量
     * @return 查询结果的列表，包含评论节点和相似度分数
     */
	@SneakyThrows
    public List<Map<String, Object>> queryCommentsByTextVector(String text) {
        // 替换为你的查询向量
        double[] queryVector = getTextVectorFromHttp(text);

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
             Session session = driver.session()) {

            // 执行查询
            List<Record> results = session.run("MATCH (c:Comment) " +
                            "WITH c, gds.similarity.cosine(c.text_vector, $query_vector) AS score " +
                            "WHERE score > 0.8 " +
                            "RETURN c,score",
                    Values.parameters("query_vector", queryVector)).list();

            // 打印结果
            for (Record record : results) {
                System.out.println(record.get("c").asNode().get("text").asString());
                System.out.println(record.get("score").asDouble());
            }
        }
        return null;
    }

    //给你一个text,帮我查询Comment 中 text_vector 向量相似度最高的(class)
    @SneakyThrows
    public Map<String, Object> findMostSimilarComment(String text) {
        double[] textVector = getTextVectorFromHttp(text);
        List<Map<String, Object>> comments = getAllComments();
        Map<String, Object> mostSimilarComment = null;
        double highestSimilarity = -1;

        for (Map<String, Object> comment : comments) {
            double[] commentVector = convertListToFloatArray((List<Double>) comment.get("text_vector"));
            double similarity = calculateCosineSimilarity(textVector, commentVector);
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                mostSimilarComment = comment;
            }
        }

        return mostSimilarComment;
    }

    //List<Float> 转变成 float[](method)
    public double[] convertListToFloatArray(List<Double> floatList) {
        double[] floatArray = new double[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            floatArray[i] = floatList.get(i);
        }
        return floatArray;
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vectorA 第一个向量
     * @param vectorB 第二个向量
     * @return 两个向量的余弦相似度
     */
	private double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }


    //调用一个Http 接口获取到Comment 的 text_vector float[](根据text),然后填充到 Comment中的text_vector中(class)
    @SneakyThrows
    public void fillCommentTextVectors() {
        List<Map<String, Object>> comments = getAllComments();
        for (Map<String, Object> comment : comments) {
            String text = (String) comment.get("text");
            double[] textVector = getTextVectorFromHttp(text);
            System.out.println(textVector.length);
            comment.put("text_vector", textVector);
        }

        updateCommentsInNeo4j(comments);
    }

    //把Comment的修改,刷新回neo4j(class)
    public void updateCommentsInNeo4j(List<Map<String, Object>> comments) {
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
             Session session = driver.session()) {
            for (Map<String, Object> comment : comments) {
                Long commentId = (Long) comment.get("commentId");
                double[] textVector = (double[]) comment.get("text_vector");
                Map<String, Object> params = new HashMap<>();
                params.put("id", commentId);
                params.put("text_vector", textVector);
                session.run("MATCH (c:Comment) WHERE id(c)=$id SET c.text_vector = $text_vector", params);
            }
        }
    }


    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static Gson gson = new Gson();


    private  double[] getTextVectorFromHttp(String text) throws IOException {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("text", text);

        RequestBody body = RequestBody.create(jsonRequest.toString(), JSON);
        Request request = new Request.Builder()
                .url(embeddingUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            JsonArray dataArray = JsonParser.parseString(jsonObject.getAsJsonPrimitive("data").getAsString()).getAsJsonArray();

            double[] result = new double[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                result[i] = dataArray.get(i).getAsFloat();
            }

            return result;
        }
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

    //删除所有节点(class)
    public void deleteAllNodes() {
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
             Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    /**
     * 将指定的Java文件解析后写入到Neo4j数据库中
     *
     * @param filePath Java文件的路径
     */
	@SneakyThrows
    private void writeToNeo4j(String filePath) {
        //写入到neo4j中
        // 替换成你的 Java 文件路径
        String projectName = "MyProject";

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
             Session session = driver.session()) {

            // 解析 Java 文件
            CompilationUnit cu = new JavaParser().parse(new File(filePath)).getResult().get();

            // 创建 Project 节点
            createProjectNode(session, projectName);

            // 创建 File 节点
            createFileNode(session, projectName, filePath);

            // 遍历类、接口、方法等
            cu.accept(new Visitor(session, projectName, filePath), null);
        }
    }

    private static void createProjectNode(Session session, String projectName) {
        Map<String, Object> projectParams = new HashMap<>();
        projectParams.put("name", projectName);
        session.run("MERGE (p:Project {name: $name})", projectParams);
    }

    private static void createFileNode(Session session, String projectName, String filePath) {
        Map<String, Object> fileParams = new HashMap<>();
        fileParams.put("name", filePath);
        fileParams.put("path", filePath);
        fileParams.put("language", "Java");
        session.run("MERGE (f:File {name: $name, path: $path, language: $language})", fileParams);

        // 创建 CONTAINS 关系 (Project -[:CONTAINS]-> File)
        Map<String, Object> containsParams = new HashMap<>();
        containsParams.put("projectName", projectName);
        containsParams.put("fileName", filePath);
        session.run("MATCH (p:Project {name: $projectName}) " +
                        "MATCH (f:File {name: $fileName}) " +
                        "MERGE (p)-[:CONTAINS]->(f)",
                containsParams);
    }

    private static class Visitor extends com.github.javaparser.ast.visitor.VoidVisitorAdapter<Void> {

        private final Session session;
        private final String projectName;
        private final String filePath;

        public Visitor(Session session, String projectName, String filePath) {
            this.session = session;
            this.projectName = projectName;
            this.filePath = filePath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            // 创建 Class/Interface 节点
            Map<String, Object> classParams = new HashMap<>();
            classParams.put("name", n.getNameAsString());
            classParams.put("fullName", n.getFullyQualifiedName().orElse(""));

            session.run("MERGE (c:Class {name: $name, full_name: $fullName})", classParams);

            // 创建 CONTAINS 关系 (File -[:CONTAINS]-> Class)
            Map<String, Object> containsParams = new HashMap<>();
            containsParams.put("fileName", filePath);
            containsParams.put("className", n.getNameAsString());
            session.run("MATCH (f:File {name: $fileName}) " +
                            "MATCH (c:Class {name: $className}) " +
                            "MERGE (f)-[:CONTAINS]->(c)",
                    containsParams);

            super.visit(n, arg);

        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            super.visit(n, arg);

            // 创建 Method 节点
            Map<String, Object> methodParams = new HashMap<>();
            methodParams.put("name", n.getNameAsString());
            methodParams.put("signature", n.getSignature().asString());
            methodParams.put("code_vector", new float[]{}); // 替换为实际的代码向量

            session.run("MERGE (m:Method {name: $name, signature: $signature, code_vector: $code_vector})", methodParams);

            // 创建 DECLARES 关系 (Class -[:DECLARES]-> Method)
            Map<String, Object> declaresParams = new HashMap<>();

            if (n.findAncestor(ClassOrInterfaceDeclaration.class).isEmpty()) {
                return;
            }

            declaresParams.put("className", n.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString());
            declaresParams.put("methodName", n.getNameAsString());

            session.run("MATCH (c:Class {name: $className}) " +
                            "MATCH (m:Method {name: $methodName}) " +
                            "MERGE (c)-[:DECLARES]->(m)",
                    declaresParams);

            // 处理注释
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
            documentsParams.put("methodName", n.getNameAsString());
            documentsParams.put("methodSignature", n.getSignature().asString());
            session.run("MATCH (comment:Comment {text: $commentText}) " +
                            "MATCH (m:Method {name: $methodName, signature: $methodSignature}) " +
                            "MERGE (comment)-[:DOCUMENTS]->(m)",
                    documentsParams);
        }

    }


}
