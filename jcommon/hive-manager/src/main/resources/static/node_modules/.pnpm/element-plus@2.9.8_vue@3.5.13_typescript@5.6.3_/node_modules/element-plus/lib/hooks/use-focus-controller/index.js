'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var index = require('../use-prop/index.js');
var types = require('../../utils/types.js');
var shared = require('@vue/shared');

function useFocusController(target, {
  beforeFocus,
  afterFocus,
  beforeBlur,
  afterBlur
} = {}) {
  const instance = vue.getCurrentInstance();
  const { emit } = instance;
  const wrapperRef = vue.shallowRef();
  const disabled = index.useProp("disabled");
  const isFocused = vue.ref(false);
  const handleFocus = (event) => {
    const cancelFocus = shared.isFunction(beforeFocus) ? beforeFocus(event) : false;
    if (cancelFocus || isFocused.value)
      return;
    isFocused.value = true;
    emit("focus", event);
    afterFocus == null ? void 0 : afterFocus();
  };
  const handleBlur = (event) => {
    var _a;
    const cancelBlur = shared.isFunction(beforeBlur) ? beforeBlur(event) : false;
    if (cancelBlur || event.relatedTarget && ((_a = wrapperRef.value) == null ? void 0 : _a.contains(event.relatedTarget)))
      return;
    isFocused.value = false;
    emit("blur", event);
    afterBlur == null ? void 0 : afterBlur();
  };
  const handleClick = () => {
    var _a, _b;
    if (((_a = wrapperRef.value) == null ? void 0 : _a.contains(document.activeElement)) && wrapperRef.value !== document.activeElement || disabled.value)
      return;
    (_b = target.value) == null ? void 0 : _b.focus();
  };
  vue.watch([wrapperRef, disabled], ([el, disabled2]) => {
    if (!el)
      return;
    if (disabled2) {
      el.removeAttribute("tabindex");
    } else {
      el.setAttribute("tabindex", "-1");
    }
  });
  core.useEventListener(wrapperRef, "focus", handleFocus, true);
  core.useEventListener(wrapperRef, "blur", handleBlur, true);
  core.useEventListener(wrapperRef, "click", handleClick, true);
  if (process.env.NODE_ENV === "test") {
    vue.onMounted(() => {
      const targetEl = types.isElement(target.value) ? target.value : document.querySelector("input,textarea");
      if (targetEl) {
        core.useEventListener(targetEl, "focus", handleFocus, true);
        core.useEventListener(targetEl, "blur", handleBlur, true);
      }
    });
  }
  return {
    isFocused,
    wrapperRef,
    handleFocus,
    handleBlur
  };
}

exports.useFocusController = useFocusController;
//# sourceMappingURL=index.js.map
