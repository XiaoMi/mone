/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Context object for passing metadata and attributes through the transport layer.
 *
 * <p>
 * This context can be used to:
 * <ul>
 * <li>Pass authentication tokens or credentials</li>
 * <li>Carry request-specific metadata</li>
 * <li>Store tracing or monitoring information</li>
 * <li>Share data between request customizers and handlers</li>
 * </ul>
 *
 * <p>
 * The context is typically accessed from Reactor's context using the {@link #KEY}.
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
public class McpTransportContext {

    /**
     * Key for storing this context in Reactor's context.
     */
    public static final String KEY = "mcp.transport.context";

    /**
     * Empty context instance for use when no context is needed.
     */
    public static final McpTransportContext EMPTY = new McpTransportContext(Collections.emptyMap());

    /**
     * Attributes stored in this context.
     */
    private final Map<String, Object> attributes;

    /**
     * Creates a new context with the given attributes.
     *
     * @param attributes the attributes map
     */
    private McpTransportContext(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    /**
     * Creates a new empty context.
     *
     * @return a new empty context
     */
    public static McpTransportContext create() {
        return new McpTransportContext(Collections.emptyMap());
    }

    /**
     * Creates a new context with a single attribute.
     *
     * @param key the attribute key
     * @param value the attribute value
     * @return a new context with the attribute
     */
    public static McpTransportContext of(String key, Object value) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(key, value);
        return new McpTransportContext(attrs);
    }

    /**
     * Creates a new context with the given attributes.
     *
     * @param attributes the attributes map
     * @return a new context with the attributes
     */
    public static McpTransportContext of(Map<String, Object> attributes) {
        return new McpTransportContext(attributes);
    }

    /**
     * Adds or updates an attribute in this context.
     *
     * @param key the attribute key
     * @param value the attribute value
     * @return a new context with the updated attribute
     */
    public McpTransportContext with(String key, Object value) {
        Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.put(key, value);
        return new McpTransportContext(newAttrs);
    }

    /**
     * Gets an attribute from this context.
     *
     * @param key the attribute key
     * @return an Optional containing the value if present
     */
    public Optional<Object> get(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    /**
     * Gets an attribute from this context with a specific type.
     *
     * @param <T> the expected type
     * @param key the attribute key
     * @param type the expected class
     * @return an Optional containing the typed value if present and of correct type
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        return get(key)
                .filter(type::isInstance)
                .map(value -> (T) value);
    }

    /**
     * Gets an attribute or returns a default value.
     *
     * @param key the attribute key
     * @param defaultValue the default value
     * @return the attribute value or default
     */
    public Object getOrDefault(String key, Object defaultValue) {
        return attributes.getOrDefault(key, defaultValue);
    }

    /**
     * Checks if an attribute exists.
     *
     * @param key the attribute key
     * @return true if the attribute exists
     */
    public boolean contains(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Returns all attributes as an immutable map.
     *
     * @return immutable map of attributes
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Checks if this context is empty.
     *
     * @return true if no attributes are present
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public String toString() {
        return "McpTransportContext{" +
                "attributes=" + attributes.keySet() +
                '}';
    }

}
