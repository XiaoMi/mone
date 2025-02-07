package run.mone.m78.service.service.multiModal.audio;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class AudioProtocol {

    private byte messageType;
    private int uniqueId;
    private int seqId;  // 新添加的序列ID
    private int dataLength;
    private byte[] payload;

    // 构造方法
    public AudioProtocol(byte messageType, int uniqueId, int seqId, byte[] payload) {
        this.messageType = messageType;
        this.uniqueId = uniqueId;
        this.seqId = seqId;
        this.payload = payload != null ? payload : new byte[0];
        this.dataLength = this.payload.length;
    }

    // 序列化方法
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + 4 + 4 + dataLength);
        buffer.put(messageType);     // 消息类型
        buffer.putInt(uniqueId);     // 唯一 ID
        buffer.putInt(seqId);        // 序列 ID
        buffer.putInt(dataLength);   // 数据长度
        buffer.put(payload);         // 数据负载
        return buffer.array();
    }

    // 反序列化方法
    public static AudioProtocol fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte messageType = buffer.get();    // 消息类型
        int uniqueId = buffer.getInt();     // 唯一 ID
        int seqId = buffer.getInt();        // 序列 ID
        int dataLength = buffer.getInt();   // 数据长度
        byte[] payload = new byte[dataLength];
        buffer.get(payload);                // 数据负载
        return new AudioProtocol(messageType, uniqueId, seqId, payload);
    }
}
