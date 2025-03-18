package run.mone.mcp.high.risk.element;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import run.mone.mcp.high.risk.element.function.HighRiskElementFunction;
import run.mone.mcp.high.risk.element.function.HighRiskElementMockFunction;

public class HighRiskElementTest {

    @Test
    public void test() {
        System.out.println(
                new HighRiskElementMockFunction("6666").apply(Map.of("operation", "open")));
    }
}
