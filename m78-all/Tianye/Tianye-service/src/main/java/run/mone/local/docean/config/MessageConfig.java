package run.mone.local.docean.config;

import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import run.mone.local.docean.handler.MenuHandler;
import run.mone.local.docean.handler.WxMessageHandler;
import run.mone.local.docean.handler.WxMsgfLogHandler;

import javax.annotation.Resource;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.TEXT;


/**
 * @author caobaoyu
 * @description: 三方IM SDK配置
 * @date 2024-02-24 10:24
 */
@Configuration
public class MessageConfig {

    @Value("${wx.mp.config.appId}")
    private String appId;

    @Value("${wx.mp.config.secret}")
    private String secret;

    @Value("${wx.mp.config.token}")
    private String token;

    @Value("${wx.mp.config.aesKey}")
    private String aesKey;

    @Resource
    private WxMessageHandler wxMessageHandler;

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(this.appId);
        configStorage.setSecret(this.secret);
        configStorage.setToken(this.token);
        configStorage.setAesKey(this.aesKey);
        return configStorage;
    }

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpMessageRouter wxMpMessageRouter() {
        WxMpService wxMpService = wxMpService();
        WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        newRouter.rule().handler(new WxMsgfLogHandler()).next();
        newRouter.rule().async(false).msgType(TEXT).handler(new WxMessageHandler()).end();
        newRouter.rule().async(false).msgType(EVENT).handler(new MenuHandler()).end();
        return newRouter;
    }


}
