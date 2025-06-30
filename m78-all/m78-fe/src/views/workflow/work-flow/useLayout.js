import dagre from '@dagrejs/dagre'
import { Position, useVueFlow } from '@vue-flow/core'
import { ref } from 'vue'

/**
 * Composable to run the layout algorithm on the graph.
 * It uses the `dagre` library to calculate the layout of the nodes and edges.
 */
export function useLayout() {
  const { findNode } = useVueFlow()

  const graph = ref(new dagre.graphlib.Graph())

  const previousDirection = ref('LR')

  // if else节点在选择器节点内部，所以要校正source,target;
  const transformEdges = (edges) => {
    return edges.map((it) => {
      const { source, target, sourceNodeId, targetNodeId } = it
      const newIt = it
      if (source != sourceNodeId) {
        newIt.source = sourceNodeId
      }
      if (target != targetNodeId) {
        newIt.target = targetNodeId
      }
      return newIt
    })
  }
  function layout(nodes, edges, direction) {
    const newEdges = transformEdges(edges)
    // we create a new graph instance, in case some nodes/edges were removed, otherwise dagre would act as if they were still there
    const dagreGraph = new dagre.graphlib.Graph()

    graph.value = dagreGraph

    dagreGraph.setDefaultEdgeLabel(() => ({}))

    const isHorizontal = direction === 'LR'
    dagreGraph.setGraph({
      rankdir: direction,
      nodesep: 200 // 增加同一层级节点之间的水平间距
    })

    previousDirection.value = direction

    for (const node of nodes) {
      // if you need width+height of nodes for your layout, you can use the dimensions property of the internal node (`GraphNode` type)
      const graphNode = findNode(node.id)

      dagreGraph.setNode(node.id, {
        width: graphNode.dimensions.width || 150,
        height: graphNode.dimensions.height || 50
      })
    }

    for (const edge of newEdges) {
      dagreGraph.setEdge(edge.source, edge.target)
    }

    dagre.layout(dagreGraph)

    // set nodes with updated positions
    return nodes.map((node) => {
      const nodeWithPosition = dagreGraph.node(node.id)

      return {
        ...node,
        targetPosition: isHorizontal ? Position.Left : Position.Top,
        sourcePosition: isHorizontal ? Position.Right : Position.Bottom,
        position: { x: nodeWithPosition.x, y: nodeWithPosition.y }
      }
    })
  }

  return { graph, layout, previousDirection }
}
