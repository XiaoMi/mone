'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var icon = require('./icon.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');
var style = require('../../../utils/dom/style.js');

const __default__ = vue.defineComponent({
  name: "ElIcon",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: icon.iconProps,
  setup(__props) {
    const props = __props;
    const ns = index.useNamespace("icon");
    const style$1 = vue.computed(() => {
      const { size, color } = props;
      if (!size && !color)
        return {};
      return {
        fontSize: types.isUndefined(size) ? void 0 : style.addUnit(size),
        "--color": color
      };
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("i", vue.mergeProps({
        class: vue.unref(ns).b(),
        style: vue.unref(style$1)
      }, _ctx.$attrs), [
        vue.renderSlot(_ctx.$slots, "default")
      ], 16);
    };
  }
});
var Icon = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "icon.vue"]]);

exports["default"] = Icon;
//# sourceMappingURL=icon2.js.map
