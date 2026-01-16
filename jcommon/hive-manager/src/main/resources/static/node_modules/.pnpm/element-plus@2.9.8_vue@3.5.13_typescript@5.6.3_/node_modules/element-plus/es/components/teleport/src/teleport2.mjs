import { defineComponent, renderSlot, openBlock, createBlock, Teleport as Teleport$1 } from 'vue';
import { teleportProps } from './teleport.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "teleport",
  props: teleportProps,
  setup(__props) {
    return (_ctx, _cache) => {
      return _ctx.disabled ? renderSlot(_ctx.$slots, "default", { key: 0 }) : (openBlock(), createBlock(Teleport$1, {
        key: 1,
        to: _ctx.to
      }, [
        renderSlot(_ctx.$slots, "default")
      ], 8, ["to"]));
    };
  }
});
var Teleport = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "teleport.vue"]]);

export { Teleport as default };
//# sourceMappingURL=teleport2.mjs.map
