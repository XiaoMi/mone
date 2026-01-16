import { defineComponent, computed, ref, openBlock, createElementBlock, normalizeClass, unref, createVNode, mergeProps, createSlots, renderList, withCtx, renderSlot, normalizeProps, guardReactiveProps, createElementVNode, normalizeStyle, withModifiers, nextTick } from 'vue';
import { pick } from 'lodash-unified';
import { ElInput } from '../../input/index.mjs';
import { ElTooltip } from '../../tooltip/index.mjs';
import { mentionProps, mentionEmits } from './mention.mjs';
import { getCursorPosition, getMentionCtx } from './helper.mjs';
import ElMentionDropdown from './mention-dropdown2.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { inputProps } from '../../input/src/input.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';
import { useId } from '../../../hooks/use-id/index.mjs';
import { useFocusController } from '../../../hooks/use-focus-controller/index.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { EVENT_CODE } from '../../../constants/aria.mjs';
import { isFunction } from '@vue/shared';

const __default__ = defineComponent({
  name: "ElMention",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: mentionProps,
  emits: mentionEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const passInputProps = computed(() => pick(props, Object.keys(inputProps)));
    const ns = useNamespace("mention");
    const disabled = useFormDisabled();
    const contentId = useId();
    const elInputRef = ref();
    const tooltipRef = ref();
    const dropdownRef = ref();
    const visible = ref(false);
    const cursorStyle = ref();
    const mentionCtx = ref();
    const computedPlacement = computed(() => props.showArrow ? props.placement : `${props.placement}-start`);
    const computedFallbackPlacements = computed(() => props.showArrow ? ["bottom", "top"] : ["bottom-start", "top-start"]);
    const filteredOptions = computed(() => {
      const { filterOption, options } = props;
      if (!mentionCtx.value || !filterOption)
        return options;
      return options.filter((option) => filterOption(mentionCtx.value.pattern, option));
    });
    const dropdownVisible = computed(() => {
      return visible.value && (!!filteredOptions.value.length || props.loading);
    });
    const hoveringId = computed(() => {
      var _a;
      return `${contentId.value}-${(_a = dropdownRef.value) == null ? void 0 : _a.hoveringIndex}`;
    });
    const handleInputChange = (value) => {
      emit(UPDATE_MODEL_EVENT, value);
      syncAfterCursorMove();
    };
    const handleInputKeyDown = (event) => {
      var _a, _b, _c, _d;
      if (!("code" in event) || ((_a = elInputRef.value) == null ? void 0 : _a.isComposing))
        return;
      switch (event.code) {
        case EVENT_CODE.left:
        case EVENT_CODE.right:
          syncAfterCursorMove();
          break;
        case EVENT_CODE.up:
        case EVENT_CODE.down:
          if (!visible.value)
            return;
          event.preventDefault();
          (_b = dropdownRef.value) == null ? void 0 : _b.navigateOptions(event.code === EVENT_CODE.up ? "prev" : "next");
          break;
        case EVENT_CODE.enter:
        case EVENT_CODE.numpadEnter:
          if (!visible.value)
            return;
          event.preventDefault();
          if ((_c = dropdownRef.value) == null ? void 0 : _c.hoverOption) {
            (_d = dropdownRef.value) == null ? void 0 : _d.selectHoverOption();
          } else {
            visible.value = false;
          }
          break;
        case EVENT_CODE.esc:
          if (!visible.value)
            return;
          event.preventDefault();
          visible.value = false;
          break;
        case EVENT_CODE.backspace:
          if (props.whole && mentionCtx.value) {
            const { splitIndex, selectionEnd, pattern, prefixIndex, prefix } = mentionCtx.value;
            const inputEl = getInputEl();
            if (!inputEl)
              return;
            const inputValue = inputEl.value;
            const matchOption = props.options.find((item) => item.value === pattern);
            const isWhole = isFunction(props.checkIsWhole) ? props.checkIsWhole(pattern, prefix) : matchOption;
            if (isWhole && splitIndex !== -1 && splitIndex + 1 === selectionEnd) {
              event.preventDefault();
              const newValue = inputValue.slice(0, prefixIndex) + inputValue.slice(splitIndex + 1);
              emit(UPDATE_MODEL_EVENT, newValue);
              const newSelectionEnd = prefixIndex;
              nextTick(() => {
                inputEl.selectionStart = newSelectionEnd;
                inputEl.selectionEnd = newSelectionEnd;
                syncDropdownVisible();
              });
            }
          }
      }
    };
    const { wrapperRef } = useFocusController(elInputRef, {
      beforeFocus() {
        return disabled.value;
      },
      afterFocus() {
        syncAfterCursorMove();
      },
      beforeBlur(event) {
        var _a;
        return (_a = tooltipRef.value) == null ? void 0 : _a.isFocusInsideContent(event);
      },
      afterBlur() {
        visible.value = false;
      }
    });
    const handleInputMouseDown = () => {
      syncAfterCursorMove();
    };
    const handleSelect = (item) => {
      if (!mentionCtx.value)
        return;
      const inputEl = getInputEl();
      if (!inputEl)
        return;
      const inputValue = inputEl.value;
      const { split } = props;
      const newEndPart = inputValue.slice(mentionCtx.value.end);
      const alreadySeparated = newEndPart.startsWith(split);
      const newMiddlePart = `${item.value}${alreadySeparated ? "" : split}`;
      const newValue = inputValue.slice(0, mentionCtx.value.start) + newMiddlePart + newEndPart;
      emit(UPDATE_MODEL_EVENT, newValue);
      emit("select", item, mentionCtx.value.prefix);
      const newSelectionEnd = mentionCtx.value.start + newMiddlePart.length + (alreadySeparated ? 1 : 0);
      nextTick(() => {
        inputEl.selectionStart = newSelectionEnd;
        inputEl.selectionEnd = newSelectionEnd;
        inputEl.focus();
        syncDropdownVisible();
      });
    };
    const getInputEl = () => {
      var _a, _b;
      return props.type === "textarea" ? (_a = elInputRef.value) == null ? void 0 : _a.textarea : (_b = elInputRef.value) == null ? void 0 : _b.input;
    };
    const syncAfterCursorMove = () => {
      setTimeout(() => {
        syncCursor();
        syncDropdownVisible();
        nextTick(() => {
          var _a;
          return (_a = tooltipRef.value) == null ? void 0 : _a.updatePopper();
        });
      }, 0);
    };
    const syncCursor = () => {
      const inputEl = getInputEl();
      if (!inputEl)
        return;
      const caretPosition = getCursorPosition(inputEl);
      const inputRect = inputEl.getBoundingClientRect();
      const elInputRect = elInputRef.value.$el.getBoundingClientRect();
      cursorStyle.value = {
        position: "absolute",
        width: 0,
        height: `${caretPosition.height}px`,
        left: `${caretPosition.left + inputRect.left - elInputRect.left}px`,
        top: `${caretPosition.top + inputRect.top - elInputRect.top}px`
      };
    };
    const syncDropdownVisible = () => {
      const inputEl = getInputEl();
      if (document.activeElement !== inputEl) {
        visible.value = false;
        return;
      }
      const { prefix, split } = props;
      mentionCtx.value = getMentionCtx(inputEl, prefix, split);
      if (mentionCtx.value && mentionCtx.value.splitIndex === -1) {
        visible.value = true;
        emit("search", mentionCtx.value.pattern, mentionCtx.value.prefix);
        return;
      }
      visible.value = false;
    };
    expose({
      input: elInputRef,
      tooltip: tooltipRef,
      dropdownVisible
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        ref_key: "wrapperRef",
        ref: wrapperRef,
        class: normalizeClass(unref(ns).b())
      }, [
        createVNode(unref(ElInput), mergeProps(mergeProps(unref(passInputProps), _ctx.$attrs), {
          ref_key: "elInputRef",
          ref: elInputRef,
          "model-value": _ctx.modelValue,
          disabled: unref(disabled),
          role: unref(dropdownVisible) ? "combobox" : void 0,
          "aria-activedescendant": unref(dropdownVisible) ? unref(hoveringId) || "" : void 0,
          "aria-controls": unref(dropdownVisible) ? unref(contentId) : void 0,
          "aria-expanded": unref(dropdownVisible) || void 0,
          "aria-label": _ctx.ariaLabel,
          "aria-autocomplete": unref(dropdownVisible) ? "none" : void 0,
          "aria-haspopup": unref(dropdownVisible) ? "listbox" : void 0,
          onInput: handleInputChange,
          onKeydown: handleInputKeyDown,
          onMousedown: handleInputMouseDown
        }), createSlots({
          _: 2
        }, [
          renderList(_ctx.$slots, (_, name) => {
            return {
              name,
              fn: withCtx((slotProps) => [
                renderSlot(_ctx.$slots, name, normalizeProps(guardReactiveProps(slotProps)))
              ])
            };
          })
        ]), 1040, ["model-value", "disabled", "role", "aria-activedescendant", "aria-controls", "aria-expanded", "aria-label", "aria-autocomplete", "aria-haspopup"]),
        createVNode(unref(ElTooltip), {
          ref_key: "tooltipRef",
          ref: tooltipRef,
          visible: unref(dropdownVisible),
          "popper-class": [unref(ns).e("popper"), _ctx.popperClass],
          "popper-options": _ctx.popperOptions,
          placement: unref(computedPlacement),
          "fallback-placements": unref(computedFallbackPlacements),
          effect: "light",
          pure: "",
          offset: _ctx.offset,
          "show-arrow": _ctx.showArrow
        }, {
          default: withCtx(() => [
            createElementVNode("div", {
              style: normalizeStyle(cursorStyle.value)
            }, null, 4)
          ]),
          content: withCtx(() => {
            var _a;
            return [
              createVNode(ElMentionDropdown, {
                ref_key: "dropdownRef",
                ref: dropdownRef,
                options: unref(filteredOptions),
                disabled: unref(disabled),
                loading: _ctx.loading,
                "content-id": unref(contentId),
                "aria-label": _ctx.ariaLabel,
                onSelect: handleSelect,
                onClick: withModifiers((_a = elInputRef.value) == null ? void 0 : _a.focus, ["stop"])
              }, createSlots({
                _: 2
              }, [
                renderList(_ctx.$slots, (_, name) => {
                  return {
                    name,
                    fn: withCtx((slotProps) => [
                      renderSlot(_ctx.$slots, name, normalizeProps(guardReactiveProps(slotProps)))
                    ])
                  };
                })
              ]), 1032, ["options", "disabled", "loading", "content-id", "aria-label", "onClick"])
            ];
          }),
          _: 3
        }, 8, ["visible", "popper-class", "popper-options", "placement", "fallback-placements", "offset", "show-arrow"])
      ], 2);
    };
  }
});
var Mention = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "mention.vue"]]);

export { Mention as default };
//# sourceMappingURL=mention2.mjs.map
