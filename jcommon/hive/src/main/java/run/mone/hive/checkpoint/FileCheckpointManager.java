package run.mone.hive.checkpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class FileCheckpointManager {
    @Value("${hive.checkpoint.enable:false}")
    private boolean enable;
    private final String projectPath;
    private final String gitDir;
    private final File checkpointsFile;
    private final Map<String, String> checkpointMap;
    private final Gson gson = new Gson();
    private static final String DEFAULT_SHADOW_REPO_SUB_DIR = ".hive/checkpoint";
    private final boolean checkpointAvailable;

    public FileCheckpointManager(String projectPath) throws IOException, InterruptedException {
        this(projectPath, DEFAULT_SHADOW_REPO_SUB_DIR);
    }

    public static Boolean staticEnabled = null;

    public static boolean enableCheck(boolean enabled) {
        if (staticEnabled != null) {
            return staticEnabled;
        }
        staticEnabled = enableCheckInner(enabled);
        return staticEnabled;
    }

    private static boolean enableCheckInner(boolean enabled) {
        if (!enabled) {
            enabled = isCheckpointEnabledByProps();
        }
        if (enabled) {
            enabled = isCheckpointEnabledByENV();
        }
        return enabled;
    }

    public FileCheckpointManager(String projectPath, String shadowRepoSubDir) throws IOException, InterruptedException {
        boolean enabled = enableCheck(enable);
        if (projectPath.equals("/") || !enabled) {
            this.projectPath = "/";
            this.gitDir = "";
            this.checkpointsFile = new File("");
            this.checkpointMap = new ConcurrentHashMap<>();
            this.checkpointAvailable = false;
            log.warn("Project path is '/' vs {} or enable is {}, checkpoint is disabled.", projectPath, enabled);
        } else {
            this.projectPath = new File(projectPath).getCanonicalPath();
            this.gitDir = Paths.get(this.projectPath, shadowRepoSubDir).toFile().getCanonicalPath();
            this.checkpointsFile = new File(this.gitDir, "checkpoints.json");
            this.checkpointMap = loadCheckpoints();
            this.checkpointAvailable = checkGitAvailability();
            if (this.checkpointAvailable) {
                addHiveToGitignore();
                initRepository();
                // Set git user info if not present
                setGitUserInfo();
            } else {
                log.warn("Git command is not available. Skipping git related operations.");
            }
        }
    }

    private static boolean isCheckpointEnabledByENV() {
        try {
            String t = System.getenv("HIVE_CHECKPOINT_DISABLE");
            return !"true".equals(t) && !"1".equals(t) && !"yes".equals(t) && !"on".equals(t);
        } catch (Throwable ignore) {
            return true;
        }
    }

    private static boolean isCheckpointEnabledByProps() {
        try {
            String p = System.getProperty("hive.checkpoint.enable");
            if (p == null) {
                p = System.getenv("HIVE_CHECKPOINT_ENABLE");
            }
            if (p == null) {
                return false;
            }
            p = p.trim().toLowerCase();
            log.info("checkpoint enabled by props:{}", p);
            return "true".equals(p) || "1".equals(p) || "yes".equals(p) || "on".equals(p);
        } catch (Throwable ignore) {
            return false;
        }
    }

    private void setGitUserInfo() throws IOException, InterruptedException {
        if (!checkpointAvailable) {
            return;
        }
        String name = executeGitRepoCommand("config", "--get", "user.name").trim();
        String email = executeGitRepoCommand("config", "--get", "user.email").trim();
        if (name.isEmpty() || email.isEmpty()) {
            log.info("Git user info not set, setting default user.");
            executeGitRepoCommand("config", "user.name", "hive-bot");
            executeGitRepoCommand("config", "user.email", "hive-bot@xiaomi.com");
        }
    }

    private boolean checkGitAvailability() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            boolean success = exitCode == 0;
            log.info("git available:{}", success);
            return success;
        } catch (IOException | InterruptedException e) {
            log.warn("Failed to check git availability", e);
            return false;
        }
    }

    private void addHiveToGitignore() {
        Path gitignorePath = Paths.get(this.projectPath, ".gitignore");
        String hiveDir = ".hive/";

        try {
            if (!Files.exists(gitignorePath)) {
                Files.writeString(gitignorePath, hiveDir + "\n", StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                log.info(".gitignore not found, created a new one and added '{}'", hiveDir);
                return;
            }

            List<String> lines = Files.readAllLines(gitignorePath);
            boolean alreadyExists = lines.stream().anyMatch(line -> line.trim().equals(hiveDir.trim()) || line.trim().equals(".hive"));

            if (!alreadyExists) {
                Files.writeString(gitignorePath, "\n" + hiveDir + "\n", StandardOpenOption.APPEND);
                log.info("Added '{}' to .gitignore", hiveDir);
            } else {
                log.debug("'{}' already exists in .gitignore", hiveDir);
            }
        } catch (IOException e) {
            log.error("Failed to read or write .gitignore file at: " + gitignorePath, e);
        }
    }

    private void initRepository() throws IOException, InterruptedException {
        if (!checkpointAvailable) {
            return;
        }
        File gitDirFile = new File(this.gitDir);
        if (!gitDirFile.exists()) {
            log.info("start init git dir:{}", this.gitDir);
            gitDirFile.mkdirs();
            executeGitRepoCommand("init", "--bare");

            // 在 .hive 目录下也添加一个 .gitignore 来忽略 checkpoint 目录，作为双重保险
            Path hiveGitignorePath = Paths.get(this.projectPath, ".hive", ".gitignore");
            String checkpointDir = "checkpoint/";
            try {
                Files.writeString(hiveGitignorePath, checkpointDir + "\n", StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                log.info("Created .gitignore in .hive directory to ignore '{}'", checkpointDir);
            } catch (IOException e) {
                log.error("Failed to create .gitignore in .hive directory", e);
            }
        }
    }

    public String createCheckpoint(String id) throws IOException, InterruptedException {
        if (!checkpointAvailable) {
            log.warn("Checkpoint is not available, skipping checkpoint creation.");
            return "";
        }
        String status = executeGitCommand("status", "--porcelain");
        String commitHash;

        if (status.trim().isEmpty()) {
            try {
                commitHash = executeGitCommand("rev-parse", "HEAD").trim();
            } catch (IOException e) {
                // Repository is empty, create the first commit
                log.info("Repository is empty, create the first commit");
                executeGitCommand("add", ".");
                executeGitCommand("commit", "--allow-empty", "-m", "Initial commit");
                commitHash = executeGitCommand("rev-parse", "HEAD").trim();
            }
        } else {
            executeGitCommand("add", ".");
            executeGitCommand("commit", "-m", "Checkpoint: " + id);
            commitHash = executeGitCommand("rev-parse", "HEAD").trim();
        }

        checkpointMap.put(id, commitHash);
        saveCheckpoints();
        log.info("Created checkpoint with id: {}, commit hash: {}", id, commitHash);
        return commitHash;
    }

    public void revert(String id) throws IOException, InterruptedException {
        if (!checkpointAvailable) {
            log.warn("Checkpoint is not available, skipping revert.");
            return;
        }
        // 重新加载检查点数据，确保获取到最新的映射
        try {
            refreshCheckpointMap();
        } catch (IOException e) {
            log.warn("Failed to refresh checkpoint map before revert, using cached data", e);
        }

        String commitHash = checkpointMap.get(id);
        if (commitHash == null || commitHash.isEmpty()) {
            throw new IllegalArgumentException("Checkpoint with id '" + id + "' not found.");
        }
        executeGitCommand("reset", "--hard", commitHash);
        log.info("Reverted to checkpoint with id: {}", id);
    }

    /**
     * 列出所有检查点
     * 每次都从文件重新加载最新数据，确保获取到最新的检查点信息
     *
     * @return 检查点ID到commit hash的映射
     */
    public Map<String, String> listCheckpoints() {
        try {
            // 重新从文件加载最新的检查点数据
            Map<String, String> latestCheckpoints = loadCheckpoints();
            // 同时更新内存中的缓存
            this.checkpointMap.clear();
            this.checkpointMap.putAll(latestCheckpoints);
            return new ConcurrentHashMap<>(latestCheckpoints);
        } catch (IOException e) {
            log.warn("Failed to reload checkpoints from file, returning cached data", e);
            // 如果加载失败，返回内存中的缓存数据
            return new ConcurrentHashMap<>(this.checkpointMap);
        }
    }

    /**
     * 获取最新的检查点ID
     * 基于 Git commit 时间戳，返回时间最新的检查点
     *
     * @return 最新的检查点ID，如果没有检查点则返回 null
     */
    public String getLatestCheckpointId() {
        if (!checkpointAvailable) {
            log.warn("Checkpoint is not available");
            return null;
        }

        try {
            // 刷新检查点映射
            refreshCheckpointMap();

            if (checkpointMap.isEmpty()) {
                log.debug("No checkpoints found");
                return null;
            }

            // 使用 git log 获取所有 commits，按时间倒序排列
            // 格式: commit_hash
            String logOutput = executeGitCommand("log", "--all", "--format=%H", "--date-order");

            if (logOutput == null || logOutput.trim().isEmpty()) {
                log.debug("No commits found in repository");
                return null;
            }

            // 解析每一行 commit hash
            String[] commits = logOutput.trim().split("\n");

            // 创建反向映射: commit hash -> checkpoint ID
            Map<String, String> hashToId = new ConcurrentHashMap<>();
            for (Map.Entry<String, String> entry : checkpointMap.entrySet()) {
                hashToId.put(entry.getValue().trim(), entry.getKey());
            }

            // 找到第一个（最新的）在 checkpointMap 中的 commit
            for (String commitHash : commits) {
                String hash = commitHash.trim();
                if (hashToId.containsKey(hash)) {
                    String checkpointId = hashToId.get(hash);
                    log.debug("Latest checkpoint found: {} -> {}", checkpointId, hash);
                    return checkpointId;
                }
            }

            log.debug("No matching checkpoint found in git history");
            return null;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to get latest checkpoint", e);
            return null;
        }
    }

    /**
     * 获取最新检查点的父检查点ID
     * 如果最新检查点的父提交也是一个检查点，则返回该检查点ID；否则返回 null
     *
     * @return 最新检查点的父检查点ID，如果不存在则返回 null
     */
    public String getLatestCheckpointParentId() {
        // 1. 获取最新的检查点ID
        String latestId = getLatestCheckpointId();
        if (latestId == null) {
            return null;
        }

        // 2. 获取该检查点的父检查点ID
        return getParentCheckpointId(latestId);
    }

    /**
     * 获取指定检查点的父检查点ID
     * 如果父提交也是一个检查点，则返回该检查点ID；否则返回 null
     *
     * @param checkpointId 检查点ID
     * @return 父检查点ID，如果不存在则返回 null
     */
    public String getParentCheckpointId(String checkpointId) {
        if (!checkpointAvailable || checkpointId == null) {
            return null;
        }

        try {
            // 刷新检查点映射
            refreshCheckpointMap();

            // 获取检查点对应的 commit hash
            String commitHash = checkpointMap.get(checkpointId);
            if (commitHash == null) {
                log.warn("Checkpoint {} not found in map", checkpointId);
                return null;
            }

            // 获取父 commit hash
            String parentCommitHash = getParent(commitHash.trim());
            if (parentCommitHash == null || parentCommitHash.isEmpty()) {
                log.debug("Checkpoint {} has no parent", checkpointId);
                return null;
            }

            // 在 checkpointMap 中查找父 commit 对应的检查点ID
            for (Map.Entry<String, String> entry : checkpointMap.entrySet()) {
                if (entry.getValue().trim().equals(parentCommitHash)) {
                    log.debug("Found parent checkpoint: {} for checkpoint: {}", entry.getKey(), checkpointId);
                    return entry.getKey();
                }
            }

            log.debug("Parent commit {} is not a checkpoint", parentCommitHash);
            return null;

        } catch (IOException e) {
            log.error("Failed to get parent checkpoint for {}", checkpointId, e);
            return null;
        }
    }


    private String executeGitCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "--git-dir=" + this.gitDir, "--work-tree=" + this.projectPath);
        processBuilder.command().addAll(java.util.Arrays.asList(command));
        return execute(processBuilder, command);
    }

    private String executeGitRepoCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "--git-dir=" + this.gitDir);
        processBuilder.command().addAll(java.util.Arrays.asList(command));
        return execute(processBuilder, command);
    }

    private String execute(ProcessBuilder processBuilder, String... command) throws IOException, InterruptedException {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            // config --get user.name will return 1 if not set
            if (command.length > 2 && command[0].equals("config") && command[1].equals("--get")) {
                return "";
            }
            // rev-parse HEAD will return 128 if not set
            if (command.length > 1 && command[0].equals("rev-parse") && command[1].equals("HEAD")) {
                log.warn("Git rev-parse HEAD failed, maybe the repository is empty. Output: {}", output);
                throw new IOException("Git command failed with exit code " + exitCode + ": " + output);
            }
            log.error("Error executing git command: {}. Output: {}", String.join(" ", command), output);
            throw new IOException("Git command failed with exit code " + exitCode + ": " + output);
        }

        return output.toString();
    }

    private Map<String, String> loadCheckpoints() throws IOException {
        if (checkpointsFile.exists()) {
            try (FileReader reader = new FileReader(checkpointsFile)) {
                Type type = new TypeToken<ConcurrentHashMap<String, String>>() {
                }.getType();
                Map<String, String> loadedMap = gson.fromJson(reader, type);
                if (loadedMap != null) {
                    return loadedMap;
                }
            }
        }
        return new ConcurrentHashMap<>();
    }

    private void saveCheckpoints() throws IOException {
        if (!checkpointsFile.getParentFile().exists()) {
            Files.createDirectories(checkpointsFile.getParentFile().toPath());
        }
        try (FileWriter writer = new FileWriter(checkpointsFile)) {
            gson.toJson(checkpointMap, writer);
        }
    }

    /**
     * 从文件刷新内存中的检查点映射
     * 用于确保内存缓存与文件保持同步
     */
    private void refreshCheckpointMap() throws IOException {
        Map<String, String> latestCheckpoints = loadCheckpoints();
        this.checkpointMap.clear();
        this.checkpointMap.putAll(latestCheckpoints);
        log.debug("Refreshed checkpoint map, loaded {} checkpoints", latestCheckpoints.size());
    }

    /**
     * Generate a unified diff from the given checkpoint to it's parent.
     * If {@code checkpointId} is null or empty, uses {@code HEAD} as the base.
     *
     * @param checkpointId checkpoint id or commit-ish; when null/empty, use HEAD
     * @param paths        optional path list to limit scope; when null/empty, diff entire tree
     * @param contextLines unified context lines (e.g., 3, 5)
     * @return diff text (may be empty if no changes), or a message if checkpoint is unavailable
     */
    public String diffFromCheckpoint(String checkpointId, List<String> paths, int contextLines) {
        if (!checkpointAvailable) {
            return "Checkpoint is not available (git disabled or not found).";
        }
        String base = "HEAD";
        if (checkpointId != null && !checkpointId.isEmpty()) {
            String mapped = checkpointMap.getOrDefault(checkpointId, checkpointId);
            base = mapped;
        }
        String parent = getParent(base);

        try {
            ProcessBuilder pb = new ProcessBuilder("git",
                    "--git-dir=" + this.gitDir,
                    "--work-tree=" + this.projectPath,
                    "diff",
                    "--no-color",
                    "--unified=" + Math.max(0, contextLines));

            // 如果有父提交，对比 parent..base；否则显示 base 的全部内容
            if (parent != null && !parent.isEmpty()) {
                pb.command().add(parent);
                pb.command().add(base);
            } else {
                // 首次提交，没有父提交，显示该提交引入的所有内容
                pb.command().add("--root");
                pb.command().add(base);
            }

            pb.command().add("--");

            if (paths != null && !paths.isEmpty()) {
                pb.command().addAll(paths);
            } else {
                pb.command().add(".");
            }

            log.info("Executing git diff command: {}", String.join(" ", pb.command()));
            String diff = execute(pb, "diff");

            return diff;
        } catch (IOException | InterruptedException e) {
            log.error("Failed to run git diff", e);
            return "Failed to run git diff: " + e.getMessage();
        }
    }

    /**
     * 获取指定提交的父提交hash
     *
     * @param commitish commit hash 或引用（如 HEAD）
     * @return 父提交的 hash，如果没有父提交（首次提交）则返回空字符串
     */
    private String getParent(String commitish) {
        if (!checkpointAvailable) {
            return "";
        }
        try {
            // 使用 git rev-parse {commit}^ 获取父提交
            // 对于首次提交，会返回错误，我们捕获并返回空字符串
            String parent = executeGitCommand("rev-parse", commitish + "^").trim();
            log.debug("Parent of {} is {}", commitish, parent);
            return parent;
        } catch (IOException | InterruptedException e) {
            // 如果是首次提交（没有父提交），git rev-parse 会失败
            log.debug("Failed to get parent of {}, possibly the initial commit: {}", commitish, e.getMessage());
            return "";
        }
    }
}
