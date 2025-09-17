package run.mone.hive.memory.longterm.embeddings.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import java.util.List;

@Slf4j
@Data
public class VertexAiEmbedding implements EmbeddingBase {
    private final EmbedderConfig config;
    
    public VertexAiEmbedding(EmbedderConfig config) {
        this.config = config;
        validateConfig();
    }
    
    @Override
    public List<Double> embed(String text, String memoryAction) {
        throw new UnsupportedOperationException("Google Vertex AI embedding implementation coming soon");
    }
    
    @Override
    public int getDimensions() {
        return config.getEmbeddingDims();
    }
}
