import { defineComponent, inject, computed, resolveComponent, openBlock, createElementBlock, normalizeClass, createCommentVNode, createBlock, withModifiers, withCtx, createElementVNode, createVNode, Fragment } from 'vue';
import { ElCheckbox } from '../../checkbox/index.mjs';
import { ElRadio } from '../../radio/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { Check, Loading, ArrowRight } from '@element-plus/icons-vue';
import NodeContent from './node-content.mjs';
import { CASCADER_PANEL_INJECTION_KEY } from './types.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const _sfc_main = defineComponent({
  name: "ElCascaderNode",
  components: {
    ElCheckbox,
    ElRadio,
    NodeContent,
    ElIcon,
    Check,
    Loading,
    ArrowRight
  },
  props: {
    node: {
      type: Object,
      required: true
    },
    menuId: String
  },
  emits: ["expand"],
  setup(props, { emit }) {
    const panel = inject(CASCADER_PANEL_INJECTION_KEY);
    const ns = useNamespace("cascader-node");
    const isHoverMenu = computed(() => panel.isHoverMenu);
    const multiple = computed(() => panel.config.multiple);
    const checkStrictly = computed(() => panel.config.checkStrictly);
    const checkedNodeId = computed(() => {
      var _a;
      return (_a = panel.checkedNodes[0]) == null ? void 0 : _a.uid;
    });
    const isDisabled = computed(() => props.node.isDisabled);
    const isLeaf = computed(() => props.node.isLeaf);
    const expandable = computed(() => checkStrictly.value && !isLeaf.value || !isDisabled.value);
    const inExpandingPath = computed(() => isInPath(panel.expandingNode));
    const inCheckedPath = computed(() => checkStrictly.value && panel.checkedNodes.some(isInPath));
    const isInPath = (node) => {
      var _a;
      const { level, uid } = props.node;
      return ((_a = node == null ? void 0 : node.pathNodes[level - 1]) == null ? void 0 : _a.uid) === uid;
    };
    const doExpand = () => {
      if (inExpandingPath.value)
        return;
      panel.expandNode(props.node);
    };
    const doCheck = (checked) => {
      const { node } = props;
      if (checked === node.checked)
        return;
      panel.handleCheckChange(node, checked);
    };
    const doLoad = () => {
      panel.lazyLoad(props.node, () => {
        if (!isLeaf.value)
          doExpand();
      });
    };
    const handleHoverExpand = (e) => {
      if (!isHoverMenu.value)
        return;
      handleExpand();
      !isLeaf.value && emit("expand", e);
    };
    const handleExpand = () => {
      const { node } = props;
      if (!expandable.value || node.loading)
        return;
      node.loaded ? doExpand() : doLoad();
    };
    const handleClick = () => {
      if (isHoverMenu.value && !isLeaf.value)
        return;
      if (isLeaf.value && !isDisabled.value && !checkStrictly.value && !multiple.value) {
        handleCheck(true);
      } else {
        handleExpand();
      }
    };
    const handleSelectCheck = (checked) => {
      if (checkStrictly.value) {
        doCheck(checked);
        if (props.node.loaded) {
          doExpand();
        }
      } else {
        handleCheck(checked);
      }
    };
    const handleCheck = (checked) => {
      if (!props.node.loaded) {
        doLoad();
      } else {
        doCheck(checked);
        !checkStrictly.value && doExpand();
      }
    };
    return {
      panel,
      isHoverMenu,
      multiple,
      checkStrictly,
      checkedNodeId,
      isDisabled,
      isLeaf,
      expandable,
      inExpandingPath,
      inCheckedPath,
      ns,
      handleHoverExpand,
      handleExpand,
      handleClick,
      handleCheck,
      handleSelectCheck
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  const _component_el_checkbox = resolveComponent("el-checkbox");
  const _component_el_radio = resolveComponent("el-radio");
  const _component_check = resolveComponent("check");
  const _component_el_icon = resolveComponent("el-icon");
  const _component_node_content = resolveComponent("node-content");
  const _component_loading = resolveComponent("loading");
  const _component_arrow_right = resolveComponent("arrow-right");
  return openBlock(), createElementBlock("li", {
    id: `${_ctx.menuId}-${_ctx.node.uid}`,
    role: "menuitem",
    "aria-haspopup": !_ctx.isLeaf,
    "aria-owns": _ctx.isLeaf ? void 0 : _ctx.menuId,
    "aria-expanded": _ctx.inExpandingPath,
    tabindex: _ctx.expandable ? -1 : void 0,
    class: normalizeClass([
      _ctx.ns.b(),
      _ctx.ns.is("selectable", _ctx.checkStrictly),
      _ctx.ns.is("active", _ctx.node.checked),
      _ctx.ns.is("disabled", !_ctx.expandable),
      _ctx.inExpandingPath && "in-active-path",
      _ctx.inCheckedPath && "in-checked-path"
    ]),
    onMouseenter: _ctx.handleHoverExpand,
    onFocus: _ctx.handleHoverExpand,
    onClick: _ctx.handleClick
  }, [
    createCommentVNode(" prefix "),
    _ctx.multiple ? (openBlock(), createBlock(_component_el_checkbox, {
      key: 0,
      "model-value": _ctx.node.checked,
      indeterminate: _ctx.node.indeterminate,
      disabled: _ctx.isDisabled,
      onClick: withModifiers(() => {
      }, ["stop"]),
      "onUpdate:modelValue": _ctx.handleSelectCheck
    }, null, 8, ["model-value", "indeterminate", "disabled", "onClick", "onUpdate:modelValue"])) : _ctx.checkStrictly ? (openBlock(), createBlock(_component_el_radio, {
      key: 1,
      "model-value": _ctx.checkedNodeId,
      label: _ctx.node.uid,
      disabled: _ctx.isDisabled,
      "onUpdate:modelValue": _ctx.handleSelectCheck,
      onClick: withModifiers(() => {
      }, ["stop"])
    }, {
      default: withCtx(() => [
        createCommentVNode("\n        Add an empty element to avoid render label,\n        do not use empty fragment here for https://github.com/vuejs/vue-next/pull/2485\n      "),
        createElementVNode("span")
      ]),
      _: 1
    }, 8, ["model-value", "label", "disabled", "onUpdate:modelValue", "onClick"])) : _ctx.isLeaf && _ctx.node.checked ? (openBlock(), createBlock(_component_el_icon, {
      key: 2,
      class: normalizeClass(_ctx.ns.e("prefix"))
    }, {
      default: withCtx(() => [
        createVNode(_component_check)
      ]),
      _: 1
    }, 8, ["class"])) : createCommentVNode("v-if", true),
    createCommentVNode(" content "),
    createVNode(_component_node_content),
    createCommentVNode(" postfix "),
    !_ctx.isLeaf ? (openBlock(), createElementBlock(Fragment, { key: 3 }, [
      _ctx.node.loading ? (openBlock(), createBlock(_component_el_icon, {
        key: 0,
        class: normalizeClass([_ctx.ns.is("loading"), _ctx.ns.e("postfix")])
      }, {
        default: withCtx(() => [
          createVNode(_component_loading)
        ]),
        _: 1
      }, 8, ["class"])) : (openBlock(), createBlock(_component_el_icon, {
        key: 1,
        class: normalizeClass(["arrow-right", _ctx.ns.e("postfix")])
      }, {
        default: withCtx(() => [
          createVNode(_component_arrow_right)
        ]),
        _: 1
      }, 8, ["class"]))
    ], 64)) : createCommentVNode("v-if", true)
  ], 42, ["id", "aria-haspopup", "aria-owns", "aria-expanded", "tabindex", "onMouseenter", "onFocus", "onClick"]);
}
var ElCascaderNode = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "node.vue"]]);

export { ElCascaderNode as default };
//# sourceMappingURL=node2.mjs.map
