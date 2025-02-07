package run.mone.mcp.song.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * @author shanwb
 * @date 2025-01-23
 */
@Data
@Slf4j
public class SongFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "songOperation";

    private String desc = "Song operations including merge, mss, vocalReplace";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["merge", "mss", "vocalReplace"],
                        "description":"The operation type of song"
                    },
                    "instrumentalPath": {
                        "type": "string",
                        "description":"Path to the instrumental file"
                    },
                    "vocalsPath": {
                        "type": "string",
                        "description":"Path to the vocals file"
                    },
                    "outputPath": {
                        "type": "string",
                        "description":"Path for the output merged file"
                    },
                    "instrumentalVolume": {
                        "type": "number",
                        "description":"Volume adjustment for instrumental (default: 1.0)"
                    },
                    "vocalsVolume": {
                        "type": "string",
                        "description":"Volume adjustment for vocals in dB (default: 10dB)"
                    }
                },
                "required": ["command"]
            }
            """;

    public SongFunction() {
        // Check if ffmpeg is available
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process p = pb.start();
            if (p.waitFor() != 0) {
                throw new RuntimeException("ffmpeg check failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("FFmpeg is not available. Please install FFmpeg first.", e);
        }
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            if ("merge".equals(command)) {
                String instrumentalPath = (String) args.get("instrumentalPath");
                String vocalsPath = (String) args.get("vocalsPath");
                String outputPath = (String) args.get("outputPath");
                Object instrumentalVolume = args.getOrDefault("instrumentalVolume", 1.0);
                Object vocalsVolume = args.getOrDefault("vocalsVolume", "10dB");

                return mergeSongs(instrumentalPath, vocalsPath, outputPath, instrumentalVolume, vocalsVolume);
            }
            // ... other commands
            
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Unknown command: " + command)),
                true
            );
        } catch (Exception e) {
            log.error("Error executing command", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            );
        }
    }

    private McpSchema.CallToolResult mergeSongs(String instrumentalPath, String vocalsPath, 
            String outputPath, Object instrumentalVolume, Object vocalsVolume) throws Exception {
        
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "mcp-song");
        tempDir.toFile().mkdirs();
        
        Path tempInstrumental = tempDir.resolve("temp_instrumental.mp3");
        Path tempVocals = tempDir.resolve("temp_vocals.mp3");
        
        try {
            // 1. Adjust instrumental volume
            String instVolStr = instrumentalVolume instanceof Number ? 
                String.valueOf(instrumentalVolume) : instrumentalVolume.toString();
            executeFFmpeg(List.of(
                "-i", instrumentalPath,
                "-filter:a", "volume=" + instVolStr,
                tempInstrumental.toString()
            ));

            // 2. Adjust vocals volume
            String vocVolStr = vocalsVolume.toString();
            executeFFmpeg(List.of(
                "-i", vocalsPath,
                "-filter:a", "volume=" + vocVolStr,
                tempVocals.toString()
            ));

            // 3. Mix audio files
            executeFFmpeg(List.of(
                "-i", tempInstrumental.toString(),
                "-i", tempVocals.toString(),
                "-filter_complex", "amix=inputs=2:duration=longest:dropout_transition=2",
                "-ac", "2",
                "-ar", "44100",
                outputPath
            ));

            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Successfully merged audio to: " + outputPath)),
                false
            );
        } finally {
            // Cleanup temp files
            tempInstrumental.toFile().delete();
            tempVocals.toFile().delete();
        }
    }

    private void executeFFmpeg(List<String> arguments) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y"); // Overwrite output files
        command.addAll(arguments);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        
        log.info("Executing FFmpeg command: {}", String.join(" ", command));
        
        Process process = pb.start();
        
        // Read output in a separate thread to prevent blocking
        Thread outputReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                }
            } catch (IOException e) {
                log.error("Error reading FFmpeg output", e);
            }
        });
        outputReader.start();

        // Wait for process to complete
        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new Exception("FFmpeg process timed out");
        }

        if (process.exitValue() != 0) {
            throw new Exception("FFmpeg process failed with exit code: " + process.exitValue());
        }
    }
}
