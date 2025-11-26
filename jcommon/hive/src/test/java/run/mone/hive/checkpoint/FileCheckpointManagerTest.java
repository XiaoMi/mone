package run.mone.hive.checkpoint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FileCheckpointManagerTest {

    private static final String TEST_PROJECT_DIR = "test_project";
    private static final String TEST_FILE_NAME = "feature.js";
    private FileCheckpointManager checkpointManager;
    private Path projectPath;
    private Path filePath;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        projectPath = Paths.get(TEST_PROJECT_DIR);
        filePath = projectPath.resolve(TEST_FILE_NAME);

        // Clean up previous test runs
        if (Files.exists(projectPath)) {
            Files.walk(projectPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        Files.createDirectories(projectPath);

        // Add .gitignore to prevent git from tracking its own repository
        Path gitignorePath = projectPath.resolve(".gitignore");
        Files.write(gitignorePath, ".hive\n".getBytes());

        checkpointManager = new FileCheckpointManager(TEST_PROJECT_DIR);
    }

    @AfterEach
    public void teardown() throws IOException {
        // Clean up after test
        if (Files.exists(projectPath)) {
            Files.walk(projectPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void testCheckpointWorkflow() throws IOException, InterruptedException {
        // 1. Initial content
        String initialContent = "// Version 1: Initial feature";
        Files.write(filePath, initialContent.getBytes());
        System.out.println("✅ Step 1: Initial file created. Content: " + readTestFile());

        // 2. Create first checkpoint
        String checkpoint1 = checkpointManager.createCheckpoint("checkpoint-1");
        System.out.println("✅ Step 2: Checkpoint 'checkpoint-1' created.");

        // 3. Modify the file
        String modifiedContent = initialContent + "\n// Version 2: Added new feature";
        Files.write(filePath, modifiedContent.getBytes());
        assertEquals(modifiedContent, readTestFile());
        System.out.println("✅ Step 3: File modified. Content: " + readTestFile());

        // 4. Create second checkpoint
        String checkpoint2 = checkpointManager.createCheckpoint("checkpoint-2");
        assertNotEquals(checkpoint1, checkpoint2);
        System.out.println("✅ Step 4: Checkpoint 'checkpoint-2' created.");

        // 5. Introduce a bug
        String bugContent = modifiedContent + "\nconsole.log('This is a bug!');";
        Files.write(filePath, bugContent.getBytes());
        assertEquals(bugContent, readTestFile());
        System.out.println("✅ Step 5: 'Bug' introduced. Content: " + readTestFile());

        // 6. Revert to first checkpoint
        checkpointManager.revert("checkpoint-1");
        assertEquals(initialContent, readTestFile());
        System.out.println("✅ Step 6: Reverted to 'checkpoint-1'. Content: " + readTestFile());

        System.out.println("🎉 Workflow test complete! The file has been successfully reverted.");
    }

    private String readTestFile() throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}
