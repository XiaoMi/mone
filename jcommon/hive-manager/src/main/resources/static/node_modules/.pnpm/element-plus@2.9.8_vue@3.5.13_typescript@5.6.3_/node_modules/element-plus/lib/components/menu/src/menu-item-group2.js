'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var menuItemGroup = require('./menu-item-group.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElMenuItemGroup"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: menuItemGroup.menuItemGroupProps,
  setup(__props) {
    const ns = index.useNamespace("menu-item-group");
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("li", {
        class: vue.normalizeClass(vue.unref(ns).b())
      }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(ns).e("title"))
        }, [
          !_ctx.$slots.title ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 0 }, [
            vue.createTextVNode(vue.toDisplayString(_ctx.title), 1)
          ], 64)) : vue.renderSlot(_ctx.$slots, "title", { key: 1 })
        ], 2),
        vue.createElementVNode("ul", null, [
          vue.renderSlot(_ctx.$slots, "default")
        ])
      ], 2);
    };
  }
});
var MenuItemGroup = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "menu-item-group.vue"]]);

exports["default"] = MenuItemGroup;
//# sourceMappingURL=menu-item-group2.js.map
