export const batchUpdateNodesRAF = (nodes, batchSize = 50, cb) => {
  return new Promise((resolve) => {
    let index = 0

    function processNextBatch() {
      const batch = nodes.slice(index, index + batchSize)
      batch.forEach((item) => {
        delete item.sourcePosition
        delete item.targetPosition
        cb && cb(item.id, item)
      })

      index += batchSize

      if (index < nodes.length) {
        requestAnimationFrame(processNextBatch)
      } else {
        // 所有节点都已更新完成
        resolve(true)
      }
    }

    requestAnimationFrame(processNextBatch)
  })
}
