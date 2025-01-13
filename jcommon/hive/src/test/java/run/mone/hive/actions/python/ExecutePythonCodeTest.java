
package run.mone.hive.actions.python;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutePythonCodeTest {

    private ExecutePythonCode executePythonCode;

    private LLM llm;

    @BeforeEach
    void setUp() {
        executePythonCode = new ExecutePythonCode();
        llm = new LLM(LLMConfig.builder().build());
        executePythonCode.setLlm(llm);
    }

    @Test
    void testExecutePythonCode_Success() {
        String code = "def add(a, b):\n    return a + b\n";
        String result = executePythonCode.executePythonCode(code + "print(add(2, 3))").getContent();
        assertEquals("<result>5</result>", result);
    }

    @Test
    void testExecutePythonCode_Error() {
        String code = "def divide(a, b):\n    return a / b\n";
        String result = executePythonCode.executePythonCode(code + "print(divide(1, 0))").getContent();
        assertTrue(result.startsWith("<error>"));
        assertTrue(result.contains("ZeroDivisionError"));
    }

    @Test
    void testRun_Success() throws Exception {
        ActionReq req = new ActionReq();
        req.setMessage(new Message("def add(a, b):\n    return a + b"));
        Message result = executePythonCode.getFunction().apply(req, executePythonCode, new ActionContext());
        System.out.println(result);
    }

    @Test
    void testRun_Error() throws Exception {
        ActionReq req = new ActionReq();
        req.setMessage(new Message("def divide(a, b):\n    return a / b"));

        Message result = executePythonCode.getFunction().apply(req, executePythonCode, new ActionContext());

        assertTrue(result.getContent().startsWith("<error>"));
        assertTrue(result.getContent().contains("ZeroDivisionError"));
    }
}
