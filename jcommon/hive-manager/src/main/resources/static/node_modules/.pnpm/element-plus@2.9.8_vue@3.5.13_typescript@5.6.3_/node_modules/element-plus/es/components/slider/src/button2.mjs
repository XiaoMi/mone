import { defineComponent, reactive, computed, toRefs, openBlock, createElementBlock, normalizeClass, unref, normalizeStyle, createVNode, withCtx, createElementVNode, toDisplayString } from 'vue';
import { ElTooltip } from '../../tooltip/index.mjs';
import { sliderButtonProps, sliderButtonEmits } from './button.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useSliderButton } from './composables/use-slider-button.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElSliderButton"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: sliderButtonProps,
  emits: sliderButtonEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = useNamespace("slider");
    const initData = reactive({
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
    const tooltipPersistent = computed(() => !showTooltip.value ? false : persistent.value);
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
    } = useSliderButton(props, initData, emit);
    const { hovering, dragging } = toRefs(initData);
    expose({
      onButtonDown,
      onKeyDown,
      setPosition,
      hovering,
      dragging
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        ref_key: "button",
        ref: button,
        class: normalizeClass([unref(ns).e("button-wrapper"), { hover: unref(hovering), dragging: unref(dragging) }]),
        style: normalizeStyle(unref(wrapperStyle)),
        tabindex: unref(disabled) ? -1 : 0,
        onMouseenter: unref(handleMouseEnter),
        onMouseleave: unref(handleMouseLeave),
        onMousedown: unref(onButtonDown),
        onFocus: unref(handleMouseEnter),
        onBlur: unref(handleMouseLeave),
        onKeydown: unref(onKeyDown)
      }, [
        createVNode(unref(ElTooltip), {
          ref_key: "tooltip",
          ref: tooltip,
          visible: unref(tooltipVisible),
          placement: _ctx.placement,
          "fallback-placements": ["top", "bottom", "right", "left"],
          "stop-popper-mouse-event": false,
          "popper-class": _ctx.tooltipClass,
          disabled: !unref(showTooltip),
          persistent: unref(tooltipPersistent)
        }, {
          content: withCtx(() => [
            createElementVNode("span", null, toDisplayString(unref(formatValue)), 1)
          ]),
          default: withCtx(() => [
            createElementVNode("div", {
              class: normalizeClass([unref(ns).e("button"), { hover: unref(hovering), dragging: unref(dragging) }])
            }, null, 2)
          ]),
          _: 1
        }, 8, ["visible", "placement", "popper-class", "disabled", "persistent"])
      ], 46, ["tabindex", "onMouseenter", "onMouseleave", "onMousedown", "onFocus", "onBlur", "onKeydown"]);
    };
  }
});
var SliderButton = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "button.vue"]]);

export { SliderButton as default };
//# sourceMappingURL=button2.mjs.map
