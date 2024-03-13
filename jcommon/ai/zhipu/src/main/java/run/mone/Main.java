package run.mone;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/10 07:55
 */
public class Main {

    private static final String requestIdTemplate = "myoz-%d";


    private static final String KEY = System.getenv("zhipu");

    private static final String API_KEY = KEY.split("\\.")[0];

    private static final String API_SECRET = KEY.split("\\.")[1];

    private static final ClientV4 client = new ClientV4.Builder(API_KEY, API_SECRET).build();


    @SneakyThrows
    public static void main(String[] args) {
        List<ChatMessage> messages = new ArrayList<>();

        String promptName = "prompt.txt";

        String content = Files.readString(Paths.get("/Users/zhangzhiyong/IdeaProjects/goodjava/mone/jcommon/ai/zhipu/src/main/resources/" + promptName));

        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), content);
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        String resContent = invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        System.out.println(resContent);
//        System.out.println("model output:"+ new Gson().toJson(invokeModelApiResp));
    }
}