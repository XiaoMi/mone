'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var useFormCommonProps = require('../../../form/src/hooks/use-form-common-props.js');
var types = require('../../../../utils/types.js');
var event = require('../../../../constants/event.js');
var aria = require('../../../../constants/aria.js');
var index = require('../../../../hooks/use-focus-controller/index.js');
var error = require('../../../../utils/error.js');
var index$1 = require('../../../../hooks/use-composition/index.js');

function useInputTag({ props, emit, formItem }) {
  const disabled = useFormCommonProps.useFormDisabled();
  const size = useFormCommonProps.useFormSize();
  const inputRef = vue.shallowRef();
  const inputValue = vue.ref();
  const tagSize = vue.computed(() => {
    return ["small"].includes(size.value) ? "small" : "default";
  });
  const placeholder = vue.computed(() => {
    var _a;
    return ((_a = props.modelValue) == null ? void 0 : _a.length) ? void 0 : props.placeholder;
  });
  const closable = vue.computed(() => !(props.readonly || disabled.value));
  const inputLimit = vue.computed(() => {
    var _a, _b;
    return types.isUndefined(props.max) ? false : ((_b = (_a = props.modelValue) == null ? void 0 : _a.length) != null ? _b : 0) >= props.max;
  });
  const handleInput = (event$1) => {
    if (inputLimit.value) {
      inputValue.value = void 0;
      return;
    }
    if (isComposing.value)
      return;
    emit(event.INPUT_EVENT, event$1.target.value);
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
      case aria.EVENT_CODE.numpadEnter:
        if (props.trigger === aria.EVENT_CODE.enter) {
          event.preventDefault();
          event.stopPropagation();
          handleAddTag();
        }
        break;
      case aria.EVENT_CODE.backspace:
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
    emit(event.UPDATE_MODEL_EVENT, list);
    emit(event.CHANGE_EVENT, list);
    emit("add-tag", value);
    inputValue.value = void 0;
  };
  const handleRemoveTag = (index) => {
    var _a;
    const value = ((_a = props.modelValue) != null ? _a : []).slice();
    const [item] = value.splice(index, 1);
    emit(event.UPDATE_MODEL_EVENT, value);
    emit(event.CHANGE_EVENT, value);
    emit("remove-tag", item);
  };
  const handleClear = () => {
    inputValue.value = void 0;
    emit(event.UPDATE_MODEL_EVENT, void 0);
    emit(event.CHANGE_EVENT, void 0);
    emit("clear");
  };
  const handleDragged = (draggingIndex, dropIndex, type) => {
    var _a;
    const value = ((_a = props.modelValue) != null ? _a : []).slice();
    const [draggedItem] = value.splice(draggingIndex, 1);
    const step = dropIndex > draggingIndex && type === "before" ? -1 : dropIndex < draggingIndex && type === "after" ? 1 : 0;
    value.splice(dropIndex + step, 0, draggedItem);
    emit(event.UPDATE_MODEL_EVENT, value);
    emit(event.CHANGE_EVENT, value);
  };
  const focus = () => {
    var _a;
    (_a = inputRef.value) == null ? void 0 : _a.focus();
  };
  const blur = () => {
    var _a;
    (_a = inputRef.value) == null ? void 0 : _a.blur();
  };
  const { wrapperRef, isFocused } = index.useFocusController(inputRef, {
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
        (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, "blur").catch((err) => error.debugWarn(err));
      }
    }
  });
  const {
    isComposing,
    handleCompositionStart,
    handleCompositionUpdate,
    handleCompositionEnd
  } = index$1.useComposition({ afterComposition: handleInput });
  vue.watch(() => props.modelValue, () => {
    var _a;
    if (props.validateEvent) {
      (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, event.CHANGE_EVENT).catch((err) => error.debugWarn(err));
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

exports.useInputTag = useInputTag;
//# sourceMappingURL=use-input-tag.js.map
