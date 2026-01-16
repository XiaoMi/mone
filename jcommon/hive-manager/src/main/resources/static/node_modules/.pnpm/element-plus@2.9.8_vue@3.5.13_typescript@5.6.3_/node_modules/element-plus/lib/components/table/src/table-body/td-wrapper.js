'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');

const __default__ = vue.defineComponent({
  name: "TableTdWrapper"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: {
    colspan: {
      type: Number,
      default: 1
    },
    rowspan: {
      type: Number,
      default: 1
    }
  },
  setup(__props) {
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("td", {
        colspan: __props.colspan,
        rowspan: __props.rowspan
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 8, ["colspan", "rowspan"]);
    };
  }
});
var TdWrapper = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "td-wrapper.vue"]]);

exports["default"] = TdWrapper;
//# sourceMappingURL=td-wrapper.js.map
