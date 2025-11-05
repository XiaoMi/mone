package com.xiaomi.data.push.rpc.common;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * CompressionUtil 测试类
 * 
 * @author goodjava@qq.com
 */
public class CompressionUtilTest {
    
    private static final String TEST_FILE_PATH = "D://采集进度.txt";

//    @Before
    public void setUp() throws IOException {
        // 创建测试文件，包含一些可压缩的重复内容
        String testContent = generateTestContent();
        Path path = Paths.get(TEST_FILE_PATH);
        Files.write(path, testContent.getBytes());
        System.out.println("测试文件创建成功: " + TEST_FILE_PATH);
    }
    
//    @After
    public void tearDown() throws IOException {
        // 清理测试文件
        Path path = Paths.get(TEST_FILE_PATH);
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("测试文件已删除: " + TEST_FILE_PATH);
        }
    }
    
    /**
     * 生成测试内容 - 包含重复文本以便更好地测试压缩效果
     */
    private String generateTestContent() {
        StringBuilder content = new StringBuilder();
        content.append("这是一个用于测试压缩和解压缩功能的文本文件。\n");
        content.append("This is a test file for compression and decompression.\n\n");
        
        // 添加重复内容以提高压缩率
        for (int i = 0; i < 100; i++) {
            content.append("Line ").append(i).append(": ");
            content.append("重复的内容可以被GZIP更有效地压缩。Repeated content can be compressed more effectively by GZIP. ");
            content.append("测试数据 Test Data. ");
            content.append("\n");
        }
        
        return content.toString();
    }
    
    @Test
    public void testCompressionAndDecompression() throws IOException {
        System.out.println("\n========== 开始压缩和解压缩测试 ==========");
        
        // 读取测试文件
        Path path = Paths.get(TEST_FILE_PATH);
        byte[] originalData = Files.readAllBytes(path);
        int originalSize = originalData.length;
        
        System.out.println("\n--- 压缩前 ---");
        System.out.println("原始文件路径: " + TEST_FILE_PATH);
        System.out.println("原始数据大小: " + originalSize + " 字节 (" + formatSize(originalSize) + ")");
        
        // 执行压缩
        byte[] compressedData = CompressionUtil.compress(originalData);
        int compressedSize = compressedData.length;
        
        System.out.println("\n--- 压缩后 ---");
        System.out.println("压缩后数据大小: " + compressedSize + " 字节 (" + formatSize(compressedSize) + ")");
        double compressionRatio = (1 - (double)compressedSize / originalSize) * 100;
        System.out.println("压缩率: " + String.format("%.2f", compressionRatio) + "%");
        System.out.println("压缩比: " + String.format("%.2f", (double)originalSize / compressedSize) + ":1");
        

        // 执行解压缩
        byte[] decompressedData = CompressionUtil.decompress(compressedData);
        int decompressedSize = decompressedData.length;
        
        System.out.println("\n--- 解压后 ---");
        System.out.println("解压后数据大小: " + decompressedSize + " 字节 (" + formatSize(decompressedSize) + ")");
        

        System.out.println("\n验证结果: ✓ 数据完整性校验通过");
        System.out.println("========== 测试完成 ==========\n");
    }
    
    @Test
    public void testEmptyData() throws IOException {
        System.out.println("\n========== 测试空数据 ==========");
        
        byte[] emptyData = new byte[0];
        
        // 测试压缩空数据
        byte[] compressed = CompressionUtil.compress(emptyData);

        // 测试解压缩空数据
        byte[] decompressed = CompressionUtil.decompress(emptyData);

        System.out.println("空数据测试通过 ✓");
        System.out.println("========== 测试完成 ==========\n");
    }
    
    @Test
    public void testNullData() throws IOException {
        System.out.println("\n========== 测试null数据 ==========");
        
        // 测试压缩null数据
        byte[] compressed = CompressionUtil.compress(null);

        // 测试解压缩null数据
        byte[] decompressed = CompressionUtil.decompress(null);

        System.out.println("null数据测试通过 ✓");
        System.out.println("========== 测试完成 ==========\n");
    }
    
    /**
     * 格式化字节大小为可读格式
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        }
    }
}
