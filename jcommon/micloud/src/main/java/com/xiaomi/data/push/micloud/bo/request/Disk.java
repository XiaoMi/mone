package com.xiaomi.data.push.micloud.bo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Disk implements Serializable {

    @JsonProperty("type")
    String type;

    @JsonProperty("number")
    int number;

    @JsonProperty("size")
    int size;
}
