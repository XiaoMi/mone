package run.mone.m78.ip.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModelRes implements Serializable {

    String name;
    String value;
    Boolean vip; // Nullable
    Long points; // Nullable, User points in z platform


    int maxToken;
    int moduleClassNum;
    boolean supportJsonResponse;
    boolean optimizeTokens;



    public ModelRes(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ModelRes() {
    }
}
