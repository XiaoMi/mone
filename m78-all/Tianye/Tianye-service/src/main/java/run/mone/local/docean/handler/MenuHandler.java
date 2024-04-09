package run.mone.local.docean.handler;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.Map;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-02-29 16:12
 */
@Slf4j
public class MenuHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        if ("guanyu".equals(wxMpXmlMessage.getEventKey())) {
            return WxMpXmlOutMessage.TEXT().content("您好，我是关羽1号，您的OpenId为：" + wxMpXmlMessage.getFromUser())
                    .fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                    .build();
        }
        return null;
    }
}
