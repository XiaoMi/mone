package run.mone.m78.service.bo.feishu;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-29 23:47
 */
@Data
public class EventMessage implements Serializable {
    @SerializedName("schema")
    public String schema;
    @SerializedName("header")
    public Header header;
    @SerializedName("event")
    public Event event;
}