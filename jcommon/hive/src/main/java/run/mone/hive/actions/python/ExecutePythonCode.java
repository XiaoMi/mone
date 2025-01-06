package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class ExecutePythonCode extends Action {


    private final String prompt = """
            You are a Python code executor. Your task is to execute the following Python code:
            
            
            ${code}
            
            The code should be executed with the following parameters:
            
            ${params}
            
            Please provide the execution result without any additional explanations. Wrap the result in <result></result> tags.
            """;

    private final String paramGenerationPrompt = """
            You are an AI assistant specialized in analyzing Python code and generating appropriate test parameters. Your task is to examine the following Python code and suggest suitable parameters for testing:
            
            ${code}
            
            Please provide a set of test parameters that will effectively exercise the main functionality of this code. Consider the following guidelines:
            
            1. Identify the main function or entry point of the code.
            2. Determine the expected input types (e.g., integers, strings, lists, dictionaries).
            3. Include edge cases and typical use cases in your parameter suggestions.
            4. If the code requires specific formats (e.g., date strings, file paths), provide examples that match those formats.
            5. If the code interacts with external resources (e.g., files, APIs), suggest mock data or placeholders.
            
            Format your response as a Python dictionary or a list of arguments, depending on how the main function accepts input. For example:
            
            {"param1": value1, "param2": value2} or [arg1, arg2, arg3]
            
            Provide only the parameter suggestions without any additional explanation.
            """;


    public ExecutePythonCode() {
        setFunction((req, action) -> {
            String code = req.getMessage().getContent();
            String params = generateParameters(code);
            String renderedPrompt = AiTemplate.renderTemplate(prompt, ImmutableMap.of(
                    "code", code,
                    "params", params
            ));

            String result = null;
            try {
                result = executePythonCode(code, params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "<result>" + result + "</result>";
        });
    }

    private String generateParameters(String code) {
        String promptForParams = AiTemplate.renderTemplate(paramGenerationPrompt, ImmutableMap.of(
                "code", code
        ));
        // Use LLMProvider to generate parameters
        return llm.chat(promptForParams);
    }

    private String executePythonCode(String code, String params) throws Exception {
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
        int exitCode = -1;
        Process process = processBuilder.start();
        // Read the output


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
            exitCode = process.exitValue();
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