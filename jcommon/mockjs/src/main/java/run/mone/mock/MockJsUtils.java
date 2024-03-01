package run.mone.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public class MockJsUtils {
    /**
     * Javascript执行引擎
     */
    public static final ScriptEngine MOCK_JS_ENGINE;
    /**
     * mockjs的资源路径
     */
    private static final String MOCK_JS_PATH = "js/mock-min.js";

    static {
        MOCK_JS_ENGINE = new ScriptEngineManager().getEngineByName("js");
        try (
                InputStream mockJs = MockJsUtils.class.getClassLoader().getResourceAsStream(MOCK_JS_PATH);
                InputStreamReader reader = new InputStreamReader(mockJs)
        ) {
            MOCK_JS_ENGINE.eval(reader);
        } catch (ScriptException | IOException e) {
            log.error("执行MockJs错误", e);
        } catch (Throwable e) {
            log.error("内部错误", e);
        }
    }

    public static String mock(String template) {
        template = StringUtils.trimToEmpty(template);

        try {
            String result = MOCK_JS_ENGINE.eval("JSON.stringify(" + template + ")").toString();
            return result;
        } catch (Throwable e) {
            log.error("执行Mock.mock错误", e);
        }

        return null;
    }

    public static List<String> batchMock(String template, int number) {
        return batchOperation(new ArrayList<>(), () -> mock(template), number);
    }

    public static List<String> batchOperation(List<String> res, Supplier<String> supplier, int number) {
        int batchNumber = 1000;
        int n = ((number - res.size()) / batchNumber) + 1;

        for (int j = 0; j <= n; j++) {
            int defaultNumber = batchNumber;
            IntStream.range(0, defaultNumber)
                    .parallel() // 将流转换为并行流
                    .forEach(i -> {
                        // 这里是要并行执行的操作
                        String mockData = supplier.get();
                        res.add(mockData);
                    });
        }

        if (res.size() >= number) {
            List<String> res1 = res.subList(0, number);
            return res1;
        } else {
            return batchOperation(res, supplier, number);
        }
    }


}