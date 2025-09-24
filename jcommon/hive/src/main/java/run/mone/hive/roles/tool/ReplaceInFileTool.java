package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件内容替换工具，使用SEARCH/REPLACE块格式进行精确的文件内容替换
 * 
 * 支持的格式：
 * ------- SEARCH
 * [要查找的确切内容]
 * =======
 * [要替换为的新内容]
 * +++++++ REPLACE
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ReplaceInFileTool implements ITool {

    public static final String name = "replace_in_file";

    // SEARCH/REPLACE块的标记符
    private static final String SEARCH_BLOCK_START = "------- SEARCH";
    private static final String SEARCH_BLOCK_END = "=======";
    private static final String REPLACE_BLOCK_END = "+++++++ REPLACE";

    // 正则表达式模式，支持更灵活的格式
    private static final Pattern SEARCH_BLOCK_START_PATTERN = Pattern.compile("^[-]{3,}\\s+SEARCH>?$");
    private static final Pattern LEGACY_SEARCH_BLOCK_START_PATTERN = Pattern.compile("^[<]{3,}\\s+SEARCH>?$");
    private static final Pattern SEARCH_BLOCK_END_PATTERN = Pattern.compile("^[=]{3,}$");
    private static final Pattern REPLACE_BLOCK_END_PATTERN = Pattern.compile("^[+]{3,}\\s+REPLACE>?$");
    private static final Pattern LEGACY_REPLACE_BLOCK_END_PATTERN = Pattern.compile("^[>]{3,}\\s+REPLACE>?$");

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                用于对现有文件进行精确的部分内容替换的工具。使用SEARCH/REPLACE块格式来定义对文件特定部分的精确更改。
                当需要对文件的特定部分进行有针对性的更改时，应该使用此工具。
                
                **使用时机：**
                - 需要修改文件中的特定代码段或文本段
                - 需要进行精确的、有针对性的文件编辑
                - 需要替换文件中的特定函数、类或配置项
                - 需要在现有代码中添加、修改或删除特定行
                
                **重要规则：**
                1. SEARCH内容必须与文件中的内容完全匹配（包括空格、缩进、换行符）
                2. 每个SEARCH/REPLACE块只会替换第一个匹配的内容
                3. 如需多处替换，请使用多个SEARCH/REPLACE块
                4. SEARCH块应该包含足够的上下文以确保唯一匹配
                5. 要删除代码，请使用空的REPLACE部分
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (必需) 要修改的文件路径（相对于当前工作目录）
                - diff: (必需) 一个或多个遵循以下精确格式的SEARCH/REPLACE块：
                  ```
                  ------- SEARCH
                  [要查找的确切内容]
                  =======
                  [要替换为的新内容]
                  +++++++ REPLACE
                  ```
                  
                  关键规则：
                  1. SEARCH内容必须与关联的文件部分完全匹配：
                     * 逐字符匹配，包括空格、缩进、行结束符
                     * 包含所有注释、文档字符串等
                  2. SEARCH/REPLACE块只会替换第一个匹配的出现位置
                     * 如需进行多个更改，请包含多个唯一的SEARCH/REPLACE块
                     * 在每个SEARCH部分中只包含足够的行来唯一匹配需要更改的行集合
                     * 使用多个SEARCH/REPLACE块时，按它们在文件中出现的顺序列出
                  3. 保持SEARCH/REPLACE块简洁：
                     * 将大的SEARCH/REPLACE块分解为一系列较小的块，每个块更改文件的一小部分
                     * 只包含变化的行，如果需要唯一性，可以包含几行周围的行
                     * 不要在SEARCH/REPLACE块中包含长串不变的行
                     * 每行必须完整，不要中途截断行，因为这可能导致匹配失败
                  4. 特殊操作：
                     * 移动代码：使用两个SEARCH/REPLACE块（一个从原位置删除 + 一个在新位置插入）
                     * 删除代码：使用空的REPLACE部分
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
            <task_progress>
            任务进度清单（可选）
            </task_progress>
            """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
            <replace_in_file>
            <path>文件路径</path>
            <diff>
            搜索和替换块
            </diff>
            %s
            </replace_in_file>
            """.formatted(taskProgress);
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 检查必要参数
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("replace_in_file操作缺少必需的path参数");
                result.addProperty("error", "缺少必需参数'path'");
                return result;
            }

            if (!inputJson.has("diff") || StringUtils.isBlank(inputJson.get("diff").getAsString())) {
                log.error("replace_in_file操作缺少必需的diff参数");
                result.addProperty("error", "缺少必需参数'diff'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            String diff = inputJson.get("diff").getAsString();

            return performReplaceInFile(path, diff);

        } catch (Exception e) {
            log.error("执行replace_in_file操作时发生异常", e);
            result.addProperty("error", "执行replace_in_file操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 执行文件内容替换操作
     */
    private JsonObject performReplaceInFile(String path, String diff) {
        JsonObject result = new JsonObject();

        try {
            File file = new File(path);

            // 检查文件是否存在
            if (!file.exists()) {
                log.error("要修改的文件不存在：{}", path);
                result.addProperty("error", "文件不存在: " + path);
                return result;
            }

            if (file.isDirectory()) {
                log.error("路径是一个目录，无法修改：{}", path);
                result.addProperty("error", "路径是一个目录，无法修改: " + path);
                return result;
            }

            // 读取原始文件内容
            String originalContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            
            // 解析并应用SEARCH/REPLACE块
            String newContent = applySearchReplaceBlocks(originalContent, diff);
            
            // 写入新内容
            Files.writeString(file.toPath(), newContent, StandardCharsets.UTF_8);
            
            log.info("成功应用替换到文件：{}", path);
            result.addProperty("result", "文件内容已成功更新: " + path);

        } catch (IOException e) {
            log.error("读写文件时发生异常：{}", path, e);
            result.addProperty("error", "文件操作失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("应用替换时发生异常：{}", path, e);
            result.addProperty("error", "内容替换失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 应用SEARCH/REPLACE块到原始内容
     */
    private String applySearchReplaceBlocks(String originalContent, String diff) throws Exception {
        List<SearchReplaceBlock> blocks = parseSearchReplaceBlocks(diff);
        
        if (blocks.isEmpty()) {
            throw new Exception("未找到有效的SEARCH/REPLACE块");
        }
        
        String result = originalContent;
        int lastProcessedIndex = 0;
        
        for (SearchReplaceBlock block : blocks) {
            // 查找匹配位置
            int matchIndex = findMatch(result, block.searchContent, lastProcessedIndex);
            
            if (matchIndex == -1) {
                throw new Exception("SEARCH块未找到匹配内容:\n" + block.searchContent.trim());
            }
            
            // 执行替换
            String before = result.substring(0, matchIndex);
            String after = result.substring(matchIndex + block.searchContent.length());
            result = before + block.replaceContent + after;
            
            // 更新处理位置
            lastProcessedIndex = matchIndex + block.replaceContent.length();
            
            log.debug("成功应用SEARCH/REPLACE块，匹配位置: {}", matchIndex);
        }
        
        return result;
    }

    /**
     * 解析SEARCH/REPLACE块
     */
    private List<SearchReplaceBlock> parseSearchReplaceBlocks(String diff) throws Exception {
        List<SearchReplaceBlock> blocks = new ArrayList<>();
        String[] lines = diff.split("\n");
        
        StringBuilder currentSearchContent = new StringBuilder();
        StringBuilder currentReplaceContent = new StringBuilder();
        boolean inSearch = false;
        boolean inReplace = false;
        
        for (String line : lines) {
            if (isSearchBlockStart(line)) {
                // 开始新的SEARCH块
                inSearch = true;
                inReplace = false;
                currentSearchContent = new StringBuilder();
                currentReplaceContent = new StringBuilder();
                continue;
            }
            
            if (isSearchBlockEnd(line)) {
                // 结束SEARCH，开始REPLACE
                if (!inSearch) {
                    throw new Exception("发现SEARCH结束标记，但未在SEARCH状态中");
                }
                inSearch = false;
                inReplace = true;
                continue;
            }
            
            if (isReplaceBlockEnd(line)) {
                // 结束REPLACE块
                if (!inReplace) {
                    throw new Exception("发现REPLACE结束标记，但未在REPLACE状态中");
                }
                
                // 添加完整的块
                blocks.add(new SearchReplaceBlock(
                    currentSearchContent.toString(),
                    currentReplaceContent.toString()
                ));
                
                inSearch = false;
                inReplace = false;
                continue;
            }
            
            // 累积内容
            if (inSearch) {
                if (currentSearchContent.length() > 0) {
                    currentSearchContent.append("\n");
                }
                currentSearchContent.append(line);
            } else if (inReplace) {
                if (currentReplaceContent.length() > 0) {
                    currentReplaceContent.append("\n");
                }
                currentReplaceContent.append(line);
            }
        }
        
        // 检查是否有未完成的块
        if (inSearch || inReplace) {
            throw new Exception("SEARCH/REPLACE块格式不完整");
        }
        
        return blocks;
    }

    /**
     * 查找匹配内容的位置
     * 支持多种匹配策略：精确匹配、行级匹配、锚点匹配
     */
    private int findMatch(String content, String searchContent, int startIndex) {
        // 1. 精确匹配
        int exactMatch = content.indexOf(searchContent, startIndex);
        if (exactMatch != -1) {
            return exactMatch;
        }
        
        // 2. 行级匹配（忽略行首行尾空格）
        int lineMatch = findLineTrimmedMatch(content, searchContent, startIndex);
        if (lineMatch != -1) {
            return lineMatch;
        }
        
        // 3. 锚点匹配（对于3行以上的块，使用首尾行作为锚点）
        int anchorMatch = findBlockAnchorMatch(content, searchContent, startIndex);
        if (anchorMatch != -1) {
            return anchorMatch;
        }
        
        return -1;
    }

    /**
     * 行级匹配（忽略行首行尾空格）
     */
    private int findLineTrimmedMatch(String content, String searchContent, int startIndex) {
        String[] contentLines = content.split("\n");
        String[] searchLines = searchContent.split("\n");
        
        // 移除搜索内容末尾的空行
        if (searchLines.length > 0 && searchLines[searchLines.length - 1].isEmpty()) {
            String[] newSearchLines = new String[searchLines.length - 1];
            System.arraycopy(searchLines, 0, newSearchLines, 0, searchLines.length - 1);
            searchLines = newSearchLines;
        }
        
        // 找到起始行号
        int startLineNum = 0;
        int currentIndex = 0;
        while (currentIndex < startIndex && startLineNum < contentLines.length) {
            currentIndex += contentLines[startLineNum].length() + 1; // +1 for \n
            startLineNum++;
        }
        
        // 在每个可能的起始位置尝试匹配
        for (int i = startLineNum; i <= contentLines.length - searchLines.length; i++) {
            boolean matches = true;
            
            // 尝试匹配所有搜索行
            for (int j = 0; j < searchLines.length; j++) {
                String contentLineTrimmed = contentLines[i + j].trim();
                String searchLineTrimmed = searchLines[j].trim();
                
                if (!contentLineTrimmed.equals(searchLineTrimmed)) {
                    matches = false;
                    break;
                }
            }
            
            // 如果找到匹配，计算确切的字符位置
            if (matches) {
                int matchStartIndex = 0;
                for (int k = 0; k < i; k++) {
                    matchStartIndex += contentLines[k].length() + 1; // +1 for \n
                }
                return matchStartIndex;
            }
        }
        
        return -1;
    }

    /**
     * 锚点匹配（使用首尾行作为锚点）
     */
    private int findBlockAnchorMatch(String content, String searchContent, int startIndex) {
        String[] contentLines = content.split("\n");
        String[] searchLines = searchContent.split("\n");
        
        // 只对3行以上的块使用此方法
        if (searchLines.length < 3) {
            return -1;
        }
        
        // 移除搜索内容末尾的空行
        if (searchLines.length > 0 && searchLines[searchLines.length - 1].isEmpty()) {
            String[] newSearchLines = new String[searchLines.length - 1];
            System.arraycopy(searchLines, 0, newSearchLines, 0, searchLines.length - 1);
            searchLines = newSearchLines;
        }
        
        String firstLineSearch = searchLines[0].trim();
        String lastLineSearch = searchLines[searchLines.length - 1].trim();
        int searchBlockSize = searchLines.length;
        
        // 找到起始行号
        int startLineNum = 0;
        int currentIndex = 0;
        while (currentIndex < startIndex && startLineNum < contentLines.length) {
            currentIndex += contentLines[startLineNum].length() + 1;
            startLineNum++;
        }
        
        // 寻找匹配的首尾锚点
        for (int i = startLineNum; i <= contentLines.length - searchBlockSize; i++) {
            // 检查首行是否匹配
            if (!contentLines[i].trim().equals(firstLineSearch)) {
                continue;
            }
            
            // 检查尾行是否在预期位置匹配
            if (!contentLines[i + searchBlockSize - 1].trim().equals(lastLineSearch)) {
                continue;
            }
            
            // 计算确切的字符位置
            int matchStartIndex = 0;
            for (int k = 0; k < i; k++) {
                matchStartIndex += contentLines[k].length() + 1;
            }
            
            return matchStartIndex;
        }
        
        return -1;
    }

    /**
     * 检查是否为SEARCH块开始标记
     */
    private boolean isSearchBlockStart(String line) {
        return SEARCH_BLOCK_START_PATTERN.matcher(line).matches() || 
               LEGACY_SEARCH_BLOCK_START_PATTERN.matcher(line).matches();
    }

    /**
     * 检查是否为SEARCH块结束标记
     */
    private boolean isSearchBlockEnd(String line) {
        return SEARCH_BLOCK_END_PATTERN.matcher(line).matches();
    }

    /**
     * 检查是否为REPLACE块结束标记
     */
    private boolean isReplaceBlockEnd(String line) {
        return REPLACE_BLOCK_END_PATTERN.matcher(line).matches() || 
               LEGACY_REPLACE_BLOCK_END_PATTERN.matcher(line).matches();
    }

    /**
     * SEARCH/REPLACE块数据结构
     */
    private static class SearchReplaceBlock {
        final String searchContent;
        final String replaceContent;

        SearchReplaceBlock(String searchContent, String replaceContent) {
            this.searchContent = searchContent;
            this.replaceContent = replaceContent;
        }
    }
}
