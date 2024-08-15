package run.mone.z.desensitization.app.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import run.mone.z.desensitization.api.bo.DesensitizeReq;
import run.mone.z.desensitization.api.bo.SensitiveWordConfigBo;
import run.mone.z.desensitization.service.common.CodeExtractorUtils;
import run.mone.z.desensitization.service.common.CodeParseUtils;
import run.mone.z.desensitization.service.common.NewCodeDesensitizeUtils;

import java.util.List;

/**
 * @author wmin
 * @date 2023/11/14
 */
public class NewTest {

    String config = "[\n" +
            "  {\n" +
            "    \"id\": 10,\n" +
            "    \"name\": \"ip正则\",\n" +
            "    \"type\": 2,\n" +
            "    \"content\": \"^((2(5[0-5]|[0-4]\\\\d))|[0-1]?\\\\d{1,2})(\\\\.((2(5[0-5]|[0-4]\\\\d))|[0-1]?\\\\d{1,2})){3}$\",\n" +
            "    \"available\": true,\n" +
            "    \"isRegexMatch\": true,\n" +
            "    \"isCaseSensitive\": false,\n" +
            "    \"isDelete\": false,\n" +
            "    \"creator\": \"wangmin17\",\n" +
            "    \"createTime\": \"Nov 3, 2023 3:06:55 PM\",\n" +
            "    \"updateTime\": \"Nov 3, 2023 6:10:39 PM\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 9,\n" +
            "    \"name\": \"敏感key\",\n" +
            "    \"type\": 1,\n" +
            "    \"content\": \"(?i)(ip|host|pwd|password|secret|token|credentials|idCard|identityCard|)\",\n" +
            "    \"available\": true,\n" +
            "    \"isRegexMatch\": true,\n" +
            "    \"isCaseSensitive\": false,\n" +
            "    \"isDelete\": false,\n" +
            "    \"creator\": \"wangmin17\",\n" +
            "    \"createTime\": \"Nov 3, 2023 3:05:45 PM\",\n" +
            "    \"updateTime\": \"Nov 14, 2023 6:38:01 PM\"\n" +
            "  }\n" +
            "]";

    @Test
    public void testMethod() throws Exception {
        String codeSnippet = "public void setActionConfMap(Map<String, ActionInfo> actionConfMap) {\n" +
                "        String password = \"12345\";\n" +
                "        this.actionConfMap = actionConfMap;\n" +
                "    }";
        //System.out.println(CodeParseUtils.isClass(codeSnippet));
        System.out.println(CodeParseUtils.isMethod(codeSnippet));
        List<SensitiveWordConfigBo> bos = new Gson().fromJson(config, new TypeToken<List<SensitiveWordConfigBo>>() {
        }.getType());
        DesensitizeReq req = new DesensitizeReq();
        req.setText(codeSnippet);
        req.setSensitiveWordConfigBo(bos);
        System.out.println(NewCodeDesensitizeUtils.codeDesensitizeForClass(req));
    }

    @Test
    public void testClass() throws Exception {
        Pair<Boolean, String> rst =  CodeExtractorUtils.codeExtractor(MyTest.codeSnippet);
        if (rst.getKey()){
            String[] index = rst.getValue().split("-");
            String code = MyTest.codeSnippet.substring(Integer.parseInt(index[0]), Integer.parseInt(index[1])+1);
            List<SensitiveWordConfigBo> bos = new Gson().fromJson(config, new TypeToken<List<SensitiveWordConfigBo>>() {
            }.getType());
            DesensitizeReq req = new DesensitizeReq();
            req.setText(code);
            req.setSensitiveWordConfigBo(bos);
            System.out.println(CodeParseUtils.isClass(code));
            NewCodeDesensitizeUtils.codeDesensitizeForClass(req);
        }
    }
}
