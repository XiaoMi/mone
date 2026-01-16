'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var core = require('@vueuse/core');
var index$5 = require('../../../input/index.js');
var index$6 = require('../../../icon/index.js');
var index$4 = require('../../../tooltip/index.js');
var iconsVue = require('@element-plus/icons-vue');
var utils = require('../utils.js');
var props = require('./props.js');
var pickerRangeTrigger = require('./picker-range-trigger.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');
var index$2 = require('../../../../hooks/use-empty-values/index.js');
var event = require('../../../../constants/event.js');
var index = require('../../../../hooks/use-locale/index.js');
var index$1 = require('../../../../hooks/use-namespace/index.js');
var useFormItem = require('../../../form/src/hooks/use-form-item.js');
var index$3 = require('../../../../hooks/use-focus-controller/index.js');
var error = require('../../../../utils/error.js');
var shared = require('@vue/shared');
var useFormCommonProps = require('../../../form/src/hooks/use-form-common-props.js');
var aria = require('../../../../constants/aria.js');

const __default__ = vue.defineComponent({
  name: "Picker"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: props.timePickerDefaultProps,
  emits: [
    event.UPDATE_MODEL_EVENT,
    event.CHANGE_EVENT,
    "focus",
    "blur",
    "clear",
    "calendar-change",
    "panel-change",
    "visible-change",
    "keydown"
  ],
  setup(__props, { expose, emit }) {
    const props = __props;
    const attrs = vue.useAttrs();
    const { lang } = index.useLocale();
    const nsDate = index$1.useNamespace("date");
    const nsInput = index$1.useNamespace("input");
    const nsRange = index$1.useNamespace("range");
    const { form, formItem } = useFormItem.useFormItem();
    const elPopperOptions = vue.inject("ElPopperOptions", {});
    const { valueOnClear } = index$2.useEmptyValues(props, null);
    const refPopper = vue.ref();
    const inputRef = vue.ref();
    const pickerVisible = vue.ref(false);
    const pickerActualVisible = vue.ref(false);
    const valueOnOpen = vue.ref(null);
    let hasJustTabExitedInput = false;
    const { isFocused, handleFocus, handleBlur } = index$3.useFocusController(inputRef, {
      beforeFocus() {
        return props.readonly || pickerDisabled.value;
      },
      afterFocus() {
        pickerVisible.value = true;
      },
      beforeBlur(event) {
        var _a;
        return !hasJustTabExitedInput && ((_a = refPopper.value) == null ? void 0 : _a.isFocusInsideContent(event));
      },
      afterBlur() {
        handleChange();
        pickerVisible.value = false;
        hasJustTabExitedInput = false;
        props.validateEvent && (formItem == null ? void 0 : formItem.validate("blur").catch((err) => error.debugWarn(err)));
      }
    });
    const rangeInputKls = vue.computed(() => [
      nsDate.b("editor"),
      nsDate.bm("editor", props.type),
      nsInput.e("wrapper"),
      nsDate.is("disabled", pickerDisabled.value),
      nsDate.is("active", pickerVisible.value),
      nsRange.b("editor"),
      pickerSize ? nsRange.bm("editor", pickerSize.value) : "",
      attrs.class
    ]);
    const clearIconKls = vue.computed(() => [
      nsInput.e("icon"),
      nsRange.e("close-icon"),
      !showClose.value ? nsRange.e("close-icon--hidden") : ""
    ]);
    vue.watch(pickerVisible, (val) => {
      if (!val) {
        userInput.value = null;
        vue.nextTick(() => {
          emitChange(props.modelValue);
        });
      } else {
        vue.nextTick(() => {
          if (val) {
            valueOnOpen.value = props.modelValue;
          }
        });
      }
    });
    const emitChange = (val, isClear) => {
      if (isClear || !utils.valueEquals(val, valueOnOpen.value)) {
        emit(event.CHANGE_EVENT, val);
        isClear && (valueOnOpen.value = val);
        props.validateEvent && (formItem == null ? void 0 : formItem.validate("change").catch((err) => error.debugWarn(err)));
      }
    };
    const emitInput = (input) => {
      if (!utils.valueEquals(props.modelValue, input)) {
        let formatted;
        if (shared.isArray(input)) {
          formatted = input.map((item) => utils.formatter(item, props.valueFormat, lang.value));
        } else if (input) {
          formatted = utils.formatter(input, props.valueFormat, lang.value);
        }
        emit(event.UPDATE_MODEL_EVENT, input ? formatted : input, lang.value);
      }
    };
    const emitKeydown = (e) => {
      emit("keydown", e);
    };
    const refInput = vue.computed(() => {
      if (inputRef.value) {
        return Array.from(inputRef.value.$el.querySelectorAll("input"));
      }
      return [];
    });
    const setSelectionRange = (start, end, pos) => {
      const _inputs = refInput.value;
      if (!_inputs.length)
        return;
      if (!pos || pos === "min") {
        _inputs[0].setSelectionRange(start, end);
        _inputs[0].focus();
      } else if (pos === "max") {
        _inputs[1].setSelectionRange(start, end);
        _inputs[1].focus();
      }
    };
    const onPick = (date = "", visible = false) => {
      pickerVisible.value = visible;
      let result;
      if (shared.isArray(date)) {
        result = date.map((_) => _.toDate());
      } else {
        result = date ? date.toDate() : date;
      }
      userInput.value = null;
      emitInput(result);
    };
    const onBeforeShow = () => {
      pickerActualVisible.value = true;
    };
    const onShow = () => {
      emit("visible-change", true);
    };
    const onHide = () => {
      pickerActualVisible.value = false;
      pickerVisible.value = false;
      emit("visible-change", false);
    };
    const handleOpen = () => {
      pickerVisible.value = true;
    };
    const handleClose = () => {
      pickerVisible.value = false;
    };
    const pickerDisabled = vue.computed(() => {
      return props.disabled || (form == null ? void 0 : form.disabled);
    });
    const parsedValue = vue.computed(() => {
      let dayOrDays;
      if (valueIsEmpty.value) {
        if (pickerOptions.value.getDefaultValue) {
          dayOrDays = pickerOptions.value.getDefaultValue();
        }
      } else {
        if (shared.isArray(props.modelValue)) {
          dayOrDays = props.modelValue.map((d) => utils.parseDate(d, props.valueFormat, lang.value));
        } else {
          dayOrDays = utils.parseDate(props.modelValue, props.valueFormat, lang.value);
        }
      }
      if (pickerOptions.value.getRangeAvailableTime) {
        const availableResult = pickerOptions.value.getRangeAvailableTime(dayOrDays);
        if (!lodashUnified.isEqual(availableResult, dayOrDays)) {
          dayOrDays = availableResult;
          if (!valueIsEmpty.value) {
            emitInput(utils.dayOrDaysToDate(dayOrDays));
          }
        }
      }
      if (shared.isArray(dayOrDays) && dayOrDays.some((day) => !day)) {
        dayOrDays = [];
      }
      return dayOrDays;
    });
    const displayValue = vue.computed(() => {
      if (!pickerOptions.value.panelReady)
        return "";
      const formattedValue = formatDayjsToString(parsedValue.value);
      if (shared.isArray(userInput.value)) {
        return [
          userInput.value[0] || formattedValue && formattedValue[0] || "",
          userInput.value[1] || formattedValue && formattedValue[1] || ""
        ];
      } else if (userInput.value !== null) {
        return userInput.value;
      }
      if (!isTimePicker.value && valueIsEmpty.value)
        return "";
      if (!pickerVisible.value && valueIsEmpty.value)
        return "";
      if (formattedValue) {
        return isDatesPicker.value || isMonthsPicker.value || isYearsPicker.value ? formattedValue.join(", ") : formattedValue;
      }
      return "";
    });
    const isTimeLikePicker = vue.computed(() => props.type.includes("time"));
    const isTimePicker = vue.computed(() => props.type.startsWith("time"));
    const isDatesPicker = vue.computed(() => props.type === "dates");
    const isMonthsPicker = vue.computed(() => props.type === "months");
    const isYearsPicker = vue.computed(() => props.type === "years");
    const triggerIcon = vue.computed(() => props.prefixIcon || (isTimeLikePicker.value ? iconsVue.Clock : iconsVue.Calendar));
    const showClose = vue.ref(false);
    const onClearIconClick = (event) => {
      if (props.readonly || pickerDisabled.value)
        return;
      if (showClose.value) {
        event.stopPropagation();
        if (pickerOptions.value.handleClear) {
          pickerOptions.value.handleClear();
        } else {
          emitInput(valueOnClear.value);
        }
        emitChange(valueOnClear.value, true);
        showClose.value = false;
        onHide();
      }
      emit("clear");
    };
    const valueIsEmpty = vue.computed(() => {
      const { modelValue } = props;
      return !modelValue || shared.isArray(modelValue) && !modelValue.filter(Boolean).length;
    });
    const onMouseDownInput = async (event) => {
      var _a;
      if (props.readonly || pickerDisabled.value)
        return;
      if (((_a = event.target) == null ? void 0 : _a.tagName) !== "INPUT" || isFocused.value) {
        pickerVisible.value = true;
      }
    };
    const onMouseEnter = () => {
      if (props.readonly || pickerDisabled.value)
        return;
      if (!valueIsEmpty.value && props.clearable) {
        showClose.value = true;
      }
    };
    const onMouseLeave = () => {
      showClose.value = false;
    };
    const onTouchStartInput = (event) => {
      var _a;
      if (props.readonly || pickerDisabled.value)
        return;
      if (((_a = event.touches[0].target) == null ? void 0 : _a.tagName) !== "INPUT" || isFocused.value) {
        pickerVisible.value = true;
      }
    };
    const isRangeInput = vue.computed(() => {
      return props.type.includes("range");
    });
    const pickerSize = useFormCommonProps.useFormSize();
    const popperEl = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = vue.unref(refPopper)) == null ? void 0 : _a.popperRef) == null ? void 0 : _b.contentRef;
    });
    const stophandle = core.onClickOutside(inputRef, (e) => {
      const unrefedPopperEl = vue.unref(popperEl);
      const inputEl = core.unrefElement(inputRef);
      if (unrefedPopperEl && (e.target === unrefedPopperEl || e.composedPath().includes(unrefedPopperEl)) || e.target === inputEl || inputEl && e.composedPath().includes(inputEl))
        return;
      pickerVisible.value = false;
    });
    vue.onBeforeUnmount(() => {
      stophandle == null ? void 0 : stophandle();
    });
    const userInput = vue.ref(null);
    const handleChange = () => {
      if (userInput.value) {
        const value = parseUserInputToDayjs(displayValue.value);
        if (value) {
          if (isValidValue(value)) {
            emitInput(utils.dayOrDaysToDate(value));
            userInput.value = null;
          }
        }
      }
      if (userInput.value === "") {
        emitInput(valueOnClear.value);
        emitChange(valueOnClear.value, true);
        userInput.value = null;
      }
    };
    const parseUserInputToDayjs = (value) => {
      if (!value)
        return null;
      return pickerOptions.value.parseUserInput(value);
    };
    const formatDayjsToString = (value) => {
      if (!value)
        return null;
      return pickerOptions.value.formatToString(value);
    };
    const isValidValue = (value) => {
      return pickerOptions.value.isValidValue(value);
    };
    const handleKeydownInput = async (event) => {
      if (props.readonly || pickerDisabled.value)
        return;
      const { code } = event;
      emitKeydown(event);
      if (code === aria.EVENT_CODE.esc) {
        if (pickerVisible.value === true) {
          pickerVisible.value = false;
          event.preventDefault();
          event.stopPropagation();
        }
        return;
      }
      if (code === aria.EVENT_CODE.down) {
        if (pickerOptions.value.handleFocusPicker) {
          event.preventDefault();
          event.stopPropagation();
        }
        if (pickerVisible.value === false) {
          pickerVisible.value = true;
          await vue.nextTick();
        }
        if (pickerOptions.value.handleFocusPicker) {
          pickerOptions.value.handleFocusPicker();
          return;
        }
      }
      if (code === aria.EVENT_CODE.tab) {
        hasJustTabExitedInput = true;
        return;
      }
      if (code === aria.EVENT_CODE.enter || code === aria.EVENT_CODE.numpadEnter) {
        if (userInput.value === null || userInput.value === "" || isValidValue(parseUserInputToDayjs(displayValue.value))) {
          handleChange();
          pickerVisible.value = false;
        }
        event.stopPropagation();
        return;
      }
      if (userInput.value) {
        event.stopPropagation();
        return;
      }
      if (pickerOptions.value.handleKeydownInput) {
        pickerOptions.value.handleKeydownInput(event);
      }
    };
    const onUserInput = (e) => {
      userInput.value = e;
      if (!pickerVisible.value) {
        pickerVisible.value = true;
      }
    };
    const handleStartInput = (event) => {
      const target = event.target;
      if (userInput.value) {
        userInput.value = [target.value, userInput.value[1]];
      } else {
        userInput.value = [target.value, null];
      }
    };
    const handleEndInput = (event) => {
      const target = event.target;
      if (userInput.value) {
        userInput.value = [userInput.value[0], target.value];
      } else {
        userInput.value = [null, target.value];
      }
    };
    const handleStartChange = () => {
      var _a;
      const values = userInput.value;
      const value = parseUserInputToDayjs(values && values[0]);
      const parsedVal = vue.unref(parsedValue);
      if (value && value.isValid()) {
        userInput.value = [
          formatDayjsToString(value),
          ((_a = displayValue.value) == null ? void 0 : _a[1]) || null
        ];
        const newValue = [value, parsedVal && (parsedVal[1] || null)];
        if (isValidValue(newValue)) {
          emitInput(utils.dayOrDaysToDate(newValue));
          userInput.value = null;
        }
      }
    };
    const handleEndChange = () => {
      var _a;
      const values = vue.unref(userInput);
      const value = parseUserInputToDayjs(values && values[1]);
      const parsedVal = vue.unref(parsedValue);
      if (value && value.isValid()) {
        userInput.value = [
          ((_a = vue.unref(displayValue)) == null ? void 0 : _a[0]) || null,
          formatDayjsToString(value)
        ];
        const newValue = [parsedVal && parsedVal[0], value];
        if (isValidValue(newValue)) {
          emitInput(utils.dayOrDaysToDate(newValue));
          userInput.value = null;
        }
      }
    };
    const pickerOptions = vue.ref({});
    const onSetPickerOption = (e) => {
      pickerOptions.value[e[0]] = e[1];
      pickerOptions.value.panelReady = true;
    };
    const onCalendarChange = (e) => {
      emit("calendar-change", e);
    };
    const onPanelChange = (value, mode, view) => {
      emit("panel-change", value, mode, view);
    };
    const focus = () => {
      var _a;
      (_a = inputRef.value) == null ? void 0 : _a.focus();
    };
    const blur = () => {
      var _a;
      (_a = inputRef.value) == null ? void 0 : _a.blur();
    };
    vue.provide("EP_PICKER_BASE", {
      props
    });
    expose({
      focus,
      blur,
      handleOpen,
      handleClose,
      onPick
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.unref(index$4.ElTooltip), vue.mergeProps({
        ref_key: "refPopper",
        ref: refPopper,
        visible: pickerVisible.value,
        effect: "light",
        pure: "",
        trigger: "click"
      }, _ctx.$attrs, {
        role: "dialog",
        teleported: "",
        transition: `${vue.unref(nsDate).namespace.value}-zoom-in-top`,
        "popper-class": [`${vue.unref(nsDate).namespace.value}-picker__popper`, _ctx.popperClass],
        "popper-options": vue.unref(elPopperOptions),
        "fallback-placements": _ctx.fallbackPlacements,
        "gpu-acceleration": false,
        placement: _ctx.placement,
        "stop-popper-mouse-event": false,
        "hide-after": 0,
        persistent: "",
        onBeforeShow,
        onShow,
        onHide
      }), {
        default: vue.withCtx(() => [
          !vue.unref(isRangeInput) ? (vue.openBlock(), vue.createBlock(vue.unref(index$5.ElInput), {
            key: 0,
            id: _ctx.id,
            ref_key: "inputRef",
            ref: inputRef,
            "container-role": "combobox",
            "model-value": vue.unref(displayValue),
            name: _ctx.name,
            size: vue.unref(pickerSize),
            disabled: vue.unref(pickerDisabled),
            placeholder: _ctx.placeholder,
            class: vue.normalizeClass([vue.unref(nsDate).b("editor"), vue.unref(nsDate).bm("editor", _ctx.type), _ctx.$attrs.class]),
            style: vue.normalizeStyle(_ctx.$attrs.style),
            readonly: !_ctx.editable || _ctx.readonly || vue.unref(isDatesPicker) || vue.unref(isMonthsPicker) || vue.unref(isYearsPicker) || _ctx.type === "week",
            "aria-label": _ctx.ariaLabel,
            tabindex: _ctx.tabindex,
            "validate-event": false,
            onInput: onUserInput,
            onFocus: vue.unref(handleFocus),
            onBlur: vue.unref(handleBlur),
            onKeydown: handleKeydownInput,
            onChange: handleChange,
            onMousedown: onMouseDownInput,
            onMouseenter: onMouseEnter,
            onMouseleave: onMouseLeave,
            onTouchstartPassive: onTouchStartInput,
            onClick: vue.withModifiers(() => {
            }, ["stop"])
          }, {
            prefix: vue.withCtx(() => [
              vue.unref(triggerIcon) ? (vue.openBlock(), vue.createBlock(vue.unref(index$6.ElIcon), {
                key: 0,
                class: vue.normalizeClass(vue.unref(nsInput).e("icon")),
                onMousedown: vue.withModifiers(onMouseDownInput, ["prevent"]),
                onTouchstartPassive: onTouchStartInput
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(triggerIcon))))
                ]),
                _: 1
              }, 8, ["class", "onMousedown"])) : vue.createCommentVNode("v-if", true)
            ]),
            suffix: vue.withCtx(() => [
              showClose.value && _ctx.clearIcon ? (vue.openBlock(), vue.createBlock(vue.unref(index$6.ElIcon), {
                key: 0,
                class: vue.normalizeClass(`${vue.unref(nsInput).e("icon")} clear-icon`),
                onMousedown: vue.withModifiers(vue.unref(shared.NOOP), ["prevent"]),
                onClick: onClearIconClick
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.clearIcon)))
                ]),
                _: 1
              }, 8, ["class", "onMousedown"])) : vue.createCommentVNode("v-if", true)
            ]),
            _: 1
          }, 8, ["id", "model-value", "name", "size", "disabled", "placeholder", "class", "style", "readonly", "aria-label", "tabindex", "onFocus", "onBlur", "onClick"])) : (vue.openBlock(), vue.createBlock(pickerRangeTrigger["default"], {
            key: 1,
            id: _ctx.id,
            ref_key: "inputRef",
            ref: inputRef,
            "model-value": vue.unref(displayValue),
            name: _ctx.name,
            disabled: vue.unref(pickerDisabled),
            readonly: !_ctx.editable || _ctx.readonly,
            "start-placeholder": _ctx.startPlaceholder,
            "end-placeholder": _ctx.endPlaceholder,
            class: vue.normalizeClass(vue.unref(rangeInputKls)),
            style: vue.normalizeStyle(_ctx.$attrs.style),
            "aria-label": _ctx.ariaLabel,
            tabindex: _ctx.tabindex,
            autocomplete: "off",
            role: "combobox",
            onClick: onMouseDownInput,
            onFocus: vue.unref(handleFocus),
            onBlur: vue.unref(handleBlur),
            onStartInput: handleStartInput,
            onStartChange: handleStartChange,
            onEndInput: handleEndInput,
            onEndChange: handleEndChange,
            onMousedown: onMouseDownInput,
            onMouseenter: onMouseEnter,
            onMouseleave: onMouseLeave,
            onTouchstartPassive: onTouchStartInput,
            onKeydown: handleKeydownInput
          }, {
            prefix: vue.withCtx(() => [
              vue.unref(triggerIcon) ? (vue.openBlock(), vue.createBlock(vue.unref(index$6.ElIcon), {
                key: 0,
                class: vue.normalizeClass([vue.unref(nsInput).e("icon"), vue.unref(nsRange).e("icon")])
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(triggerIcon))))
                ]),
                _: 1
              }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
            ]),
            "range-separator": vue.withCtx(() => [
              vue.renderSlot(_ctx.$slots, "range-separator", {}, () => [
                vue.createElementVNode("span", {
                  class: vue.normalizeClass(vue.unref(nsRange).b("separator"))
                }, vue.toDisplayString(_ctx.rangeSeparator), 3)
              ])
            ]),
            suffix: vue.withCtx(() => [
              _ctx.clearIcon ? (vue.openBlock(), vue.createBlock(vue.unref(index$6.ElIcon), {
                key: 0,
                class: vue.normalizeClass(vue.unref(clearIconKls)),
                onMousedown: vue.withModifiers(vue.unref(shared.NOOP), ["prevent"]),
                onClick: onClearIconClick
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.clearIcon)))
                ]),
                _: 1
              }, 8, ["class", "onMousedown"])) : vue.createCommentVNode("v-if", true)
            ]),
            _: 3
          }, 8, ["id", "model-value", "name", "disabled", "readonly", "start-placeholder", "end-placeholder", "class", "style", "aria-label", "tabindex", "onFocus", "onBlur"]))
        ]),
        content: vue.withCtx(() => [
          vue.renderSlot(_ctx.$slots, "default", {
            visible: pickerVisible.value,
            actualVisible: pickerActualVisible.value,
            parsedValue: vue.unref(parsedValue),
            format: _ctx.format,
            dateFormat: _ctx.dateFormat,
            timeFormat: _ctx.timeFormat,
            unlinkPanels: _ctx.unlinkPanels,
            type: _ctx.type,
            defaultValue: _ctx.defaultValue,
            showNow: _ctx.showNow,
            onPick,
            onSelectRange: setSelectionRange,
            onSetPickerOption,
            onCalendarChange,
            onPanelChange,
            onMousedown: vue.withModifiers(() => {
            }, ["stop"])
          })
        ]),
        _: 3
      }, 16, ["visible", "transition", "popper-class", "popper-options", "fallback-placements", "placement"]);
    };
  }
});
var CommonPicker = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "picker.vue"]]);

exports["default"] = CommonPicker;
//# sourceMappingURL=picker.js.map
