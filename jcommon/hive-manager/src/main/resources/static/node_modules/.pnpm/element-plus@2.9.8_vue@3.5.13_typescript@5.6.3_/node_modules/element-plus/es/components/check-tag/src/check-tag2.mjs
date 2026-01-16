import { defineComponent, computed, openBlock, createElementBlock, normalizeClass, unref, renderSlot } from 'vue';
import { checkTagProps, checkTagEmits } from './check-tag.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElCheckTag"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: checkTagProps,
  emits: checkTagEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = useNamespace("check-tag");
    const isDisabled = computed(() => props.disabled);
    const containerKls = computed(() => [
      ns.b(),
      ns.is("checked", props.checked),
      ns.is("disabled", isDisabled.value),
      ns.m(props.type || "primary")
    ]);
    const handleChange = () => {
      if (isDisabled.value)
        return;
      const checked = !props.checked;
      emit(CHANGE_EVENT, checked);
      emit("update:checked", checked);
    };
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("span", {
        class: normalizeClass(unref(containerKls)),
        onClick: handleChange
      }, [
        renderSlot(_ctx.$slots, "default")
      ], 2);
    };
  }
});
var CheckTag = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "check-tag.vue"]]);

export { CheckTag as default };
//# sourceMappingURL=check-tag2.mjs.map
