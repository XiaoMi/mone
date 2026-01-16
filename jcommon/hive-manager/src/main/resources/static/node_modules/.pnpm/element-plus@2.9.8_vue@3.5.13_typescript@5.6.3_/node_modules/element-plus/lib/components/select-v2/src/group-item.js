'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const _sfc_main = vue.defineComponent({
  props: {
    item: {
      type: Object,
      required: true
    },
    style: {
      type: Object
    },
    height: Number
  },
  setup() {
    const ns = index.useNamespace("select");
    return {
      ns
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return vue.openBlock(), vue.createElementBlock("div", {
    class: vue.normalizeClass(_ctx.ns.be("group", "title")),
    style: vue.normalizeStyle({ ..._ctx.style, lineHeight: `${_ctx.height}px` })
  }, vue.toDisplayString(_ctx.item.label), 7);
}
var GroupItem = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "group-item.vue"]]);

exports["default"] = GroupItem;
//# sourceMappingURL=group-item.js.map
