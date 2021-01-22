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

package com.xiaomi.data.push.rpc.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcException;
import lombok.Getter;
import lombok.Setter;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author goodjava@qq.com
 */
public class RemotingCommand {

    /**
     * 0, REQUEST_COMMAND
     */
    private static final int RPC_TYPE = 0;

    // 0, RPC
    private static final int RPC_ONEWAY = 1;

    private static volatile int configVersion = 1;

    private static AtomicInteger requestId = new AtomicInteger(0);

    private int code;

    private int version = 0;

    private int opaque = requestId.getAndIncrement();

    private int flag = 0;

    @Setter
    @Getter
    private long timeout = 1000;

    /**
     * 0 json 1 msgpack
     */
    @Setter
    private int serializeType = SerializaType.json.ordinal();

    private String remark;

    private HashMap<String, String> extFields = null;

    private transient byte[] body;

    @Setter
    private transient int bodyLen;

    public RemotingCommand() {
    }

    public static RemotingCommand createRequestCommand(int code) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        setCmdVersion(cmd);
        return cmd;
    }


    /**
     * support msgpack
     * @param code
     * @param body
     * @return
     */
    public static RemotingCommand createMsgPackRequest(int code, Object body) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        setCmdVersion(cmd);
        cmd.setSerializeType(SerializaType.msgpack.ordinal());
        try {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            byte[] data = objectMapper.writeValueAsBytes(body);
            cmd.setBody(data);
        } catch (JsonProcessingException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return cmd;
    }


    public static RemotingCommand createRequestCommand(int code, int bodyLen) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        setCmdVersion(cmd);
        cmd.setBodyLen(bodyLen);
        return cmd;
    }

    private static void setCmdVersion(RemotingCommand cmd) {
        cmd.setVersion(configVersion);
    }


    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }


    public <T> T getReq(Class<T> clazz) {
        if (this.serializeType == 0) {
            String body = new String(this.body);
            return new Gson().fromJson(body, clazz);
        } else {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            try {
                return objectMapper.readValue(this.body, clazz);
            } catch (IOException e) {
                throw new RpcException(e.getMessage(), e);
            }
        }
    }


    public static RemotingCommand createResponseCommand(int code) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        return cmd;
    }

    public static RemotingCommand createMsgpackResponse(int code,Object body) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setSerializeType(SerializaType.msgpack.ordinal());
        try {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            byte[] data = objectMapper.writeValueAsBytes(body);
            cmd.setBody(data);
        } catch (JsonProcessingException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return cmd;
    }


    public static RemotingCommand createResponseCommand(int code, int bodyLen) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setBodyLen(bodyLen);
        return cmd;
    }


    public static RemotingCommand createResponseCommand(int code, String body) {
        return createResponseCommand(code, body.getBytes());
    }

    public static RemotingCommand createResponseCommand(int code, byte[] body) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setBody(body);
        setCmdVersion(cmd);
        return cmd;
    }


    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        int headerLength = byteBuffer.getInt();

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        RemotingCommand cmd = headerDecode(headerData);

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;
        return cmd;
    }


    private static RemotingCommand headerDecode(byte[] headerData) {
        return RemotingSerializable.decode(headerData, RemotingCommand.class);
    }


    public static int createNewRequestId() {
        return requestId.incrementAndGet();
    }


    public ByteBuffer encode() {
        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData = this.headerEncode();
        length += headerData.length;

        // 3> body data length
        if (this.body != null) {
            length += body.length;
        }

        if (this.bodyLen != 0) {
            length += this.bodyLen;
        }

        ByteBuffer result = ByteBuffer.allocate(4 + length);

        // length
        result.putInt(length);

        // header length
        result.putInt(headerData.length);

        // header data
        result.put(headerData);

        // body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    private byte[] headerEncode() {
        return RemotingSerializable.encode(this);
    }


    public ByteBuffer encodeHeader() {
        int bodyLen = 0;
        if (null != body) {
            bodyLen = body.length;
        }
        if (this.bodyLen > 0) {
            bodyLen = this.bodyLen;
        }
        return encodeHeader(bodyLen);
    }

    /**
     *
     */
    public ByteBuffer encodeHeader(final int bodyLength) {
        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData = this.headerEncode();

        length += headerData.length;

        // 3> body data length
        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);

        // length
        result.putInt(length);

        // header length
        result.putInt(headerData.length);

        // header data
        result.put(headerData);

        result.flip();

        return result;
    }

    public void markOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        this.flag |= bits;
    }

    public boolean isOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        return (this.flag & bits) == bits;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public String getExtField(String key) {
        return this.extFields.get(key);
    }

    public void setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
    }

    public void addExtField(String key, String value) {
        if (null == extFields) {
            extFields = new HashMap<>(1);
        }
        extFields.put(key, value);
    }

    @Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", opaque=" + opaque + "]";
    }


}