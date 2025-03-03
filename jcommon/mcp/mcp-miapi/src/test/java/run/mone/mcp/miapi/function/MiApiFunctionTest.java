package run.mone.mcp.miapi.function;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.miapi.bo.ReqParamsBo;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MiApiFunctionTest {

    @Test
    void getApiList() {
        MiApiFunction miApiFunction = new MiApiFunction();
        McpSchema.CallToolResult apiList = miApiFunction.getApiDetail("http", "测试");
        log.info("apiListMiApi: {}", new Gson().toJson(apiList));
    }

    @Test
    void getApiDetail() {
        MiApiFunction miApiFunction = new MiApiFunction();
        ReqParamsBo reqParamsBo = new ReqParamsBo();
        reqParamsBo.setProtocol(1);
        reqParamsBo.setPath("goods");
        String apiDetailMiApi = miApiFunction.getApiDetailMiApi(reqParamsBo);
        log.info("apiDetailMiApi: {}", new Gson().toJson(apiDetailMiApi));

    }
}