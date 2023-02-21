package com.xiaomi.data.push.uds.codes.msgpack;

import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.codes.ICodes;
import lombok.SneakyThrows;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 */
public class MsgpackCodes implements ICodes {

    public MsgpackCodes() {
    }

    @SneakyThrows
    @Override
    public <T> T decode(byte[] data, Type type) {
        MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(data);
        return (T) MsgpackUtils.decode(messageUnpacker);
    }

    @SneakyThrows
    @Override
    public <T> byte[] encode(T t) {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        MsgpackUtils.encode(t, t.getClass(), packer);
        return packer.toByteArray();
    }


    @Override
    public byte type() {
        return (byte)55;
    }
}
