'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var iconsVue = require('@element-plus/icons-vue');
var index$3 = require('../../icon/index.js');
var index$2 = require('../../tag/index.js');
var inputTag = require('./input-tag.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useInputTag = require('./composables/use-input-tag.js');
var useHovering = require('./composables/use-hovering.js');
var index$1 = require('../../../hooks/use-calc-input-width/index.js');
var useDragTag = require('./composables/use-drag-tag.js');
var useInputTagDom = require('./composables/use-input-tag-dom.js');
var index = require('../../../hooks/use-attrs/index.js');
var useFormItem = require('../../form/src/hooks/use-form-item.js');
var icon = require('../../../utils/vue/icon.js');
var shared = require('@vue/shared');

const __default__ = vue.defineComponent({
  name: "ElInputTag",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: inputTag.inputTagProps,
  emits: inputTag.inputTagEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const attrs = index.useAttrs();
    const slots = vue.useSlots();
    const { form, formItem } = useFormItem.useFormItem();
    const { inputId } = useFormItem.useFormItemInputId(props, { formItemContext: formItem });
    const needStatusIcon = vue.computed(() => {
      var _a;
      return (_a = form == null ? void 0 : form.statusIcon) != null ? _a : false;
    });
    const validateState = vue.computed(() => (formItem == null ? void 0 : formItem.validateState) || "");
    const validateIcon = vue.computed(() => {
      return validateState.value && icon.ValidateComponentsMap[validateState.value];
    });
    const {
      inputRef,
      wrapperRef,
      isFocused,
      inputValue,
      size,
      tagSize,
      placeholder,
      closable,
      disabled,
      handleDragged,
      handleInput,
      handleKeydown,
      handleRemoveTag,
      handleClear,
      handleCompositionStart,
      handleCompositionUpdate,
      handleCompositionEnd,
      focus,
      blur
    } = useInputTag.useInputTag({ props, emit, formItem });
    const { hovering, handleMouseEnter, handleMouseLeave } = useHovering.useHovering();
    const { calculatorRef, inputStyle } = index$1.useCalcInputWidth();
    const {
      dropIndicatorRef,
      showDropIndicator,
      handleDragStart,
      handleDragOver,
      handleDragEnd
    } = useDragTag.useDragTag({ wrapperRef, handleDragged, afterDragged: focus });
    const {
      ns,
      nsInput,
      containerKls,
      containerStyle,
      innerKls,
      showClear,
      showSuffix
    } = useInputTagDom.useInputTagDom({
      props,
      hovering,
      isFocused,
      inputValue,
      disabled,
      size,
      validateState,
      validateIcon,
      needStatusIcon
    });
    expose({
      focus,
      blur
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "wrapperRef",
        ref: wrapperRef,
        class: vue.normalizeClass(vue.unref(containerKls)),
        style: vue.normalizeStyle(vue.unref(containerStyle)),
        onMouseenter: vue.unref(handleMouseEnter),
        onMouseleave: vue.unref(handleMouseLeave)
      }, [
        vue.unref(slots).prefix ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass(vue.unref(ns).e("prefix"))
        }, [
          vue.renderSlot(_ctx.$slots, "prefix")
        ], 2)) : vue.createCommentVNode("v-if", true),
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(innerKls))
        }, [
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.modelValue, (item, index) => {
            return vue.openBlock(), vue.createBlock(vue.unref(index$2.ElTag), {
              key: index,
              size: vue.unref(tagSize),
              closable: vue.unref(closable),
              type: _ctx.tagType,
              effect: _ctx.tagEffect,
              draggable: vue.unref(closable) && _ctx.draggable,
              "disable-transitions": "",
              onClose: ($event) => vue.unref(handleRemoveTag)(index),
              onDragstart: (event) => vue.unref(handleDragStart)(event, index),
              onDragover: (event) => vue.unref(handleDragOver)(event, index),
              onDragend: vue.unref(handleDragEnd),
              onDrop: vue.withModifiers(() => {
              }, ["stop"])
            }, {
              default: vue.withCtx(() => [
                vue.renderSlot(_ctx.$slots, "tag", {
                  value: item,
                  index
                }, () => [
                  vue.createTextVNode(vue.toDisplayString(item), 1)
                ])
              ]),
              _: 2
            }, 1032, ["size", "closable", "type", "effect", "draggable", "onClose", "onDragstart", "onDragover", "onDragend", "onDrop"]);
          }), 128)),
          vue.createElementVNode("div", {
            class: vue.normalizeClass(vue.unref(ns).e("input-wrapper"))
          }, [
            vue.withDirectives(vue.createElementVNode("input", vue.mergeProps({
              id: vue.unref(inputId),
              ref_key: "inputRef",
              ref: inputRef,
              "onUpdate:modelValue": ($event) => vue.isRef(inputValue) ? inputValue.value = $event : null
            }, vue.unref(attrs), {
              type: "text",
              minlength: _ctx.minlength,
              maxlength: _ctx.maxlength,
              disabled: vue.unref(disabled),
              readonly: _ctx.readonly,
              autocomplete: _ctx.autocomplete,
              tabindex: _ctx.tabindex,
              placeholder: vue.unref(placeholder),
              autofocus: _ctx.autofocus,
              ariaLabel: _ctx.ariaLabel,
              class: vue.unref(ns).e("input"),
              style: vue.unref(inputStyle),
              onCompositionstart: vue.unref(handleCompositionStart),
              onCompositionupdate: vue.unref(handleCompositionUpdate),
              onCompositionend: vue.unref(handleCompositionEnd),
              onInput: vue.unref(handleInput),
              onKeydown: vue.unref(handleKeydown)
            }), null, 16, ["id", "onUpdate:modelValue", "minlength", "maxlength", "disabled", "readonly", "autocomplete", "tabindex", "placeholder", "autofocus", "ariaLabel", "onCompositionstart", "onCompositionupdate", "onCompositionend", "onInput", "onKeydown"]), [
              [vue.vModelText, vue.unref(inputValue)]
            ]),
            vue.createElementVNode("span", {
              ref_key: "calculatorRef",
              ref: calculatorRef,
              "aria-hidden": "true",
              class: vue.normalizeClass(vue.unref(ns).e("input-calculator")),
              textContent: vue.toDisplayString(vue.unref(inputValue))
            }, null, 10, ["textContent"])
          ], 2),
          vue.withDirectives(vue.createElementVNode("div", {
            ref_key: "dropIndicatorRef",
            ref: dropIndicatorRef,
            class: vue.normalizeClass(vue.unref(ns).e("drop-indicator"))
          }, null, 2), [
            [vue.vShow, vue.unref(showDropIndicator)]
          ])
        ], 2),
        vue.unref(showSuffix) ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 1,
          class: vue.normalizeClass(vue.unref(ns).e("suffix"))
        }, [
          vue.renderSlot(_ctx.$slots, "suffix"),
          vue.unref(showClear) ? (vue.openBlock(), vue.createBlock(vue.unref(index$3.ElIcon), {
            key: 0,
            class: vue.normalizeClass([vue.unref(ns).e("icon"), vue.unref(ns).e("clear")]),
            onMousedown: vue.withModifiers(vue.unref(shared.NOOP), ["prevent"]),
            onClick: vue.unref(handleClear)
          }, {
            default: vue.withCtx(() => [
              vue.createVNode(vue.unref(iconsVue.CircleClose))
            ]),
            _: 1
          }, 8, ["class", "onMousedown", "onClick"])) : vue.createCommentVNode("v-if", true),
          vue.unref(validateState) && vue.unref(validateIcon) && vue.unref(needStatusIcon) ? (vue.openBlock(), vue.createBlock(vue.unref(index$3.ElIcon), {
            key: 1,
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
        ], 2)) : vue.createCommentVNode("v-if", true)
      ], 46, ["onMouseenter", "onMouseleave"]);
    };
  }
});
var InputTag = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "input-tag.vue"]]);

exports["default"] = InputTag;
//# sourceMappingURL=input-tag2.js.map
