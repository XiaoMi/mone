/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.uds.po;

import com.google.gson.annotations.Expose;
import com.xiaomi.data.push.common.FlagCal;
import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.uds.codes.BytesCodes;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 */
@Data
public class UdsCommand implements Serializable {

    @Expose
    public static final AtomicLong requestId = new AtomicLong(0);

    @Expose
    private Channel channel;

    @Expose
    private BytesCodes bytesCodes = new BytesCodes();

    /**
     * 魔术码
     */
    private byte magic;

    /**
     * 用来存储标志位
     */
    private int flag;

    private Map<String, String> attachments = new HashMap<>();

    private long id;

    private String app;

    private String remoteApp;

    private long timeout = 1000;

    private String cmd;

    private String serviceName;

    private String methodName;

    private String[] paramTypes;

    private String[] params;

    private byte[][] byteParams = new byte[][]{};

    private byte[] data;

    @Expose
    private Object obj;

    private boolean mesh = true;

    private int code;

    private String message;

    /**
     * 序列化类型
     */
    private byte serializeType;

    public static UdsCommand createResponse(UdsCommand request) {
        UdsCommand res = new UdsCommand();
        res.setId(request.getId());
        res.setSerializeType(request.getSerializeType());
        return res;
    }

    public boolean isRequest() {
        return new FlagCal(this.flag).isTrue(Permission.IS_REQUEST);
    }

    public void putAtt(String key, String value) {
        this.attachments.put(key, value);
    }

    public String getAtt(String key, String defaultValue) {
        return this.attachments.getOrDefault(key, defaultValue);
    }


    /**
     * 自定义编码,为了提升编解码效率
     *
     * @return
     */
    public ByteBuf encode() {
        // magic flag meta  (methodInfo <cmd serviceName methodName paramTypes params byteParams mesh code message>)  serializeType  payload
        int methodInfoSize = 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
        CompositeByteBuf buf = Unpooled.compositeBuffer(1 + 1 + 1 + this.attachments.size() + methodInfoSize + 1 + 1);
        //magic
        buf.addComponents(true, Unpooled.buffer(1).writeByte(14));
        //flag
        buf.addComponents(true, Unpooled.buffer(4).writeInt(this.flag));
        //meta
        buf.addComponents(true, Unpooled.buffer(4).writeInt(this.attachments.size()));
        this.attachments.forEach((k, v) -> {
            byte[] kk = bytesCodes.encode(k);
            byte[] vv = bytesCodes.encode(v);
            buf.addComponents(true, Unpooled.wrappedBuffer(kk, vv));
        });

        //method info
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(this.id)));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.app))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.remoteApp))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(this.timeout)));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.cmd))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.serviceName))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.methodName))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStrs(this.paramTypes))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStrs(this.params))));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(this.byteParams)));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(this.mesh)));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(this.code)));
        buf.addComponents(true, Unpooled.wrappedBuffer(bytesCodes.encode(getStr(this.message))));

        //serializeType
        buf.addComponents(true, Unpooled.wrappedBuffer(new byte[]{this.serializeType}));

        if (null != this.data && this.data.length > 0) {
           //里边已经有数据了
        } else if (null != this.obj) {
            ICodes codes = CodesFactory.getCodes(this.serializeType);
            this.data = codes.encode(this.obj);
        } else {
            this.data = new byte[]{};
        }

        //playload
        buf.addComponents(true, Unpooled.buffer(4).writeInt(this.data.length));
        buf.addComponents(true, Unpooled.wrappedBuffer(this.data));
        return buf;
    }


    private String getStr(String str) {
        return Optional.ofNullable(str).orElse("");
    }


    private String[] getStrs(String[] strs) {
        if (null == strs) {
            return new String[]{};
        }
        return strs;
    }

    public void decode(ByteBuf buf) {
        // magic flag meta (methodInfo <serviceName methodName paramTypes params byteParams>) serializeType payload
        this.magic = buf.readByte();
        this.flag = buf.readInt();

        //meta
        int len = buf.readInt();
        for (int i = 0; i < len; i++) {
            int size = buf.readInt();
            byte[] k = new byte[size];
            buf.readBytes(k);
            size = buf.readInt();
            byte[] v = new byte[size];
            buf.readBytes(v);
            this.attachments.put(new String(k), new String(v));
        }

        //method info
        this.id = readLong(buf);
        this.app = readString(buf);
        this.remoteApp = readString(buf);
        this.timeout = readLong(buf);
        this.cmd = readString(buf);
        this.serviceName = readString(buf);
        this.methodName = readString(buf);
        this.paramTypes = readStringArray(buf);
        this.params = readStringArray(buf);
        this.byteParams = readyByteArray(buf);
        this.mesh = readBoolean(buf);
        this.code = readInt(buf);
        this.message = readString(buf);

        //serializeType
        this.serializeType = buf.readByte();

        //payload
        len = buf.readInt();
        this.data = new byte[len];
        buf.readBytes(this.data);
    }

    private long readLong(ByteBuf buf) {
        return bytesCodes.decode(buf, long.class);
    }

    private int readInt(ByteBuf buf) {
        return bytesCodes.decode(buf, int.class);
    }

    private boolean readBoolean(ByteBuf buf) {
        return bytesCodes.decode(buf, boolean.class);
    }

    private byte[][] readyByteArray(ByteBuf buf) {
        return bytesCodes.decode(buf, byte[][].class);
    }

    private String[] readStringArray(ByteBuf buf) {
        return bytesCodes.decode(buf, String[].class);
    }

    private String readString(ByteBuf buf) {
        int len = buf.readInt();
        byte[] data = new byte[len];
        buf.readBytes(data);
        return new String(data);
    }


    public static UdsCommand createErrorResponse(long id, String message) {
        UdsCommand res = new UdsCommand();
        res.setId(id);
        res.setCode(500);
        res.setMessage(message);
        return res;
    }


    public static UdsCommand createRequest() {
        UdsCommand req = new UdsCommand();
        req.setId(requestId.incrementAndGet());
        req.setSerializeType(RcurveConfig.ins().getCodeType());
        FlagCal cal = new FlagCal(0);
        cal.enable(Permission.IS_REQUEST);
        req.setFlag(cal.getFlag());
        return req;
    }

    public void setData(Object data) {
        this.obj = data;
        this.data = null;
    }


    public void setData(Object data, boolean codes) {
        if (codes) {
            setData(data);
        } else {
            this.data = (byte[]) data;
        }
    }

    public <T> T getData(Type type) {
        if (null == this.data) {
            return null;
        }
        ICodes codes = CodesFactory.getCodes(this.getSerializeType());
        return codes.decode(this.data, type);
    }

    public <T> T getData(Type type, boolean codes) {
        if (codes) {
            return getData(type);
        }
        return (T) this.data;
    }

    public void setOneway(boolean oneway) {
        if (oneway) {
            FlagCal flagCal = new FlagCal(this.flag);
            flagCal.enable(Permission.IS_ONWAY);
            this.flag = flagCal.getFlag();
        }

    }
}
