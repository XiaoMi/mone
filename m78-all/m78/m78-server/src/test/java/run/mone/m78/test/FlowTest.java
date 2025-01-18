package run.mone.m78.test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonPrimitive;
import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.flow.FlowInfo;
import run.mone.m78.api.bo.flow.FlowQryParam;
import run.mone.m78.api.bo.flow.FlowTestParam;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.bo.chat.ChatMessage;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dto.friend.ReqIMFriendDto;
import run.mone.m78.service.dto.friend.ReqIMFriendshipDto;
import run.mone.m78.service.service.flow.FlowDBService;
import run.mone.m78.service.service.friend.IMFriendService;
import run.mone.m78.service.service.flow.FlowService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class FlowTest {

    @Resource
    private FlowDBService flowDBService;

    @Resource
    private FlowService flowService;

    @Resource
    private IMFriendService imFriendService;

    @Test
    public void list() {
        String flow = "{ \"name\": \"test\", \"nodes\": [ { \"id\": 1, \"nodeType\": 1, \"nodeMetaInfo\": { \"nodeName\": \"start\", \"nodePosition\": { \"x\": \"1.1\", \"y\": \"1.1\" } }, \"inputs\": [ { \"name\": \"role\", \"type\": \"string\", \"desc\": \"desc\", \"required\":true } ] }, { \"id\": 3, \"nodeType\": 3, \"nodeMetaInfo\": { \"nodeName\": \"end\", \"nodePosition\": { \"x\": \"3.3\", \"y\": \"2.2\" } }, \"outputs\": [ { \"name\": \"duty\", \"type\": \"ref\", \"nodeId\": 2, \"paramName\": \"duty\" } ] }, { \"id\": 2, \"nodeType\": 2, \"nodeMetaInfo\": { \"nodeName\": \"LLM\", \"nodePosition\": { \"x\": \"2.2\", \"y\": \"2.2\" } }, \"inputs\": [ { \"name\": \"role\", \"type\": \"ref\", \"nodeId\": 1, \"paramName\": \"role\" } ], \"outputs\": [ { \"name\": \"duty\", \"type\": \"string\", \"desc\": \"desc\" } ], \"corSetting\": \"{\\\"gptModel\\\":\\\"gpt35_16\\\",\\\"promptContent\\\":\\\"你是一名${role}}，那么你负责干嘛呢\\\"}\" } ], \"edges\": [ { \"sourceNodeId\": \"1\", \"targetNodeId\": \"2\" }, { \"sourceNodeId\": \"2\", \"targetNodeId\": \"3\" } ] }";
        //System.out.println(flowService.createFlow(new Gson().fromJson(flow, FlowSettingInfo.class)));
        FlowQryParam flowQryParam = new FlowQryParam();
        flowQryParam.setWorkSpaceId(75L);
        flowQryParam.setNeedDetail(false);
        flowQryParam.setScale("mine");
        flowQryParam.setPageSize(1);
        flowQryParam.setPageNum(1);
        SessionAccount account = new SessionAccount();
        account.setUsername("name");
        account.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        Result<Page<FlowInfo>> rst = flowDBService.queryFlowList(flowQryParam, account);
        System.out.println(rst);
    }

    @Test
    public void generateTestInputsFromFlowBase() {
        System.out.println(flowService.generateTestInputsFromFlowBase(15, "1+2=?", "", "claude3"));
    }

    @Test
    public void testFlow() {
        FlowTestParam testParam = new FlowTestParam();
        testParam.setFlowId(15);
        testParam.setInputs(ImmutableMap.of("q",new JsonPrimitive("1+1=?")));
        testParam.setUserName("name");
        flowService.testFlow(testParam);
    }


    public static void main(String[] args) {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.compute("aaa", (k, v) -> {
            if(v == null) {
                return null;
            }
            return v;
        });
        System.out.println(map);

    }
}
