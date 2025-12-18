package run.mone.hive.shannon.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.shannon.exceptions.MessageParseError;
import run.mone.hive.shannon.types.messages.*;

import java.util.function.Consumer;

/**
 * Parser for JSON messages from Claude Code CLI.
 * Handles incremental parsing of JSON lines and converts them to typed Message objects.
 */
public class MessageParser {

    private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

    private final ObjectMapper objectMapper;
    private final Consumer<Message> messageHandler;
    private final StringBuilder buffer;

    public MessageParser(ObjectMapper objectMapper, Consumer<Message> messageHandler) {
        this.objectMapper = objectMapper;
        this.messageHandler = messageHandler;
        this.buffer = new StringBuilder();
    }

    /**
     * Parse a line of JSON output from the CLI.
     * The line may be a complete JSON object or a fragment.
     *
     * @param line the line to parse
     */
    public void parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        // Check if this looks like the start of a JSON object
        String trimmed = line.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            // If we have buffered content, try to parse it first
            if (buffer.length() > 0) {
                tryParse(buffer.toString());
                buffer.setLength(0);
            }
            buffer.append(line);
        } else {
            // This is a continuation line
            buffer.append(line);
        }

        // Try to parse the buffered content
        tryParse(buffer.toString());
    }

    /**
     * Try to parse the buffered content as JSON.
     * If successful, clear the buffer and call the message handler.
     * If unsuccessful (incomplete JSON), leave the buffer for more data.
     *
     * @param content the content to try parsing
     */
    private void tryParse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        try {
            JsonNode node = objectMapper.readTree(content);
            Message message = parseMessage(node);
            if (message != null) {
                messageHandler.accept(message);
                buffer.setLength(0); // Clear buffer on successful parse
            }
        } catch (JsonProcessingException e) {
            // Incomplete JSON - wait for more data
            logger.trace("Incomplete JSON, waiting for more data: {}", e.getMessage());
        }
    }

    /**
     * Parse a JSON node into a typed Message object.
     *
     * @param node the JSON node
     * @return the parsed message
     * @throws MessageParseError if the message type is unknown or parsing fails
     */
    private Message parseMessage(JsonNode node) throws MessageParseError {
        if (!node.has("type")) {
            logger.warn("Message missing 'type' field: {}", node);
            return null;
        }

        String type = node.get("type").asText();

        try {
            return switch (type) {
                case "user" -> objectMapper.treeToValue(node, UserMessage.class);
                case "assistant" -> objectMapper.treeToValue(node, AssistantMessage.class);
                case "system" -> objectMapper.treeToValue(node, SystemMessage.class);
                case "result" -> objectMapper.treeToValue(node, ResultMessage.class);
                case "stream_event" -> objectMapper.treeToValue(node, StreamEvent.class);
                default -> {
                    logger.warn("Unknown message type: {}", type);
                    yield null;
                }
            };
        } catch (JsonProcessingException e) {
            throw new MessageParseError(
                "Failed to parse message of type: " + type,
                node.toString(),
                e
            );
        }
    }

    /**
     * Force parsing of any remaining buffered content.
     * Called when the stream ends to process any incomplete messages.
     */
    public void flush() {
        if (buffer.length() > 0) {
            String content = buffer.toString();
            buffer.setLength(0);
            try {
                JsonNode node = objectMapper.readTree(content);
                Message message = parseMessage(node);
                if (message != null) {
                    messageHandler.accept(message);
                }
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse remaining buffer content: {}", content, e);
            }
        }
    }

    /**
     * Clear the internal buffer.
     */
    public void reset() {
        buffer.setLength(0);
    }
}
