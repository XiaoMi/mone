import { defineComponent, ref, reactive, computed, onMounted, watch, nextTick, provide, openBlock, createBlock, unref, withCtx, withDirectives, createElementBlock, withKeys, createElementVNode, normalizeClass, createVNode, createCommentVNode, createTextVNode, toDisplayString, mergeProps, normalizeStyle, vShow } from 'vue';
import { debounce } from 'lodash-unified';
import { ElButton } from '../../button/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { ElTooltip } from '../../tooltip/index.mjs';
import { ElInput } from '../../input/index.mjs';
import { ArrowDown, Close } from '@element-plus/icons-vue';
import AlphaSlider from './components/alpha-slider.mjs';
import HueSlider from './components/hue-slider.mjs';
import Predefine from './components/predefine.mjs';
import SvPanel from './components/sv-panel.mjs';
import Color from './utils/color.mjs';
import { colorPickerProps, colorPickerEmits, colorPickerContextKey } from './color-picker2.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import ClickOutside from '../../../directives/click-outside/index.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useFormItem, useFormItemInputId } from '../../form/src/hooks/use-form-item.mjs';
import { useFormSize, useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';
import { useFocusController } from '../../../hooks/use-focus-controller/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';
import { debugWarn } from '../../../utils/error.mjs';
import { EVENT_CODE } from '../../../constants/aria.mjs';

const __default__ = defineComponent({
  name: "ElColorPicker"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: colorPickerProps,
  emits: colorPickerEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const { t } = useLocale();
    const ns = useNamespace("color");
    const { formItem } = useFormItem();
    const colorSize = useFormSize();
    const colorDisabled = useFormDisabled();
    const { inputId: buttonId, isLabeledByFormItem } = useFormItemInputId(props, {
      formItemContext: formItem
    });
    const hue = ref();
    const sv = ref();
    const alpha = ref();
    const popper = ref();
    const triggerRef = ref();
    const inputRef = ref();
    const { isFocused, handleFocus, handleBlur } = useFocusController(triggerRef, {
      beforeFocus() {
        return colorDisabled.value;
      },
      beforeBlur(event) {
        var _a;
        return (_a = popper.value) == null ? void 0 : _a.isFocusInsideContent(event);
      },
      afterBlur() {
        setShowPicker(false);
        resetColor();
      }
    });
    let shouldActiveChange = true;
    const color = reactive(new Color({
      enableAlpha: props.showAlpha,
      format: props.colorFormat || "",
      value: props.modelValue
    }));
    const showPicker = ref(false);
    const showPanelColor = ref(false);
    const customInput = ref("");
    const displayedColor = computed(() => {
      if (!props.modelValue && !showPanelColor.value) {
        return "transparent";
      }
      return displayedRgb(color, props.showAlpha);
    });
    const currentColor = computed(() => {
      return !props.modelValue && !showPanelColor.value ? "" : color.value;
    });
    const buttonAriaLabel = computed(() => {
      return !isLabeledByFormItem.value ? props.ariaLabel || t("el.colorpicker.defaultLabel") : void 0;
    });
    const buttonAriaLabelledby = computed(() => {
      return isLabeledByFormItem.value ? formItem == null ? void 0 : formItem.labelId : void 0;
    });
    const btnKls = computed(() => {
      return [
        ns.b("picker"),
        ns.is("disabled", colorDisabled.value),
        ns.bm("picker", colorSize.value),
        ns.is("focused", isFocused.value)
      ];
    });
    function displayedRgb(color2, showAlpha) {
      if (!(color2 instanceof Color)) {
        throw new TypeError("color should be instance of _color Class");
      }
      const { r, g, b } = color2.toRgb();
      return showAlpha ? `rgba(${r}, ${g}, ${b}, ${color2.get("alpha") / 100})` : `rgb(${r}, ${g}, ${b})`;
    }
    function setShowPicker(value) {
      showPicker.value = value;
    }
    const debounceSetShowPicker = debounce(setShowPicker, 100, { leading: true });
    function show() {
      if (colorDisabled.value)
        return;
      setShowPicker(true);
    }
    function hide() {
      debounceSetShowPicker(false);
      resetColor();
    }
    function resetColor() {
      nextTick(() => {
        if (props.modelValue) {
          color.fromString(props.modelValue);
        } else {
          color.value = "";
          nextTick(() => {
            showPanelColor.value = false;
          });
        }
      });
    }
    function handleTrigger() {
      if (colorDisabled.value)
        return;
      if (showPicker.value) {
        resetColor();
      }
      debounceSetShowPicker(!showPicker.value);
    }
    function handleConfirm() {
      color.fromString(customInput.value);
    }
    function confirmValue() {
      const value = color.value;
      emit(UPDATE_MODEL_EVENT, value);
      emit(CHANGE_EVENT, value);
      if (props.validateEvent) {
        formItem == null ? void 0 : formItem.validate("change").catch((err) => debugWarn(err));
      }
      debounceSetShowPicker(false);
      nextTick(() => {
        const newColor = new Color({
          enableAlpha: props.showAlpha,
          format: props.colorFormat || "",
          value: props.modelValue
        });
        if (!color.compare(newColor)) {
          resetColor();
        }
      });
    }
    function clear() {
      debounceSetShowPicker(false);
      emit(UPDATE_MODEL_EVENT, null);
      emit(CHANGE_EVENT, null);
      if (props.modelValue !== null && props.validateEvent) {
        formItem == null ? void 0 : formItem.validate("change").catch((err) => debugWarn(err));
      }
      resetColor();
    }
    function handleClickOutside() {
      if (!showPicker.value)
        return;
      hide();
      isFocused.value && focus();
    }
    function handleEsc(event) {
      event.preventDefault();
      event.stopPropagation();
      setShowPicker(false);
      resetColor();
    }
    function handleKeyDown(event) {
      switch (event.code) {
        case EVENT_CODE.enter:
        case EVENT_CODE.numpadEnter:
        case EVENT_CODE.space:
          event.preventDefault();
          event.stopPropagation();
          show();
          inputRef.value.focus();
          break;
        case EVENT_CODE.esc:
          handleEsc(event);
          break;
      }
    }
    function focus() {
      triggerRef.value.focus();
    }
    function blur() {
      triggerRef.value.blur();
    }
    onMounted(() => {
      if (props.modelValue) {
        customInput.value = currentColor.value;
      }
    });
    watch(() => props.modelValue, (newVal) => {
      if (!newVal) {
        showPanelColor.value = false;
      } else if (newVal && newVal !== color.value) {
        shouldActiveChange = false;
        color.fromString(newVal);
      }
    });
    watch(() => [props.colorFormat, props.showAlpha], () => {
      color.enableAlpha = props.showAlpha;
      color.format = props.colorFormat || color.format;
      color.doOnChange();
      emit(UPDATE_MODEL_EVENT, color.value);
    });
    watch(() => currentColor.value, (val) => {
      customInput.value = val;
      shouldActiveChange && emit("activeChange", val);
      shouldActiveChange = true;
    });
    watch(() => color.value, () => {
      if (!props.modelValue && !showPanelColor.value) {
        showPanelColor.value = true;
      }
    });
    watch(() => showPicker.value, () => {
      nextTick(() => {
        var _a, _b, _c;
        (_a = hue.value) == null ? void 0 : _a.update();
        (_b = sv.value) == null ? void 0 : _b.update();
        (_c = alpha.value) == null ? void 0 : _c.update();
      });
    });
    provide(colorPickerContextKey, {
      currentColor
    });
    expose({
      color,
      show,
      hide,
      focus,
      blur
    });
    return (_ctx, _cache) => {
      return openBlock(), createBlock(unref(ElTooltip), {
        ref_key: "popper",
        ref: popper,
        visible: showPicker.value,
        "show-arrow": false,
        "fallback-placements": ["bottom", "top", "right", "left"],
        offset: 0,
        "gpu-acceleration": false,
        "popper-class": [unref(ns).be("picker", "panel"), unref(ns).b("dropdown"), _ctx.popperClass],
        "stop-popper-mouse-event": false,
        effect: "light",
        trigger: "click",
        teleported: _ctx.teleported,
        transition: `${unref(ns).namespace.value}-zoom-in-top`,
        persistent: "",
        onHide: ($event) => setShowPicker(false)
      }, {
        content: withCtx(() => [
          withDirectives((openBlock(), createElementBlock("div", {
            onKeydown: withKeys(handleEsc, ["esc"])
          }, [
            createElementVNode("div", {
              class: normalizeClass(unref(ns).be("dropdown", "main-wrapper"))
            }, [
              createVNode(HueSlider, {
                ref_key: "hue",
                ref: hue,
                class: "hue-slider",
                color: unref(color),
                vertical: ""
              }, null, 8, ["color"]),
              createVNode(SvPanel, {
                ref_key: "sv",
                ref: sv,
                color: unref(color)
              }, null, 8, ["color"])
            ], 2),
            _ctx.showAlpha ? (openBlock(), createBlock(AlphaSlider, {
              key: 0,
              ref_key: "alpha",
              ref: alpha,
              color: unref(color)
            }, null, 8, ["color"])) : createCommentVNode("v-if", true),
            _ctx.predefine ? (openBlock(), createBlock(Predefine, {
              key: 1,
              ref: "predefine",
              "enable-alpha": _ctx.showAlpha,
              color: unref(color),
              colors: _ctx.predefine
            }, null, 8, ["enable-alpha", "color", "colors"])) : createCommentVNode("v-if", true),
            createElementVNode("div", {
              class: normalizeClass(unref(ns).be("dropdown", "btns"))
            }, [
              createElementVNode("span", {
                class: normalizeClass(unref(ns).be("dropdown", "value"))
              }, [
                createVNode(unref(ElInput), {
                  ref_key: "inputRef",
                  ref: inputRef,
                  modelValue: customInput.value,
                  "onUpdate:modelValue": ($event) => customInput.value = $event,
                  "validate-event": false,
                  size: "small",
                  onKeyup: withKeys(handleConfirm, ["enter"]),
                  onBlur: handleConfirm
                }, null, 8, ["modelValue", "onUpdate:modelValue", "onKeyup"])
              ], 2),
              createVNode(unref(ElButton), {
                class: normalizeClass(unref(ns).be("dropdown", "link-btn")),
                text: "",
                size: "small",
                onClick: clear
              }, {
                default: withCtx(() => [
                  createTextVNode(toDisplayString(unref(t)("el.colorpicker.clear")), 1)
                ]),
                _: 1
              }, 8, ["class"]),
              createVNode(unref(ElButton), {
                plain: "",
                size: "small",
                class: normalizeClass(unref(ns).be("dropdown", "btn")),
                onClick: confirmValue
              }, {
                default: withCtx(() => [
                  createTextVNode(toDisplayString(unref(t)("el.colorpicker.confirm")), 1)
                ]),
                _: 1
              }, 8, ["class"])
            ], 2)
          ], 40, ["onKeydown"])), [
            [unref(ClickOutside), handleClickOutside, triggerRef.value]
          ])
        ]),
        default: withCtx(() => [
          createElementVNode("div", mergeProps({
            id: unref(buttonId),
            ref_key: "triggerRef",
            ref: triggerRef
          }, _ctx.$attrs, {
            class: unref(btnKls),
            role: "button",
            "aria-label": unref(buttonAriaLabel),
            "aria-labelledby": unref(buttonAriaLabelledby),
            "aria-description": unref(t)("el.colorpicker.description", { color: _ctx.modelValue || "" }),
            "aria-disabled": unref(colorDisabled),
            tabindex: unref(colorDisabled) ? void 0 : _ctx.tabindex,
            onKeydown: handleKeyDown,
            onFocus: unref(handleFocus),
            onBlur: unref(handleBlur)
          }), [
            unref(colorDisabled) ? (openBlock(), createElementBlock("div", {
              key: 0,
              class: normalizeClass(unref(ns).be("picker", "mask"))
            }, null, 2)) : createCommentVNode("v-if", true),
            createElementVNode("div", {
              class: normalizeClass(unref(ns).be("picker", "trigger")),
              onClick: handleTrigger
            }, [
              createElementVNode("span", {
                class: normalizeClass([unref(ns).be("picker", "color"), unref(ns).is("alpha", _ctx.showAlpha)])
              }, [
                createElementVNode("span", {
                  class: normalizeClass(unref(ns).be("picker", "color-inner")),
                  style: normalizeStyle({
                    backgroundColor: unref(displayedColor)
                  })
                }, [
                  withDirectives(createVNode(unref(ElIcon), {
                    class: normalizeClass([unref(ns).be("picker", "icon"), unref(ns).is("icon-arrow-down")])
                  }, {
                    default: withCtx(() => [
                      createVNode(unref(ArrowDown))
                    ]),
                    _: 1
                  }, 8, ["class"]), [
                    [vShow, _ctx.modelValue || showPanelColor.value]
                  ]),
                  withDirectives(createVNode(unref(ElIcon), {
                    class: normalizeClass([unref(ns).be("picker", "empty"), unref(ns).is("icon-close")])
                  }, {
                    default: withCtx(() => [
                      createVNode(unref(Close))
                    ]),
                    _: 1
                  }, 8, ["class"]), [
                    [vShow, !_ctx.modelValue && !showPanelColor.value]
                  ])
                ], 6)
              ], 2)
            ], 2)
          ], 16, ["id", "aria-label", "aria-labelledby", "aria-description", "aria-disabled", "tabindex", "onFocus", "onBlur"])
        ]),
        _: 1
      }, 8, ["visible", "popper-class", "teleported", "transition", "onHide"]);
    };
  }
});
var ColorPicker = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "color-picker.vue"]]);

export { ColorPicker as default };
//# sourceMappingURL=color-picker.mjs.map
