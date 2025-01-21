package run.mone.local.docean.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.util.GsonUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 11:36
 */
public class GsonTest {


    @Test
    public void testReplace() {
        String str = "{\n\"output\": \"package run.mone.demo.controller;\n\nimport com.xiaomi.youpin.docean.anno.Controller;\nimport com.xiaomi.youpin.docean.anno.RequestMapping;" +
                "\nimport org.bson.Document;\nimport run.mone.demo.bo.Article;\nimport run.mone.demo.bo.Comment;\nimport run.mone.demo.service.ArticleService;" +
                "\nimport run.mone.demo.service.CommentService;\n\nimport javax.annotation.Resource;\nimport java.util.List;\nimport java.util.stream.Collectors;" +
                "\n\n/**\n * 评论控制器\n *\n * @author ChatGPT\n " +
                "*/\n@Controller\n" +
                "@RequestMapping(path = \"/comment\")" +
                "return article;\n \\\"   }\n}\n\\\"\"\n}";
        str = JsonElementUtils.fixJson(str);
        System.out.println(JsonParser.parseString(str));
    }

    @Test
    public void test5() {
        String str = "{\n\"output\":\"\\\"package run.mone.demo.controller;\n\nimport com.xiaomi.youpin.docean.anno.RequestMapping;\nimport org.bson.Document;\nimport run.mone.demo.bo.ExamResult;\nimport run.mone.demo.bo.User;\nimport run.mone.demo.service.ExamResultService;\nimport run.mone.demo.service.UserService;\n\nimport javax.annotation.Resource;\nimport java.util.HashMap;\nimport java.util.List;\nimport java.util.Map;\nimport java.util.stream.Collectors;\n\n/**\n * 统计数据控制器\n *\n * @author ChatGPT\n */\n@RequestMapping(path = \\\"/statistics\\\")\npublic class DataStatisticsController extends MongodbController {\n\n    @Resource\n    private UserService userService;\n\n    @Resource\n    private ExamResultService examResultService;\n\n    public DataStatisticsController() {\n        super(\\\"statistics\\\");\n    }\n\n    /**\n     * 获取成绩分布统计\n     *\n     * @return 成绩分布统计结果\n     */\n    @RequestMapping(path = \\\"/scoreDistribution\\\")\n    public Map<String, Long> getScoreDistribution() {\n        List<ExamResult> examResults = examResultService.findAll();\n        Map<Integer, Long> scoreDistributionMap = examResults.stream()\n                .collect(Collectors.groupingBy(ExamResult::getScore, Collectors.counting()));\n        Map<String, Long> result = new HashMap<>();\n        for (Map.Entry<Integer, Long> entry : scoreDistributionMap.entrySet()) {\n            result.put(entry.getKey().toString(), entry.getValue());\n        }\n        return result;\n    }\n\n    /**\n     * 获取用户总数统计\n     *\n     * @return 用户总数\n     */\n    @RequestMapping(path = \\\"/userCount\\\")\n    public long getUserCount() {\n        return userService.countDocuments();\n    }\n}\"\n}";
        System.out.println(str);

        System.out.println(JsonParser.parseString(str));

    }

    @Test
    public void test6() {
        String json = "{\n\"key\": \"\"Value with \"improper escape\" \"and another \\\"error\\\" here.\"\"} ";
        json = JsonElementUtils.fixJson(json);
        System.out.println(JsonParser.parseString(json));
    }



    private static String fixJsonString(String invalidJson, AtomicInteger num) {
        if (num.get() >= 15) {
            return invalidJson;
        }
        try {
            // 尝试解析JSON字符串
            new JsonParser().parse(invalidJson);
            // 如果解析成功,说明JSON字符串有效,直接返回
            System.out.println("success");
            return invalidJson;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            // 从异常消息中获取出错位置的索引
            String errorMessage = e.getMessage();
            int idx = errorMessage.indexOf("at line 1 column ");
            if (idx != -1) {
                idx += "at line 1 column ".length();
                int endIdx = errorMessage.indexOf(" path", idx);
                String positionStr = errorMessage.substring(idx, endIdx);
                idx = Math.max(Integer.parseInt(positionStr) - 4, 0);
            } else {
                // 无法获取出错位置,返回null
                return null;
            }

            // 使用StringBuilder构建修复后的JSON字符串
            StringBuilder sb = new StringBuilder(invalidJson);
            for (int i = idx; i < sb.length(); i++) {
                if (sb.charAt(i) == '"') {
                    sb.insert(i, '\\');
                    break;
                }
            }

            // 返回修复后的JSON字符串
            String str = sb.toString();
            try {
                new JsonParser().parse(str);
                return str;
            } catch (JsonSyntaxException ex) {
                num.incrementAndGet();
                return fixJsonString(str, num);
            }
        }
    }


    @Test
    public void test2() {
        String str = "{\n" +
                "    \"output\": \"{\\n\\\"output\\\": \\\"package run.mone.demo.controller;\\n\\nimport com.xiaomi.youpin.docean.anno.Controller;\\nimport com.xiaomi.youpin.docean.anno.RequestMapping;\\nimport org.bson.Document;\\nimport run.mone.demo.bo.Article;\\nimport run.mone.demo.bo.Comment;\\nimport run.mone.demo.service.ArticleService;\\nimport run.mone.demo.service.CommentService;\\n\\nimport javax.annotation.Resource;\\nimport java.util.List;\\nimport java.util.stream.Collectors;\\n\\n/**\\n * 评论控制器\\n *\\n * @author ChatGPT\\n */\\n@Controller\\n@RequestMapping(path = \\\"/comment\\\")\\npublic class CommentController extends MongodbController {\\n\\n    @Resource\\n    private CommentService commentService;\\n\\n    @Resource\\n    private ArticleService articleService;\\n\\n    public CommentController() {\\n        super(\\\"comments\\\");\\n    }\\n\\n    /**\\n     * 根据文章ID获取所有评论\\n     *\\n     * @param articleId 文章ID\\n     * @return 评论列表\\n     */\\n    @RequestMapping(path = \\\"/getByArticleId\\\")\\n    public List<Comment> getCommentsByArticleId(int articleId) {\\n        return commentService.getCommentsByArticleId(articleId);\\n    }\\n\\n    /**\\n     * 添加新评论\\n     *\\n     * @param comment 评论对象\\n     * @return 是否添加成功\\n     */\\n    @RequestMapping(path = \\\"/add\\\")\\n    public boolean addComment(Comment comment) {\\n        return commentService.addComment(comment);\\n    }\\n\\n    /**\\n     * 更新评论\\n     *\\n     * @param comment 更新后的评论对象\\n     * @return 是否更新成功\\n     */\\n    @RequestMapping(path = \\\"/update\\\")\\n    public boolean updateComment(Comment comment) {\\n        return commentService.updateComment(comment);\\n    }\\n\\n    /**\\n     * 删除评论\\n     *\\n     * @param commentId 评论ID\\n     * @return 是否删除成功\\n     */\\n    @RequestMapping(path = \\\"/delete\\\")\\n    public boolean deleteComment(int commentId) {\\n        return commentService.deleteComment(commentId);\\n    }\\n\\n    /**\\n     * 将 Comment 对象转换为 Document\\n     *\\n     * @param comment 评论对象\\n     * @return Document\\n     */\\n    private Document convertCommentToDocument(Comment comment) {\\n        Document document = new Document();\\n        document.append(\\\"articleId\\\", comment.getArticleId());\\n        document.append(\\\"content\\\", comment.getContent());\\n        document.append(\\\"author\\\", comment.getAuthor());\\n        document.append(\\\"createTime\\\", comment.getCreateTime());\\n        return document;\\n    }\\n\\n    /**\\n     * 将 Document 转换为 Comment 对象\\n     *\\n     * @param document Document\\n     * @return 评论对象\\n     */\\n    private Comment convertDocumentToComment(Document document) {\\n        Comment comment = new Comment();\\n        comment.setArticleId(document.getInteger(\\\"articleId\\\"));\\n        comment.setContent(document.getString(\\\"content\\\"));\\n        comment.setAuthor(document.getString(\\\"author\\\"));\\n        comment.setCreateTime(document.getDate(\\\"createTime\\\"));\\n        return comment;\\n    }\\n\\n    /**\\n     * 获取文章及其评论\\n     *\\n     * @param articleId 文章ID\\n     * @return 文章对象及其评论列表\\n     */\\n    @RequestMapping(path = \\\"/getArticleWithComments\\\")\\n    public Article getArticleWithComments(int articleId) {\\n        Article article = articleService.findArticleById(articleId);\\n        List<Comment> comments = getCommentsByArticleId(articleId);\\n        article.setComments(comments);\\n        return article;\\n    }\\n}\\n\\\"\\n}\"\n" +
                "}";

        String s = JsonParser.parseString(str).getAsJsonObject().get("output").getAsString();
        System.out.println(s);
    }

    @Test
    public void test3() {
        String str = "{\n" +
                "    \"output\": \"{\\n\\\"output\\\": \\\"package run.mone.demo.controller;\\n\\nimport com.xiaomi.youpin.docean.anno.Controller;\\nimport com.xiaomi.youpin.docean.anno.RequestMapping;\\nimport org.bson.Document;\\nimport run.mone.demo.bo.Article;\\nimport run.mone.demo.bo.Comment;\\nimport run.mone.demo.service.ArticleService;\\nimport run.mone.demo.service.CommentService;\\n\\nimport javax.annotation.Resource;\\nimport java.util.List;\\nimport java.util.stream.Collectors;\\n\\n/**\\n * 评论控制器\\n *\\n * @author ChatGPT\\n */\\n@Controller\\n@RequestMapping(path = \\\"/comment\\\")\\npublic class CommentController extends MongodbController {\\n\\n    @Resource\\n    private CommentService commentService;\\n\\n    @Resource\\n    private ArticleService articleService;\\n\\n    public CommentController() {\\n        super(\\\"comments\\\");\\n    }\\n\\n    /**\\n     * 根据文章ID获取所有评论\\n     *\\n     * @param articleId 文章ID\\n     * @return 评论列表\\n     */\\n    @RequestMapping(path = \\\"/getByArticleId\\\")\\n    public List<Comment> getCommentsByArticleId(int articleId) {\\n        return commentService.getCommentsByArticleId(articleId);\\n    }\\n\\n    /**\\n     * 添加新评论\\n     *\\n     * @param comment 评论对象\\n     * @return 是否添加成功\\n     */\\n    @RequestMapping(path = \\\"/add\\\")\\n    public boolean addComment(Comment comment) {\\n        return commentService.addComment(comment);\\n    }\\n\\n    /**\\n     * 更新评论\\n     *\\n     * @param comment 更新后的评论对象\\n     * @return 是否更新成功\\n     */\\n    @RequestMapping(path = \\\"/update\\\")\\n    public boolean updateComment(Comment comment) {\\n        return commentService.updateComment(comment);\\n    }\\n\\n    /**\\n     * 删除评论\\n     *\\n     * @param commentId 评论ID\\n     * @return 是否删除成功\\n     */\\n    @RequestMapping(path = \\\"/delete\\\")\\n    public boolean deleteComment(int commentId) {\\n        return commentService.deleteComment(commentId);\\n    }\\n\\n    /**\\n     * 将 Comment 对象转换为 Document\\n     *\\n     * @param comment 评论对象\\n     * @return Document\\n     */\\n    private Document convertCommentToDocument(Comment comment) {\\n        Document document = new Document();\\n        document.append(\\\"articleId\\\", comment.getArticleId());\\n        document.append(\\\"content\\\", comment.getContent());\\n        document.append(\\\"author\\\", comment.getAuthor());\\n        document.append(\\\"createTime\\\", comment.getCreateTime());\\n        return document;\\n    }\\n\\n    /**\\n     * 将 Document 转换为 Comment 对象\\n     *\\n     * @param document Document\\n     * @return 评论对象\\n     */\\n    private Comment convertDocumentToComment(Document document) {\\n        Comment comment = new Comment();\\n        comment.setArticleId(document.getInteger(\\\"articleId\\\"));\\n        comment.setContent(document.getString(\\\"content\\\"));\\n        comment.setAuthor(document.getString(\\\"author\\\"));\\n        comment.setCreateTime(document.getDate(\\\"createTime\\\"));\\n        return comment;\\n    }\\n\\n    /**\\n     * 获取文章及其评论\\n     *\\n     * @param articleId 文章ID\\n     * @return 文章对象及其评论列表\\n     */\\n    @RequestMapping(path = \\\"/getArticleWithComments\\\")\\n    public Article getArticleWithComments(int articleId) {\\n        Article article = articleService.findArticleById(articleId);\\n        List<Comment> comments = getCommentsByArticleId(articleId);\\n        article.setComments(comments);\\n        return article;\\n    }\\n}\\n\\\"\\n}\"\n" +
                "}";

        System.out.println(str);

        String s = JsonParser.parseString(str).getAsJsonObject().get("output").getAsString();
        System.out.println(s);
    }


    @Test
    public void test1() {
        String str = "{\n\"output\": \"package run.mone.demo.controller;\n\nimport com.xiaomi.youpin.docean.anno.Controller;\nimport com.xiaomi.youpin.docean.anno.RequestMapping;\nimport org.bson.Document;\nimport run.mone.demo.bo.Article;\nimport run.mone.demo.bo.Comment;\nimport run.mone.demo.service.ArticleService;\nimport run.mone.demo.service.CommentService;\n\nimport javax.annotation.Resource;\nimport java.util.List;\nimport java.util.stream.Collectors;\n\n/**\n * 评论控制器\n *\n * @author ChatGPT\n */\n@Controller\n@RequestMapping(path = \"/comment\")\npublic class CommentController extends MongodbController {\n\n    @Resource\n    private CommentService commentService;\n\n    @Resource\n    private ArticleService articleService;\n\n    public CommentController() {\n        super(\"comments\");\n    }\n\n    /**\n     * 根据文章ID获取所有评论\n     *\n     * @param articleId 文章ID\n     * @return 评论列表\n     */\n    @RequestMapping(path = \"/getByArticleId\")\n    public List<Comment> getCommentsByArticleId(int articleId) {\n        return commentService.getCommentsByArticleId(articleId);\n    }\n\n    /**\n     * 添加新评论\n     *\n     * @param comment 评论对象\n     * @return 是否添加成功\n     */\n    @RequestMapping(path = \"/add\")\n    public boolean addComment(Comment comment) {\n        return commentService.addComment(comment);\n    }\n\n    /**\n     * 更新评论\n     *\n     * @param comment 更新后的评论对象\n     * @return 是否更新成功\n     */\n    @RequestMapping(path = \"/update\")\n    public boolean updateComment(Comment comment) {\n        return commentService.updateComment(comment);\n    }\n\n    /**\n     * 删除评论\n     *\n     * @param commentId 评论ID\n     * @return 是否删除成功\n     */\n    @RequestMapping(path = \"/delete\")\n    public boolean deleteComment(int commentId) {\n        return commentService.deleteComment(commentId);\n    }\n\n    /**\n     * 将 Comment 对象转换为 Document\n     *\n     * @param comment 评论对象\n     * @return Document\n     */\n    private Document convertCommentToDocument(Comment comment) {\n        Document document = new Document();\n        document.append(\"articleId\", comment.getArticleId());\n        document.append(\"content\", comment.getContent());\n        document.append(\"author\", comment.getAuthor());\n        document.append(\"createTime\", comment.getCreateTime());\n        return document;\n    }\n\n    /**\n     * 将 Document 转换为 Comment 对象\n     *\n     * @param document Document\n     * @return 评论对象\n     */\n    private Comment convertDocumentToComment(Document document) {\n        Comment comment = new Comment();\n        comment.setArticleId(document.getInteger(\"articleId\"));\n        comment.setContent(document.getString(\"content\"));\n        comment.setAuthor(document.getString(\"author\"));\n        comment.setCreateTime(document.getDate(\"createTime\"));\n        return comment;\n    }\n\n    /**\n     * 获取文章及其评论\n     *\n     * @param articleId 文章ID\n     * @return 文章对象及其评论列表\n     */\n    @RequestMapping(path = \"/getArticleWithComments\")\n    public Article getArticleWithComments(int articleId) {\n        Article article = articleService.findArticleById(articleId);\n        List<Comment> comments = getCommentsByArticleId(articleId);\n        article.setComments(comments);\n        return article;\n    }\n}\n\"\n}";
        System.out.println(str);
        JsonElement v = JsonParser.parseString(str);
        System.out.println(v);
    }


    @Test
    public void testObjectToJsonObject() {
        // 创建一个测试对象
        TestObject testObj = new TestObject();
        testObj.setName("TestName");
        testObj.setValue(123);

        // 调用待测试的方法
        JsonObject jsonObject = GsonUtils.objectToJsonObject(testObj);

        // 验证结果是否符合预期
        assertNotNull("The result should not be null", jsonObject);
        assertTrue("The result should be a JsonObject", jsonObject.isJsonObject());
        assertEquals("TestName", jsonObject.get("name").getAsString());
        assertEquals(123, jsonObject.get("value").getAsInt());


        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);
        System.out.println(GsonUtils.objectToJsonObject(list));

    }

    // 测试用的内部类
    private static class TestObject {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

