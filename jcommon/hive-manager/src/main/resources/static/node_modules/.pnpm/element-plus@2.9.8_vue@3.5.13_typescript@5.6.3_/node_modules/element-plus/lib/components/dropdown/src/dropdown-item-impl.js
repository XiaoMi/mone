'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../icon/index.js');
var dropdown = require('./dropdown.js');
var tokens = require('./tokens.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var rovingFocusGroup = require('../../roving-focus-group/src/roving-focus-group.js');
var tokens$1 = require('../../roving-focus-group/src/tokens.js');
var collection = require('../../collection/src/collection.js');
var index$1 = require('../../../hooks/use-namespace/index.js');
var refs = require('../../../utils/vue/refs.js');
var event = require('../../../utils/dom/event.js');
var aria = require('../../../constants/aria.js');

const _sfc_main = vue.defineComponent({
  name: "DropdownItemImpl",
  components: {
    ElIcon: index.ElIcon
  },
  props: dropdown.dropdownItemProps,
  emits: ["pointermove", "pointerleave", "click", "clickimpl"],
  setup(_, { emit }) {
    const ns = index$1.useNamespace("dropdown");
    const { role: menuRole } = vue.inject(tokens.DROPDOWN_INJECTION_KEY, void 0);
    const { collectionItemRef: dropdownCollectionItemRef } = vue.inject(dropdown.DROPDOWN_COLLECTION_ITEM_INJECTION_KEY, void 0);
    const { collectionItemRef: rovingFocusCollectionItemRef } = vue.inject(rovingFocusGroup.ROVING_FOCUS_ITEM_COLLECTION_INJECTION_KEY, void 0);
    const {
      rovingFocusGroupItemRef,
      tabIndex,
      handleFocus,
      handleKeydown: handleItemKeydown,
      handleMousedown
    } = vue.inject(tokens$1.ROVING_FOCUS_GROUP_ITEM_INJECTION_KEY, void 0);
    const itemRef = refs.composeRefs(dropdownCollectionItemRef, rovingFocusCollectionItemRef, rovingFocusGroupItemRef);
    const role = vue.computed(() => {
      if (menuRole.value === "menu") {
        return "menuitem";
      } else if (menuRole.value === "navigation") {
        return "link";
      }
      return "button";
    });
    const handleKeydown = event.composeEventHandlers((e) => {
      if ([aria.EVENT_CODE.enter, aria.EVENT_CODE.numpadEnter, aria.EVENT_CODE.space].includes(e.code)) {
        e.preventDefault();
        e.stopImmediatePropagation();
        emit("clickimpl", e);
        return true;
      }
    }, handleItemKeydown);
    return {
      ns,
      itemRef,
      dataset: {
        [collection.COLLECTION_ITEM_SIGN]: ""
      },
      role,
      tabIndex,
      handleFocus,
      handleKeydown,
      handleMousedown
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  const _component_el_icon = vue.resolveComponent("el-icon");
  return vue.openBlock(), vue.createElementBlock(vue.Fragment, null, [
    _ctx.divided ? (vue.openBlock(), vue.createElementBlock("li", {
      key: 0,
      role: "separator",
      class: vue.normalizeClass(_ctx.ns.bem("menu", "item", "divided"))
    }, null, 2)) : vue.createCommentVNode("v-if", true),
    vue.createElementVNode("li", vue.mergeProps({ ref: _ctx.itemRef }, { ..._ctx.dataset, ..._ctx.$attrs }, {
      "aria-disabled": _ctx.disabled,
      class: [_ctx.ns.be("menu", "item"), _ctx.ns.is("disabled", _ctx.disabled)],
      tabindex: _ctx.tabIndex,
      role: _ctx.role,
      onClick: (e) => _ctx.$emit("clickimpl", e),
      onFocus: _ctx.handleFocus,
      onKeydown: vue.withModifiers(_ctx.handleKeydown, ["self"]),
      onMousedown: _ctx.handleMousedown,
      onPointermove: (e) => _ctx.$emit("pointermove", e),
      onPointerleave: (e) => _ctx.$emit("pointerleave", e)
    }), [
      _ctx.icon ? (vue.openBlock(), vue.createBlock(_component_el_icon, { key: 0 }, {
        default: vue.withCtx(() => [
          (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.icon)))
        ]),
        _: 1
      })) : vue.createCommentVNode("v-if", true),
      vue.renderSlot(_ctx.$slots, "default")
    ], 16, ["aria-disabled", "tabindex", "role", "onClick", "onFocus", "onKeydown", "onMousedown", "onPointermove", "onPointerleave"])
  ], 64);
}
var ElDropdownItemImpl = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "dropdown-item-impl.vue"]]);

exports["default"] = ElDropdownItemImpl;
//# sourceMappingURL=dropdown-item-impl.js.map
