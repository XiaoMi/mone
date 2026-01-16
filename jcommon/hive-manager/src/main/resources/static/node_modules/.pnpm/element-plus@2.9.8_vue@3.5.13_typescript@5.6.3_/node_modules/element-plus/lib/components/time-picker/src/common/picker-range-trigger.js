'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var props = require('./props.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../../hooks/use-attrs/index.js');
var index$1 = require('../../../../hooks/use-namespace/index.js');
var index$2 = require('../../../../hooks/use-focus-controller/index.js');

const __default__ = vue.defineComponent({
  name: "PickerRangeTrigger",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: props.timePickerRangeTriggerProps,
  emits: [
    "mouseenter",
    "mouseleave",
    "click",
    "touchstart",
    "focus",
    "blur",
    "startInput",
    "endInput",
    "startChange",
    "endChange"
  ],
  setup(__props, { expose, emit }) {
    const attrs = index.useAttrs();
    const nsDate = index$1.useNamespace("date");
    const nsRange = index$1.useNamespace("range");
    const inputRef = vue.ref();
    const endInputRef = vue.ref();
    const { wrapperRef, isFocused } = index$2.useFocusController(inputRef);
    const handleClick = (evt) => {
      emit("click", evt);
    };
    const handleMouseEnter = (evt) => {
      emit("mouseenter", evt);
    };
    const handleMouseLeave = (evt) => {
      emit("mouseleave", evt);
    };
    const handleTouchStart = (evt) => {
      emit("mouseenter", evt);
    };
    const handleStartInput = (evt) => {
      emit("startInput", evt);
    };
    const handleEndInput = (evt) => {
      emit("endInput", evt);
    };
    const handleStartChange = (evt) => {
      emit("startChange", evt);
    };
    const handleEndChange = (evt) => {
      emit("endChange", evt);
    };
    const focus = () => {
      var _a;
      (_a = inputRef.value) == null ? void 0 : _a.focus();
    };
    const blur = () => {
      var _a, _b;
      (_a = inputRef.value) == null ? void 0 : _a.blur();
      (_b = endInputRef.value) == null ? void 0 : _b.blur();
    };
    expose({
      focus,
      blur
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "wrapperRef",
        ref: wrapperRef,
        class: vue.normalizeClass([vue.unref(nsDate).is("active", vue.unref(isFocused)), _ctx.$attrs.class]),
        style: vue.normalizeStyle(_ctx.$attrs.style),
        onClick: handleClick,
        onMouseenter: handleMouseEnter,
        onMouseleave: handleMouseLeave,
        onTouchstartPassive: handleTouchStart
      }, [
        vue.renderSlot(_ctx.$slots, "prefix"),
        vue.createElementVNode("input", vue.mergeProps(vue.unref(attrs), {
          id: _ctx.id && _ctx.id[0],
          ref_key: "inputRef",
          ref: inputRef,
          name: _ctx.name && _ctx.name[0],
          placeholder: _ctx.startPlaceholder,
          value: _ctx.modelValue && _ctx.modelValue[0],
          class: vue.unref(nsRange).b("input"),
          disabled: _ctx.disabled,
          onInput: handleStartInput,
          onChange: handleStartChange
        }), null, 16, ["id", "name", "placeholder", "value", "disabled"]),
        vue.renderSlot(_ctx.$slots, "range-separator"),
        vue.createElementVNode("input", vue.mergeProps(vue.unref(attrs), {
          id: _ctx.id && _ctx.id[1],
          ref_key: "endInputRef",
          ref: endInputRef,
          name: _ctx.name && _ctx.name[1],
          placeholder: _ctx.endPlaceholder,
          value: _ctx.modelValue && _ctx.modelValue[1],
          class: vue.unref(nsRange).b("input"),
          disabled: _ctx.disabled,
          onInput: handleEndInput,
          onChange: handleEndChange
        }), null, 16, ["id", "name", "placeholder", "value", "disabled"]),
        vue.renderSlot(_ctx.$slots, "suffix")
      ], 38);
    };
  }
});
var PickerRangeTrigger = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "picker-range-trigger.vue"]]);

exports["default"] = PickerRangeTrigger;
//# sourceMappingURL=picker-range-trigger.js.map
