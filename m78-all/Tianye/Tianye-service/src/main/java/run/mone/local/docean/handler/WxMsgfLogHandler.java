package run.mone.local.docean.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-02-29 16:25
 */
public class WxMsgfLogHandler extends AbstractHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        logger.info("\n接收到WeChat请求消息，内容：{}", gson.toJson(wxMessage));
        return null;
    }
}
