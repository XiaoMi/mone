package run.mone.hive.memory.longterm.graph.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BM25算法实现，用于图搜索结果重排
 * 基于Okapi BM25算法
 */
@Slf4j
public class BM25Ranker {

    private final double k1;
    private final double b;
    private final List<List<String>> corpus;
    private final Map<String, Double> idf;
    private final double avgLength;

    /**
     * 构造BM25排序器
     *
     * @param documents 文档列表，每个文档是一个字符串列表
     * @param k1 调节词频饱和度的参数，通常取值1.2-2.0
     * @param b 调节文档长度归一化的参数，通常取值0.75
     */
    public BM25Ranker(List<List<String>> documents, double k1, double b) {
        this.k1 = k1;
        this.b = b;
        this.corpus = new ArrayList<>(documents);
        this.idf = computeIDF();
        this.avgLength = computeAverageLength();
    }

    /**
     * 使用默认参数构造BM25排序器
     *
     * @param documents 文档列表
     */
    public BM25Ranker(List<List<String>> documents) {
        this(documents, 1.5, 0.75);
    }

    /**
     * 计算IDF值
     *
     * @return 词项到IDF值的映射
     */
    private Map<String, Double> computeIDF() {
        Map<String, Double> idfMap = new HashMap<>();
        int n = corpus.size();

        // 计算每个词项在多少个文档中出现
        Map<String, Integer> docFreq = new HashMap<>();
        for (List<String> doc : corpus) {
            Set<String> uniqueTerms = new HashSet<>(doc);
            for (String term : uniqueTerms) {
                docFreq.put(term, docFreq.getOrDefault(term, 0) + 1);
            }
        }

        // 计算IDF
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            String term = entry.getKey();
            int df = entry.getValue();
            double idf = Math.log((n - df + 0.5) / (df + 0.5));
            idfMap.put(term, idf);
        }

        return idfMap;
    }

    /**
     * 计算平均文档长度
     *
     * @return 平均长度
     */
    private double computeAverageLength() {
        if (corpus.isEmpty()) {
            return 0.0;
        }

        int totalLength = corpus.stream()
            .mapToInt(List::size)
            .sum();

        return (double) totalLength / corpus.size();
    }

    /**
     * 计算查询与文档的BM25分数
     *
     * @param query 查询词列表
     * @param docIndex 文档索引
     * @return BM25分数
     */
    public double score(List<String> query, int docIndex) {
        if (docIndex >= corpus.size()) {
            return 0.0;
        }

        List<String> doc = corpus.get(docIndex);
        Map<String, Integer> termFreq = getTermFrequency(doc);
        double docLength = doc.size();

        double score = 0.0;
        for (String term : query) {
            double tf = termFreq.getOrDefault(term, 0);
            double idfValue = idf.getOrDefault(term, 0.0);

            double numerator = tf * (k1 + 1);
            double denominator = tf + k1 * (1 - b + b * (docLength / avgLength));

            score += idfValue * (numerator / denominator);
        }

        return score;
    }

    /**
     * 获取文档的词频统计
     *
     * @param doc 文档
     * @return 词频映射
     */
    private Map<String, Integer> getTermFrequency(List<String> doc) {
        Map<String, Integer> tf = new HashMap<>();
        for (String term : doc) {
            tf.put(term, tf.getOrDefault(term, 0) + 1);
        }
        return tf;
    }

    /**
     * 对查询进行排序，返回前N个最相关的文档
     *
     * @param query 查询词列表
     * @param n 返回的文档数量
     * @return 排序后的文档索引列表
     */
    public List<Integer> getTopN(List<String> query, int n) {
        List<ScoredDoc> scoredDocs = new ArrayList<>();

        for (int i = 0; i < corpus.size(); i++) {
            double score = score(query, i);
            scoredDocs.add(new ScoredDoc(i, score));
        }

        // 按分数降序排序
        scoredDocs.sort((a, b) -> Double.compare(b.score, a.score));

        return scoredDocs.stream()
            .limit(n)
            .map(doc -> doc.index)
            .collect(Collectors.toList());
    }

    /**
     * 对查询进行排序，返回前N个最相关的文档内容
     *
     * @param query 查询词列表
     * @param n 返回的文档数量
     * @return 排序后的文档列表
     */
    public List<List<String>> getTopNDocuments(List<String> query, int n) {
        List<Integer> topIndices = getTopN(query, n);
        return topIndices.stream()
            .map(index -> corpus.get(index))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有文档的分数
     *
     * @param query 查询词列表
     * @return 文档索引到分数的映射
     */
    public Map<Integer, Double> getAllScores(List<String> query) {
        Map<Integer, Double> scores = new HashMap<>();
        for (int i = 0; i < corpus.size(); i++) {
            scores.put(i, score(query, i));
        }
        return scores;
    }

    /**
     * 分数和索引的封装类
     */
    private static class ScoredDoc {
        final int index;
        final double score;

        ScoredDoc(int index, double score) {
            this.index = index;
            this.score = score;
        }
    }

    /**
     * 将文本分词
     *
     * @param text 输入文本
     * @return 词项列表
     */
    public static List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(text.toLowerCase().trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * 从图记忆列表创建BM25排序器
     *
     * @param memories 图记忆列表
     * @return BM25排序器
     */
    public static BM25Ranker fromGraphMemories(List<Map<String, Object>> memories) {
        List<List<String>> documents = memories.stream()
            .map(memory -> {
                String source = (String) memory.get("source");
                String relationship = (String) memory.get("relationship");
                String destination = (String) memory.get("destination");

                List<String> tokens = new ArrayList<>();
                if (source != null) tokens.addAll(tokenize(source));
                if (relationship != null) tokens.addAll(tokenize(relationship));
                if (destination != null) tokens.addAll(tokenize(destination));

                return tokens;
            })
            .collect(Collectors.toList());

        return new BM25Ranker(documents);
    }
}