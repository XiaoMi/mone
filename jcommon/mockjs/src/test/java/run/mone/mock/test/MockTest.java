package run.mone.mock.test;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import run.mone.mock.MockJsUtils;

import javax.script.ScriptEngine;
import java.util.List;

@Ignore
public class MockTest {

    @Test
    public void mockjsTest() {
        String input = "{\n" +
                "  \"boolean|1\": true\n" +
                "}";
        String output = MockJsUtils.mock(input);
        System.out.println(output);
    }

    @Test
    public void mockjsTestBatch() {
        String input = "{\n" +
                "  \"boolean|1\": true\n" +
                "}";
        List<String> output = MockJsUtils.batchMock(input, 10000);
        System.out.println(output);
    }

    @Test
    public void mockjsTestRandom() {
        String input = "Random.title(3, 5)";
        String output = MockJsUtils.random(input);
        System.out.println(output);
    }

    @Test
    public void mockjsTestRandomBatch() {
        String input = "Random.title(3, 5)";
        List<String> output = MockJsUtils.batchRandom(input, 9999);
        System.out.println(output);
    }


}
