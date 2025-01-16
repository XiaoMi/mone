package run.mone.m78.service.asr.xiaoai;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedError;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.asr.AsrResponse;
import run.mone.m78.service.asr.xiaoai.DTO.AudioMessageReqDTO;
import run.mone.m78.service.asr.xiaoai.DTO.AudioMessageRespDTO;
import run.mone.m78.service.asr.xiaoai.DTO.XiaoAiWebSocketDTO;
import run.mone.m78.service.asr.xiaoai.DTO.XiaoaiDomainEnum;
import run.mone.m78.service.asr.xiaoai.client.WebSocketClient;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static run.mone.m78.service.asr.xiaoai.client.WebSocketClient.finishPayload;
import static run.mone.m78.service.asr.xiaoai.client.WebSocketClient.payload;


/**
 * @author liuchuankang
 * @Type XiaoAiService.java
 * @Desc
 * @date 2024/8/27 11:31
 */
@Slf4j
@Service
public class XiaoAiService {
	public static ConcurrentHashMap<String, WebSocketSession> M78_WEB_SOCKET_SESSION_MAP = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, XiaoAiWebSocketDTO> XIAOAI_WEB_SOCKET_CLIENT_MAP = new ConcurrentHashMap<>();
	public static AttributeKey<String> XIAOAI_SERVER_CHANNEL_DTO = AttributeKey.valueOf("XIAOAI_SERVER_CHANNEL_DTO");

	private WebSocketClient webSocketClient;

	/**
	 * 初始化方法，用于创建并启动WebSocket客户端
	 *
	 * @throws Exception 启动WebSocket客户端时可能抛出的异常
	 */
	@PostConstruct
	@SneakyThrows
	public void init() {
		webSocketClient = new WebSocketClient(XiaoaiDomainEnum.PRO.getDomain() + "/speech/asr/v1/general");
		webSocketClient.start();
	}

	/**
	 * 建立与WebSocket的连接
	 *
	 * @param session WebSocket会话对象
	 * @throws RuntimeException 如果在连接过程中发生中断异常
	 */
	public void doConnect(WebSocketSession session) {

		Channel xiaoaiChannel = null;
		try {
			xiaoaiChannel = webSocketClient.doConnect();
			XiaoAiWebSocketDTO xiaoAiWebSocketDTO = getXiaoAiWebSocketDTO(session, xiaoaiChannel);
			M78_WEB_SOCKET_SESSION_MAP.put(session.getId(), session);
			XIAOAI_WEB_SOCKET_CLIENT_MAP.put(session.getId(), xiaoAiWebSocketDTO);
			xiaoaiChannel.attr(XIAOAI_SERVER_CHANNEL_DTO).set(session.getId());
			log.warn("sessionId={},XiaoAiService xiaoaiChannel.active={}", session.getId(), xiaoaiChannel.isActive());
		} catch (InterruptedException e) {
			log.error("与xiaoai建立链接异常，sessionId={}", session.getId());
			throw new RuntimeException(e);
		}
	}

	private static XiaoAiWebSocketDTO getXiaoAiWebSocketDTO(WebSocketSession session, Channel xiaoaiChannel) {
		XiaoAiWebSocketDTO xiaoAiWebSocketDTO = new XiaoAiWebSocketDTO();
		xiaoAiWebSocketDTO.setChannel(xiaoaiChannel);
		xiaoAiWebSocketDTO.setSessionId(session.getId());
		return xiaoAiWebSocketDTO;
	}

	/**
	 * 发送音频数据给小爱
	 *
	 * @param session            WebSocket会话
	 * @param xiaoAiWebSocketDTO 小爱WebSocket数据传输对象
	 * @param audioData          要发送的音频数据
	 *                           <p>
	 *                           该方法首先检查客户端和小爱的连接状态，如果客户端已提交结束状态或小爱链接已关闭，则不进行处理。
	 *                           如果连接正常，则将音频数据转发给小爱，并在发送完成后记录发送结果。
	 *                           在发送过程中如果发生异常，将构建错误响应并通过WebSocket会话发送错误信息。
	 */
	public void sendAudio(WebSocketSession session, XiaoAiWebSocketDTO xiaoAiWebSocketDTO, byte[] audioData) {
		if (xiaoAiWebSocketDTO.isClientFinish()) {
			log.warn("sessionId={},client已提交结束状态，不处理", xiaoAiWebSocketDTO.getSessionId());
			return;
		}
		if (xiaoAiWebSocketDTO.isXiaoaiFinish()) {
			log.error("sessionId={},与小爱链接已关闭不发送消息", xiaoAiWebSocketDTO.getSessionId());
			return;
		}
		log.warn("sessionId={},--------转发给小爱数据-----", xiaoAiWebSocketDTO.getSessionId());
		try {
			xiaoAiWebSocketDTO.getChannel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(audioData, 0, audioData.length))).addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture futureListener) throws Exception {
					log.warn("futureListener={}", JSON.toJSONString(futureListener));
					if (futureListener.isSuccess()) {
						log.warn("sessionId={},sendAudio转发音频成功 ", xiaoAiWebSocketDTO.getSessionId());
						//                            xiaoAiWebSocketDTO.getChannel().attr(XIAOAI_CHANNEL).set(xiaoAiWebSocketDTO);
					} else {
						log.error("sessionId={},sendAudio转发音频失败", xiaoAiWebSocketDTO.getSessionId());
					}
				}

			});
		} catch (Exception e) {
			AsrRecognizedRes res = AsrRecognizedRes.builder().code(AsrRecognizedError.ASR_SEND_AUDIO_DATA_ERROR.getCode()).message("fail").build();
			WsSessionHolder.INSTANCE.sendMsgBySessionId(session.getId(), JSON.toJSONString(res), WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_RESULT, "");
			log.error("sessionId={},sendAudio 异常", xiaoAiWebSocketDTO.getSessionId(), e);
		}
	}

	/**
	 * 发送音频数据通过WebSocket连接
	 *
	 * @param session            WebSocket会话对象
	 * @param xiaoAiWebSocketDTO 包含音频数据和会话信息的DTO对象
	 *                           <p>
	 *                           该方法从指定的音频文件中读取数据，并通过WebSocket通道发送音频数据。
	 *                           在发送过程中，若通道处于活动状态，则持续读取音频数据并发送，直到文件读取完毕或通道不再活跃。
	 *                           发送完成后，会记录发送成功或失败的日志信息。
	 */
	public void sendTest(WebSocketSession session, XiaoAiWebSocketDTO xiaoAiWebSocketDTO) {
		//读取音频，可根据实际情况更改逻辑
		InputStream input = WebSocketClient.class.getClassLoader().getResourceAsStream("audio-1.pcm");
		int length = 0;
		byte[] buffer = new byte[10240];
		while (true && xiaoAiWebSocketDTO.getChannel() != null && xiaoAiWebSocketDTO.getChannel().isActive()
		) {
			try {
				if (!((length = input.read(buffer)) != -1)) break;
				xiaoAiWebSocketDTO.getChannel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(buffer, 0, length))).addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture futureListener) throws Exception {
						log.warn("sendTest futureListener={}", JSON.toJSONString(futureListener));
						if (futureListener.isSuccess()) {
							log.warn("sessionId={},sendTest sendTest转发音频成功", xiaoAiWebSocketDTO.getSessionId());
							//                            xiaoAiWebSocketDTO.getChannel().attr(XIAOAI_CHANNEL).set(xiaoAiWebSocketDTO);
						} else {
							log.error("sessionId={},sendTest sendTest转发音频失败", xiaoAiWebSocketDTO.getSessionId());
						}
					}

				});
				Thread.sleep(32);
			} catch (Exception e) {
				log.error("sessionId={},sendTest 异常", xiaoAiWebSocketDTO.getSessionId());
			}
		}

	}

	/**
	 * 启动与小爱同学的WebSocket连接
	 *
	 * @param xiaoAiWebSocketDTO 包含WebSocket连接信息的DTO对象
	 *                           <p>
	 *                           该方法会尝试在50次循环内等待WebSocket握手完成，若握手成功，则发送初始化消息。
	 *                           如果发送成功，将允许发送数据；否则记录错误信息。
	 */
	public static void start(XiaoAiWebSocketDTO xiaoAiWebSocketDTO) {
		int count = 0;
		while (count < 50 && !xiaoAiWebSocketDTO.isHandshakeComplate()) {
			try {
				count++;
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				log.error("sessionId={},start 异常", xiaoAiWebSocketDTO.getSessionId());
			}
		}
		String payload = payload();
		TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(payload);
		xiaoAiWebSocketDTO.getChannel().writeAndFlush(textWebSocketFrame).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) {
				log.warn("sessionId={},xiaoaiChannel payload futureListener={}", xiaoAiWebSocketDTO.getSessionId(), JSON.toJSONString(futureListener));
				if (futureListener.isSuccess()) {
					xiaoAiWebSocketDTO.setCanSend(true);
					AsrResponse.sendMessageBySessionId(xiaoAiWebSocketDTO.getSessionId(), 0, "establish with asr websocket success", WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_START);
					log.warn("startSendToXiaoAiStart 可以开始发送数据，xiaoAiWebSocketDTO={}", JSON.toJSONString(xiaoAiWebSocketDTO));
				} else {
					log.error("startSendToXiaoAiStart 发送数据状态失败，xiaoAiWebSocketDTO={}", JSON.toJSONString(xiaoAiWebSocketDTO));
				}
			}

		});

	}

	/**
	 * 处理WebSocket会话的结束逻辑
	 *
	 * @param session            当前的WebSocket会话
	 * @param xiaoAiWebSocketDTO 包含小爱WebSocket相关信息的DTO对象
	 */

	public void finish(WebSocketSession session, XiaoAiWebSocketDTO xiaoAiWebSocketDTO) {
		xiaoAiWebSocketDTO.setClientFinish(true);
		if (!xiaoAiWebSocketDTO.isXiaoaiFinish()) {
			String finishPayload = finishPayload();
			log.warn("sessionId={},finish 给小爱发送结束标识finishPayload={}", xiaoAiWebSocketDTO.getSessionId(), JSON.toJSONString(finishPayload));
			xiaoAiWebSocketDTO.getChannel().writeAndFlush(new TextWebSocketFrame(finishPayload)).addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture futureListener) {
					log.info("xiaoaiChannel finish futureListener={}", JSON.toJSONString(futureListener));
					if (futureListener.isSuccess()) {
						xiaoAiWebSocketDTO.setCanSend(false);
						log.warn("XiaoAiService finish 结束数据发送成功，xiaoAiWebSocketDTO={}", JSON.toJSONString(xiaoAiWebSocketDTO));
					} else {
						log.error("XiaoAiService finish 结束数据发送异常，xiaoAiWebSocketDTO={}", JSON.toJSONString(xiaoAiWebSocketDTO));
					}
				}

			});
		}
	}

	/**
	 * 关闭WebSocket连接并清理相关数据
	 *
	 * @param session WebSocket会话对象
	 * @return 无返回值
	 */
	public void close(WebSocketSession session) {
		log.warn("webSocket断开连接,sessionId={}", session.getId());
		try {
			XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(session.getId());
			if (xiaoAiWebSocketDTO == null) {
				log.warn("sessionId={},xiaoAiWebSocketDTO 未找到连接，不处理", session.getId());
				return;
			}
			finish(session, xiaoAiWebSocketDTO);
			int count = 0;
			while (count < 100 && !xiaoAiWebSocketDTO.isXiaoaiFinish()) {
				try {
					count++;
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					log.error("sessionId={},close结果异常", session.getId());
				}
			}
			log.warn("sessionId={},webSocket断开连接清理数据，xiaoAiWebSocketDTO={}", session.getId(), JSON.toJSONString(xiaoAiWebSocketDTO));
		} catch (Throwable e) {
			log.error("sessionId={},close结果异常", session.getId(), e);
		}
		log.warn("sessionId={},webSocket断开连接清理map", session.getId());
		XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.remove(session.getId());
		XiaoAiService.M78_WEB_SOCKET_SESSION_MAP.remove(session.getId());

		// 连接关闭后的操作
		WsSessionHolder.INSTANCE.clearSessionBySessionId(session.getId());
	}

	/**
	 * 处理WebSocket消息
	 *
	 * @param session 当前的WebSocket会话
	 * @param message 接收到的文本消息
	 *                <p>
	 *                该方法根据接收到的消息类型进行不同的处理：
	 *                1. 如果消息类型为MULTI_MODAL_AUDIO_BASE64，则解码音频数据并发送音频。
	 *                2. 如果消息类型为MULTI_MODAL_AUDIO_END，则结束当前音频会话。
	 *                <p>
	 *                如果未找到对应的WebSocket连接，则记录错误日志。
	 */
	public void handler(WebSocketSession session, TextMessage message) {
		XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(session.getId());
		if (xiaoAiWebSocketDTO == null) {
			log.error("sessionId={},xiaoAiWebSocketDTO 未找到连接，不处理", session.getId());
		}
		String payload = message.getPayload();
		log.info("sessionId={},处理消息 payload={}", session.getId(), payload);
		AudioMessageReqDTO audioMessageDTO = JSON.parseObject(payload, AudioMessageReqDTO.class);
		if (audioMessageDTO.getType().equals(WebsocketMessageType.MULTI_MODAL_AUDIO_BASE64)) {
			sendAudio(session, xiaoAiWebSocketDTO, Base64.getDecoder().decode(audioMessageDTO.getData()));
		} else if (audioMessageDTO.getType().equals(WebsocketMessageType.MULTI_MODAL_AUDIO_END)) {
			finish(session, xiaoAiWebSocketDTO);
		}
	}

	/**
	 * 处理并发送二进制消息
	 *
	 * @param session WebSocket会话对象
	 * @param message 二进制消息对象
	 * @return 无返回值
	 * <p>
	 * 该方法从WebSocket会话中获取对应的XiaoAiWebSocketDTO对象，如果未找到则记录错误日志。
	 * 然后从BinaryMessage中提取音频数据，并记录接收到的消息长度。
	 * 最后调用sendAudio方法将音频数据发送到指定的会话。
	 */
	public void writeBinaryMessage(WebSocketSession session, BinaryMessage message) {
		XiaoAiWebSocketDTO xiaoAiWebSocketDTO = XiaoAiService.XIAOAI_WEB_SOCKET_CLIENT_MAP.get(session.getId());
		if (xiaoAiWebSocketDTO == null) {
			log.error("sessionId={},xiaoAiWebSocketDTO 未找到连接，不处理", session.getId());
		}
		ByteBuffer payload = message.getPayload();
		byte[] audioData = new byte[payload.remaining()];
		payload.get(audioData);
		log.info("get binaray message session_id: {}, length: {}", session.getId(), audioData.length);
		sendAudio(session, xiaoAiWebSocketDTO, audioData);
	}

}
