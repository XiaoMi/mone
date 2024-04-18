export class Graph {
  nodes: { id: string }[]
  adj: Record<string, string[]>

  constructor(lines: { sourceNodeId: string; targetNodeId: string }[], nodes: { id: string }[]) {
    this.nodes = nodes || []

    const adj: Record<string, string[]> = {}
    lines.forEach((it) => {
      if (adj[it.sourceNodeId]) {
        adj[it.sourceNodeId].push(it.targetNodeId)
      } else {
        adj[it.sourceNodeId] = [it.targetNodeId]
      }
    })
    this.adj = adj
  }

  BFS(sortNodes: { id: string }[]) {
    const visited: Record<string, boolean> = {}
    const nodes = this.nodes
    for (const node of nodes) {
      const queue: string[] = [node.id]
      BFSUtil(sortNodes, nodes, this.adj, visited, queue)
    }
  }
}

const BFSUtil = (
  sortNodes: { id: string }[],
  nodes: { id: string }[],
  adj: Record<string, string[]>,
  visited: Record<string, boolean>,
  queue: string[]
) => {
  while (queue.length > 0) {
    const q = queue.shift()
    if (q != null && !visited[q]) {
      visited[q] = true
      const ids = adj[q]
      if (ids) {
        ids.forEach((it) => {
          queue.push(it)
        })
      }
      const item = nodes.find((it) => it.id === q)
      if (item) sortNodes.push(item)
    }
  }
}
