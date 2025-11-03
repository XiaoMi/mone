package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;
import run.mone.mcp.multimodal.service.GuiAgentService;

import javax.annotation.PostConstruct;

/**
 * Example application showing how to use the GUI agent functions
 * Run with VM argument: -Dspring.profiles.active=gui-agent-example
 */
@RequiredArgsConstructor
//@Service
public class GuiAgentExample {


    private final GuiAgentService guiAgentService;

    private final GuiAgent guiAgent;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() throws Exception {
        UnicastProcessor<String> processor = UnicastProcessor.create();
        FluxSink<String> sink = processor.sink();
//        String instruction = "把鼠标移动到mvn插件上(在右上角 thx)";
//        String instruction = "点击下屏幕右上角的小机器人(在右上角 thx)";
        String instruction = "点击下屏幕右上角的搜索<向放大镜的那个图标>(在右上角 thx)";
        guiAgent.run(instruction, sink);
    }
}