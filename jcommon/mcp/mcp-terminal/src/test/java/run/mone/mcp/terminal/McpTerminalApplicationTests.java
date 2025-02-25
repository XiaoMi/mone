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

    @Test
    void test2() throws InterruptedException {
        String id = terminalFunction.openTerminal();
        System.out.println(id);
        Thread.sleep(2000);
        System.out.println("+++++++");
        String s = terminalFunction.executeCommand("cd /Users/a1/hera", id);
        System.out.println(s);
    }

    @Test
    void test3() throws InterruptedException {
        String id = "41548";
        String s1 = terminalFunction.executeCommand("cd /Users/a1/hera", id);
        System.out.println(s1);
        System.out.println("++++++");
        String s = terminalFunction.executeCommand("vim test2.txt", id);
        System.out.println(s);
    }

    @Test
    void test4() {
        String outputFilePath = "/Users/a1/hera/terminal_output_" + 38368 + ".log";
        String s = terminalFunction.readAndFilterOutput(outputFilePath);
        System.out.println(s);
    }

    @Test
    void test5() {
        String s = terminalFunction.closeTerminal("38715");
        System.out.println(s);
    }

    @Test
    void test6() {
        String s = terminalFunction.simulationKeyPresses("43431",":wq");
        System.out.println(s);
    }
}
