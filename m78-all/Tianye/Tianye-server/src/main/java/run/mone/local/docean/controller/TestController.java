package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import run.mone.local.docean.bo.PromptRequest;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.DoceanRpcClient;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.local.docean.service.tool.MessageService;
import run.mone.local.docean.service.ZService;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author zhangping17
 */
@Slf4j
@Controller
public class TestController {

    @Resource
    private ZService zService;

    @Resource
    private DoceanRpcClient client;

    @Resource
    private MessageService messageService;

    @Resource
    private WxMpService wxMpService;

    @Value("${wx.mp.config.appId}")
    private String wxAppid;


    @RequestMapping(path = "/testUserByToken", method = "get")
    public String testUserByToken() {
        try {
            return zService.getUserByToken("X");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error";
        }
    }

    @RequestMapping(path = "/testGetKnowledgeIdByUserName", method = "get")
    public Long testGetKnowledgeIdByUserName() {
        try {
            return zService.getKnowledgeIdByUserName("zhangzhiyong1", new HashMap<>());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @RequestMapping(path = "/testGetPromptIdByUserName", method = "get")
    public Long testGetPromptIdByUserName() {
        try {
            return zService.getPromptIdByUserName("zhangping17");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @RequestMapping(path = "/testGetPromptIdByUserNameAndLabel", method = "get")
    public Long testGetPromptIdByUserNameAndLabel() {
        try {
            return zService.getPromptIdByUserNameAndLabel("zhangping17", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @RequestMapping(path = "/health", method = "get")
    public String health() {
        return "ok";
    }


    //发送message
    @RequestMapping(path = "/message", method = "get")
    public String message() {
        AiResult res = client.req(TianyeCmd.messageReq, client.getServerAddr(), AiMessage.newBuilder().setCmd("message").build());
        return "ok:" + res;
    }

    //调用prompt(class)
    @RequestMapping(path = "/api/prompt", method = "post")
    public String prompt(PromptRequest promptRequest) {
        try {
            AiResult aiResult = client.req(TianyeCmd.messageReq, client.getServerAddr(), AiMessage.newBuilder().setCmd("prompt").build());
            log.info("{}", aiResult);
        } catch (Exception e) {
            log.error("Error occurred while processing prompt request: {}", e.getMessage(), e);
        }
        return "ok";
    }

    @RequestMapping(path = "/wx/msg", method = "get")
    public String wxMsgSend() {
        messageService.sendWxMsg("X", "hello world");
        return "success";
    }

    @RequestMapping(path = "/menu/create", method = "get")
    public String createMenu() {
        WxMenu wxMenu = new WxMenu();
        WxMenuButton button1 = new WxMenuButton();
        button1.setType(WxConsts.MenuButtonType.CLICK);
        button1.setName("呼叫关羽");
        button1.setKey("guanyu");
        wxMenu.getButtons().add(button1);
        try {
            wxMpService.getMenuService().menuCreate(wxMenu);
        } catch (WxErrorException e) {
            log.error("createMenu e:",e);
        }
        return "ok";
    }
}
