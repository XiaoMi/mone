package run.mone.local.docean.handler;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import run.mone.local.docean.service.IMRecordService;
import run.mone.m78.api.IMRecordProvider;
import run.mone.m78.api.bo.im.ExecuteBotReqDTO;
import run.mone.m78.api.bo.im.HasBotReqDTO;
import run.mone.m78.api.bo.im.IMRecordDTO;
import run.mone.m78.api.bo.im.M78IMRelationDTO;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-02-26 11:03
 */
@Slf4j
public class WxMessageHandler extends AbstractHandler {


    private static Gson gson = new Gson();

    // todo: 加定时任务，在一定时间内没有下一条消息就清空
    private Map<String, List<Message>> msgHistory = new ConcurrentHashMap<>();

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) {
        pool.submit(() -> {
            String msg = null;
            if ("呼叫关羽".equals(wxMpXmlMessage.getContent())) {
                msg = "您的openId:" + wxMpXmlMessage.getFromUser();
            } else {
                String regex = "-?\\d+";
                IMRecordProvider imRecordProvider = ((IMRecordService) Ioc.ins().getBean(IMRecordService.class)).getIMRecordProvider();
                HasBotReqDTO req = new HasBotReqDTO();
                req.setUser(wxMpXmlMessage.getFromUser());
                req.setImType(2);
                List<M78IMRelationDTO> imRelationDTOS = imRecordProvider.hasBot(req);
                if (imRelationDTOS != null && imRelationDTOS.size() > 0) {
                    //有发布的bot
                    IMRecordDTO imRecord = new IMRecordDTO();
                    imRecord.setUserName(wxMpXmlMessage.getFromUser());
                    imRecord.setImTypeId(2);
                    imRecord.setStatus(0);
                    IMRecordDTO getRecord = imRecordProvider.get(imRecord);
                    if (getRecord != null) {
                        //有使用中的bot，调用bot
                        log.info("imRecord:{}", imRecord);
                        if ("结束本次会话".equals(wxMpXmlMessage.getContent())) {
                            IMRecordDTO deleteRecord = new IMRecordDTO();
                            deleteRecord.setUserName(getRecord.getUserName());
                            deleteRecord.setImTypeId(2);
                            deleteRecord.setStatus(0);
                            imRecordProvider.delete(imRecord);
                            msg = "已为您结束本次会话，欢迎下次使用";
                        } else {
                            //调用bot
                            ExecuteBotReqDTO reqDTO = new ExecuteBotReqDTO();
                            reqDTO.setUsername(getRecord.getUserName());
                            reqDTO.setBotId(getRecord.getBotId().longValue());
                            reqDTO.setInput(wxMpXmlMessage.getContent());
                            reqDTO.setTopicId("1");
                            msg = imRecordProvider.executeBot(reqDTO);
                        }

                    } else {
                        //没有使用中的bot
                        // 判断字符串是否匹配整数的正则表达式
                        if (Pattern.matches(regex, wxMpXmlMessage.getContent())) {
                            IMRecordDTO addIMRecord = new IMRecordDTO();
                            addIMRecord.setBotId(new BigInteger(wxMpXmlMessage.getContent()));
                            addIMRecord.setUserName((wxMpXmlMessage.getFromUser()));
                            addIMRecord.setImTypeId(2);
                            addIMRecord.setStatus(0);
                            if (imRecordProvider.add(addIMRecord)) {
                                msg = "请输入问题";
                            } else {
                                StringBuffer content = new StringBuffer();
                                for (int i = 0; i < imRelationDTOS.size(); i++) {
                                    content.append("\n [" + imRelationDTOS.get(i).getBotId() + "]" + imRelationDTOS.get(i).getBotName() + ";");
                                }
                                msg = "欢迎您使用本公众号，提供的机器人有:" + content.toString() + "\n请回复数字";
                            }
                        } else {
                            StringBuffer content = new StringBuffer();
                            for (int i = 0; i < imRelationDTOS.size(); i++) {
                                content.append("\n [" + imRelationDTOS.get(i).getBotId() + "]" + imRelationDTOS.get(i).getBotName() + ";");
                            }
                            msg = "欢迎您使用本公众号，提供的机器人有:" + content.toString() + "\n请回复数字";
                        }
                    }
                } else {
                    //没有发布过的bot
                    msg = "请先创建bot";
                }
            }

            WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(wxMpXmlMessage.getFromUser()).content(msg).build();
            try {
                //TODO 调用bot执行
                wxMpService.getKefuService().sendKefuMessage(message);
            } catch (WxErrorException e) {
                logger.error("wx send kfMsg error:", e);
            }
        });

        return null;
    }

    private String postProxy(String param) {
        Map<String, Object> body = new HashMap<>();
        body.put("promptName", "wx_msg");
        body.put("model", "gpt-4-1106-Preview-2");
        body.put("stream", false);
        body.put("zzToken", "X");
        body.put("type", 0);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("content", param);
        body.put("paramMap", paramMap);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        return HttpClientV5.post("http://127.0.0.1/api/z-proxy/ask", gson.toJson(body), header, 100000);
    }

}
