import { getCurrentInstance, shallowRef, ref, watch, onMounted } from 'vue';
import { useEventListener } from '@vueuse/core';
import { useProp } from '../use-prop/index.mjs';
import { isElement } from '../../utils/types.mjs';
import { isFunction } from '@vue/shared';

function useFocusController(target, {
  beforeFocus,
  afterFocus,
  beforeBlur,
  afterBlur
} = {}) {
  const instance = getCurrentInstance();
  const { emit } = instance;
  const wrapperRef = shallowRef();
  const disabled = useProp("disabled");
  const isFocused = ref(false);
  const handleFocus = (event) => {
    const cancelFocus = isFunction(beforeFocus) ? beforeFocus(event) : false;
    if (cancelFocus || isFocused.value)
      return;
    isFocused.value = true;
    emit("focus", event);
    afterFocus == null ? void 0 : afterFocus();
  };
  const handleBlur = (event) => {
    var _a;
    const cancelBlur = isFunction(beforeBlur) ? beforeBlur(event) : false;
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
  watch([wrapperRef, disabled], ([el, disabled2]) => {
    if (!el)
      return;
    if (disabled2) {
      el.removeAttribute("tabindex");
    } else {
      el.setAttribute("tabindex", "-1");
    }
  });
  useEventListener(wrapperRef, "focus", handleFocus, true);
  useEventListener(wrapperRef, "blur", handleBlur, true);
  useEventListener(wrapperRef, "click", handleClick, true);
  if (process.env.NODE_ENV === "test") {
    onMounted(() => {
      const targetEl = isElement(target.value) ? target.value : document.querySelector("input,textarea");
      if (targetEl) {
        useEventListener(targetEl, "focus", handleFocus, true);
        useEventListener(targetEl, "blur", handleBlur, true);
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

export { useFocusController };
//# sourceMappingURL=index.mjs.map
