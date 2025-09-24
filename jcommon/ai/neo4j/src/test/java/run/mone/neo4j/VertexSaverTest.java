package run.mone.neo4j;

import org.junit.*;
import org.neo4j.driver.Session;

import java.util.*;

import static org.junit.Assert.*;

/**
 * VertexSaver单元测试
 * 注意：此测试需要运行中的Neo4j实例
 */
public class VertexSaverTest {

    private VertexSaver vertexSaver;
    private static final String TEST_LABEL = "TestVertex";
    
    @Before
    public void setUp() {
        vertexSaver = new VertexSaver();
        // 如果环境变量中没有密码，可以在这里设置测试密码
        // vertexSaver.setPassword("your_test_password");
        
        // 清理测试数据
        try {
            vertexSaver.deleteVerticesByLabel(TEST_LABEL);
        } catch (Exception e) {
            // 忽略清理时的错误
        }
    }
    
    @After
    public void tearDown() {
        // 清理测试数据
        try {
            vertexSaver.deleteVerticesByLabel(TEST_LABEL);
        } catch (Exception e) {
            // 忽略清理时的错误
        }
    }

    @Test
    public void testConstructorWithEnvironmentVariable() {
        VertexSaver saver = new VertexSaver();
        assertNotNull(saver);
        // 密码应该从环境变量读取，如果没有设置则为空字符串
        assertNotNull(saver.getPassword());
    }

    @Test
    public void testSetPassword() {
        String testPassword = "test123";
        VertexSaver saver = new VertexSaver().setPassword(testPassword);
        assertEquals(testPassword, saver.getPassword());
    }

    @Test
    public void testSetNeo4jUri() {
        String testUri = "bolt://test:7687";
        VertexSaver saver = new VertexSaver().setNeo4jUri(testUri);
        assertEquals(testUri, saver.getNEO4J_URI());
    }

    @Test
    public void testSetNeo4jUser() {
        String testUser = "testuser";
        VertexSaver saver = new VertexSaver().setNeo4jUser(testUser);
        assertEquals(testUser, saver.getNEO4J_USER());
    }

    @Test
    public void testGetSession() {
        try (Session session = vertexSaver.getSession()) {
            assertNotNull(session);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSaveVerticesWithValidData() {
        // 准备测试数据
        List<Map<String, Object>> vertices = createTestVertices();
        
        // 保存顶点
        try {
            vertexSaver.saveVertices(vertices, TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 验证保存结果
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(3, savedVertices.size());
        
        // 验证具体数据
        Map<String, Object> vertex1 = vertexSaver.getVertexByName("TestPerson1", TEST_LABEL);
        assertNotNull(vertex1);
        assertEquals("TestPerson1", vertex1.get("name"));
        assertEquals(25L, vertex1.get("age")); // Neo4j返回Long类型
        assertEquals("Engineer", vertex1.get("job"));
    }

    @Test
    public void testSaveVerticesWithDefaultLabel() {
        List<Map<String, Object>> vertices = createTestVertices();
        
        try {
            vertexSaver.saveVertices(vertices);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 验证使用默认标签保存的数据
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel("Vertex");
        assertEquals(3, savedVertices.size());
        
        // 清理默认标签的数据
        vertexSaver.deleteVerticesByLabel("Vertex");
    }

    @Test
    public void testSaveVerticesWithNullOrEmptyList() {
        // 测试null列表
        try {
            vertexSaver.saveVertices(null, TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 测试空列表
        try {
            vertexSaver.saveVertices(new ArrayList<>(), TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 验证没有数据被保存
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(0, savedVertices.size());
    }

    @Test
    public void testSaveVerticesWithInvalidData() {
        List<Map<String, Object>> vertices = new ArrayList<>();
        
        // 添加null顶点
        vertices.add(null);
        
        // 添加没有name字段的顶点
        Map<String, Object> vertexWithoutName = new HashMap<>();
        vertexWithoutName.put("age", 30);
        vertices.add(vertexWithoutName);
        
        // 添加name为空的顶点
        Map<String, Object> vertexWithEmptyName = new HashMap<>();
        vertexWithEmptyName.put("name", "");
        vertexWithEmptyName.put("age", 35);
        vertices.add(vertexWithEmptyName);
        
        // 添加有效顶点
        Map<String, Object> validVertex = new HashMap<>();
        validVertex.put("name", "ValidPerson");
        validVertex.put("age", 40);
        vertices.add(validVertex);
        
        // 应该只保存有效的顶点
        try {
            vertexSaver.saveVertices(vertices, TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(1, savedVertices.size());
        assertEquals("ValidPerson", savedVertices.get(0).get("name"));
    }

    @Test
    public void testGetVertexByName() {
        // 先保存一些测试数据
        List<Map<String, Object>> vertices = createTestVertices();
        vertexSaver.saveVertices(vertices, TEST_LABEL);
        
        // 测试查询存在的顶点
        Map<String, Object> vertex = vertexSaver.getVertexByName("TestPerson2", TEST_LABEL);
        assertNotNull(vertex);
        assertEquals("TestPerson2", vertex.get("name"));
        assertEquals(30L, vertex.get("age"));
        
        // 测试查询不存在的顶点
        Map<String, Object> nonExistentVertex = vertexSaver.getVertexByName("NonExistent", TEST_LABEL);
        assertNull(nonExistentVertex);
        
        // 测试null参数
        Map<String, Object> nullNameVertex = vertexSaver.getVertexByName(null, TEST_LABEL);
        assertNull(nullNameVertex);
    }

    @Test
    public void testGetVerticesByLabel() {
        // 先保存一些测试数据
        List<Map<String, Object>> vertices = createTestVertices();
        vertexSaver.saveVertices(vertices, TEST_LABEL);
        
        // 测试查询存在的标签
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(3, savedVertices.size());
        
        // 测试查询不存在的标签
        List<Map<String, Object>> nonExistentVertices = vertexSaver.getVerticesByLabel("NonExistentLabel");
        assertEquals(0, nonExistentVertices.size());
        
        // 测试null标签（应该使用默认标签）
        List<Map<String, Object>> defaultLabelVertices = vertexSaver.getVerticesByLabel(null);
        assertNotNull(defaultLabelVertices);
    }

    @Test
    public void testDeleteVerticesByLabel() {
        // 先保存一些测试数据
        List<Map<String, Object>> vertices = createTestVertices();
        vertexSaver.saveVertices(vertices, TEST_LABEL);
        
        // 验证数据已保存
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(3, savedVertices.size());
        
        // 删除指定标签的顶点
        try {
            vertexSaver.deleteVerticesByLabel(TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 验证数据已删除
        List<Map<String, Object>> deletedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(0, deletedVertices.size());
    }

    @Test
    public void testUpdateExistingVertex() {
        // 保存初始数据
        Map<String, Object> originalVertex = new HashMap<>();
        originalVertex.put("name", "UpdateTest");
        originalVertex.put("age", 25);
        originalVertex.put("status", "active");
        
        List<Map<String, Object>> vertices = Arrays.asList(originalVertex);
        vertexSaver.saveVertices(vertices, TEST_LABEL);
        
        // 更新数据
        Map<String, Object> updatedVertex = new HashMap<>();
        updatedVertex.put("name", "UpdateTest");
        updatedVertex.put("age", 30);
        updatedVertex.put("status", "inactive");
        updatedVertex.put("newField", "newValue");
        
        List<Map<String, Object>> updatedVertices = Arrays.asList(updatedVertex);
        vertexSaver.saveVertices(updatedVertices, TEST_LABEL);
        
        // 验证更新结果
        Map<String, Object> result = vertexSaver.getVertexByName("UpdateTest", TEST_LABEL);
        assertNotNull(result);
        assertEquals("UpdateTest", result.get("name"));
        assertEquals(30L, result.get("age"));
        assertEquals("inactive", result.get("status"));
        assertEquals("newValue", result.get("newField"));
        
        // 验证只有一个顶点（更新而不是创建新的）
        List<Map<String, Object>> allVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(1, allVertices.size());
    }

    @Test
    public void testLargeDataSet() {
        // 创建大量测试数据
        List<Map<String, Object>> largeVertices = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Map<String, Object> vertex = new HashMap<>();
            vertex.put("name", "LargeTest" + i);
            vertex.put("index", i);
            vertex.put("category", i % 10);
            largeVertices.add(vertex);
        }
        
        // 保存大量数据
        try {
            vertexSaver.saveVertices(largeVertices, TEST_LABEL);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
        
        // 验证保存结果
        List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel(TEST_LABEL);
        assertEquals(500, savedVertices.size());
        
        // 验证特定数据
        Map<String, Object> testVertex = vertexSaver.getVertexByName("LargeTest100", TEST_LABEL);
        assertNotNull(testVertex);
        assertEquals(100L, testVertex.get("index"));
        assertEquals(0L, testVertex.get("category")); // 100 % 10 = 0
    }

    /**
     * 创建测试用的顶点数据
     */
    private List<Map<String, Object>> createTestVertices() {
        List<Map<String, Object>> vertices = new ArrayList<>();
        
        Map<String, Object> vertex1 = new HashMap<>();
        vertex1.put("name", "TestPerson1");
        vertex1.put("age", 25);
        vertex1.put("job", "Engineer");
        vertices.add(vertex1);
        
        Map<String, Object> vertex2 = new HashMap<>();
        vertex2.put("name", "TestPerson2");
        vertex2.put("age", 30);
        vertex2.put("job", "Designer");
        vertex2.put("city", "Beijing");
        vertices.add(vertex2);
        
        Map<String, Object> vertex3 = new HashMap<>();
        vertex3.put("name", "TestPerson3");
        vertex3.put("age", 35);
        vertex3.put("department", "Marketing");
        vertices.add(vertex3);
        
        return vertices;
    }
}
