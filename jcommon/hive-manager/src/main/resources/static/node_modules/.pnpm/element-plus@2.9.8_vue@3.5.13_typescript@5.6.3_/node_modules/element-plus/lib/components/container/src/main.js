'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElMain"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  setup(__props) {
    const ns = index.useNamespace("main");
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("main", {
        class: vue.normalizeClass(vue.unref(ns).b())
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 2);
    };
  }
});
var Main = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "main.vue"]]);

exports["default"] = Main;
//# sourceMappingURL=main.js.map
