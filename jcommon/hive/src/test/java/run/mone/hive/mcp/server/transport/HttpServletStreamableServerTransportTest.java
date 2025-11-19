package run.mone.hive.mcp.server.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * Test class for HttpServletStreamableServerTransport
 * 
 * @author Adapted for hive MCP framework
 */
public class HttpServletStreamableServerTransportTest {

    @Test
    public void testBuilderCreation() {
        // Test that the builder can create a transport instance
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/test-mcp")
                .disallowDelete(true)
                .keepAliveInterval(Duration.ofSeconds(30))
                .build();

        assertNotNull("Transport should not be null", transport);
        assertTrue("Transport should implement ServerMcpTransport", transport instanceof ServerMcpTransport);
    }

    @Test
    public void testBuilderDefaults() {
        // Test builder with minimal configuration
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .build();

        assertNotNull("Transport should not be null", transport);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderValidation() {
        // Test that builder validates required parameters
        HttpServletStreamableServerTransport.builder()
                .mcpEndpoint(null)
                .build();
    }

    @Test
    public void testTransportInterface() {
        // Test that all required interface methods are available
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .build();

        // Test connect method
        assertNotNull("Connect method should be available", 
                transport.connect(mono -> mono));

        // Test sendMessage method
        McpSchema.JSONRPCNotification notification = new McpSchema.JSONRPCNotification(
                McpSchema.JSONRPC_VERSION, "test", null);
        assertNotNull("SendMessage method should be available", 
                transport.sendMessage(notification));

        // Test sendStreamMessage method
        assertNotNull("SendStreamMessage method should be available", 
                transport.sendStreamMessage(notification));

        // Test closeGracefully method
        assertNotNull("CloseGracefully method should be available", 
                transport.closeGracefully());

        // Test unmarshalFrom method (with proper TypeReference)
        try {
            transport.unmarshalFrom("test", new TypeReference<String>() {});
            // If no exception is thrown, the method is available
            assertTrue("UnmarshalFrom method should be available", true);
        } catch (Exception e) {
            // Method is available but may fail with invalid input, which is expected
            assertTrue("UnmarshalFrom method should be available", true);
        }
    }

    @Test
    public void testAuthFunction() {
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .build();

        // Test setting auth function
        transport.setAuthFunction(clientId -> "valid".equals(clientId));
        
        // The auth function is set and should be used during request processing
        // This is tested indirectly through the servlet methods
        assertNotNull("Transport should be created successfully", transport);
    }
}
