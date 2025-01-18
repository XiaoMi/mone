
package run.mone.mcp.docker.function;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class DockerFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "dockerOperation";

    private String desc = "Docker operations including container and image management";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["listContainers", "createContainer", "startContainer", "stopContainer", "removeContainer", "listImages", "pullImage", "removeImage"],
                        "description":"The operation to perform on Docker"
                    },
                    "imageName": {
                        "type": "string",
                        "description":"The name of the Docker image"
                    },
                    "containerName": {
                        "type": "string",
                        "description":"The name of the Docker container"
                    },
                    "containerId": {
                        "type": "string",
                        "description":"The ID of the Docker container"
                    },
                    "imageId": {
                        "type": "string",
                        "description":"The ID of the Docker image"
                    }
                },
                "required": ["operation"]
            }
            """;

    private DockerClient dockerClient;

    public DockerFunction() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        log.info("operation: {}", operation);

        try {
            String result = switch (operation) {
                case "listContainers" -> listContainers();
                case "createContainer" -> createContainer((String) arguments.get("imageName"), (String) arguments.get("containerName"));
                case "startContainer" -> startContainer((String) arguments.get("containerId"));
                case "stopContainer" -> stopContainer((String) arguments.get("containerId"));
                case "removeContainer" -> removeContainer((String) arguments.get("containerId"));
                case "listImages" -> listImages();
                case "pullImage" -> pullImage((String) arguments.get("imageName"));
                case "removeImage" -> removeImage((String) arguments.get("imageId"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String listContainers() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        return containers.toString();
    }

    private String createContainer(String imageName, String containerName) {
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
        return container.getId();
    }

    private String startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
        return "Container started: " + containerId;
    }

    private String stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        return "Container stopped: " + containerId;
    }

    private String removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
        return "Container removed: " + containerId;
    }

    private String listImages() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        return images.toString();
    }

    @SneakyThrows
    private String pullImage(String imageName) {
        dockerClient.pullImageCmd(imageName).start().awaitCompletion();
        return "Image pulled: " + imageName;
    }

    private String removeImage(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
        return "Image removed: " + imageId;
    }
}
