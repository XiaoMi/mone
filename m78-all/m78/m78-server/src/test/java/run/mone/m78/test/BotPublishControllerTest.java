package run.mone.m78.test;

import jnr.ffi.annotations.In;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.server.controller.BotController;
import run.mone.m78.service.dto.BotPublishDto;
import run.mone.m78.service.service.bot.BotService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class BotPublishControllerTest {

    @Resource
    private BotController botController;

    @Autowired
    private BotService botService;


    //{"botld":"100021","versionRecord":"","publishImChannel":["2"],"permissions":0}

    @Test
    public void testPublish() {
        BotPublishDto botPublishDto = new BotPublishDto();
        botPublishDto.setBotId(1l);
        botPublishDto.setPublisher("name");
        botPublishDto.setVersionRecord("1");
        botPublishDto.setOpenId("");
        botPublishDto.setPermissions(0);
        List<Integer> channels = new ArrayList<>();
        //channels.add(1);
        channels.add(2);
        botPublishDto.setPublishImChannel(channels);

        botService.publishBot("name", botPublishDto);
    }
}
