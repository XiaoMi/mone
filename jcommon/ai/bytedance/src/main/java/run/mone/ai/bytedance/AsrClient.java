package run.mone.ai.bytedance;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class AsrParams {
    private App app;
    private User user;
    private Request request;
    private Audio audio;

    public AsrParams(App app, User user, Request request, Audio audio) {
        this.app = app;
        this.user = user;
        this.request = request;
        this.audio = audio;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public static class App {
        private String appid;
        private String cluster;
        private String token;

        public App(String appid, String cluster, String token) {
            this.appid = appid;
            this.cluster = cluster;
            this.token = token;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getCluster() {
            return cluster;
        }

        public void setCluster(String cluster) {
            this.cluster = cluster;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class User {
        private String uid;

        public User(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

    public static class Request {
        private String reqid;
        private String workflow;
        private int nbest;
        private boolean show_utterances;
        private String result_type;
        private int sequence;

        public Request(String reqid, String workflow, int nbest, boolean show_utterances, String result_type, int sequence) {
            this.reqid = reqid;
            this.workflow = workflow;
            this.nbest = nbest;
            this.show_utterances = show_utterances;
            this.result_type = result_type;
            this.sequence = sequence;
        }

        public String getReqid() {
            return reqid;
        }

        public void setReqid(String reqid) {
            this.reqid = reqid;
        }

        public String getWorkflow() {
            return workflow;
        }

        public void setWorkflow(String workflow) {
            this.workflow = workflow;
        }

        public int getNbest() {
            return nbest;
        }

        public void setNbest(int nbest) {
            this.nbest = nbest;
        }

        public boolean isShow_utterances() {
            return show_utterances;
        }

        public void setShow_utterances(boolean show_utterances) {
            this.show_utterances = show_utterances;
        }

        public String getResult_type() {
            return result_type;
        }

        public void setResult_type(String result_type) {
            this.result_type = result_type;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }
    }

    public static class Audio {
        private String format;
        private String codec;
        private int rate;
        private int bits;
        private int channels;

        public Audio(String format, String codec, int rate, int bits, int channels) {
            this.format = format;
            this.codec = codec;
            this.rate = rate;
            this.bits = bits;
            this.channels = channels;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getCodec() {
            return codec;
        }

        public void setCodec(String codec) {
            this.codec = codec;
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public int getBits() {
            return bits;
        }

        public void setBits(int bits) {
            this.bits = bits;
        }

        public int getChannels() {
            return channels;
        }

        public void setChannels(int channels) {
            this.channels = channels;
        }
    }
}

class AsrResponse {
    private String reqid = "unknow";
    private int code = 0;
    private String message = "";
    private int sequence = 0;
    private Result[] result;
    private Addition addition;

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Result[] getResult() {
        return result;
    }

    public void setResult(Result[] result) {
        this.result = result;
    }

    public Addition getAddition() {
        return addition;
    }

    public void setAddition(Addition addition) {
        this.addition = addition;
    }

    public static class Result {
        private String text;
        private int confidence;
        private String language;
        private Utterances[] utterances;
        private float global_confidence;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Utterances[] getUtterances() {
            return utterances;
        }

        public void setUtterances(Utterances[] utterances) {
            this.utterances = utterances;
        }

        public float getGlobal_confidence() {
            return global_confidence;
        }

        public void setGlobal_confidence(float global_confidence) {
            this.global_confidence = global_confidence;
        }
    }

    public static class Utterances {
        private String text;
        private int start_time;
        private int end_time;
        private boolean definite;
        private String language;
        private Words[] words;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getStart_time() {
            return start_time;
        }

        public void setStart_time(int start_time) {
            this.start_time = start_time;
        }

        public int getEnd_time() {
            return end_time;
        }

        public void setEnd_time(int end_time) {
            this.end_time = end_time;
        }

        public boolean isDefinite() {
            return definite;
        }

        public void setDefinite(boolean definite) {
            this.definite = definite;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Words[] getWords() {
            return words;
        }

        public void setWords(Words[] words) {
            this.words = words;
        }
    }

    public static class Words {
        private String text;
        private int start_time;
        private int end_time;
        private int blank_duration;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getStart_time() {
            return start_time;
        }

        public void setStart_time(int start_time) {
            this.start_time = start_time;
        }

        public int getEnd_time() {
            return end_time;
        }

        public void setEnd_time(int end_time) {
            this.end_time = end_time;
        }

        public int getBlank_duration() {
            return blank_duration;
        }

        public void setBlank_duration(int blank_duration) {
            this.blank_duration = blank_duration;
        }
    }

    public static class Addition {
        private String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}

public class AsrClient extends WebSocketClient {
    private static final String URL = "wss://openspeech.bytedance.com/api/v2/asr";
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
    private String appid;
    private String token;
    private String sk;
    private String cluster;
    private String workflow = "audio_in,resample,partition,vad,fe,decode";
    private String uid = "user_id";
    private int nhest = 1;
    private boolean show_utterances = true;
    private String result_type = "full";
    private String format = "wav";
    private String codec = "raw";
    private int sample_rate = 16000;
    private int channels = 1;
    private int bits = 16;
    private AuthType authType = AuthType.TOKEN;
    private byte[] params_msg = null;
    private AsrResponse asr_response;
    private CountDownLatch recv_latch = null;
    private int recv_timeout = 5;
    private boolean recv_suc = true;

    static AsrClient build() throws URISyntaxException {
        URI uri = new URI(URL);
        return new AsrClient(uri);
    }

    // TODO 接受一个 listener 监听消息, onOpen, onMessage, onError, onComplete
    private AsrClient(URI uri) {
        super(uri);
    }

    public class ProtocolVersion {
        static public int PROTOCOL_VERSION = 0b0001;
    }

    public class MessageType {
        static public int FULL_CLIENT_REQUEST = 0b0001;
        static public int AUDIO_ONLY_CLIENT_REQUEST = 0b0010;
        static public int FULL_SERVER_RESPONSE = 0b1001;
        static public int SERVER_ACK = 0b1011;
        static public int ERROR_MESSAGE_FROM_SERVER = 0b1111;
    }

    public class MessageTypeFlag {
        static public int NO_SEQUENCE_NUMBER = 0b0000;
        static public int POSITIVE_SEQUENCE_CLIENT_ASSGIN = 0b0001;
        static public int NEGATIVE_SEQUENCE_SERVER_ASSGIN = 0b0010;
        static public int NEGATIVE_SEQUENCE_CLIENT_ASSGIN = 0b0011;
    }

    public class MessageSerial {
        static public int NO_SERIAL = 0b0000;
        static public int JSON = 0b0001;
        static public int CUSTOM_SERIAL = 0b1111;
    }

    public class MessageCompress {
        static public int NO_COMPRESS = 0b0000;
        static public int GZIP = 0b0001;
        static public int CUSTOM_COMPRESS = 0b1111;
    }

    public enum AuthType {
        TOKEN,
        SIGNATURE;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("asr client onOpen");
    }

    @Override
    public void onMessage(String s) {
        logger.info("onMessage String, should be onMessage(ByteBuffer) called");
//        try {
//            if (parse_response(s) != 0) {
//                logger.error("error happends to close connection");
//                close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            if (parse_response(bytes) != 0) {
                recv_suc = false;
                logger.error("error happends to close connection");
                close();
            }
            recv_latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info("asr onClose {}, {}, {}", i, s, b);
    }

    @Override
    public void onError(Exception e) {
        logger.info("asr onError {}", e.getMessage());
        recv_suc = false;
        recv_latch.countDown();
        this.close();
    }

//    public int asr_connect() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//        this.params_msg = construct_param();
//        set_auth_header();
//        this.connect();
//        return 0;
//    }

    public boolean asr_sync_connect() throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException {
        this.params_msg = construct_param();
        set_auth_header();
        boolean ret = this.connectBlocking();
        if (!ret) {
            return ret;
        }
        recv_latch = new CountDownLatch(1);
        this.send(this.params_msg);
        ret = recv_latch.await(recv_timeout, TimeUnit.SECONDS);
        return ret && recv_suc;
    }

    public AsrResponse asr_send(byte[] audio, boolean is_last) throws IOException, InterruptedException {
        recv_latch = new CountDownLatch(1);
        byte[] payload = construct_audio_payload(audio, is_last);
        this.send(payload);
        boolean ret = recv_latch.await(recv_timeout, TimeUnit.SECONDS);
        if (!ret) {
            logger.error("recv message timeout");
            this.close();
            return new AsrResponse();
        }
        return asr_response;
    }

    public int asr_close() {
        this.close();
        return 0;
    }

    private void set_auth_header() throws NoSuchAlgorithmException, InvalidKeyException {
        if (authType == AuthType.TOKEN) {
            this.addHeader("Authorization", "Bearer; " + token);
            return;
        }

        String custom_header = "Custom";
        String custom_cont = "auth_custom";
        this.addHeader(custom_header, custom_cont);

        String str = "GET " + getURI().getPath() + " HTTP/1.1\n"
                + custom_cont + "\n";
        byte[] str_byte = str.getBytes(StandardCharsets.UTF_8);
        byte[] data = concat_byte(str_byte, this.params_msg);

        byte[] sk_byte = this.sk.getBytes(StandardCharsets.UTF_8);
        String HMAC_SHA256 = "HmacSHA256";
        Mac sha256Hmac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(sk_byte, HMAC_SHA256);
        sha256Hmac.init(keySpec);
        byte[] mac_data = sha256Hmac.doFinal(data);

        String base64_data = Base64.getUrlEncoder().encodeToString(mac_data);
        String auth_cont = "HMAC256; access_token=\"" + this.token
                + "\"; mac=\"" + base64_data
                + "\"; h=\"" + custom_header + "\"";
        this.addHeader("Authorization", auth_cont);
    }

    private byte[] gzip_compress(byte[] content) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(content.length);
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(content);
        gzip.close();
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    private byte[] gzip_decompress(byte[] content) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(content);
        GZIPInputStream gzip = new GZIPInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = gzip.read(buff, 0, buff.length)) > 0) {
            out.write(buff, 0, len);
        }
        byte[] result = out.toByteArray();
        in.close();
        gzip.close();
        out.close();
        return result;
    }

    private byte[] construct_param() throws IOException {
        int header_len = 4;
        byte[] header = new byte[header_len];
        header[0] = (byte) (ProtocolVersion.PROTOCOL_VERSION << 4 | (header_len >> 2));
        header[1] = (byte) (MessageType.FULL_CLIENT_REQUEST << 4 | MessageTypeFlag.NO_SEQUENCE_NUMBER);
         header[2] = (byte) (MessageSerial.JSON << 4 | MessageCompress.GZIP);
        header[3] = 0;

        String reqid = UUID.randomUUID().toString();
        AsrParams.App app = new AsrParams.App(appid, cluster, token);
        AsrParams.User user = new AsrParams.User(uid);
        AsrParams.Request request = new AsrParams.Request(reqid, workflow, 1, show_utterances, result_type, 1);
        AsrParams.Audio audio = new AsrParams.Audio(format, codec, sample_rate, bits, channels);
        AsrParams asr_params = new AsrParams(app, user, request, audio);
        ObjectMapper mapper = new ObjectMapper();
//        String params_json = mapper.writeValueAsString(asr_params);
        byte[] payload = mapper.writeValueAsBytes(asr_params);
        logger.info("params_json {}", new String(payload));
        payload = gzip_compress(payload);

        // java big-endian default
        int payload_len = payload.length;
        ByteBuffer bb = ByteBuffer.allocate(4);
        //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        bb.putInt(payload_len);
        byte[] pl_byte = bb.array();

        return concat_byte(header, pl_byte, payload);
    }

    private int parse_response(ByteBuffer msg) throws IOException {
        byte[] msg_byte = msg.array();
        int header_len = (msg_byte[0] & 0x0f) << 2;
        int message_type = (msg_byte[1] & 0xf0) >> 4;
        int message_type_flag = msg_byte[1] & 0x0f;
        int message_serial = (msg_byte[2] & 0xf0) >> 4;
        int message_compress = msg_byte[2] & 0x0f;
        byte[] payload = null;
        int payload_len = 0;
        int payload_offset = header_len;

        if (message_type == MessageType.FULL_SERVER_RESPONSE) {
            ByteBuffer bb = ByteBuffer.wrap(msg_byte, payload_offset, 4);
            payload_len = bb.getInt();
            payload_offset += 4;
        } else if (message_type == MessageType.SERVER_ACK) {
            ByteBuffer bb = ByteBuffer.wrap(msg_byte, payload_offset, 4);
            int seq = bb.getInt();
            payload_offset += 4;
            if (msg_byte.length > 8) {
                payload_len = ByteBuffer.wrap(msg_byte, payload_offset, 4).getInt();
                payload_offset += 4;
            }
        } else if (message_type == MessageType.ERROR_MESSAGE_FROM_SERVER) {
            int error_code = ByteBuffer.wrap(msg_byte, payload_offset, 4).getInt();
            payload_offset += 4;
            payload_len = ByteBuffer.wrap(msg_byte, payload_offset, 4).getInt();
            payload_offset += 4;
        } else {
            logger.error("unsupported message type {}", message_type);
            return -1;
        }

        payload = new byte[msg_byte.length - payload_offset];
        System.arraycopy(msg_byte, payload_offset, payload, 0, payload.length);
        if (message_compress == MessageCompress.GZIP) {
            payload = gzip_decompress(payload);
        }
        if (message_serial == MessageSerial.JSON) {
            ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            asr_response = mapper.readValue(payload, 0, payload.length, AsrResponse.class);
        }
        if (asr_response.getCode() != 1000) {
            logger.error("asr resposne {}", new String(payload));
            return -1;
        }
        if (asr_response.getSequence() < 0) {
            logger.debug("get last response");
        }
        logger.info("asr response {}", new String(payload));

        return 0;
    }

    private byte[] construct_audio_payload(byte[] audio, boolean is_last) throws IOException {
        int header_len = 4;
        byte[] header = new byte[header_len];
        header[0] = (byte) (ProtocolVersion.PROTOCOL_VERSION << 4 | (header_len >> 2));
        if (!is_last) {
            header[1] = (byte) (MessageType.AUDIO_ONLY_CLIENT_REQUEST << 4 | MessageTypeFlag.NO_SEQUENCE_NUMBER);
        } else {
            header[1] = (byte) (MessageType.AUDIO_ONLY_CLIENT_REQUEST << 4 | MessageTypeFlag.NEGATIVE_SEQUENCE_SERVER_ASSGIN);
        }
        header[2] = (byte) (MessageSerial.JSON << 4 | MessageCompress.GZIP);
        header[3] = 0;

        byte[] payload = gzip_compress(audio);
        int payload_len = payload.length;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(payload_len);
        byte[] pl_byte = bb.array();

        return concat_byte(header, pl_byte, payload);
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setShow_utterances(boolean show_utterances) {
        this.show_utterances = show_utterances;
    }

    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public void setSample_rate(int sample_rate) {
        this.sample_rate = sample_rate;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public AsrResponse getAsrResponse() {
        return asr_response;
    }

    private byte[] concat_byte(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private byte[] concat_byte(byte[] first, byte[] second, byte[] third) {
        byte[] result = new byte[first.length + second.length + third.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        System.arraycopy(third, 0, result, first.length+second.length, third.length);
        return result;
    }
}
