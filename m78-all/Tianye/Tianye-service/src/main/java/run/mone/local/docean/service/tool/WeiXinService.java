package run.mone.local.docean.service.tool;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import run.mone.local.docean.service.api.ImStrategy;

import javax.annotation.Resource;

@Slf4j
@Service(name = "weiXin")
public class WeiXinService implements ImStrategy {

    @Resource
    private WxMpService wxMpService;

    @Value("${wx.templateId}")
    private String wxTemplateId;

    @Resource
    private WxMpMessageRouter messageRouter;

    @Override
    public boolean sendMessage(String message, String toId) {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(toId)
                .templateId(wxTemplateId)
                .build();
        templateMessage.addData(new WxMpTemplateData("keyword", message));
        try {
            String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            log.info("msgId:{}", msgId);
            return true;
        } catch (WxErrorException e) {
            log.error("wx send msg error:", e);
            return false;
        }
    }

    @Override
    public String replyMessage(String body, String toId) {
        // 明文传输的消息
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(body.toString());
        WxMpXmlOutMessage outMessage = this.route(inMessage);
        if (outMessage == null) {
            return "";
        }
        return outMessage.toXml();
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }
        return null;

    }
}
