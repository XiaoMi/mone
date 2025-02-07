package run.mone.m78.service.bo.chatgpt;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModelRes implements Serializable {

    String name;
    String value;
    Boolean vip; // Nullable
    Long points; // Nullable, User points in z platform


    public ModelRes(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ModelRes() {
    }
}
