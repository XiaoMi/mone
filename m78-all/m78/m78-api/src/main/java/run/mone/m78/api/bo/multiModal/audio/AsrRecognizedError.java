package run.mone.m78.api.bo.multiModal.audio;

public enum AsrRecognizedError {
    ASR_ESTABLISH_FAILED(700), // 连接失败
    ASR_RECOGNIZED_ERROR(701), // 识别错误
    ASR_HANDSHAKE_CONN_LIMIT(702), // 连接数超限

    ASR_SEND_AUDIO_DATA_ERROR(703), // 发送音频数据错误
    ASR_AUTH_CHECK_FAILED(704), // 鉴权失败
    ASR_PARAMS_ERROR(705),// 参数错误

	ASR_OFFLINE_SUBMIT_ERROR(710),

	ASR_OFFLINE_TASK_WAITING(720),
	ASR_OFFLINE_TASK_RUNNING(721),
	ASR_OFFLINE_TASK_FAILED(722);


	private Integer code;

    AsrRecognizedError(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
