import { defineComponent, useSlots, computed, openBlock, createBlock, resolveDynamicComponent, unref, normalizeClass, withCtx, createElementVNode, withDirectives, createElementBlock, isRef, withModifiers, vModelCheckbox, renderSlot, Fragment, createTextVNode, toDisplayString, createCommentVNode } from 'vue';
import { checkboxProps, checkboxEmits } from './checkbox.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useCheckbox } from './composables/use-checkbox.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElCheckbox"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: checkboxProps,
  emits: checkboxEmits,
  setup(__props) {
    const props = __props;
    const slots = useSlots();
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
    } = useCheckbox(props, slots);
    const ns = useNamespace("checkbox");
    const compKls = computed(() => {
      return [
        ns.b(),
        ns.m(checkboxSize.value),
        ns.is("disabled", isDisabled.value),
        ns.is("bordered", props.border),
        ns.is("checked", isChecked.value)
      ];
    });
    const spanKls = computed(() => {
      return [
        ns.e("input"),
        ns.is("disabled", isDisabled.value),
        ns.is("checked", isChecked.value),
        ns.is("indeterminate", props.indeterminate),
        ns.is("focus", isFocused.value)
      ];
    });
    return (_ctx, _cache) => {
      return openBlock(), createBlock(resolveDynamicComponent(!unref(hasOwnLabel) && unref(isLabeledByFormItem) ? "span" : "label"), {
        class: normalizeClass(unref(compKls)),
        "aria-controls": _ctx.indeterminate ? _ctx.ariaControls : null,
        onClick: unref(onClickRoot)
      }, {
        default: withCtx(() => {
          var _a, _b, _c, _d;
          return [
            createElementVNode("span", {
              class: normalizeClass(unref(spanKls))
            }, [
              _ctx.trueValue || _ctx.falseValue || _ctx.trueLabel || _ctx.falseLabel ? withDirectives((openBlock(), createElementBlock("input", {
                key: 0,
                id: unref(inputId),
                "onUpdate:modelValue": ($event) => isRef(model) ? model.value = $event : null,
                class: normalizeClass(unref(ns).e("original")),
                type: "checkbox",
                indeterminate: _ctx.indeterminate,
                name: _ctx.name,
                tabindex: _ctx.tabindex,
                disabled: unref(isDisabled),
                "true-value": (_b = (_a = _ctx.trueValue) != null ? _a : _ctx.trueLabel) != null ? _b : true,
                "false-value": (_d = (_c = _ctx.falseValue) != null ? _c : _ctx.falseLabel) != null ? _d : false,
                onChange: unref(handleChange),
                onFocus: ($event) => isFocused.value = true,
                onBlur: ($event) => isFocused.value = false,
                onClick: withModifiers(() => {
                }, ["stop"])
              }, null, 42, ["id", "onUpdate:modelValue", "indeterminate", "name", "tabindex", "disabled", "true-value", "false-value", "onChange", "onFocus", "onBlur", "onClick"])), [
                [vModelCheckbox, unref(model)]
              ]) : withDirectives((openBlock(), createElementBlock("input", {
                key: 1,
                id: unref(inputId),
                "onUpdate:modelValue": ($event) => isRef(model) ? model.value = $event : null,
                class: normalizeClass(unref(ns).e("original")),
                type: "checkbox",
                indeterminate: _ctx.indeterminate,
                disabled: unref(isDisabled),
                value: unref(actualValue),
                name: _ctx.name,
                tabindex: _ctx.tabindex,
                onChange: unref(handleChange),
                onFocus: ($event) => isFocused.value = true,
                onBlur: ($event) => isFocused.value = false,
                onClick: withModifiers(() => {
                }, ["stop"])
              }, null, 42, ["id", "onUpdate:modelValue", "indeterminate", "disabled", "value", "name", "tabindex", "onChange", "onFocus", "onBlur", "onClick"])), [
                [vModelCheckbox, unref(model)]
              ]),
              createElementVNode("span", {
                class: normalizeClass(unref(ns).e("inner"))
              }, null, 2)
            ], 2),
            unref(hasOwnLabel) ? (openBlock(), createElementBlock("span", {
              key: 0,
              class: normalizeClass(unref(ns).e("label"))
            }, [
              renderSlot(_ctx.$slots, "default"),
              !_ctx.$slots.default ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
                createTextVNode(toDisplayString(_ctx.label), 1)
              ], 64)) : createCommentVNode("v-if", true)
            ], 2)) : createCommentVNode("v-if", true)
          ];
        }),
        _: 3
      }, 8, ["class", "aria-controls", "onClick"]);
    };
  }
});
var Checkbox = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "checkbox.vue"]]);

export { Checkbox as default };
//# sourceMappingURL=checkbox2.mjs.map
