CREATE VECTOR INDEX document_embeddings
FOR (n:Document) ON (n.embedding)
OPTIONS {
  indexConfig: {
    `vector.dimensions`: 768,                    // 向量维度，根据实际情况调整
    `vector.similarity_function`: 'cosine'      // 相似度函数：cosine/euclidean
  }
};