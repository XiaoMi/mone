import { defineComponent, useSlots, inject, computed, openBlock, createElementBlock, normalizeClass, unref, withDirectives, isRef, withModifiers, vModelCheckbox, normalizeStyle, renderSlot, createTextVNode, toDisplayString, createCommentVNode } from 'vue';
import { checkboxGroupContextKey } from './constants.mjs';
import { checkboxProps, checkboxEmits } from './checkbox.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useCheckbox } from './composables/use-checkbox.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElCheckboxButton"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: checkboxProps,
  emits: checkboxEmits,
  setup(__props) {
    const props = __props;
    const slots = useSlots();
    const {
      isFocused,
      isChecked,
      isDisabled,
      checkboxButtonSize,
      model,
      actualValue,
      handleChange
    } = useCheckbox(props, slots);
    const checkboxGroup = inject(checkboxGroupContextKey, void 0);
    const ns = useNamespace("checkbox");
    const activeStyle = computed(() => {
      var _a, _b, _c, _d;
      const fillValue = (_b = (_a = checkboxGroup == null ? void 0 : checkboxGroup.fill) == null ? void 0 : _a.value) != null ? _b : "";
      return {
        backgroundColor: fillValue,
        borderColor: fillValue,
        color: (_d = (_c = checkboxGroup == null ? void 0 : checkboxGroup.textColor) == null ? void 0 : _c.value) != null ? _d : "",
        boxShadow: fillValue ? `-1px 0 0 0 ${fillValue}` : void 0
      };
    });
    const labelKls = computed(() => {
      return [
        ns.b("button"),
        ns.bm("button", checkboxButtonSize.value),
        ns.is("disabled", isDisabled.value),
        ns.is("checked", isChecked.value),
        ns.is("focus", isFocused.value)
      ];
    });
    return (_ctx, _cache) => {
      var _a, _b, _c, _d;
      return openBlock(), createElementBlock("label", {
        class: normalizeClass(unref(labelKls))
      }, [
        _ctx.trueValue || _ctx.falseValue || _ctx.trueLabel || _ctx.falseLabel ? withDirectives((openBlock(), createElementBlock("input", {
          key: 0,
          "onUpdate:modelValue": ($event) => isRef(model) ? model.value = $event : null,
          class: normalizeClass(unref(ns).be("button", "original")),
          type: "checkbox",
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
        }, null, 42, ["onUpdate:modelValue", "name", "tabindex", "disabled", "true-value", "false-value", "onChange", "onFocus", "onBlur", "onClick"])), [
          [vModelCheckbox, unref(model)]
        ]) : withDirectives((openBlock(), createElementBlock("input", {
          key: 1,
          "onUpdate:modelValue": ($event) => isRef(model) ? model.value = $event : null,
          class: normalizeClass(unref(ns).be("button", "original")),
          type: "checkbox",
          name: _ctx.name,
          tabindex: _ctx.tabindex,
          disabled: unref(isDisabled),
          value: unref(actualValue),
          onChange: unref(handleChange),
          onFocus: ($event) => isFocused.value = true,
          onBlur: ($event) => isFocused.value = false,
          onClick: withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["onUpdate:modelValue", "name", "tabindex", "disabled", "value", "onChange", "onFocus", "onBlur", "onClick"])), [
          [vModelCheckbox, unref(model)]
        ]),
        _ctx.$slots.default || _ctx.label ? (openBlock(), createElementBlock("span", {
          key: 2,
          class: normalizeClass(unref(ns).be("button", "inner")),
          style: normalizeStyle(unref(isChecked) ? unref(activeStyle) : void 0)
        }, [
          renderSlot(_ctx.$slots, "default", {}, () => [
            createTextVNode(toDisplayString(_ctx.label), 1)
          ])
        ], 6)) : createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var CheckboxButton = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "checkbox-button.vue"]]);

export { CheckboxButton as default };
//# sourceMappingURL=checkbox-button.mjs.map
