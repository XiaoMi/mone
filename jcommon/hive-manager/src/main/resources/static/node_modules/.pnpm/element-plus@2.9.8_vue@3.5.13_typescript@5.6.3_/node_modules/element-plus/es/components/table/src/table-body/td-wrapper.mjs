import { defineComponent, openBlock, createElementBlock, renderSlot } from 'vue';
import _export_sfc from '../../../../_virtual/plugin-vue_export-helper.mjs';

const __default__ = defineComponent({
  name: "TableTdWrapper"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
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
      return openBlock(), createElementBlock("td", {
        colspan: __props.colspan,
        rowspan: __props.rowspan
      }, [
        renderSlot(_ctx.$slots, "default")
      ], 8, ["colspan", "rowspan"]);
    };
  }
});
var TdWrapper = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "td-wrapper.vue"]]);

export { TdWrapper as default };
//# sourceMappingURL=td-wrapper.mjs.map
