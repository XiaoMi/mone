import { defineComponent, computed, openBlock, createElementBlock, normalizeClass, unref, withDirectives, createElementVNode, isRef, withModifiers, vModelRadio, normalizeStyle, renderSlot, createTextVNode, toDisplayString } from 'vue';
import { useRadio } from './use-radio.mjs';
import { radioButtonProps } from './radio-button.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElRadioButton"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: radioButtonProps,
  setup(__props) {
    const props = __props;
    const ns = useNamespace("radio");
    const { radioRef, focus, size, disabled, modelValue, radioGroup, actualValue } = useRadio(props);
    const activeStyle = computed(() => {
      return {
        backgroundColor: (radioGroup == null ? void 0 : radioGroup.fill) || "",
        borderColor: (radioGroup == null ? void 0 : radioGroup.fill) || "",
        boxShadow: (radioGroup == null ? void 0 : radioGroup.fill) ? `-1px 0 0 0 ${radioGroup.fill}` : "",
        color: (radioGroup == null ? void 0 : radioGroup.textColor) || ""
      };
    });
    return (_ctx, _cache) => {
      var _a;
      return openBlock(), createElementBlock("label", {
        class: normalizeClass([
          unref(ns).b("button"),
          unref(ns).is("active", unref(modelValue) === unref(actualValue)),
          unref(ns).is("disabled", unref(disabled)),
          unref(ns).is("focus", unref(focus)),
          unref(ns).bm("button", unref(size))
        ])
      }, [
        withDirectives(createElementVNode("input", {
          ref_key: "radioRef",
          ref: radioRef,
          "onUpdate:modelValue": ($event) => isRef(modelValue) ? modelValue.value = $event : null,
          class: normalizeClass(unref(ns).be("button", "original-radio")),
          value: unref(actualValue),
          type: "radio",
          name: _ctx.name || ((_a = unref(radioGroup)) == null ? void 0 : _a.name),
          disabled: unref(disabled),
          onFocus: ($event) => focus.value = true,
          onBlur: ($event) => focus.value = false,
          onClick: withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["onUpdate:modelValue", "value", "name", "disabled", "onFocus", "onBlur", "onClick"]), [
          [vModelRadio, unref(modelValue)]
        ]),
        createElementVNode("span", {
          class: normalizeClass(unref(ns).be("button", "inner")),
          style: normalizeStyle(unref(modelValue) === unref(actualValue) ? unref(activeStyle) : {}),
          onKeydown: withModifiers(() => {
          }, ["stop"])
        }, [
          renderSlot(_ctx.$slots, "default", {}, () => [
            createTextVNode(toDisplayString(_ctx.label), 1)
          ])
        ], 46, ["onKeydown"])
      ], 2);
    };
  }
});
var RadioButton = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "radio-button.vue"]]);

export { RadioButton as default };
//# sourceMappingURL=radio-button2.mjs.map
