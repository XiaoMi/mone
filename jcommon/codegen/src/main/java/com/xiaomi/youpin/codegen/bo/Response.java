package com.xiaomi.youpin.codegen.bo;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class Response<D> implements Serializable {

    private int code;
    private String msg;
    private D data;
    private String traceId;
    private String spanId;
    private String cmd;
    private int id;

    private String senderId;
    private String receiverId;
    private String message;
    private String group;

    private Map<String, String> attachments;


    public Response(int code, String msg, D data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(int code, String msg, D data, String cmd) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.cmd = cmd;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public static <D> Response<D> success(D data) {
        return new Response<>(0, "succ", data);
    }

    public static <D> Response<D> success(String cmd, D data) {
        return new Response<>(0, "succ", data, cmd);
    }


}
