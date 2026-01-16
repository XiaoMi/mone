import { defineComponent, computed, reactive, toRefs, provide, resolveComponent, resolveDirective, withDirectives, openBlock, createElementBlock, normalizeClass, createVNode, withCtx, createElementVNode, withModifiers, renderSlot, createCommentVNode, Fragment, renderList, normalizeStyle, createTextVNode, toDisplayString, createBlock, withKeys, vModelText, resolveDynamicComponent, vShow, createSlots, normalizeProps, guardReactiveProps } from 'vue';
import { ElTooltip } from '../../tooltip/index.mjs';
import { ElTag } from '../../tag/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import ElSelectMenu from './select-dropdown.mjs';
import useSelect from './useSelect.mjs';
import { SelectProps, selectEmits } from './defaults.mjs';
import { selectV2InjectionKey } from './token.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import ClickOutside from '../../../directives/click-outside/index.mjs';
import { isArray } from '@vue/shared';
import { useCalcInputWidth } from '../../../hooks/use-calc-input-width/index.mjs';

const _sfc_main = defineComponent({
  name: "ElSelectV2",
  components: {
    ElSelectMenu,
    ElTag,
    ElTooltip,
    ElIcon
  },
  directives: { ClickOutside },
  props: SelectProps,
  emits: selectEmits,
  setup(props, { emit }) {
    const modelValue = computed(() => {
      const { modelValue: rawModelValue, multiple } = props;
      const fallback = multiple ? [] : void 0;
      if (isArray(rawModelValue)) {
        return multiple ? rawModelValue : fallback;
      }
      return multiple ? fallback : rawModelValue;
    });
    const API = useSelect(reactive({
      ...toRefs(props),
      modelValue
    }), emit);
    const { calculatorRef, inputStyle } = useCalcInputWidth();
    provide(selectV2InjectionKey, {
      props: reactive({
        ...toRefs(props),
        height: API.popupHeight,
        modelValue
      }),
      expanded: API.expanded,
      tooltipRef: API.tooltipRef,
      onSelect: API.onSelect,
      onHover: API.onHover,
      onKeyboardNavigate: API.onKeyboardNavigate,
      onKeyboardSelect: API.onKeyboardSelect
    });
    const selectedLabel = computed(() => {
      if (!props.multiple) {
        return API.states.selectedLabel;
      }
      return API.states.cachedOptions.map((i) => i.label);
    });
    return {
      ...API,
      modelValue,
      selectedLabel,
      calculatorRef,
      inputStyle
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  const _component_el_tag = resolveComponent("el-tag");
  const _component_el_tooltip = resolveComponent("el-tooltip");
  const _component_el_icon = resolveComponent("el-icon");
  const _component_el_select_menu = resolveComponent("el-select-menu");
  const _directive_click_outside = resolveDirective("click-outside");
  return withDirectives((openBlock(), createElementBlock("div", {
    ref: "selectRef",
    class: normalizeClass([_ctx.nsSelect.b(), _ctx.nsSelect.m(_ctx.selectSize)]),
    onMouseenter: ($event) => _ctx.states.inputHovering = true,
    onMouseleave: ($event) => _ctx.states.inputHovering = false
  }, [
    createVNode(_component_el_tooltip, {
      ref: "tooltipRef",
      visible: _ctx.dropdownMenuVisible,
      teleported: _ctx.teleported,
      "popper-class": [_ctx.nsSelect.e("popper"), _ctx.popperClass],
      "gpu-acceleration": false,
      "stop-popper-mouse-event": false,
      "popper-options": _ctx.popperOptions,
      "fallback-placements": _ctx.fallbackPlacements,
      effect: _ctx.effect,
      placement: _ctx.placement,
      pure: "",
      transition: `${_ctx.nsSelect.namespace.value}-zoom-in-top`,
      trigger: "click",
      persistent: _ctx.persistent,
      "append-to": _ctx.appendTo,
      "show-arrow": _ctx.showArrow,
      offset: _ctx.offset,
      onBeforeShow: _ctx.handleMenuEnter,
      onHide: ($event) => _ctx.states.isBeforeHide = false
    }, {
      default: withCtx(() => [
        createElementVNode("div", {
          ref: "wrapperRef",
          class: normalizeClass([
            _ctx.nsSelect.e("wrapper"),
            _ctx.nsSelect.is("focused", _ctx.isFocused),
            _ctx.nsSelect.is("hovering", _ctx.states.inputHovering),
            _ctx.nsSelect.is("filterable", _ctx.filterable),
            _ctx.nsSelect.is("disabled", _ctx.selectDisabled)
          ]),
          onClick: withModifiers(_ctx.toggleMenu, ["prevent"])
        }, [
          _ctx.$slots.prefix ? (openBlock(), createElementBlock("div", {
            key: 0,
            ref: "prefixRef",
            class: normalizeClass(_ctx.nsSelect.e("prefix"))
          }, [
            renderSlot(_ctx.$slots, "prefix")
          ], 2)) : createCommentVNode("v-if", true),
          createElementVNode("div", {
            ref: "selectionRef",
            class: normalizeClass([
              _ctx.nsSelect.e("selection"),
              _ctx.nsSelect.is("near", _ctx.multiple && !_ctx.$slots.prefix && !!_ctx.modelValue.length)
            ])
          }, [
            _ctx.multiple ? renderSlot(_ctx.$slots, "tag", { key: 0 }, () => [
              (openBlock(true), createElementBlock(Fragment, null, renderList(_ctx.showTagList, (item) => {
                return openBlock(), createElementBlock("div", {
                  key: _ctx.getValueKey(_ctx.getValue(item)),
                  class: normalizeClass(_ctx.nsSelect.e("selected-item"))
                }, [
                  createVNode(_component_el_tag, {
                    closable: !_ctx.selectDisabled && !_ctx.getDisabled(item),
                    size: _ctx.collapseTagSize,
                    type: _ctx.tagType,
                    effect: _ctx.tagEffect,
                    "disable-transitions": "",
                    style: normalizeStyle(_ctx.tagStyle),
                    onClose: ($event) => _ctx.deleteTag($event, item)
                  }, {
                    default: withCtx(() => [
                      createElementVNode("span", {
                        class: normalizeClass(_ctx.nsSelect.e("tags-text"))
                      }, [
                        renderSlot(_ctx.$slots, "label", {
                          label: _ctx.getLabel(item),
                          value: _ctx.getValue(item)
                        }, () => [
                          createTextVNode(toDisplayString(_ctx.getLabel(item)), 1)
                        ])
                      ], 2)
                    ]),
                    _: 2
                  }, 1032, ["closable", "size", "type", "effect", "style", "onClose"])
                ], 2);
              }), 128)),
              _ctx.collapseTags && _ctx.modelValue.length > _ctx.maxCollapseTags ? (openBlock(), createBlock(_component_el_tooltip, {
                key: 0,
                ref: "tagTooltipRef",
                disabled: _ctx.dropdownMenuVisible || !_ctx.collapseTagsTooltip,
                "fallback-placements": ["bottom", "top", "right", "left"],
                effect: _ctx.effect,
                placement: "bottom",
                teleported: _ctx.teleported
              }, {
                default: withCtx(() => [
                  createElementVNode("div", {
                    ref: "collapseItemRef",
                    class: normalizeClass(_ctx.nsSelect.e("selected-item"))
                  }, [
                    createVNode(_component_el_tag, {
                      closable: false,
                      size: _ctx.collapseTagSize,
                      type: _ctx.tagType,
                      effect: _ctx.tagEffect,
                      style: normalizeStyle(_ctx.collapseTagStyle),
                      "disable-transitions": ""
                    }, {
                      default: withCtx(() => [
                        createElementVNode("span", {
                          class: normalizeClass(_ctx.nsSelect.e("tags-text"))
                        }, " + " + toDisplayString(_ctx.modelValue.length - _ctx.maxCollapseTags), 3)
                      ]),
                      _: 1
                    }, 8, ["size", "type", "effect", "style"])
                  ], 2)
                ]),
                content: withCtx(() => [
                  createElementVNode("div", {
                    ref: "tagMenuRef",
                    class: normalizeClass(_ctx.nsSelect.e("selection"))
                  }, [
                    (openBlock(true), createElementBlock(Fragment, null, renderList(_ctx.collapseTagList, (selected) => {
                      return openBlock(), createElementBlock("div", {
                        key: _ctx.getValueKey(_ctx.getValue(selected)),
                        class: normalizeClass(_ctx.nsSelect.e("selected-item"))
                      }, [
                        createVNode(_component_el_tag, {
                          class: "in-tooltip",
                          closable: !_ctx.selectDisabled && !_ctx.getDisabled(selected),
                          size: _ctx.collapseTagSize,
                          type: _ctx.tagType,
                          effect: _ctx.tagEffect,
                          "disable-transitions": "",
                          onClose: ($event) => _ctx.deleteTag($event, selected)
                        }, {
                          default: withCtx(() => [
                            createElementVNode("span", {
                              class: normalizeClass(_ctx.nsSelect.e("tags-text"))
                            }, [
                              renderSlot(_ctx.$slots, "label", {
                                label: _ctx.getLabel(selected),
                                value: _ctx.getValue(selected)
                              }, () => [
                                createTextVNode(toDisplayString(_ctx.getLabel(selected)), 1)
                              ])
                            ], 2)
                          ]),
                          _: 2
                        }, 1032, ["closable", "size", "type", "effect", "onClose"])
                      ], 2);
                    }), 128))
                  ], 2)
                ]),
                _: 3
              }, 8, ["disabled", "effect", "teleported"])) : createCommentVNode("v-if", true)
            ]) : createCommentVNode("v-if", true),
            createElementVNode("div", {
              class: normalizeClass([
                _ctx.nsSelect.e("selected-item"),
                _ctx.nsSelect.e("input-wrapper"),
                _ctx.nsSelect.is("hidden", !_ctx.filterable)
              ])
            }, [
              withDirectives(createElementVNode("input", {
                id: _ctx.inputId,
                ref: "inputRef",
                "onUpdate:modelValue": ($event) => _ctx.states.inputValue = $event,
                style: normalizeStyle(_ctx.inputStyle),
                autocomplete: _ctx.autocomplete,
                tabindex: _ctx.tabindex,
                "aria-autocomplete": "list",
                "aria-haspopup": "listbox",
                autocapitalize: "off",
                "aria-expanded": _ctx.expanded,
                "aria-label": _ctx.ariaLabel,
                class: normalizeClass([_ctx.nsSelect.e("input"), _ctx.nsSelect.is(_ctx.selectSize)]),
                disabled: _ctx.selectDisabled,
                role: "combobox",
                readonly: !_ctx.filterable,
                spellcheck: "false",
                type: "text",
                name: _ctx.name,
                onInput: _ctx.onInput,
                onCompositionstart: _ctx.handleCompositionStart,
                onCompositionupdate: _ctx.handleCompositionUpdate,
                onCompositionend: _ctx.handleCompositionEnd,
                onKeydown: [
                  withKeys(withModifiers(($event) => _ctx.onKeyboardNavigate("backward"), ["stop", "prevent"]), ["up"]),
                  withKeys(withModifiers(($event) => _ctx.onKeyboardNavigate("forward"), ["stop", "prevent"]), ["down"]),
                  withKeys(withModifiers(_ctx.onKeyboardSelect, ["stop", "prevent"]), ["enter"]),
                  withKeys(withModifiers(_ctx.handleEsc, ["stop", "prevent"]), ["esc"]),
                  withKeys(withModifiers(_ctx.handleDel, ["stop"]), ["delete"])
                ],
                onClick: withModifiers(_ctx.toggleMenu, ["stop"])
              }, null, 46, ["id", "onUpdate:modelValue", "autocomplete", "tabindex", "aria-expanded", "aria-label", "disabled", "readonly", "name", "onInput", "onCompositionstart", "onCompositionupdate", "onCompositionend", "onKeydown", "onClick"]), [
                [vModelText, _ctx.states.inputValue]
              ]),
              _ctx.filterable ? (openBlock(), createElementBlock("span", {
                key: 0,
                ref: "calculatorRef",
                "aria-hidden": "true",
                class: normalizeClass(_ctx.nsSelect.e("input-calculator")),
                textContent: toDisplayString(_ctx.states.inputValue)
              }, null, 10, ["textContent"])) : createCommentVNode("v-if", true)
            ], 2),
            _ctx.shouldShowPlaceholder ? (openBlock(), createElementBlock("div", {
              key: 1,
              class: normalizeClass([
                _ctx.nsSelect.e("selected-item"),
                _ctx.nsSelect.e("placeholder"),
                _ctx.nsSelect.is("transparent", !_ctx.hasModelValue || _ctx.expanded && !_ctx.states.inputValue)
              ])
            }, [
              _ctx.hasModelValue ? renderSlot(_ctx.$slots, "label", {
                key: 0,
                label: _ctx.currentPlaceholder,
                value: _ctx.modelValue
              }, () => [
                createElementVNode("span", null, toDisplayString(_ctx.currentPlaceholder), 1)
              ]) : (openBlock(), createElementBlock("span", { key: 1 }, toDisplayString(_ctx.currentPlaceholder), 1))
            ], 2)) : createCommentVNode("v-if", true)
          ], 2),
          createElementVNode("div", {
            ref: "suffixRef",
            class: normalizeClass(_ctx.nsSelect.e("suffix"))
          }, [
            _ctx.iconComponent ? withDirectives((openBlock(), createBlock(_component_el_icon, {
              key: 0,
              class: normalizeClass([_ctx.nsSelect.e("caret"), _ctx.nsInput.e("icon"), _ctx.iconReverse])
            }, {
              default: withCtx(() => [
                (openBlock(), createBlock(resolveDynamicComponent(_ctx.iconComponent)))
              ]),
              _: 1
            }, 8, ["class"])), [
              [vShow, !_ctx.showClearBtn]
            ]) : createCommentVNode("v-if", true),
            _ctx.showClearBtn && _ctx.clearIcon ? (openBlock(), createBlock(_component_el_icon, {
              key: 1,
              class: normalizeClass([
                _ctx.nsSelect.e("caret"),
                _ctx.nsInput.e("icon"),
                _ctx.nsSelect.e("clear")
              ]),
              onClick: withModifiers(_ctx.handleClear, ["prevent", "stop"])
            }, {
              default: withCtx(() => [
                (openBlock(), createBlock(resolveDynamicComponent(_ctx.clearIcon)))
              ]),
              _: 1
            }, 8, ["class", "onClick"])) : createCommentVNode("v-if", true),
            _ctx.validateState && _ctx.validateIcon && _ctx.needStatusIcon ? (openBlock(), createBlock(_component_el_icon, {
              key: 2,
              class: normalizeClass([
                _ctx.nsInput.e("icon"),
                _ctx.nsInput.e("validateIcon"),
                _ctx.nsInput.is("loading", _ctx.validateState === "validating")
              ])
            }, {
              default: withCtx(() => [
                (openBlock(), createBlock(resolveDynamicComponent(_ctx.validateIcon)))
              ]),
              _: 1
            }, 8, ["class"])) : createCommentVNode("v-if", true)
          ], 2)
        ], 10, ["onClick"])
      ]),
      content: withCtx(() => [
        createVNode(_component_el_select_menu, {
          ref: "menuRef",
          data: _ctx.filteredOptions,
          width: _ctx.popperSize,
          "hovering-index": _ctx.states.hoveringIndex,
          "scrollbar-always-on": _ctx.scrollbarAlwaysOn
        }, createSlots({
          default: withCtx((scope) => [
            renderSlot(_ctx.$slots, "default", normalizeProps(guardReactiveProps(scope)))
          ]),
          _: 2
        }, [
          _ctx.$slots.header ? {
            name: "header",
            fn: withCtx(() => [
              createElementVNode("div", {
                class: normalizeClass(_ctx.nsSelect.be("dropdown", "header"))
              }, [
                renderSlot(_ctx.$slots, "header")
              ], 2)
            ])
          } : void 0,
          _ctx.$slots.loading && _ctx.loading ? {
            name: "loading",
            fn: withCtx(() => [
              createElementVNode("div", {
                class: normalizeClass(_ctx.nsSelect.be("dropdown", "loading"))
              }, [
                renderSlot(_ctx.$slots, "loading")
              ], 2)
            ])
          } : _ctx.loading || _ctx.filteredOptions.length === 0 ? {
            name: "empty",
            fn: withCtx(() => [
              createElementVNode("div", {
                class: normalizeClass(_ctx.nsSelect.be("dropdown", "empty"))
              }, [
                renderSlot(_ctx.$slots, "empty", {}, () => [
                  createElementVNode("span", null, toDisplayString(_ctx.emptyText), 1)
                ])
              ], 2)
            ])
          } : void 0,
          _ctx.$slots.footer ? {
            name: "footer",
            fn: withCtx(() => [
              createElementVNode("div", {
                class: normalizeClass(_ctx.nsSelect.be("dropdown", "footer"))
              }, [
                renderSlot(_ctx.$slots, "footer")
              ], 2)
            ])
          } : void 0
        ]), 1032, ["data", "width", "hovering-index", "scrollbar-always-on"])
      ]),
      _: 3
    }, 8, ["visible", "teleported", "popper-class", "popper-options", "fallback-placements", "effect", "placement", "transition", "persistent", "append-to", "show-arrow", "offset", "onBeforeShow", "onHide"])
  ], 42, ["onMouseenter", "onMouseleave"])), [
    [_directive_click_outside, _ctx.handleClickOutside, _ctx.popperRef]
  ]);
}
var Select = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "select.vue"]]);

export { Select as default };
//# sourceMappingURL=select.mjs.map
