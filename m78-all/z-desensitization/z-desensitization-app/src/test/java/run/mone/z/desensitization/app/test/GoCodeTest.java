package run.mone.z.desensitization.app.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import run.mone.antlr.golang.GoCode;
import run.mone.antlr.golang.ParseResult;
import run.mone.z.desensitization.api.bo.SensitiveWordConfigBo;
import run.mone.z.desensitization.service.common.NewCodeDesensitizeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/2/1 22:50
 */
public class GoCodeTest {


    @Test
    public void testParse() {
        try {
            String config = "[\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 1,\n" +
                    "\t\t\t\"content\": \"(?i)(sessiontoken|session|token|jwt)\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 1,\n" +
                    "\t\t\t\"content\": \"(?i)(username|user|admin)\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 2,\n" +
                    "\t\t\t\"content\": \"^\\\\d{17}(\\\\d|X)$\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 1,\n" +
                    "\t\t\t\"content\": \"(?i)(jdbcurl|url|dbconnection|dbconnectionstr|dbconnectionstring)\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 1,\n" +
                    "\t\t\t\"content\": \"(?i)(ak|sk|accesskey|secretkey)\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 2,\n" +
                    "\t\t\t\"content\": \"^jdbc:(\\\\w+)\\\\:\\\\/\\\\/(\\\\w+)(:\\\\d+)?\\\\/([\\\\w\\\\.-]+)$\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 2,\n" +
                    "\t\t\t\"content\": \"^((2(5[0-5]|[0-4]\\\\d))|[0-1]?\\\\d{1,2})(\\\\.((2(5[0-5]|[0-4]\\\\d))|[0-1]?\\\\d{1,2})){3}$\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"type\": 1,\n" +
                    "\t\t\t\"content\": \"(?i)(ip|host|pwd|password|secret|token|credentials|idCard|identityCard|)\",\n" +
                    "\t\t\t\"isRegexMatch\": true,\n" +
                    "\t\t\t\"isCaseSensitive\": false\n" +
                    "\t\t}\n" +
                    "\t]";
            String source = new String(Files.readAllBytes(Paths.get("/home/mason/Documents/Workspaces/xiaomi/z-desensitization/z-desensitization-app/src/test/java/run/mone/z/desensitization/app/test/test.go")));
            ParseResult parseResult = GoCode.parse(source);
            String res = NewCodeDesensitizeUtils.doDesensitize(source, parseResult, new Gson().fromJson(config, new TypeToken<List<SensitiveWordConfigBo>>() {}.getType()));
            System.out.println("Original:" + source);
            System.out.println(" \n --------------------------------------------------------------------------------------------------- \n");
            System.out.println("Processed:" + res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
