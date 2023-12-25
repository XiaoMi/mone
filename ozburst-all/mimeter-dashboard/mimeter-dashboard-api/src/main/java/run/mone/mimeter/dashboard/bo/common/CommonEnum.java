package run.mone.mimeter.dashboard.bo.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonEnum implements Serializable {
    private int code;
    private String ChineseDesc;
    private String EnglishDesc;

    private String unit;


    public CommonEnum(int code, String ChineseDesc, String EnglishDesc) {
        this.code = code;
        this.ChineseDesc = ChineseDesc;
        this.EnglishDesc = EnglishDesc;
    }

    public CommonEnum(int code, String ChineseDesc) {
        this.code = code;
        this.ChineseDesc = ChineseDesc;
    }

    public CommonEnum(String ChineseDesc, String EnglishDesc) {
        this.ChineseDesc = ChineseDesc;
        this.EnglishDesc = EnglishDesc;
    }

    public CommonEnum(String ChineseDesc, String EnglishDesc, String unit) {
        this.ChineseDesc = ChineseDesc;
        this.EnglishDesc = EnglishDesc;
        this.unit = unit;
    }
}
