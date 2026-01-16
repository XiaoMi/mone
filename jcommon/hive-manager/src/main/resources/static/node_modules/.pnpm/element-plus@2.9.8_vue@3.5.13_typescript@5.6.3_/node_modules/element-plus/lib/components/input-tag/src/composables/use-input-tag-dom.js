'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../../../hooks/use-namespace/index.js');

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
  const attrs = vue.useAttrs();
  const slots = vue.useSlots();
  const ns = index.useNamespace("input-tag");
  const nsInput = index.useNamespace("input");
  const containerKls = vue.computed(() => [
    ns.b(),
    ns.is("focused", isFocused.value),
    ns.is("hovering", hovering.value),
    ns.is("disabled", disabled.value),
    ns.m(size.value),
    ns.e("wrapper"),
    attrs.class
  ]);
  const containerStyle = vue.computed(() => [attrs.style]);
  const innerKls = vue.computed(() => {
    var _a, _b;
    return [
      ns.e("inner"),
      ns.is("draggable", props.draggable),
      ns.is("left-space", !((_a = props.modelValue) == null ? void 0 : _a.length) && !slots.prefix),
      ns.is("right-space", !((_b = props.modelValue) == null ? void 0 : _b.length) && !showSuffix.value)
    ];
  });
  const showClear = vue.computed(() => {
    var _a;
    return props.clearable && !disabled.value && !props.readonly && (((_a = props.modelValue) == null ? void 0 : _a.length) || inputValue.value) && (isFocused.value || hovering.value);
  });
  const showSuffix = vue.computed(() => {
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

exports.useInputTagDom = useInputTagDom;
//# sourceMappingURL=use-input-tag-dom.js.map
