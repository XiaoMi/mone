package run.mone.mock.test;

import org.junit.Ignore;
import org.junit.Test;
import run.mone.mock.MockJsUtils;

import java.util.List;

@Ignore
public class MockTest {

    @Test
    public void mockjsTest() {
        String input = "{\n" +
                "  \"string|1-10\": \"★\"\n" +
                "}";
        long begin = System.currentTimeMillis();
        String output = MockJsUtils.mock(input);
        long costtime = System.currentTimeMillis() - begin;
        System.out.println(output + " ====== cost time " + costtime);
    }

    @Test
    public void mockjsTestBatch() {
        String input = "{\n" +
                "  \"string|1-10\": \"★\"\n" +
                "}";
        long begin = System.currentTimeMillis();
        List<String> output = MockJsUtils.batchMock(input, 50000);
        long costtime = System.currentTimeMillis() - begin;
        System.out.println(output);
    }

    @Test
    public void mockjsTestRandom() {
        String input = "Random.title(3, 5)";
        long begin = System.currentTimeMillis();
        String output = MockJsUtils.random(input);
        long costtime = System.currentTimeMillis() - begin;
        System.out.println(output);
    }

    @Test
    public void mockjsTestRandomBatch() {
        String input = "Random.title(3, 5)";
        long begin = System.currentTimeMillis();
        List<String> output = MockJsUtils.batchRandom(input, 9999);
        long costtime = System.currentTimeMillis() - begin;
        System.out.println(output);
    }


}
