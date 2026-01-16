import { useAttrs, useSlots, computed } from 'vue';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';

function useInputTagDom({
  props,
  isFocused,
  hovering,
  disabled,
  inputValue,
  size,
  validateState,
  validateIcon,
  needStatusIcon
}) {
  const attrs = useAttrs();
  const slots = useSlots();
  const ns = useNamespace("input-tag");
  const nsInput = useNamespace("input");
  const containerKls = computed(() => [
    ns.b(),
    ns.is("focused", isFocused.value),
    ns.is("hovering", hovering.value),
    ns.is("disabled", disabled.value),
    ns.m(size.value),
    ns.e("wrapper"),
    attrs.class
  ]);
  const containerStyle = computed(() => [attrs.style]);
  const innerKls = computed(() => {
    var _a, _b;
    return [
      ns.e("inner"),
      ns.is("draggable", props.draggable),
      ns.is("left-space", !((_a = props.modelValue) == null ? void 0 : _a.length) && !slots.prefix),
      ns.is("right-space", !((_b = props.modelValue) == null ? void 0 : _b.length) && !showSuffix.value)
    ];
  });
  const showClear = computed(() => {
    var _a;
    return props.clearable && !disabled.value && !props.readonly && (((_a = props.modelValue) == null ? void 0 : _a.length) || inputValue.value) && (isFocused.value || hovering.value);
  });
  const showSuffix = computed(() => {
    return slots.suffix || showClear.value || validateState.value && validateIcon.value && needStatusIcon.value;
  });
  return {
    ns,
    nsInput,
    containerKls,
    containerStyle,
    innerKls,
    showClear,
    showSuffix
  };
}

export { useInputTagDom };
//# sourceMappingURL=use-input-tag-dom.mjs.map
