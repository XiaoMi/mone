package run.mone.ai.minimax.bo;

public enum ModelEnum {

    Speech01("speech-01", "中文"),
    Speech02("speech-02", "中文、英文、中英混合、日文、韩文");

    public String modelName;

    public String description;

    ModelEnum(String name, String description) {
        this.modelName = name;
        this.description = description;
    }
}
