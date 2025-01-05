
package run.mone.hive.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class PythonExecutor {

    public String executePythonCode(String code, String params) throws Exception {
        // Create a temporary file to store the Python code
        Path tempFile = Files.createTempFile("python_code_", ".py");

        Files.write(tempFile, code.getBytes());

        // Prepare the command to execute Python code
        ProcessBuilder processBuilder = new ProcessBuilder("python3", tempFile.toString());
        processBuilder.redirectErrorStream(true);

        // Set the working directory to the user's home directory
        processBuilder.directory(new File(System.getProperty("user.home")));
        
        // Start the process
        StringBuilder output = new StringBuilder();
        Process process = processBuilder.start();

        // Read the output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for the process to complete
        boolean completed = process.waitFor(30, TimeUnit.SECONDS);

        // Delete the temporary file
        Files.delete(tempFile);

        if (!completed) {
            process.destroyForcibly();
            output.append("Error: Python code execution timed out after 30 seconds.");
        } else {
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                output.append("Error: Python code execution failed with exit code: ")
                        .append(exitCode)
                        .append("\n");
            }
        }

        String result = output.toString().trim();
        if (result.isEmpty()) {
            result = "No output or error message received.";
        }
        return result;
    }
}
