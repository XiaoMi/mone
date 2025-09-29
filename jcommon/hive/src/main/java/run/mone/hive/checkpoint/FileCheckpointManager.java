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

@Slf4j
public class FileCheckpointManager {

    private final String projectPath;
    private final String gitDir;
    private final File checkpointsFile;
    private final Map<String, String> checkpointMap;
    private final Gson gson = new Gson();
    private static final String DEFAULT_SHADOW_REPO_SUB_DIR = ".hive/checkpoint";

    public FileCheckpointManager(String projectPath) throws IOException, InterruptedException {
        this(projectPath, DEFAULT_SHADOW_REPO_SUB_DIR);
    }

    public FileCheckpointManager(String projectPath, String shadowRepoSubDir) throws IOException, InterruptedException {
        this.projectPath = new File(projectPath).getCanonicalPath();
        this.gitDir = Paths.get(this.projectPath, shadowRepoSubDir).toFile().getCanonicalPath();
        this.checkpointsFile = new File(this.gitDir, "checkpoints.json");
        this.checkpointMap = loadCheckpoints();
        addHiveToGitignore();
        initRepository();
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
        File gitDirFile = new File(this.gitDir);
        if (!gitDirFile.exists()) {
            log.info("start init git dir:{}", this.gitDir);
            gitDirFile.mkdirs();
            executeGitRepoCommand("init", "--bare");
        }
    }

    public String createCheckpoint(String id) throws IOException, InterruptedException {
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
}
