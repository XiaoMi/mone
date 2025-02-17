
package run.mone.mcp.fetch.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Collections;
import java.util.Map;

class FetchFunctionTest {

    private FetchFunction fetchFunction;

    @BeforeEach
    void setUp() {
        fetchFunction = new FetchFunction();
    }

    @Test
    void testFetchContentFromBaidu() {
        Map<String, Object> arguments = Collections.singletonMap("url", "https://www.baidu.com");
        
        McpSchema.CallToolResult result = fetchFunction.apply(arguments);

        System.out.println(result);
        
    }

}
