'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var core = require('@vueuse/core');
var index = require('../../../hooks/use-locale/index.js');
var index$1 = require('../../../hooks/use-id/index.js');
var index$2 = require('../../../hooks/use-namespace/index.js');
var index$3 = require('../../../hooks/use-composition/index.js');
var index$4 = require('../../../hooks/use-focus-controller/index.js');
var error = require('../../../utils/error.js');
var useFormItem = require('../../form/src/hooks/use-form-item.js');
var index$5 = require('../../../hooks/use-empty-values/index.js');
var shared = require('@vue/shared');
var icon = require('../../../utils/vue/icon.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var types = require('../../../utils/types.js');
var aria = require('../../../constants/aria.js');
var event = require('../../../constants/event.js');
var scroll = require('../../../utils/dom/scroll.js');

const useSelect = (props, emit) => {
  const { t } = index.useLocale();
  const contentId = index$1.useId();
  const nsSelect = index$2.useNamespace("select");
  const nsInput = index$2.useNamespace("input");
  const states = vue.reactive({
    inputValue: "",
    options: /* @__PURE__ */ new Map(),
    cachedOptions: /* @__PURE__ */ new Map(),
    optionValues: [],
    selected: [],
    selectionWidth: 0,
    collapseItemWidth: 0,
    selectedLabel: "",
    hoveringIndex: -1,
    previousQuery: null,
    inputHovering: false,
    menuVisibleOnFocus: false,
    isBeforeHide: false
  });
  const selectRef = vue.ref();
  const selectionRef = vue.ref();
  const tooltipRef = vue.ref();
  const tagTooltipRef = vue.ref();
  const inputRef = vue.ref();
  const prefixRef = vue.ref();
  const suffixRef = vue.ref();
  const menuRef = vue.ref();
  const tagMenuRef = vue.ref();
  const collapseItemRef = vue.ref();
  const scrollbarRef = vue.ref();
  const {
    isComposing,
    handleCompositionStart,
    handleCompositionUpdate,
    handleCompositionEnd
  } = index$3.useComposition({
    afterComposition: (e) => onInput(e)
  });
  const { wrapperRef, isFocused, handleBlur } = index$4.useFocusController(inputRef, {
    beforeFocus() {
      return selectDisabled.value;
    },
    afterFocus() {
      if (props.automaticDropdown && !expanded.value) {
        expanded.value = true;
        states.menuVisibleOnFocus = true;
      }
    },
    beforeBlur(event) {
      var _a, _b;
      return ((_a = tooltipRef.value) == null ? void 0 : _a.isFocusInsideContent(event)) || ((_b = tagTooltipRef.value) == null ? void 0 : _b.isFocusInsideContent(event));
    },
    afterBlur() {
      var _a;
      expanded.value = false;
      states.menuVisibleOnFocus = false;
      if (props.validateEvent) {
        (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, "blur").catch((err) => error.debugWarn(err));
      }
    }
  });
  const expanded = vue.ref(false);
  const hoverOption = vue.ref();
  const { form, formItem } = useFormItem.useFormItem();
  const { inputId } = useFormItem.useFormItemInputId(props, {
    formItemContext: formItem
  });
  const { valueOnClear, isEmptyValue } = index$5.useEmptyValues(props);
  const selectDisabled = vue.computed(() => props.disabled || (form == null ? void 0 : form.disabled));
  const hasModelValue = vue.computed(() => {
    return shared.isArray(props.modelValue) ? props.modelValue.length > 0 : !isEmptyValue(props.modelValue);
  });
  const needStatusIcon = vue.computed(() => {
    var _a;
    return (_a = form == null ? void 0 : form.statusIcon) != null ? _a : false;
  });
  const showClose = vue.computed(() => {
    return props.clearable && !selectDisabled.value && states.inputHovering && hasModelValue.value;
  });
  const iconComponent = vue.computed(() => props.remote && props.filterable && !props.remoteShowSuffix ? "" : props.suffixIcon);
  const iconReverse = vue.computed(() => nsSelect.is("reverse", !!(iconComponent.value && expanded.value)));
  const validateState = vue.computed(() => (formItem == null ? void 0 : formItem.validateState) || "");
  const validateIcon = vue.computed(() => validateState.value && icon.ValidateComponentsMap[validateState.value]);
  const debounce = vue.computed(() => props.remote ? 300 : 0);
  const isRemoteSearchEmpty = vue.computed(() => props.remote && !states.inputValue && states.options.size === 0);
  const emptyText = vue.computed(() => {
    if (props.loading) {
      return props.loadingText || t("el.select.loading");
    } else {
      if (props.filterable && states.inputValue && states.options.size > 0 && filteredOptionsCount.value === 0) {
        return props.noMatchText || t("el.select.noMatch");
      }
      if (states.options.size === 0) {
        return props.noDataText || t("el.select.noData");
      }
    }
    return null;
  });
  const filteredOptionsCount = vue.computed(() => optionsArray.value.filter((option) => option.visible).length);
  const optionsArray = vue.computed(() => {
    const list = Array.from(states.options.values());
    const newList = [];
    states.optionValues.forEach((item) => {
      const index = list.findIndex((i) => i.value === item);
      if (index > -1) {
        newList.push(list[index]);
      }
    });
    return newList.length >= list.length ? newList : list;
  });
  const cachedOptionsArray = vue.computed(() => Array.from(states.cachedOptions.values()));
  const showNewOption = vue.computed(() => {
    const hasExistingOption = optionsArray.value.filter((option) => {
      return !option.created;
    }).some((option) => {
      return option.currentLabel === states.inputValue;
    });
    return props.filterable && props.allowCreate && states.inputValue !== "" && !hasExistingOption;
  });
  const updateOptions = () => {
    if (props.filterable && shared.isFunction(props.filterMethod))
      return;
    if (props.filterable && props.remote && shared.isFunction(props.remoteMethod))
      return;
    optionsArray.value.forEach((option) => {
      var _a;
      (_a = option.updateOption) == null ? void 0 : _a.call(option, states.inputValue);
    });
  };
  const selectSize = useFormCommonProps.useFormSize();
  const collapseTagSize = vue.computed(() => ["small"].includes(selectSize.value) ? "small" : "default");
  const dropdownMenuVisible = vue.computed({
    get() {
      return expanded.value && !isRemoteSearchEmpty.value;
    },
    set(val) {
      expanded.value = val;
    }
  });
  const shouldShowPlaceholder = vue.computed(() => {
    if (props.multiple && !types.isUndefined(props.modelValue)) {
      return lodashUnified.castArray(props.modelValue).length === 0 && !states.inputValue;
    }
    const value = shared.isArray(props.modelValue) ? props.modelValue[0] : props.modelValue;
    return props.filterable || types.isUndefined(value) ? !states.inputValue : true;
  });
  const currentPlaceholder = vue.computed(() => {
    var _a;
    const _placeholder = (_a = props.placeholder) != null ? _a : t("el.select.placeholder");
    return props.multiple || !hasModelValue.value ? _placeholder : states.selectedLabel;
  });
  const mouseEnterEventName = vue.computed(() => core.isIOS ? null : "mouseenter");
  vue.watch(() => props.modelValue, (val, oldVal) => {
    if (props.multiple) {
      if (props.filterable && !props.reserveKeyword) {
        states.inputValue = "";
        handleQueryChange("");
      }
    }
    setSelected();
    if (!lodashUnified.isEqual(val, oldVal) && props.validateEvent) {
      formItem == null ? void 0 : formItem.validate("change").catch((err) => error.debugWarn(err));
    }
  }, {
    flush: "post",
    deep: true
  });
  vue.watch(() => expanded.value, (val) => {
    if (val) {
      handleQueryChange(states.inputValue);
    } else {
      states.inputValue = "";
      states.previousQuery = null;
      states.isBeforeHide = true;
    }
    emit("visible-change", val);
  });
  vue.watch(() => states.options.entries(), () => {
    if (!core.isClient)
      return;
    setSelected();
    if (props.defaultFirstOption && (props.filterable || props.remote) && filteredOptionsCount.value) {
      checkDefaultFirstOption();
    }
  }, {
    flush: "post"
  });
  vue.watch([() => states.hoveringIndex, optionsArray], ([val]) => {
    if (types.isNumber(val) && val > -1) {
      hoverOption.value = optionsArray.value[val] || {};
    } else {
      hoverOption.value = {};
    }
    optionsArray.value.forEach((option) => {
      option.hover = hoverOption.value === option;
    });
  });
  vue.watchEffect(() => {
    if (states.isBeforeHide)
      return;
    updateOptions();
  });
  const handleQueryChange = (val) => {
    if (states.previousQuery === val || isComposing.value) {
      return;
    }
    states.previousQuery = val;
    if (props.filterable && shared.isFunction(props.filterMethod)) {
      props.filterMethod(val);
    } else if (props.filterable && props.remote && shared.isFunction(props.remoteMethod)) {
      props.remoteMethod(val);
    }
    if (props.defaultFirstOption && (props.filterable || props.remote) && filteredOptionsCount.value) {
      vue.nextTick(checkDefaultFirstOption);
    } else {
      vue.nextTick(updateHoveringIndex);
    }
  };
  const checkDefaultFirstOption = () => {
    const optionsInDropdown = optionsArray.value.filter((n) => n.visible && !n.disabled && !n.states.groupDisabled);
    const userCreatedOption = optionsInDropdown.find((n) => n.created);
    const firstOriginOption = optionsInDropdown[0];
    const valueList = optionsArray.value.map((item) => item.value);
    states.hoveringIndex = getValueIndex(valueList, userCreatedOption || firstOriginOption);
  };
  const setSelected = () => {
    if (!props.multiple) {
      const value = shared.isArray(props.modelValue) ? props.modelValue[0] : props.modelValue;
      const option = getOption(value);
      states.selectedLabel = option.currentLabel;
      states.selected = [option];
      return;
    } else {
      states.selectedLabel = "";
    }
    const result = [];
    if (!types.isUndefined(props.modelValue)) {
      lodashUnified.castArray(props.modelValue).forEach((value) => {
        result.push(getOption(value));
      });
    }
    states.selected = result;
  };
  const getOption = (value) => {
    let option;
    const isObjectValue = shared.isPlainObject(value);
    for (let i = states.cachedOptions.size - 1; i >= 0; i--) {
      const cachedOption = cachedOptionsArray.value[i];
      const isEqualValue = isObjectValue ? lodashUnified.get(cachedOption.value, props.valueKey) === lodashUnified.get(value, props.valueKey) : cachedOption.value === value;
      if (isEqualValue) {
        option = {
          value,
          currentLabel: cachedOption.currentLabel,
          get isDisabled() {
            return cachedOption.isDisabled;
          }
        };
        break;
      }
    }
    if (option)
      return option;
    const label = isObjectValue ? value.label : value != null ? value : "";
    const newOption = {
      value,
      currentLabel: label
    };
    return newOption;
  };
  const updateHoveringIndex = () => {
    states.hoveringIndex = optionsArray.value.findIndex((item) => states.selected.some((selected) => getValueKey(selected) === getValueKey(item)));
  };
  const resetSelectionWidth = () => {
    states.selectionWidth = selectionRef.value.getBoundingClientRect().width;
  };
  const resetCollapseItemWidth = () => {
    states.collapseItemWidth = collapseItemRef.value.getBoundingClientRect().width;
  };
  const updateTooltip = () => {
    var _a, _b;
    (_b = (_a = tooltipRef.value) == null ? void 0 : _a.updatePopper) == null ? void 0 : _b.call(_a);
  };
  const updateTagTooltip = () => {
    var _a, _b;
    (_b = (_a = tagTooltipRef.value) == null ? void 0 : _a.updatePopper) == null ? void 0 : _b.call(_a);
  };
  const onInputChange = () => {
    if (states.inputValue.length > 0 && !expanded.value) {
      expanded.value = true;
    }
    handleQueryChange(states.inputValue);
  };
  const onInput = (event) => {
    states.inputValue = event.target.value;
    if (props.remote) {
      debouncedOnInputChange();
    } else {
      return onInputChange();
    }
  };
  const debouncedOnInputChange = lodashUnified.debounce(() => {
    onInputChange();
  }, debounce.value);
  const emitChange = (val) => {
    if (!lodashUnified.isEqual(props.modelValue, val)) {
      emit(event.CHANGE_EVENT, val);
    }
  };
  const getLastNotDisabledIndex = (value) => lodashUnified.findLastIndex(value, (it) => {
    const option = states.cachedOptions.get(it);
    return option && !option.disabled && !option.states.groupDisabled;
  });
  const deletePrevTag = (e) => {
    if (!props.multiple)
      return;
    if (e.code === aria.EVENT_CODE.delete)
      return;
    if (e.target.value.length <= 0) {
      const value = lodashUnified.castArray(props.modelValue).slice();
      const lastNotDisabledIndex = getLastNotDisabledIndex(value);
      if (lastNotDisabledIndex < 0)
        return;
      const removeTagValue = value[lastNotDisabledIndex];
      value.splice(lastNotDisabledIndex, 1);
      emit(event.UPDATE_MODEL_EVENT, value);
      emitChange(value);
      emit("remove-tag", removeTagValue);
    }
  };
  const deleteTag = (event$1, tag) => {
    const index = states.selected.indexOf(tag);
    if (index > -1 && !selectDisabled.value) {
      const value = lodashUnified.castArray(props.modelValue).slice();
      value.splice(index, 1);
      emit(event.UPDATE_MODEL_EVENT, value);
      emitChange(value);
      emit("remove-tag", tag.value);
    }
    event$1.stopPropagation();
    focus();
  };
  const deleteSelected = (event$1) => {
    event$1.stopPropagation();
    const value = props.multiple ? [] : valueOnClear.value;
    if (props.multiple) {
      for (const item of states.selected) {
        if (item.isDisabled)
          value.push(item.value);
      }
    }
    emit(event.UPDATE_MODEL_EVENT, value);
    emitChange(value);
    states.hoveringIndex = -1;
    expanded.value = false;
    emit("clear");
    focus();
  };
  const handleOptionSelect = (option) => {
    var _a;
    if (props.multiple) {
      const value = lodashUnified.castArray((_a = props.modelValue) != null ? _a : []).slice();
      const optionIndex = getValueIndex(value, option);
      if (optionIndex > -1) {
        value.splice(optionIndex, 1);
      } else if (props.multipleLimit <= 0 || value.length < props.multipleLimit) {
        value.push(option.value);
      }
      emit(event.UPDATE_MODEL_EVENT, value);
      emitChange(value);
      if (option.created) {
        handleQueryChange("");
      }
      if (props.filterable && !props.reserveKeyword) {
        states.inputValue = "";
      }
    } else {
      emit(event.UPDATE_MODEL_EVENT, option.value);
      emitChange(option.value);
      expanded.value = false;
    }
    focus();
    if (expanded.value)
      return;
    vue.nextTick(() => {
      scrollToOption(option);
    });
  };
  const getValueIndex = (arr, option) => {
    if (types.isUndefined(option))
      return -1;
    if (!shared.isObject(option.value))
      return arr.indexOf(option.value);
    return arr.findIndex((item) => {
      return lodashUnified.isEqual(lodashUnified.get(item, props.valueKey), getValueKey(option));
    });
  };
  const scrollToOption = (option) => {
    var _a, _b, _c, _d, _e;
    const targetOption = shared.isArray(option) ? option[0] : option;
    let target = null;
    if (targetOption == null ? void 0 : targetOption.value) {
      const options = optionsArray.value.filter((item) => item.value === targetOption.value);
      if (options.length > 0) {
        target = options[0].$el;
      }
    }
    if (tooltipRef.value && target) {
      const menu = (_d = (_c = (_b = (_a = tooltipRef.value) == null ? void 0 : _a.popperRef) == null ? void 0 : _b.contentRef) == null ? void 0 : _c.querySelector) == null ? void 0 : _d.call(_c, `.${nsSelect.be("dropdown", "wrap")}`);
      if (menu) {
        scroll.scrollIntoView(menu, target);
      }
    }
    (_e = scrollbarRef.value) == null ? void 0 : _e.handleScroll();
  };
  const onOptionCreate = (vm) => {
    states.options.set(vm.value, vm);
    states.cachedOptions.set(vm.value, vm);
  };
  const onOptionDestroy = (key, vm) => {
    if (states.options.get(key) === vm) {
      states.options.delete(key);
    }
  };
  const popperRef = vue.computed(() => {
    var _a, _b;
    return (_b = (_a = tooltipRef.value) == null ? void 0 : _a.popperRef) == null ? void 0 : _b.contentRef;
  });
  const handleMenuEnter = () => {
    states.isBeforeHide = false;
    vue.nextTick(() => {
      var _a;
      (_a = scrollbarRef.value) == null ? void 0 : _a.update();
      scrollToOption(states.selected);
    });
  };
  const focus = () => {
    var _a;
    (_a = inputRef.value) == null ? void 0 : _a.focus();
  };
  const blur = () => {
    var _a;
    if (expanded.value) {
      expanded.value = false;
      vue.nextTick(() => {
        var _a2;
        return (_a2 = inputRef.value) == null ? void 0 : _a2.blur();
      });
      return;
    }
    (_a = inputRef.value) == null ? void 0 : _a.blur();
  };
  const handleClearClick = (event) => {
    deleteSelected(event);
  };
  const handleClickOutside = (event) => {
    expanded.value = false;
    if (isFocused.value) {
      const _event = new FocusEvent("focus", event);
      vue.nextTick(() => handleBlur(_event));
    }
  };
  const handleEsc = () => {
    if (states.inputValue.length > 0) {
      states.inputValue = "";
    } else {
      expanded.value = false;
    }
  };
  const toggleMenu = () => {
    if (selectDisabled.value)
      return;
    if (core.isIOS)
      states.inputHovering = true;
    if (states.menuVisibleOnFocus) {
      states.menuVisibleOnFocus = false;
    } else {
      expanded.value = !expanded.value;
    }
  };
  const selectOption = () => {
    if (!expanded.value) {
      toggleMenu();
    } else {
      const option = optionsArray.value[states.hoveringIndex];
      if (option && !option.isDisabled) {
        handleOptionSelect(option);
      }
    }
  };
  const getValueKey = (item) => {
    return shared.isObject(item.value) ? lodashUnified.get(item.value, props.valueKey) : item.value;
  };
  const optionsAllDisabled = vue.computed(() => optionsArray.value.filter((option) => option.visible).every((option) => option.isDisabled));
  const showTagList = vue.computed(() => {
    if (!props.multiple) {
      return [];
    }
    return props.collapseTags ? states.selected.slice(0, props.maxCollapseTags) : states.selected;
  });
  const collapseTagList = vue.computed(() => {
    if (!props.multiple) {
      return [];
    }
    return props.collapseTags ? states.selected.slice(props.maxCollapseTags) : [];
  });
  const navigateOptions = (direction) => {
    if (!expanded.value) {
      expanded.value = true;
      return;
    }
    if (states.options.size === 0 || filteredOptionsCount.value === 0 || isComposing.value)
      return;
    if (!optionsAllDisabled.value) {
      if (direction === "next") {
        states.hoveringIndex++;
        if (states.hoveringIndex === states.options.size) {
          states.hoveringIndex = 0;
        }
      } else if (direction === "prev") {
        states.hoveringIndex--;
        if (states.hoveringIndex < 0) {
          states.hoveringIndex = states.options.size - 1;
        }
      }
      const option = optionsArray.value[states.hoveringIndex];
      if (option.isDisabled || !option.visible) {
        navigateOptions(direction);
      }
      vue.nextTick(() => scrollToOption(hoverOption.value));
    }
  };
  const getGapWidth = () => {
    if (!selectionRef.value)
      return 0;
    const style = window.getComputedStyle(selectionRef.value);
    return Number.parseFloat(style.gap || "6px");
  };
  const tagStyle = vue.computed(() => {
    const gapWidth = getGapWidth();
    const maxWidth = collapseItemRef.value && props.maxCollapseTags === 1 ? states.selectionWidth - states.collapseItemWidth - gapWidth : states.selectionWidth;
    return { maxWidth: `${maxWidth}px` };
  });
  const collapseTagStyle = vue.computed(() => {
    return { maxWidth: `${states.selectionWidth}px` };
  });
  const popupScroll = (data) => {
    emit("popup-scroll", data);
  };
  core.useResizeObserver(selectionRef, resetSelectionWidth);
  core.useResizeObserver(menuRef, updateTooltip);
  core.useResizeObserver(wrapperRef, updateTooltip);
  core.useResizeObserver(tagMenuRef, updateTagTooltip);
  core.useResizeObserver(collapseItemRef, resetCollapseItemWidth);
  vue.onMounted(() => {
    setSelected();
  });
  return {
    inputId,
    contentId,
    nsSelect,
    nsInput,
    states,
    isFocused,
    expanded,
    optionsArray,
    hoverOption,
    selectSize,
    filteredOptionsCount,
    updateTooltip,
    updateTagTooltip,
    debouncedOnInputChange,
    onInput,
    deletePrevTag,
    deleteTag,
    deleteSelected,
    handleOptionSelect,
    scrollToOption,
    hasModelValue,
    shouldShowPlaceholder,
    currentPlaceholder,
    mouseEnterEventName,
    needStatusIcon,
    showClose,
    iconComponent,
    iconReverse,
    validateState,
    validateIcon,
    showNewOption,
    updateOptions,
    collapseTagSize,
    setSelected,
    selectDisabled,
    emptyText,
    handleCompositionStart,
    handleCompositionUpdate,
    handleCompositionEnd,
    onOptionCreate,
    onOptionDestroy,
    handleMenuEnter,
    focus,
    blur,
    handleClearClick,
    handleClickOutside,
    handleEsc,
    toggleMenu,
    selectOption,
    getValueKey,
    navigateOptions,
    dropdownMenuVisible,
    showTagList,
    collapseTagList,
    popupScroll,
    tagStyle,
    collapseTagStyle,
    popperRef,
    inputRef,
    tooltipRef,
    tagTooltipRef,
    prefixRef,
    suffixRef,
    selectRef,
    wrapperRef,
    selectionRef,
    scrollbarRef,
    menuRef,
    tagMenuRef,
    collapseItemRef
  };
};

exports.useSelect = useSelect;
//# sourceMappingURL=useSelect.js.map
