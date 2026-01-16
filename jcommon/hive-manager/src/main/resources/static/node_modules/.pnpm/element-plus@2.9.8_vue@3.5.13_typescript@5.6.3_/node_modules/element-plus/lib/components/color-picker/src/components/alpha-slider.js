'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var alphaSlider = require('../props/alpha-slider.js');
var useAlphaSlider = require('../composables/use-alpha-slider.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');

const COMPONENT_NAME = "ElColorAlphaSlider";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: alphaSlider.alphaSliderProps,
  setup(__props, { expose }) {
    const props = __props;
    const {
      alpha,
      alphaLabel,
      bar,
      thumb,
      handleDrag,
      handleClick,
      handleKeydown
    } = useAlphaSlider.useAlphaSlider(props);
    const { rootKls, barKls, barStyle, thumbKls, thumbStyle, update } = useAlphaSlider.useAlphaSliderDOM(props, {
      bar,
      thumb,
      handleDrag
    });
    expose({
      update,
      bar,
      thumb
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(rootKls))
      }, [
        vue.createElementVNode("div", {
          ref_key: "bar",
          ref: bar,
          class: vue.normalizeClass(vue.unref(barKls)),
          style: vue.normalizeStyle(vue.unref(barStyle)),
          onClick: vue.unref(handleClick)
        }, null, 14, ["onClick"]),
        vue.createElementVNode("div", {
          ref_key: "thumb",
          ref: thumb,
          class: vue.normalizeClass(vue.unref(thumbKls)),
          style: vue.normalizeStyle(vue.unref(thumbStyle)),
          "aria-label": vue.unref(alphaLabel),
          "aria-valuenow": vue.unref(alpha),
          "aria-orientation": _ctx.vertical ? "vertical" : "horizontal",
          "aria-valuemin": "0",
          "aria-valuemax": "100",
          role: "slider",
          tabindex: "0",
          onKeydown: vue.unref(handleKeydown)
        }, null, 46, ["aria-label", "aria-valuenow", "aria-orientation", "onKeydown"])
      ], 2);
    };
  }
});
var AlphaSlider = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "alpha-slider.vue"]]);

exports["default"] = AlphaSlider;
//# sourceMappingURL=alpha-slider.js.map
