import { shallowRef, ref, computed, watch } from 'vue';
import { useFormDisabled, useFormSize } from '../../../form/src/hooks/use-form-common-props.mjs';
import { isUndefined } from '../../../../utils/types.mjs';
import { INPUT_EVENT, UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../../constants/event.mjs';
import { EVENT_CODE } from '../../../../constants/aria.mjs';
import { useFocusController } from '../../../../hooks/use-focus-controller/index.mjs';
import { debugWarn } from '../../../../utils/error.mjs';
import { useComposition } from '../../../../hooks/use-composition/index.mjs';

function useInputTag({ props, emit, formItem }) {
  const disabled = useFormDisabled();
  const size = useFormSize();
  const inputRef = shallowRef();
  const inputValue = ref();
  const tagSize = computed(() => {
    return ["small"].includes(size.value) ? "small" : "default";
  });
  const placeholder = computed(() => {
    var _a;
    return ((_a = props.modelValue) == null ? void 0 : _a.length) ? void 0 : props.placeholder;
  });
  const closable = computed(() => !(props.readonly || disabled.value));
  const inputLimit = computed(() => {
    var _a, _b;
    return isUndefined(props.max) ? false : ((_b = (_a = props.modelValue) == null ? void 0 : _a.length) != null ? _b : 0) >= props.max;
  });
  const handleInput = (event) => {
    if (inputLimit.value) {
      inputValue.value = void 0;
      return;
    }
    if (isComposing.value)
      return;
    emit(INPUT_EVENT, event.target.value);
  };
  const handleKeydown = (event) => {
    var _a;
    if (isComposing.value)
      return;
    switch (event.code) {
      case props.trigger:
        event.preventDefault();
        event.stopPropagation();
        handleAddTag();
        break;
      case EVENT_CODE.numpadEnter:
        if (props.trigger === EVENT_CODE.enter) {
          event.preventDefault();
          event.stopPropagation();
          handleAddTag();
        }
        break;
      case EVENT_CODE.backspace:
        if (!inputValue.value && ((_a = props.modelValue) == null ? void 0 : _a.length)) {
          event.preventDefault();
          event.stopPropagation();
          handleRemoveTag(props.modelValue.length - 1);
        }
        break;
    }
  };
  const handleAddTag = () => {
    var _a, _b;
    const value = (_a = inputValue.value) == null ? void 0 : _a.trim();
    if (!value || inputLimit.value)
      return;
    const list = [...(_b = props.modelValue) != null ? _b : [], value];
    emit(UPDATE_MODEL_EVENT, list);
    emit(CHANGE_EVENT, list);
    emit("add-tag", value);
    inputValue.value = void 0;
  };
  const handleRemoveTag = (index) => {
    var _a;
    const value = ((_a = props.modelValue) != null ? _a : []).slice();
    const [item] = value.splice(index, 1);
    emit(UPDATE_MODEL_EVENT, value);
    emit(CHANGE_EVENT, value);
    emit("remove-tag", item);
  };
  const handleClear = () => {
    inputValue.value = void 0;
    emit(UPDATE_MODEL_EVENT, void 0);
    emit(CHANGE_EVENT, void 0);
    emit("clear");
  };
  const handleDragged = (draggingIndex, dropIndex, type) => {
    var _a;
    const value = ((_a = props.modelValue) != null ? _a : []).slice();
    const [draggedItem] = value.splice(draggingIndex, 1);
    const step = dropIndex > draggingIndex && type === "before" ? -1 : dropIndex < draggingIndex && type === "after" ? 1 : 0;
    value.splice(dropIndex + step, 0, draggedItem);
    emit(UPDATE_MODEL_EVENT, value);
    emit(CHANGE_EVENT, value);
  };
  const focus = () => {
    var _a;
    (_a = inputRef.value) == null ? void 0 : _a.focus();
  };
  const blur = () => {
    var _a;
    (_a = inputRef.value) == null ? void 0 : _a.blur();
  };
  const { wrapperRef, isFocused } = useFocusController(inputRef, {
    beforeFocus() {
      return disabled.value;
    },
    afterBlur() {
      var _a;
      if (props.saveOnBlur) {
        handleAddTag();
      } else {
        inputValue.value = void 0;
      }
      if (props.validateEvent) {
        (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, "blur").catch((err) => debugWarn(err));
      }
    }
  });
  const {
    isComposing,
    handleCompositionStart,
    handleCompositionUpdate,
    handleCompositionEnd
  } = useComposition({ afterComposition: handleInput });
  watch(() => props.modelValue, () => {
    var _a;
    if (props.validateEvent) {
      (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, CHANGE_EVENT).catch((err) => debugWarn(err));
    }
  });
  return {
    inputRef,
    wrapperRef,
    isFocused,
    isComposing,
    inputValue,
    size,
    tagSize,
    placeholder,
    closable,
    disabled,
    inputLimit,
    handleDragged,
    handleInput,
    handleKeydown,
    handleAddTag,
    handleRemoveTag,
    handleClear,
    handleCompositionStart,
    handleCompositionUpdate,
    handleCompositionEnd,
    focus,
    blur
  };
}

export { useInputTag };
//# sourceMappingURL=use-input-tag.mjs.map
