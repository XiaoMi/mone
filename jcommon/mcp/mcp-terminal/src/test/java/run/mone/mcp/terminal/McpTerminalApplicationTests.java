package run.mone.mcp.terminal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.mcp.terminal.function.TerminalFunction;

@SpringBootTest
class McpTerminalApplicationTests {

    @Test
    void contextLoads() {
    }

    TerminalFunction terminalFunction = new TerminalFunction();

    @Test
    void test() {
        String id = terminalFunction.openTerminal();
        System.out.println(id);
    }

}
