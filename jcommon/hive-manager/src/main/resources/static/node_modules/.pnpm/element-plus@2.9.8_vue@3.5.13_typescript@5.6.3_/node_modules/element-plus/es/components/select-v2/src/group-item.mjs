import { defineComponent, openBlock, createElementBlock, normalizeClass, normalizeStyle, toDisplayString } from 'vue';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const _sfc_main = defineComponent({
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
    const ns = useNamespace("select");
    return {
      ns
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return openBlock(), createElementBlock("div", {
    class: normalizeClass(_ctx.ns.be("group", "title")),
    style: normalizeStyle({ ..._ctx.style, lineHeight: `${_ctx.height}px` })
  }, toDisplayString(_ctx.item.label), 7);
}
var GroupItem = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "group-item.vue"]]);

export { GroupItem as default };
//# sourceMappingURL=group-item.mjs.map
