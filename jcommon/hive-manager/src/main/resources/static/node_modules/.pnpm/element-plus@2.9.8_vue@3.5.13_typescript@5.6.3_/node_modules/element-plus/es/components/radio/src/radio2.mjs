import { defineComponent, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, withDirectives, isRef, withModifiers, vModelRadio, renderSlot, createTextVNode, toDisplayString, nextTick } from 'vue';
import { radioProps, radioEmits } from './radio.mjs';
import { useRadio } from './use-radio.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElRadio"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: radioProps,
  emits: radioEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = useNamespace("radio");
    const { radioRef, radioGroup, focus, size, disabled, modelValue, actualValue } = useRadio(props, emit);
    function handleChange() {
      nextTick(() => emit(CHANGE_EVENT, modelValue.value));
    }
    return (_ctx, _cache) => {
      var _a;
      return openBlock(), createElementBlock("label", {
        class: normalizeClass([
          unref(ns).b(),
          unref(ns).is("disabled", unref(disabled)),
          unref(ns).is("focus", unref(focus)),
          unref(ns).is("bordered", _ctx.border),
          unref(ns).is("checked", unref(modelValue) === unref(actualValue)),
          unref(ns).m(unref(size))
        ])
      }, [
        createElementVNode("span", {
          class: normalizeClass([
            unref(ns).e("input"),
            unref(ns).is("disabled", unref(disabled)),
            unref(ns).is("checked", unref(modelValue) === unref(actualValue))
          ])
        }, [
          withDirectives(createElementVNode("input", {
            ref_key: "radioRef",
            ref: radioRef,
            "onUpdate:modelValue": ($event) => isRef(modelValue) ? modelValue.value = $event : null,
            class: normalizeClass(unref(ns).e("original")),
            value: unref(actualValue),
            name: _ctx.name || ((_a = unref(radioGroup)) == null ? void 0 : _a.name),
            disabled: unref(disabled),
            checked: unref(modelValue) === unref(actualValue),
            type: "radio",
            onFocus: ($event) => focus.value = true,
            onBlur: ($event) => focus.value = false,
            onChange: handleChange,
            onClick: withModifiers(() => {
            }, ["stop"])
          }, null, 42, ["onUpdate:modelValue", "value", "name", "disabled", "checked", "onFocus", "onBlur", "onClick"]), [
            [vModelRadio, unref(modelValue)]
          ]),
          createElementVNode("span", {
            class: normalizeClass(unref(ns).e("inner"))
          }, null, 2)
        ], 2),
        createElementVNode("span", {
          class: normalizeClass(unref(ns).e("label")),
          onKeydown: withModifiers(() => {
          }, ["stop"])
        }, [
          renderSlot(_ctx.$slots, "default", {}, () => [
            createTextVNode(toDisplayString(_ctx.label), 1)
          ])
        ], 42, ["onKeydown"])
      ], 2);
    };
  }
});
var Radio = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "radio.vue"]]);

export { Radio as default };
//# sourceMappingURL=radio2.mjs.map
