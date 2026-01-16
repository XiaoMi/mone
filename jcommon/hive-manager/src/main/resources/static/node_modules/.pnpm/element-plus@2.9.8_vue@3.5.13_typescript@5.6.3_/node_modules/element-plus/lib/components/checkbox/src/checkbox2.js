'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var checkbox = require('./checkbox.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useCheckbox = require('./composables/use-checkbox.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElCheckbox"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: checkbox.checkboxProps,
  emits: checkbox.checkboxEmits,
  setup(__props) {
    const props = __props;
    const slots = vue.useSlots();
    const {
      inputId,
      isLabeledByFormItem,
      isChecked,
      isDisabled,
      isFocused,
      checkboxSize,
      hasOwnLabel,
      model,
      actualValue,
      handleChange,
      onClickRoot
    } = useCheckbox.useCheckbox(props, slots);
    const ns = index.useNamespace("checkbox");
    const compKls = vue.computed(() => {
      return [
        ns.b(),
        ns.m(checkboxSize.value),
        ns.is("disabled", isDisabled.value),
        ns.is("bordered", props.border),
        ns.is("checked", isChecked.value)
      ];
    });
    const spanKls = vue.computed(() => {
      return [
        ns.e("input"),
        ns.is("disabled", isDisabled.value),
        ns.is("checked", isChecked.value),
        ns.is("indeterminate", props.indeterminate),
        ns.is("focus", isFocused.value)
      ];
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(!vue.unref(hasOwnLabel) && vue.unref(isLabeledByFormItem) ? "span" : "label"), {
        class: vue.normalizeClass(vue.unref(compKls)),
        "aria-controls": _ctx.indeterminate ? _ctx.ariaControls : null,
        onClick: vue.unref(onClickRoot)
      }, {
        default: vue.withCtx(() => {
          var _a, _b, _c, _d;
          return [
            vue.createElementVNode("span", {
              class: vue.normalizeClass(vue.unref(spanKls))
            }, [
              _ctx.trueValue || _ctx.falseValue || _ctx.trueLabel || _ctx.falseLabel ? vue.withDirectives((vue.openBlock(), vue.createElementBlock("input", {
                key: 0,
                id: vue.unref(inputId),
                "onUpdate:modelValue": ($event) => vue.isRef(model) ? model.value = $event : null,
                class: vue.normalizeClass(vue.unref(ns).e("original")),
                type: "checkbox",
                indeterminate: _ctx.indeterminate,
                name: _ctx.name,
                tabindex: _ctx.tabindex,
                disabled: vue.unref(isDisabled),
                "true-value": (_b = (_a = _ctx.trueValue) != null ? _a : _ctx.trueLabel) != null ? _b : true,
                "false-value": (_d = (_c = _ctx.falseValue) != null ? _c : _ctx.falseLabel) != null ? _d : false,
                onChange: vue.unref(handleChange),
                onFocus: ($event) => isFocused.value = true,
                onBlur: ($event) => isFocused.value = false,
                onClick: vue.withModifiers(() => {
                }, ["stop"])
              }, null, 42, ["id", "onUpdate:modelValue", "indeterminate", "name", "tabindex", "disabled", "true-value", "false-value", "onChange", "onFocus", "onBlur", "onClick"])), [
                [vue.vModelCheckbox, vue.unref(model)]
              ]) : vue.withDirectives((vue.openBlock(), vue.createElementBlock("input", {
                key: 1,
                id: vue.unref(inputId),
                "onUpdate:modelValue": ($event) => vue.isRef(model) ? model.value = $event : null,
                class: vue.normalizeClass(vue.unref(ns).e("original")),
                type: "checkbox",
                indeterminate: _ctx.indeterminate,
                disabled: vue.unref(isDisabled),
                value: vue.unref(actualValue),
                name: _ctx.name,
                tabindex: _ctx.tabindex,
                onChange: vue.unref(handleChange),
                onFocus: ($event) => isFocused.value = true,
                onBlur: ($event) => isFocused.value = false,
                onClick: vue.withModifiers(() => {
                }, ["stop"])
              }, null, 42, ["id", "onUpdate:modelValue", "indeterminate", "disabled", "value", "name", "tabindex", "onChange", "onFocus", "onBlur", "onClick"])), [
                [vue.vModelCheckbox, vue.unref(model)]
              ]),
              vue.createElementVNode("span", {
                class: vue.normalizeClass(vue.unref(ns).e("inner"))
              }, null, 2)
            ], 2),
            vue.unref(hasOwnLabel) ? (vue.openBlock(), vue.createElementBlock("span", {
              key: 0,
              class: vue.normalizeClass(vue.unref(ns).e("label"))
            }, [
              vue.renderSlot(_ctx.$slots, "default"),
              !_ctx.$slots.default ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 0 }, [
                vue.createTextVNode(vue.toDisplayString(_ctx.label), 1)
              ], 64)) : vue.createCommentVNode("v-if", true)
            ], 2)) : vue.createCommentVNode("v-if", true)
          ];
        }),
        _: 3
      }, 8, ["class", "aria-controls", "onClick"]);
    };
  }
});
var Checkbox = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "checkbox.vue"]]);

exports["default"] = Checkbox;
//# sourceMappingURL=checkbox2.js.map
