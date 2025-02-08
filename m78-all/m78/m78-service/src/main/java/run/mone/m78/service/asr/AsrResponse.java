package run.mone.m78.service.asr;

import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;

/**
 * @author liuchuankang
 * @Type AsrResponse.java
 * @Desc
 * @date 2024/9/26 15:40
 */
public class AsrResponse {
	public static void sendMessageBySessionId(String sessionId, Integer code, String message, String messageType) {
		String result = AsrRecognizedRes.generateCodeMsg(code, message);
		WsSessionHolder.INSTANCE.sendMsgBySessionId(sessionId, result, messageType);
	}

	public static void sendRecognizedDataBySessionId(String sessionId, AsrRecognizedRes.AsrRecognizedData data) {
		String result = AsrRecognizedRes.generateRecognizedData(data);
		WsSessionHolder.INSTANCE.sendMsgBySessionId(sessionId, result, WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_RESULT);
	}
}
