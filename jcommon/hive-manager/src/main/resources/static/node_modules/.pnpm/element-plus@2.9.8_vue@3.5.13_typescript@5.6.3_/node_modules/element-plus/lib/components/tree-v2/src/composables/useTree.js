'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var virtualTree = require('../virtual-tree.js');
var useCheck = require('./useCheck.js');
var useFilter = require('./useFilter.js');
var shared = require('@vue/shared');

function useTree(props, emit) {
  const expandedKeySet = vue.ref(new Set(props.defaultExpandedKeys));
  const currentKey = vue.ref();
  const tree = vue.shallowRef();
  const listRef = vue.ref();
  vue.watch(() => props.currentNodeKey, (key) => {
    currentKey.value = key;
  }, {
    immediate: true
  });
  vue.watch(() => props.data, (data) => {
    setData(data);
  }, {
    immediate: true
  });
  const {
    isIndeterminate,
    isChecked,
    toggleCheckbox,
    getCheckedKeys,
    getCheckedNodes,
    getHalfCheckedKeys,
    getHalfCheckedNodes,
    setChecked,
    setCheckedKeys
  } = useCheck.useCheck(props, tree);
  const { doFilter, hiddenNodeKeySet, isForceHiddenExpandIcon } = useFilter.useFilter(props, tree);
  const valueKey = vue.computed(() => {
    var _a;
    return ((_a = props.props) == null ? void 0 : _a.value) || virtualTree.TreeOptionsEnum.KEY;
  });
  const childrenKey = vue.computed(() => {
    var _a;
    return ((_a = props.props) == null ? void 0 : _a.children) || virtualTree.TreeOptionsEnum.CHILDREN;
  });
  const disabledKey = vue.computed(() => {
    var _a;
    return ((_a = props.props) == null ? void 0 : _a.disabled) || virtualTree.TreeOptionsEnum.DISABLED;
  });
  const labelKey = vue.computed(() => {
    var _a;
    return ((_a = props.props) == null ? void 0 : _a.label) || virtualTree.TreeOptionsEnum.LABEL;
  });
  const flattenTree = vue.computed(() => {
    var _a;
    const expandedKeys = expandedKeySet.value;
    const hiddenKeys = hiddenNodeKeySet.value;
    const flattenNodes = [];
    const nodes = ((_a = tree.value) == null ? void 0 : _a.treeNodes) || [];
    const stack = [];
    for (let i = nodes.length - 1; i >= 0; --i) {
      stack.push(nodes[i]);
    }
    while (stack.length) {
      const node = stack.pop();
      if (hiddenKeys.has(node.key))
        continue;
      flattenNodes.push(node);
      if (node.children && expandedKeys.has(node.key)) {
        for (let i = node.children.length - 1; i >= 0; --i) {
          stack.push(node.children[i]);
        }
      }
    }
    return flattenNodes;
  });
  const isNotEmpty = vue.computed(() => {
    return flattenTree.value.length > 0;
  });
  function createTree(data) {
    const treeNodeMap = /* @__PURE__ */ new Map();
    const levelTreeNodeMap = /* @__PURE__ */ new Map();
    let maxLevel = 1;
    function traverse(nodes, level = 1, parent = void 0) {
      var _a;
      const siblings = [];
      for (const rawNode of nodes) {
        const value = getKey(rawNode);
        const node = {
          level,
          key: value,
          data: rawNode
        };
        node.label = getLabel(rawNode);
        node.parent = parent;
        const children = getChildren(rawNode);
        node.disabled = getDisabled(rawNode);
        node.isLeaf = !children || children.length === 0;
        if (children && children.length) {
          node.children = traverse(children, level + 1, node);
        }
        siblings.push(node);
        treeNodeMap.set(value, node);
        if (!levelTreeNodeMap.has(level)) {
          levelTreeNodeMap.set(level, []);
        }
        (_a = levelTreeNodeMap.get(level)) == null ? void 0 : _a.push(node);
      }
      if (level > maxLevel) {
        maxLevel = level;
      }
      return siblings;
    }
    const treeNodes = traverse(data);
    return {
      treeNodeMap,
      levelTreeNodeMap,
      maxLevel,
      treeNodes
    };
  }
  function filter(query) {
    const keys = doFilter(query);
    if (keys) {
      expandedKeySet.value = keys;
    }
  }
  function getChildren(node) {
    return node[childrenKey.value];
  }
  function getKey(node) {
    if (!node) {
      return "";
    }
    return node[valueKey.value];
  }
  function getDisabled(node) {
    return node[disabledKey.value];
  }
  function getLabel(node) {
    return node[labelKey.value];
  }
  function toggleExpand(node) {
    const expandedKeys = expandedKeySet.value;
    if (expandedKeys.has(node.key)) {
      collapseNode(node);
    } else {
      expandNode(node);
    }
  }
  function setExpandedKeys(keys) {
    const expandedKeys = /* @__PURE__ */ new Set();
    const nodeMap = tree.value.treeNodeMap;
    keys.forEach((k) => {
      let node = nodeMap.get(k);
      while (node && !expandedKeys.has(node.key)) {
        expandedKeys.add(node.key);
        node = node.parent;
      }
    });
    expandedKeySet.value = expandedKeys;
  }
  function handleNodeClick(node, e) {
    emit(virtualTree.NODE_CLICK, node.data, node, e);
    handleCurrentChange(node);
    if (props.expandOnClickNode) {
      toggleExpand(node);
    }
    if (props.showCheckbox && (props.checkOnClickNode || node.isLeaf && props.checkOnClickLeaf) && !node.disabled) {
      toggleCheckbox(node, !isChecked(node), true);
    }
  }
  function handleNodeDrop(node, e) {
    emit(virtualTree.NODE_DROP, node.data, node, e);
  }
  function handleCurrentChange(node) {
    if (!isCurrent(node)) {
      currentKey.value = node.key;
      emit(virtualTree.CURRENT_CHANGE, node.data, node);
    }
  }
  function handleNodeCheck(node, checked) {
    toggleCheckbox(node, checked);
  }
  function expandNode(node) {
    const keySet = expandedKeySet.value;
    if (tree.value && props.accordion) {
      const { treeNodeMap } = tree.value;
      keySet.forEach((key) => {
        const treeNode = treeNodeMap.get(key);
        if (node && node.level === (treeNode == null ? void 0 : treeNode.level)) {
          keySet.delete(key);
        }
      });
    }
    keySet.add(node.key);
    emit(virtualTree.NODE_EXPAND, node.data, node);
  }
  function collapseNode(node) {
    expandedKeySet.value.delete(node.key);
    emit(virtualTree.NODE_COLLAPSE, node.data, node);
  }
  function isExpanded(node) {
    return expandedKeySet.value.has(node.key);
  }
  function isDisabled(node) {
    return !!node.disabled;
  }
  function isCurrent(node) {
    const current = currentKey.value;
    return current !== void 0 && current === node.key;
  }
  function getCurrentNode() {
    var _a, _b;
    if (!currentKey.value)
      return void 0;
    return (_b = (_a = tree.value) == null ? void 0 : _a.treeNodeMap.get(currentKey.value)) == null ? void 0 : _b.data;
  }
  function getCurrentKey() {
    return currentKey.value;
  }
  function setCurrentKey(key) {
    currentKey.value = key;
  }
  function setData(data) {
    vue.nextTick(() => tree.value = createTree(data));
  }
  function getNode(data) {
    var _a;
    const key = shared.isObject(data) ? getKey(data) : data;
    return (_a = tree.value) == null ? void 0 : _a.treeNodeMap.get(key);
  }
  function scrollToNode(key, strategy = "auto") {
    const node = getNode(key);
    if (node && listRef.value) {
      listRef.value.scrollToItem(flattenTree.value.indexOf(node), strategy);
    }
  }
  function scrollTo(offset) {
    var _a;
    (_a = listRef.value) == null ? void 0 : _a.scrollTo(offset);
  }
  return {
    tree,
    flattenTree,
    isNotEmpty,
    listRef,
    getKey,
    getChildren,
    toggleExpand,
    toggleCheckbox,
    isExpanded,
    isChecked,
    isIndeterminate,
    isDisabled,
    isCurrent,
    isForceHiddenExpandIcon,
    handleNodeClick,
    handleNodeDrop,
    handleNodeCheck,
    getCurrentNode,
    getCurrentKey,
    setCurrentKey,
    getCheckedKeys,
    getCheckedNodes,
    getHalfCheckedKeys,
    getHalfCheckedNodes,
    setChecked,
    setCheckedKeys,
    filter,
    setData,
    getNode,
    expandNode,
    collapseNode,
    setExpandedKeys,
    scrollToNode,
    scrollTo
  };
}

exports.useTree = useTree;
//# sourceMappingURL=useTree.js.map
