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

    private static Boolean staticEnabled = null;

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
            if ("true".equals(t) || "1".equals(t) || "yes".equals(t) || "on".equals(t)) {
                return false;
            }
            return true;
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
        String commitHash = checkpointMap.get(id);
        if (commitHash == null || commitHash.isEmpty()) {
            throw new IllegalArgumentException("Checkpoint with id '" + id + "' not found.");
        }
        executeGitCommand("reset", "--hard", commitHash);
        log.info("Reverted to checkpoint with id: {}", id);
    }

    public Map<String, String> listCheckpoints() {
        return new ConcurrentHashMap<>(this.checkpointMap);
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
     * Generate a unified diff from the given checkpoint to current work-tree.
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

        try {
            // Add all files to staging area (including untracked files) to include them in diff
            executeGitCommand("add", "-A");

            ProcessBuilder pb = new ProcessBuilder("git",
                    "--git-dir=" + this.gitDir,
                    "--work-tree=" + this.projectPath,
                    "diff",
                    "--cached",  // Show diff between staged changes and base commit
                    "--no-color",
                    "--unified=" + Math.max(0, contextLines),
                    base,
                    "--");

            if (paths != null && !paths.isEmpty()) {
                pb.command().addAll(paths);
            } else {
                pb.command().add(".");
            }

            log.info("Executing git diff command: {}", String.join(" ", pb.command()));
            String diff = execute(pb, "diff");

            // Reset staging area to avoid affecting future operations
            executeGitCommand("reset", "HEAD");

            return diff;
        } catch (IOException | InterruptedException e) {
            log.error("Failed to run git diff", e);
            // Try to reset even on error to keep repo clean
            try {
                executeGitCommand("reset", "HEAD");
            } catch (Exception resetEx) {
                log.warn("Failed to reset after diff error", resetEx);
            }
            return "Failed to run git diff: " + e.getMessage();
        }
    }
}
