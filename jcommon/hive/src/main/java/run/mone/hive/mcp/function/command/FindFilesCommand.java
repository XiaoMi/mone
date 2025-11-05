package run.mone.hive.mcp.function.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 查找文件命令处理类
 * 支持模糊查询、递归查询和缓存
 * 
 * 命令格式：
 * /findfiles <path> [pattern] [--recursive]
 * 
 * 示例：
 * /findfiles /home/user/project *.java --recursive
 * /findfiles /home/user/project test
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class FindFilesCommand extends BaseCommand {

    /**
     * 缓存配置：最大1000个条目，30分钟过期
     */
    private static final Cache<String, List<String>> FILE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    /**
     * 最大返回文件数量限制，防止结果过大
     */
    private static final int MAX_FILES = 500;

    public FindFilesCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/findfiles");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 解析命令参数
            CommandParams params = parseCommand(message);
            
            if (params.path == null || params.path.isEmpty()) {
                return Flux.just(createErrorResult("路径参数不能为空。使用格式：/findfiles <path> [pattern] [--recursive]"));
            }

            // 生成缓存键
            String cacheKey = generateCacheKey(params);
            
            // 尝试从缓存获取
            List<String> cachedFiles = FILE_CACHE.getIfPresent(cacheKey);
            if (cachedFiles != null) {
                log.info("从缓存获取文件列表，路径：{}, 文件数：{}", params.path, cachedFiles.size());
                return Flux.just(createSuccessResult(formatFileList(cachedFiles, true)));
            }

            // 执行文件查找
            List<String> files = findFiles(params);
            
            // 存入缓存
            FILE_CACHE.put(cacheKey, files);
            
            String result = formatFileList(files, false);
            return Flux.just(createSuccessResult(result));
            
        } catch (Exception e) {
            log.error("查找文件失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("查找文件失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/findfiles";
    }

    @Override
    public String getCommandDescription() {
        return "查找指定文件夹下的文件列表，支持模糊查询和递归查询";
    }

    /**
     * 解析命令参数
     */
    private CommandParams parseCommand(String message) {
        String[] parts = message.trim().split("\\s+");
        CommandParams params = new CommandParams();
        
        if (parts.length < 2) {
            return params;
        }
        
        // 第一个参数是路径
        params.path = parts[1];
        
        // 解析其他参数
        for (int i = 2; i < parts.length; i++) {
            if ("--recursive".equalsIgnoreCase(parts[i]) || "-r".equalsIgnoreCase(parts[i])) {
                params.recursive = true;
            } else if (!parts[i].startsWith("--")) {
                params.pattern = parts[i];
            }
        }
        
        return params;
    }

    /**
     * 生成缓存键
     */
    private String generateCacheKey(CommandParams params) {
        return String.format("%s|%s|%s", 
                params.path, 
                params.pattern != null ? params.pattern : "", 
                params.recursive);
    }

    /**
     * 查找文件
     */
    private List<String> findFiles(CommandParams params) throws Exception {
        Path basePath = Paths.get(params.path);
        
        if (!Files.exists(basePath)) {
            throw new IllegalArgumentException("路径不存在: " + params.path);
        }
        
        if (!Files.isDirectory(basePath)) {
            throw new IllegalArgumentException("路径不是一个目录: " + params.path);
        }

        List<String> result = new ArrayList<>();
        Pattern regexPattern = createPattern(params.pattern);

        Safe.run(() -> {
            if (params.recursive) {
                // 递归查找
                try (Stream<Path> paths = Files.walk(basePath)) {
                    result.addAll(
                        paths.filter(Files::isRegularFile)
                             .filter(p -> matchesPattern(p, regexPattern))
                             .limit(MAX_FILES)
                             .map(Path::toString)
                             .collect(Collectors.toList())
                    );
                }
            } else {
                // 非递归查找
                try (Stream<Path> paths = Files.list(basePath)) {
                    result.addAll(
                        paths.filter(Files::isRegularFile)
                             .filter(p -> matchesPattern(p, regexPattern))
                             .limit(MAX_FILES)
                             .map(Path::toString)
                             .collect(Collectors.toList())
                    );
                }
            }
        });

        log.info("查找文件完成，路径：{}, 模式：{}, 递归：{}, 找到文件数：{}", 
                params.path, params.pattern, params.recursive, result.size());
        
        return result;
    }

    /**
     * 创建匹配模式
     * 支持通配符 * 和 ?
     */
    private Pattern createPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return null;
        }
        
        // 转换通配符为正则表达式
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".");
        
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查路径是否匹配模式
     */
    private boolean matchesPattern(Path path, Pattern pattern) {
        if (pattern == null) {
            return true;
        }
        
        String fileName = path.getFileName().toString();
        return pattern.matcher(fileName).matches();
    }

    /**
     * 格式化文件列表输出
     */
    private String formatFileList(List<String> files, boolean fromCache) {
        StringBuilder sb = new StringBuilder();
        
        if (fromCache) {
            sb.append("📋 [缓存] 找到 ").append(files.size()).append(" 个文件");
        } else {
            sb.append("📋 找到 ").append(files.size()).append(" 个文件");
        }
        
        if (files.size() >= MAX_FILES) {
            sb.append("（已达到最大限制 ").append(MAX_FILES).append("）");
        }
        
        sb.append(":\n\n");
        
        if (files.isEmpty()) {
            sb.append("未找到匹配的文件");
        } else {
            for (int i = 0; i < files.size(); i++) {
                sb.append(i + 1).append(". ").append(files.get(i)).append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * 命令参数类
     */
    private static class CommandParams {
        String path;
        String pattern;
        boolean recursive = false;
    }

    /**
     * 清除缓存（可选的辅助方法）
     */
    public static void clearCache() {
        FILE_CACHE.invalidateAll();
        log.info("文件查找缓存已清空");
    }

    /**
     * 获取缓存统计信息（可选的辅助方法）
     */
    public static String getCacheStats() {
        return String.format("缓存统计 - 大小: %d, 命中率: %.2f%%", 
                FILE_CACHE.size(), 
                FILE_CACHE.stats().hitRate() * 100);
    }
}