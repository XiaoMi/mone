package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import run.mone.local.docean.enums.ImEnum;
import run.mone.local.docean.service.ImContext;
import run.mone.local.docean.service.api.ImStrategy;
import run.mone.local.docean.service.tool.MessageService;
import run.mone.local.docean.util.MessageUtil;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author zhangping17
 */
@Slf4j
@Controller
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private ImContext imContext;

    @RequestMapping(path = "/webhook/event")
    public String query(Object body) {
        try {
            log.info("body:{}", MessageUtil.getGson().toJson(body));
            messageService.reply(body);
        } catch (Exception e) {
            log.error("webhook error", e);
        }
        return "ok";
    }

    @RequestMapping(path = "/webhook/event1")
    public Object query1(String body) {
        try {
            log.info("body:{}", MessageUtil.getGson().toJson(body));
            return imContext.replyMessage(body, "", getImType(body));
        } catch (Exception e) {
            log.error("webhook error", e);
        }
        return "ok";
    }

    private ImEnum getImType(String body) {
        if (isXml(body)) {
            return ImEnum.WEIXIN;
        }
        return ImEnum.FEISHU;
    }

    private boolean isXml(String body) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // 禁用DTD、禁止外部实体解析
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            InputStream is = new ByteArrayInputStream(body.getBytes());
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(is);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
