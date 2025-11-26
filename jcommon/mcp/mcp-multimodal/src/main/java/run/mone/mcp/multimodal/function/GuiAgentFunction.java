package run.mone.mcp.multimodal.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.service.GuiAgentService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class GuiAgentFunction implements McpFunction {

    @Autowired
    private GuiAgentService guiAgentService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["analyze", "execute", "visualize"],
                        "description": "The GUI agent operation to perform"
                    },
                    "imagePath": {
                        "type": "string",
                        "description": "Path to the screenshot image file"
                    },
                    "instruction": {
                        "type": "string",
                        "description": "User instruction for the GUI agent"
                    },
                    "parsedOutput": {
                        "type": "string",
                        "description": "The parsed output from a previous analyze operation"
                    },
                    "outputImagePath": {
                        "type": "string",
                        "description": "Path to save the output image with visualizations"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        return Flux.defer(() -> {
            try {
                Flux<String> result;
                
                switch (operation) {
                    case "analyze" -> {
                        String imagePath = (String) arguments.get("imagePath");
                        String instruction = (String) arguments.get("instruction");
                        
                        if (imagePath == null || instruction == null) {
                            return Flux.just(new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("Error: Missing required parameters")), 
                                true
                            ));
                        }
                        
                        AtomicReference<String> modelOutput = new AtomicReference<>();
                        
                        return guiAgentService.run(imagePath, instruction, Prompt.systemPrompt)
                            .doOnNext(modelOutput::set)
                            .flatMapMany(output -> {
                                String parsedOutput = guiAgentService.parseActionOutput(output);
                                return Flux.just(
                                    "Model output:\n" + modelOutput.get() + "\n\n" +
                                    "Parsed action:\n" + parsedOutput
                                );
                            })
                            .map(res -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(res)), 
                                false
                            ));
                    }
                    
                    case "execute" -> {
                        String parsedOutput = (String) arguments.get("parsedOutput");
                        
                        if (parsedOutput == null) {
                            return Flux.just(new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("Error: Missing parsed output")), 
                                true
                            ));
                        }
                        
                        return guiAgentService.executeAction(parsedOutput)
                            .map(res -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(res)), 
                                false
                            ));
                    }
                    
                    case "visualize" -> {
                        String imagePath = (String) arguments.get("imagePath");
                        String parsedOutput = (String) arguments.get("parsedOutput");
                        String outputImagePath = (String) arguments.get("outputImagePath");
                        
                        if (imagePath == null || parsedOutput == null || outputImagePath == null) {
                            return Flux.just(new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("Error: Missing required parameters")), 
                                true
                            ));
                        }
                        
                        String resultPath = guiAgentService.visualizeAction(imagePath, parsedOutput, outputImagePath);
                        return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Visualization saved to: " + resultPath)), 
                            false
                        ));
                    }
                    
                    default -> {
                        return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Error: Unknown operation: " + operation)), 
                            true
                        ));
                    }
                }
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())), 
                    true
                ));
            }
        });
    }

    @Override
    public String getName() {
        return "guiAgent";
    }

    @Override
    public String getDesc() {
        return "GUI Agent functionality for analyzing screenshots, executing UI actions, and visualizing results.";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 