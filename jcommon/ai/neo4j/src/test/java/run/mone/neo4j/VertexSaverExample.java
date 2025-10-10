package run.mone.neo4j;

import java.util.*;

/**
 * VertexSaver使用示例
 */
public class VertexSaverExample {
    
    public static void main(String[] args) {
        // 检查是否启用演示模式
        boolean demoMode = args.length > 0 && "demo".equals(args[0]);
        
        if (demoMode) {
            System.out.println("=== VertexSaver演示模式 ===");
            System.out.println("此模式演示如何使用VertexSaver，但不会连接到实际的Neo4j数据库");
            runDemoMode();
        } else {
            System.out.println("=== VertexSaver实际运行模式 ===");
            System.out.println("此模式需要连接到运行中的Neo4j数据库");
            System.out.println("如果没有Neo4j数据库，请使用: java VertexSaverExample demo");
            runRealMode();
        }
    }
    
    /**
     * 演示模式 - 不需要实际的Neo4j连接
     */
    private static void runDemoMode() {
        System.out.println("\n1. 创建VertexSaver实例:");
        System.out.println("   VertexSaver vertexSaver = new VertexSaver();");
        
        System.out.println("\n2. 设置连接配置:");
        System.out.println("   vertexSaver.setPassword(\"your_password\");");
        System.out.println("   vertexSaver.setNeo4jUri(\"bolt://localhost:7687\");");
        
        System.out.println("\n3. 准备示例数据:");
        List<Map<String, Object>> vertices = createSampleData();
        for (int i = 0; i < vertices.size(); i++) {
            System.out.println("   顶点" + (i + 1) + ": " + vertices.get(i));
        }
        
        System.out.println("\n4. 保存顶点操作:");
        System.out.println("   vertexSaver.saveVertices(vertices, \"Person\");");
        System.out.println("   → 将执行Cypher: MERGE (v:Person {name: $name}) ON CREATE SET ... ON MATCH SET ...");
        
        System.out.println("\n5. 查询操作:");
        System.out.println("   vertexSaver.getVerticesByLabel(\"Person\");");
        System.out.println("   → 将执行Cypher: MATCH (v:Person) RETURN v");
        
        System.out.println("\n6. 按名称查询:");
        System.out.println("   vertexSaver.getVertexByName(\"张三\", \"Person\");");
        System.out.println("   → 将执行Cypher: MATCH (v:Person {name: $name}) RETURN v");
        
        System.out.println("\n7. 更新操作:");
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", "张三");
        updatedData.put("age", 31);
        updatedData.put("job", "高级工程师");
        System.out.println("   更新数据: " + updatedData);
        System.out.println("   → MERGE操作会自动处理插入或更新");
        
        System.out.println("\n8. 清理操作:");
        System.out.println("   vertexSaver.deleteVerticesByLabel(\"Person\");");
        System.out.println("   → 将执行Cypher: MATCH (v:Person) DETACH DELETE v");
        
        System.out.println("\n演示完成！要连接真实数据库，请运行: java VertexSaverExample");
    }
    
    /**
     * 实际运行模式 - 需要Neo4j连接
     */
    private static void runRealMode() {
        // 创建VertexSaver实例
        VertexSaver vertexSaver = new VertexSaver();
        
        // 如果需要，可以设置自定义配置
        // vertexSaver.setPassword("your_password");
        // vertexSaver.setNeo4jUri("bolt://your_host:7687");
        
        try {
            // 先测试连接
            System.out.println("测试Neo4j连接...");
            testConnection(vertexSaver);
            
            // 准备要保存的顶点数据
            List<Map<String, Object>> vertices = createSampleData();
            
            // 保存顶点到Neo4j，使用自定义标签
            System.out.println("保存顶点数据到Neo4j...");
            vertexSaver.saveVertices(vertices, "Person");
            
            // 查询保存的数据
            System.out.println("\n查询所有Person顶点:");
            List<Map<String, Object>> savedVertices = vertexSaver.getVerticesByLabel("Person");
            for (Map<String, Object> vertex : savedVertices) {
                System.out.println("  " + vertex);
            }
            
            // 根据名称查询特定顶点
            System.out.println("\n查询名为'张三'的顶点:");
            Map<String, Object> zhangsan = vertexSaver.getVertexByName("张三", "Person");
            if (zhangsan != null) {
                System.out.println("  " + zhangsan);
            }
            
            // 更新数据示例
            System.out.println("\n更新张三的年龄...");
            Map<String, Object> updatedZhangsan = new HashMap<>();
            updatedZhangsan.put("name", "张三");
            updatedZhangsan.put("age", 31);
            updatedZhangsan.put("job", "高级工程师");
            updatedZhangsan.put("city", "北京");
            updatedZhangsan.put("experience", "5年");
            
            vertexSaver.saveVertices(Arrays.asList(updatedZhangsan), "Person");
            
            // 验证更新结果
            Map<String, Object> updatedResult = vertexSaver.getVertexByName("张三", "Person");
            System.out.println("更新后的张三: " + updatedResult);
            
            // 清理数据（可选）
            System.out.println("\n清理测试数据...");
            vertexSaver.deleteVerticesByLabel("Person");
            System.out.println("数据清理完成");
            
        } catch (Exception e) {
            System.err.println("执行过程中发生错误: " + e.getMessage());
            System.err.println("\n可能的解决方案:");
            System.err.println("1. 确保Neo4j数据库正在运行");
            System.err.println("2. 检查连接配置 (URI: bolt://localhost:7687)");
            System.err.println("3. 设置正确的用户名和密码");
            System.err.println("4. 设置环境变量 NEO4J_PASSWORD");
            System.err.println("5. 或者运行演示模式: java VertexSaverExample demo");
            
            // 只在调试时打印完整堆栈跟踪
            if (Boolean.getBoolean("debug")) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 测试Neo4j连接
     */
    private static void testConnection(VertexSaver vertexSaver) {
        try {
            // 尝试获取会话来测试连接
            try (var session = vertexSaver.getSession()) {
                session.run("RETURN 1 as test");
                System.out.println("✓ Neo4j连接成功");
            }
        } catch (Exception e) {
            System.err.println("✗ Neo4j连接失败");
            throw new RuntimeException("无法连接到Neo4j数据库", e);
        }
    }
    
    /**
     * 创建示例数据
     */
    private static List<Map<String, Object>> createSampleData() {
        List<Map<String, Object>> vertices = new ArrayList<>();
        
        // 员工1
        Map<String, Object> person1 = new HashMap<>();
        person1.put("name", "张三");
        person1.put("age", 28);
        person1.put("job", "软件工程师");
        person1.put("city", "北京");
        vertices.add(person1);
        
        // 员工2
        Map<String, Object> person2 = new HashMap<>();
        person2.put("name", "李四");
        person2.put("age", 32);
        person2.put("job", "产品经理");
        person2.put("city", "上海");
        person2.put("department", "产品部");
        vertices.add(person2);
        
        // 员工3
        Map<String, Object> person3 = new HashMap<>();
        person3.put("name", "王五");
        person3.put("age", 26);
        person3.put("job", "UI设计师");
        person3.put("city", "深圳");
        person3.put("skills", Arrays.asList("Photoshop", "Sketch", "Figma"));
        vertices.add(person3);
        
        // 员工4
        Map<String, Object> person4 = new HashMap<>();
        person4.put("name", "赵六");
        person4.put("age", 35);
        person4.put("job", "技术总监");
        person4.put("city", "杭州");
        person4.put("team_size", 15);
        vertices.add(person4);
        
        return vertices;
    }
}
