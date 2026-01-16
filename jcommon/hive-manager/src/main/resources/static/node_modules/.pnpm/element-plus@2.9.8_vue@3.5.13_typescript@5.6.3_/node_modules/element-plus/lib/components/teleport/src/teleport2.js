'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var teleport = require('./teleport.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');

const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  __name: "teleport",
  props: teleport.teleportProps,
  setup(__props) {
    return (_ctx, _cache) => {
      return _ctx.disabled ? vue.renderSlot(_ctx.$slots, "default", { key: 0 }) : (vue.openBlock(), vue.createBlock(vue.Teleport, {
        key: 1,
        to: _ctx.to
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 8, ["to"]));
    };
  }
});
var Teleport = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "teleport.vue"]]);

exports["default"] = Teleport;
//# sourceMappingURL=teleport2.js.map
