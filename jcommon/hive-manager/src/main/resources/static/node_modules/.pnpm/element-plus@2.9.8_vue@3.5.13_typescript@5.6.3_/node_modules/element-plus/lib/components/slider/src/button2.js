'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$1 = require('../../tooltip/index.js');
var button = require('./button.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useSliderButton = require('./composables/use-slider-button.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElSliderButton"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: button.sliderButtonProps,
  emits: button.sliderButtonEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = index.useNamespace("slider");
    const initData = vue.reactive({
      hovering: false,
      dragging: false,
      isClick: false,
      startX: 0,
      currentX: 0,
      startY: 0,
      currentY: 0,
      startPosition: 0,
      newPosition: 0,
      oldValue: props.modelValue
    });
    const tooltipPersistent = vue.computed(() => !showTooltip.value ? false : persistent.value);
    const {
      disabled,
      button,
      tooltip,
      showTooltip,
      persistent,
      tooltipVisible,
      wrapperStyle,
      formatValue,
      handleMouseEnter,
      handleMouseLeave,
      onButtonDown,
      onKeyDown,
      setPosition
    } = useSliderButton.useSliderButton(props, initData, emit);
    const { hovering, dragging } = vue.toRefs(initData);
    expose({
      onButtonDown,
      onKeyDown,
      setPosition,
      hovering,
      dragging
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "button",
        ref: button,
        class: vue.normalizeClass([vue.unref(ns).e("button-wrapper"), { hover: vue.unref(hovering), dragging: vue.unref(dragging) }]),
        style: vue.normalizeStyle(vue.unref(wrapperStyle)),
        tabindex: vue.unref(disabled) ? -1 : 0,
        onMouseenter: vue.unref(handleMouseEnter),
        onMouseleave: vue.unref(handleMouseLeave),
        onMousedown: vue.unref(onButtonDown),
        onFocus: vue.unref(handleMouseEnter),
        onBlur: vue.unref(handleMouseLeave),
        onKeydown: vue.unref(onKeyDown)
      }, [
        vue.createVNode(vue.unref(index$1.ElTooltip), {
          ref_key: "tooltip",
          ref: tooltip,
          visible: vue.unref(tooltipVisible),
          placement: _ctx.placement,
          "fallback-placements": ["top", "bottom", "right", "left"],
          "stop-popper-mouse-event": false,
          "popper-class": _ctx.tooltipClass,
          disabled: !vue.unref(showTooltip),
          persistent: vue.unref(tooltipPersistent)
        }, {
          content: vue.withCtx(() => [
            vue.createElementVNode("span", null, vue.toDisplayString(vue.unref(formatValue)), 1)
          ]),
          default: vue.withCtx(() => [
            vue.createElementVNode("div", {
              class: vue.normalizeClass([vue.unref(ns).e("button"), { hover: vue.unref(hovering), dragging: vue.unref(dragging) }])
            }, null, 2)
          ]),
          _: 1
        }, 8, ["visible", "placement", "popper-class", "disabled", "persistent"])
      ], 46, ["tabindex", "onMouseenter", "onMouseleave", "onMousedown", "onFocus", "onBlur", "onKeydown"]);
    };
  }
});
var SliderButton = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "button.vue"]]);

exports["default"] = SliderButton;
//# sourceMappingURL=button2.js.map
