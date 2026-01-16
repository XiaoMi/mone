import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { mutable } from '../../../utils/typescript.mjs';
import { iconPropType } from '../../../utils/vue/icon.mjs';
import { isBoolean } from '../../../utils/types.mjs';

const ROOT_TREE_INJECTION_KEY = Symbol();
const EMPTY_NODE = {
  key: -1,
  level: -1,
  data: {}
};
var TreeOptionsEnum = /* @__PURE__ */ ((TreeOptionsEnum2) => {
  TreeOptionsEnum2["KEY"] = "id";
  TreeOptionsEnum2["LABEL"] = "label";
  TreeOptionsEnum2["CHILDREN"] = "children";
  TreeOptionsEnum2["DISABLED"] = "disabled";
  TreeOptionsEnum2["CLASS"] = "";
  return TreeOptionsEnum2;
})(TreeOptionsEnum || {});
var SetOperationEnum = /* @__PURE__ */ ((SetOperationEnum2) => {
  SetOperationEnum2["ADD"] = "add";
  SetOperationEnum2["DELETE"] = "delete";
  return SetOperationEnum2;
})(SetOperationEnum || {});
const itemSize = {
  type: Number,
  default: 26
};
const treeProps = buildProps({
  data: {
    type: definePropType(Array),
    default: () => mutable([])
  },
  emptyText: {
    type: String
  },
  height: {
    type: Number,
    default: 200
  },
  props: {
    type: definePropType(Object),
    default: () => mutable({
      children: "children" /* CHILDREN */,
      label: "label" /* LABEL */,
      disabled: "disabled" /* DISABLED */,
      value: "id" /* KEY */,
      class: "" /* CLASS */
    })
  },
  highlightCurrent: {
    type: Boolean,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    default: false
  },
  defaultCheckedKeys: {
    type: definePropType(Array),
    default: () => mutable([])
  },
  checkStrictly: {
    type: Boolean,
    default: false
  },
  defaultExpandedKeys: {
    type: definePropType(Array),
    default: () => mutable([])
  },
  indent: {
    type: Number,
    default: 16
  },
  itemSize,
  icon: {
    type: iconPropType
  },
  expandOnClickNode: {
    type: Boolean,
    default: true
  },
  checkOnClickNode: {
    type: Boolean,
    default: false
  },
  checkOnClickLeaf: {
    type: Boolean,
    default: true
  },
  currentNodeKey: {
    type: definePropType([String, Number])
  },
  accordion: {
    type: Boolean,
    default: false
  },
  filterMethod: {
    type: definePropType(Function)
  },
  perfMode: {
    type: Boolean,
    default: true
  }
});
const treeNodeProps = buildProps({
  node: {
    type: definePropType(Object),
    default: () => mutable(EMPTY_NODE)
  },
  expanded: {
    type: Boolean,
    default: false
  },
  checked: {
    type: Boolean,
    default: false
  },
  indeterminate: {
    type: Boolean,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  current: {
    type: Boolean,
    default: false
  },
  hiddenExpandIcon: {
    type: Boolean,
    default: false
  },
  itemSize
});
const treeNodeContentProps = buildProps({
  node: {
    type: definePropType(Object),
    required: true
  }
});
const NODE_CLICK = "node-click";
const NODE_DROP = "node-drop";
const NODE_EXPAND = "node-expand";
const NODE_COLLAPSE = "node-collapse";
const CURRENT_CHANGE = "current-change";
const NODE_CHECK = "check";
const NODE_CHECK_CHANGE = "check-change";
const NODE_CONTEXTMENU = "node-contextmenu";
const treeEmits = {
  [NODE_CLICK]: (data, node, e) => data && node && e,
  [NODE_DROP]: (data, node, e) => data && node && e,
  [NODE_EXPAND]: (data, node) => data && node,
  [NODE_COLLAPSE]: (data, node) => data && node,
  [CURRENT_CHANGE]: (data, node) => data && node,
  [NODE_CHECK]: (data, checkedInfo) => data && checkedInfo,
  [NODE_CHECK_CHANGE]: (data, checked) => data && isBoolean(checked),
  [NODE_CONTEXTMENU]: (evt, data, node) => evt && data && node
};
const treeNodeEmits = {
  click: (node, e) => !!(node && e),
  drop: (node, e) => !!(node && e),
  toggle: (node) => !!node,
  check: (node, checked) => node && isBoolean(checked)
};

export { CURRENT_CHANGE, NODE_CHECK, NODE_CHECK_CHANGE, NODE_CLICK, NODE_COLLAPSE, NODE_CONTEXTMENU, NODE_DROP, NODE_EXPAND, ROOT_TREE_INJECTION_KEY, SetOperationEnum, TreeOptionsEnum, treeEmits, treeNodeContentProps, treeNodeEmits, treeNodeProps, treeProps };
//# sourceMappingURL=virtual-tree.mjs.map
