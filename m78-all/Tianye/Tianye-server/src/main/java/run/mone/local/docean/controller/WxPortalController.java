package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : caobaoyu
 * @className : WxUserController
 * @date: 2024/02/26
 * @description: 微信服务器消息解析接口
 */
@Slf4j
@Controller
public class WxPortalController {
    @Resource
    private WxMpService wxService;

    @Resource
    private WxMpMessageRouter messageRouter;

    // 这个是用来做Url配置的
    @RequestMapping(path = "/webhook/event1", method = "get")
    public String authGet(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr") String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "false";
    }


    @RequestMapping(path = "/auth")
    public String post(String string) {
        System.out.println(string);
        // 明文传输的消息
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(string);
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
