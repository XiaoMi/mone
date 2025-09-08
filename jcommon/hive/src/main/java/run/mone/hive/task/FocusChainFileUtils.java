package run.mone.hive.task;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Focus Chain文件操作工具类
 * 对应Cline中的file-utils.ts
 */
public class FocusChainFileUtils {
    
    private static final String FOCUS_CHAIN_FILE_NAME = "focus-chain.md";
    private static final Pattern CHECKLIST_PATTERN = Pattern.compile("^\\s*-\\s*\\[[x\\s]\\].*$", Pattern.MULTILINE);
    
    /**
     * 获取Focus Chain文件路径
     */
    public static String getFocusChainFilePath(String taskDir, String taskId) {
        return Paths.get(taskDir, FOCUS_CHAIN_FILE_NAME).toString();
    }
    
    /**
     * 创建标准的markdown内容结构
     */
    public static String createFocusChainMarkdownContent(String taskId, String focusChainList) {
        return String.format(
            "# Focus Chain List for Task %s\n\n" +
            "<!-- Edit this markdown file to update your focus chain list -->\n" +
            "<!-- Use the format: - [ ] for incomplete items and - [x] for completed items -->\n\n" +
            "%s\n\n" +
            "<!-- Save this file and the focus chain list will be updated in the task -->",
            taskId, focusChainList
        );
    }
    
    /**
     * 从文本内容中提取Focus Chain项目
     * 返回匹配清单项目格式的行数组
     */
    public static List<String> extractFocusChainItemsFromText(String text) {
        List<String> items = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return items;
        }
        
        Matcher matcher = CHECKLIST_PATTERN.matcher(text);
        while (matcher.find()) {
            items.add(matcher.group().trim());
        }
        
        return items;
    }
    
    /**
     * 从文本中提取Focus Chain列表
     * 返回完整的清单字符串，如果没有找到则返回null
     */
    public static String extractFocusChainListFromText(String text) {
        List<String> items = extractFocusChainItemsFromText(text);
        return items.isEmpty() ? null : String.join("\n", items);
    }
    
    /**
     * 确保Focus Chain文件存在，如果不存在则使用提供的内容创建
     * 返回文件路径
     */
    public static String ensureFocusChainFile(String taskDir, String taskId, String initialFocusChainContent) 
            throws IOException {
        // 确保任务目录存在
        Path taskDirPath = Paths.get(taskDir);
        if (!Files.exists(taskDirPath)) {
            Files.createDirectories(taskDirPath);
        }
        
        String focusChainFilePath = getFocusChainFilePath(taskDir, taskId);
        Path filePath = Paths.get(focusChainFilePath);
        
        // 检查文件是否存在
        if (!Files.exists(filePath)) {
            // 创建文件
            String focusChainContent = (initialFocusChainContent != null && !initialFocusChainContent.trim().isEmpty()) 
                ? initialFocusChainContent 
                : "- [ ] Example checklist item\n- [ ] Another checklist item\n- [x] Completed example item";
            
            String fileContent = createFocusChainMarkdownContent(taskId, focusChainContent);
            Files.write(filePath, fileContent.getBytes("UTF-8"));
        }
        
        return focusChainFilePath;
    }
    
    /**
     * 读取Focus Chain文件内容
     */
    public static String readFocusChainFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            return null;
        }
        return new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
    }
    
    /**
     * 写入Focus Chain文件
     */
    public static void writeFocusChainFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes("UTF-8"));
    }
    
    /**
     * 解析Focus Chain列表的统计信息
     */
    public static FocusChainCounts parseFocusChainListCounts(String focusChainList) {
        if (focusChainList == null || focusChainList.trim().isEmpty()) {
            return new FocusChainCounts(0, 0);
        }
        
        List<String> items = extractFocusChainItemsFromText(focusChainList);
        int totalItems = items.size();
        int completedItems = 0;
        
        for (String item : items) {
            if (item.contains("[x]") || item.contains("[X]")) {
                completedItems++;
            }
        }
        
        return new FocusChainCounts(totalItems, completedItems);
    }
    
    /**
     * Focus Chain统计信息类
     */
    public static class FocusChainCounts {
        private final int totalItems;
        private final int completedItems;
        
        public FocusChainCounts(int totalItems, int completedItems) {
            this.totalItems = totalItems;
            this.completedItems = completedItems;
        }
        
        public int getTotalItems() {
            return totalItems;
        }
        
        public int getCompletedItems() {
            return completedItems;
        }
        
        public int getIncompleteItems() {
            return totalItems - completedItems;
        }
        
        @Override
        public String toString() {
            return String.format("FocusChainCounts{total=%d, completed=%d, incomplete=%d}", 
                totalItems, completedItems, getIncompleteItems());
        }
    }
}
