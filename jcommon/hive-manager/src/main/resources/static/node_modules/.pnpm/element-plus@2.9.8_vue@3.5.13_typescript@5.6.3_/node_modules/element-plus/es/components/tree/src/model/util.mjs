const NODE_KEY = "$treeNodeId";
const markNodeData = function(node, data) {
  if (!data || data[NODE_KEY])
    return;
  Object.defineProperty(data, NODE_KEY, {
    value: node.id,
    enumerable: false,
    configurable: false,
    writable: false
  });
};
const getNodeKey = (key, data) => data == null ? void 0 : data[key || NODE_KEY];
const handleCurrentChange = (store, emit, setCurrent) => {
  const preCurrentNode = store.value.currentNode;
  setCurrent();
  const currentNode = store.value.currentNode;
  if (preCurrentNode === currentNode)
    return;
  emit("current-change", currentNode ? currentNode.data : null, currentNode);
};

export { NODE_KEY, getNodeKey, handleCurrentChange, markNodeData };
//# sourceMappingURL=util.mjs.map
