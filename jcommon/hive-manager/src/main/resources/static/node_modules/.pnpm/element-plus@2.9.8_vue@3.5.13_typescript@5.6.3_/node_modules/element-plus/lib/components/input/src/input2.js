'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var lodashUnified = require('lodash-unified');
var index$4 = require('../../icon/index.js');
var iconsVue = require('@element-plus/icons-vue');
var utils = require('./utils.js');
var input = require('./input.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-attrs/index.js');
var useFormItem = require('../../form/src/hooks/use-form-item.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var index$2 = require('../../../hooks/use-focus-controller/index.js');
var icon = require('../../../utils/vue/icon.js');
var index$3 = require('../../../hooks/use-composition/index.js');
var event = require('../../../constants/event.js');
var index$5 = require('../../../hooks/use-cursor/index.js');
var index$1 = require('../../../hooks/use-namespace/index.js');
var error = require('../../../utils/error.js');
var shared = require('@vue/shared');

const COMPONENT_NAME = "ElInput";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME,
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: input.inputProps,
  emits: input.inputEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const rawAttrs = vue.useAttrs();
    const attrs = index.useAttrs();
    const slots = vue.useSlots();
    const containerKls = vue.computed(() => [
      props.type === "textarea" ? nsTextarea.b() : nsInput.b(),
      nsInput.m(inputSize.value),
      nsInput.is("disabled", inputDisabled.value),
      nsInput.is("exceed", inputExceed.value),
      {
        [nsInput.b("group")]: slots.prepend || slots.append,
        [nsInput.m("prefix")]: slots.prefix || props.prefixIcon,
        [nsInput.m("suffix")]: slots.suffix || props.suffixIcon || props.clearable || props.showPassword,
        [nsInput.bm("suffix", "password-clear")]: showClear.value && showPwdVisible.value,
        [nsInput.b("hidden")]: props.type === "hidden"
      },
      rawAttrs.class
    ]);
    const wrapperKls = vue.computed(() => [
      nsInput.e("wrapper"),
      nsInput.is("focus", isFocused.value)
    ]);
    const { form: elForm, formItem: elFormItem } = useFormItem.useFormItem();
    const { inputId } = useFormItem.useFormItemInputId(props, {
      formItemContext: elFormItem
    });
    const inputSize = useFormCommonProps.useFormSize();
    const inputDisabled = useFormCommonProps.useFormDisabled();
    const nsInput = index$1.useNamespace("input");
    const nsTextarea = index$1.useNamespace("textarea");
    const input = vue.shallowRef();
    const textarea = vue.shallowRef();
    const hovering = vue.ref(false);
    const passwordVisible = vue.ref(false);
    const countStyle = vue.ref();
    const textareaCalcStyle = vue.shallowRef(props.inputStyle);
    const _ref = vue.computed(() => input.value || textarea.value);
    const { wrapperRef, isFocused, handleFocus, handleBlur } = index$2.useFocusController(_ref, {
      beforeFocus() {
        return inputDisabled.value;
      },
      afterBlur() {
        var _a;
        if (props.validateEvent) {
          (_a = elFormItem == null ? void 0 : elFormItem.validate) == null ? void 0 : _a.call(elFormItem, "blur").catch((err) => error.debugWarn(err));
        }
      }
    });
    const needStatusIcon = vue.computed(() => {
      var _a;
      return (_a = elForm == null ? void 0 : elForm.statusIcon) != null ? _a : false;
    });
    const validateState = vue.computed(() => (elFormItem == null ? void 0 : elFormItem.validateState) || "");
    const validateIcon = vue.computed(() => validateState.value && icon.ValidateComponentsMap[validateState.value]);
    const passwordIcon = vue.computed(() => passwordVisible.value ? iconsVue.View : iconsVue.Hide);
    const containerStyle = vue.computed(() => [
      rawAttrs.style
    ]);
    const textareaStyle = vue.computed(() => [
      props.inputStyle,
      textareaCalcStyle.value,
      { resize: props.resize }
    ]);
    const nativeInputValue = vue.computed(() => lodashUnified.isNil(props.modelValue) ? "" : String(props.modelValue));
    const showClear = vue.computed(() => props.clearable && !inputDisabled.value && !props.readonly && !!nativeInputValue.value && (isFocused.value || hovering.value));
    const showPwdVisible = vue.computed(() => props.showPassword && !inputDisabled.value && !!nativeInputValue.value && (!!nativeInputValue.value || isFocused.value));
    const isWordLimitVisible = vue.computed(() => props.showWordLimit && !!props.maxlength && (props.type === "text" || props.type === "textarea") && !inputDisabled.value && !props.readonly && !props.showPassword);
    const textLength = vue.computed(() => nativeInputValue.value.length);
    const inputExceed = vue.computed(() => !!isWordLimitVisible.value && textLength.value > Number(props.maxlength));
    const suffixVisible = vue.computed(() => !!slots.suffix || !!props.suffixIcon || showClear.value || props.showPassword || isWordLimitVisible.value || !!validateState.value && needStatusIcon.value);
    const [recordCursor, setCursor] = index$5.useCursor(input);
    core.useResizeObserver(textarea, (entries) => {
      onceInitSizeTextarea();
      if (!isWordLimitVisible.value || props.resize !== "both")
        return;
      const entry = entries[0];
      const { width } = entry.contentRect;
      countStyle.value = {
        right: `calc(100% - ${width + 15 + 6}px)`
      };
    });
    const resizeTextarea = () => {
      const { type, autosize } = props;
      if (!core.isClient || type !== "textarea" || !textarea.value)
        return;
      if (autosize) {
        const minRows = shared.isObject(autosize) ? autosize.minRows : void 0;
        const maxRows = shared.isObject(autosize) ? autosize.maxRows : void 0;
        const textareaStyle2 = utils.calcTextareaHeight(textarea.value, minRows, maxRows);
        textareaCalcStyle.value = {
          overflowY: "hidden",
          ...textareaStyle2
        };
        vue.nextTick(() => {
          textarea.value.offsetHeight;
          textareaCalcStyle.value = textareaStyle2;
        });
      } else {
        textareaCalcStyle.value = {
          minHeight: utils.calcTextareaHeight(textarea.value).minHeight
        };
      }
    };
    const createOnceInitResize = (resizeTextarea2) => {
      let isInit = false;
      return () => {
        var _a;
        if (isInit || !props.autosize)
          return;
        const isElHidden = ((_a = textarea.value) == null ? void 0 : _a.offsetParent) === null;
        if (!isElHidden) {
          resizeTextarea2();
          isInit = true;
        }
      };
    };
    const onceInitSizeTextarea = createOnceInitResize(resizeTextarea);
    const setNativeInputValue = () => {
      const input2 = _ref.value;
      const formatterValue = props.formatter ? props.formatter(nativeInputValue.value) : nativeInputValue.value;
      if (!input2 || input2.value === formatterValue)
        return;
      input2.value = formatterValue;
    };
    const handleInput = async (event$1) => {
      recordCursor();
      let { value } = event$1.target;
      if (props.formatter && props.parser) {
        value = props.parser(value);
      }
      if (isComposing.value)
        return;
      if (value === nativeInputValue.value) {
        setNativeInputValue();
        return;
      }
      emit(event.UPDATE_MODEL_EVENT, value);
      emit(event.INPUT_EVENT, value);
      await vue.nextTick();
      setNativeInputValue();
      setCursor();
    };
    const handleChange = (event$1) => {
      let { value } = event$1.target;
      if (props.formatter && props.parser) {
        value = props.parser(value);
      }
      emit(event.CHANGE_EVENT, value);
    };
    const {
      isComposing,
      handleCompositionStart,
      handleCompositionUpdate,
      handleCompositionEnd
    } = index$3.useComposition({ emit, afterComposition: handleInput });
    const handlePasswordVisible = () => {
      recordCursor();
      passwordVisible.value = !passwordVisible.value;
      setTimeout(setCursor);
    };
    const focus = () => {
      var _a;
      return (_a = _ref.value) == null ? void 0 : _a.focus();
    };
    const blur = () => {
      var _a;
      return (_a = _ref.value) == null ? void 0 : _a.blur();
    };
    const handleMouseLeave = (evt) => {
      hovering.value = false;
      emit("mouseleave", evt);
    };
    const handleMouseEnter = (evt) => {
      hovering.value = true;
      emit("mouseenter", evt);
    };
    const handleKeydown = (evt) => {
      emit("keydown", evt);
    };
    const select = () => {
      var _a;
      (_a = _ref.value) == null ? void 0 : _a.select();
    };
    const clear = () => {
      emit(event.UPDATE_MODEL_EVENT, "");
      emit(event.CHANGE_EVENT, "");
      emit("clear");
      emit(event.INPUT_EVENT, "");
    };
    vue.watch(() => props.modelValue, () => {
      var _a;
      vue.nextTick(() => resizeTextarea());
      if (props.validateEvent) {
        (_a = elFormItem == null ? void 0 : elFormItem.validate) == null ? void 0 : _a.call(elFormItem, "change").catch((err) => error.debugWarn(err));
      }
    });
    vue.watch(nativeInputValue, () => setNativeInputValue());
    vue.watch(() => props.type, async () => {
      await vue.nextTick();
      setNativeInputValue();
      resizeTextarea();
    });
    vue.onMounted(() => {
      if (!props.formatter && props.parser) {
        error.debugWarn(COMPONENT_NAME, "If you set the parser, you also need to set the formatter.");
      }
      setNativeInputValue();
      vue.nextTick(resizeTextarea);
    });
    expose({
      input,
      textarea,
      ref: _ref,
      textareaStyle,
      autosize: vue.toRef(props, "autosize"),
      isComposing,
      focus,
      blur,
      select,
      clear,
      resizeTextarea
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass([
          vue.unref(containerKls),
          {
            [vue.unref(nsInput).bm("group", "append")]: _ctx.$slots.append,
            [vue.unref(nsInput).bm("group", "prepend")]: _ctx.$slots.prepend
          }
        ]),
        style: vue.normalizeStyle(vue.unref(containerStyle)),
        onMouseenter: handleMouseEnter,
        onMouseleave: handleMouseLeave
      }, [
        vue.createCommentVNode(" input "),
        _ctx.type !== "textarea" ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 0 }, [
          vue.createCommentVNode(" prepend slot "),
          _ctx.$slots.prepend ? (vue.openBlock(), vue.createElementBlock("div", {
            key: 0,
            class: vue.normalizeClass(vue.unref(nsInput).be("group", "prepend"))
          }, [
            vue.renderSlot(_ctx.$slots, "prepend")
          ], 2)) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("div", {
            ref_key: "wrapperRef",
            ref: wrapperRef,
            class: vue.normalizeClass(vue.unref(wrapperKls))
          }, [
            vue.createCommentVNode(" prefix slot "),
            _ctx.$slots.prefix || _ctx.prefixIcon ? (vue.openBlock(), vue.createElementBlock("span", {
              key: 0,
              class: vue.normalizeClass(vue.unref(nsInput).e("prefix"))
            }, [
              vue.createElementVNode("span", {
                class: vue.normalizeClass(vue.unref(nsInput).e("prefix-inner"))
              }, [
                vue.renderSlot(_ctx.$slots, "prefix"),
                _ctx.prefixIcon ? (vue.openBlock(), vue.createBlock(vue.unref(index$4.ElIcon), {
                  key: 0,
                  class: vue.normalizeClass(vue.unref(nsInput).e("icon"))
                }, {
                  default: vue.withCtx(() => [
                    (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.prefixIcon)))
                  ]),
                  _: 1
                }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
              ], 2)
            ], 2)) : vue.createCommentVNode("v-if", true),
            vue.createElementVNode("input", vue.mergeProps({
              id: vue.unref(inputId),
              ref_key: "input",
              ref: input,
              class: vue.unref(nsInput).e("inner")
            }, vue.unref(attrs), {
              minlength: _ctx.minlength,
              maxlength: _ctx.maxlength,
              type: _ctx.showPassword ? passwordVisible.value ? "text" : "password" : _ctx.type,
              disabled: vue.unref(inputDisabled),
              readonly: _ctx.readonly,
              autocomplete: _ctx.autocomplete,
              tabindex: _ctx.tabindex,
              "aria-label": _ctx.ariaLabel,
              placeholder: _ctx.placeholder,
              style: _ctx.inputStyle,
              form: _ctx.form,
              autofocus: _ctx.autofocus,
              role: _ctx.containerRole,
              onCompositionstart: vue.unref(handleCompositionStart),
              onCompositionupdate: vue.unref(handleCompositionUpdate),
              onCompositionend: vue.unref(handleCompositionEnd),
              onInput: handleInput,
              onChange: handleChange,
              onKeydown: handleKeydown
            }), null, 16, ["id", "minlength", "maxlength", "type", "disabled", "readonly", "autocomplete", "tabindex", "aria-label", "placeholder", "form", "autofocus", "role", "onCompositionstart", "onCompositionupdate", "onCompositionend"]),
            vue.createCommentVNode(" suffix slot "),
            vue.unref(suffixVisible) ? (vue.openBlock(), vue.createElementBlock("span", {
              key: 1,
              class: vue.normalizeClass(vue.unref(nsInput).e("suffix"))
            }, [
              vue.createElementVNode("span", {
                class: vue.normalizeClass(vue.unref(nsInput).e("suffix-inner"))
              }, [
                !vue.unref(showClear) || !vue.unref(showPwdVisible) || !vue.unref(isWordLimitVisible) ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 0 }, [
                  vue.renderSlot(_ctx.$slots, "suffix"),
                  _ctx.suffixIcon ? (vue.openBlock(), vue.createBlock(vue.unref(index$4.ElIcon), {
                    key: 0,
                    class: vue.normalizeClass(vue.unref(nsInput).e("icon"))
                  }, {
                    default: vue.withCtx(() => [
                      (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.suffixIcon)))
                    ]),
                    _: 1
                  }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
                ], 64)) : vue.createCommentVNode("v-if", true),
                vue.unref(showClear) ? (vue.openBlock(), vue.createBlock(vue.unref(index$4.ElIcon), {
                  key: 1,
                  class: vue.normalizeClass([vue.unref(nsInput).e("icon"), vue.unref(nsInput).e("clear")]),
                  onMousedown: vue.withModifiers(vue.unref(shared.NOOP), ["prevent"]),
                  onClick: clear
                }, {
                  default: vue.withCtx(() => [
                    vue.createVNode(vue.unref(iconsVue.CircleClose))
                  ]),
                  _: 1
                }, 8, ["class", "onMousedown"])) : vue.createCommentVNode("v-if", true),
                vue.unref(showPwdVisible) ? (vue.openBlock(), vue.createBlock(vue.unref(index$4.ElIcon), {
                  key: 2,
                  class: vue.normalizeClass([vue.unref(nsInput).e("icon"), vue.unref(nsInput).e("password")]),
                  onClick: handlePasswordVisible
                }, {
                  default: vue.withCtx(() => [
                    (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(passwordIcon))))
                  ]),
                  _: 1
                }, 8, ["class"])) : vue.createCommentVNode("v-if", true),
                vue.unref(isWordLimitVisible) ? (vue.openBlock(), vue.createElementBlock("span", {
                  key: 3,
                  class: vue.normalizeClass(vue.unref(nsInput).e("count"))
                }, [
                  vue.createElementVNode("span", {
                    class: vue.normalizeClass(vue.unref(nsInput).e("count-inner"))
                  }, vue.toDisplayString(vue.unref(textLength)) + " / " + vue.toDisplayString(_ctx.maxlength), 3)
                ], 2)) : vue.createCommentVNode("v-if", true),
                vue.unref(validateState) && vue.unref(validateIcon) && vue.unref(needStatusIcon) ? (vue.openBlock(), vue.createBlock(vue.unref(index$4.ElIcon), {
                  key: 4,
                  class: vue.normalizeClass([
                    vue.unref(nsInput).e("icon"),
                    vue.unref(nsInput).e("validateIcon"),
                    vue.unref(nsInput).is("loading", vue.unref(validateState) === "validating")
                  ])
                }, {
                  default: vue.withCtx(() => [
                    (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(validateIcon))))
                  ]),
                  _: 1
                }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
              ], 2)
            ], 2)) : vue.createCommentVNode("v-if", true)
          ], 2),
          vue.createCommentVNode(" append slot "),
          _ctx.$slots.append ? (vue.openBlock(), vue.createElementBlock("div", {
            key: 1,
            class: vue.normalizeClass(vue.unref(nsInput).be("group", "append"))
          }, [
            vue.renderSlot(_ctx.$slots, "append")
          ], 2)) : vue.createCommentVNode("v-if", true)
        ], 64)) : (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 1 }, [
          vue.createCommentVNode(" textarea "),
          vue.createElementVNode("textarea", vue.mergeProps({
            id: vue.unref(inputId),
            ref_key: "textarea",
            ref: textarea,
            class: [vue.unref(nsTextarea).e("inner"), vue.unref(nsInput).is("focus", vue.unref(isFocused))]
          }, vue.unref(attrs), {
            minlength: _ctx.minlength,
            maxlength: _ctx.maxlength,
            tabindex: _ctx.tabindex,
            disabled: vue.unref(inputDisabled),
            readonly: _ctx.readonly,
            autocomplete: _ctx.autocomplete,
            style: vue.unref(textareaStyle),
            "aria-label": _ctx.ariaLabel,
            placeholder: _ctx.placeholder,
            form: _ctx.form,
            autofocus: _ctx.autofocus,
            rows: _ctx.rows,
            role: _ctx.containerRole,
            onCompositionstart: vue.unref(handleCompositionStart),
            onCompositionupdate: vue.unref(handleCompositionUpdate),
            onCompositionend: vue.unref(handleCompositionEnd),
            onInput: handleInput,
            onFocus: vue.unref(handleFocus),
            onBlur: vue.unref(handleBlur),
            onChange: handleChange,
            onKeydown: handleKeydown
          }), null, 16, ["id", "minlength", "maxlength", "tabindex", "disabled", "readonly", "autocomplete", "aria-label", "placeholder", "form", "autofocus", "rows", "role", "onCompositionstart", "onCompositionupdate", "onCompositionend", "onFocus", "onBlur"]),
          vue.unref(isWordLimitVisible) ? (vue.openBlock(), vue.createElementBlock("span", {
            key: 0,
            style: vue.normalizeStyle(countStyle.value),
            class: vue.normalizeClass(vue.unref(nsInput).e("count"))
          }, vue.toDisplayString(vue.unref(textLength)) + " / " + vue.toDisplayString(_ctx.maxlength), 7)) : vue.createCommentVNode("v-if", true)
        ], 64))
      ], 38);
    };
  }
});
var Input = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "input.vue"]]);

exports["default"] = Input;
//# sourceMappingURL=input2.js.map
