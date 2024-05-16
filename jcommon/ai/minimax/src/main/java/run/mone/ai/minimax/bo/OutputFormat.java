package run.mone.ai.minimax.bo;

public enum OutputFormat {

    mp3("mp3"),
    wav("wav"),
    pcm("pcm"),
    flac("flac"),
    aac("aac");

    public String outputFormat;

    OutputFormat(String OutputFormat){
        this.outputFormat = OutputFormat;
    }
}
