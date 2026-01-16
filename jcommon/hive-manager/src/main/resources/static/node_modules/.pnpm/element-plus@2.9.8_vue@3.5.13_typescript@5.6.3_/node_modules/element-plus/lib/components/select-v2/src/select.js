'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$1 = require('../../tooltip/index.js');
var index = require('../../tag/index.js');
var index$2 = require('../../icon/index.js');
var selectDropdown = require('./select-dropdown.js');
var useSelect = require('./useSelect.js');
var defaults = require('./defaults.js');
var token = require('./token.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index$3 = require('../../../directives/click-outside/index.js');
var shared = require('@vue/shared');
var index$4 = require('../../../hooks/use-calc-input-width/index.js');

const _sfc_main = vue.defineComponent({
  name: "ElSelectV2",
  components: {
    ElSelectMenu: selectDropdown["default"],
    ElTag: index.ElTag,
    ElTooltip: index$1.ElTooltip,
    ElIcon: index$2.ElIcon
  },
  directives: { ClickOutside: index$3["default"] },
  props: defaults.SelectProps,
  emits: defaults.selectEmits,
  setup(props, { emit }) {
    const modelValue = vue.computed(() => {
      const { modelValue: rawModelValue, multiple } = props;
      const fallback = multiple ? [] : void 0;
      if (shared.isArray(rawModelValue)) {
        return multiple ? rawModelValue : fallback;
      }
      return multiple ? fallback : rawModelValue;
    });
    const API = useSelect["default"](vue.reactive({
      ...vue.toRefs(props),
      modelValue
    }), emit);
    const { calculatorRef, inputStyle } = index$4.useCalcInputWidth();
    vue.provide(token.selectV2InjectionKey, {
      props: vue.reactive({
        ...vue.toRefs(props),
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
    const selectedLabel = vue.computed(() => {
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
  const _component_el_tag = vue.resolveComponent("el-tag");
  const _component_el_tooltip = vue.resolveComponent("el-tooltip");
  const _component_el_icon = vue.resolveComponent("el-icon");
  const _component_el_select_menu = vue.resolveComponent("el-select-menu");
  const _directive_click_outside = vue.resolveDirective("click-outside");
  return vue.withDirectives((vue.openBlock(), vue.createElementBlock("div", {
    ref: "selectRef",
    class: vue.normalizeClass([_ctx.nsSelect.b(), _ctx.nsSelect.m(_ctx.selectSize)]),
    onMouseenter: ($event) => _ctx.states.inputHovering = true,
    onMouseleave: ($event) => _ctx.states.inputHovering = false
  }, [
    vue.createVNode(_component_el_tooltip, {
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
      default: vue.withCtx(() => [
        vue.createElementVNode("div", {
          ref: "wrapperRef",
          class: vue.normalizeClass([
            _ctx.nsSelect.e("wrapper"),
            _ctx.nsSelect.is("focused", _ctx.isFocused),
            _ctx.nsSelect.is("hovering", _ctx.states.inputHovering),
            _ctx.nsSelect.is("filterable", _ctx.filterable),
            _ctx.nsSelect.is("disabled", _ctx.selectDisabled)
          ]),
          onClick: vue.withModifiers(_ctx.toggleMenu, ["prevent"])
        }, [
          _ctx.$slots.prefix ? (vue.openBlock(), vue.createElementBlock("div", {
            key: 0,
            ref: "prefixRef",
            class: vue.normalizeClass(_ctx.nsSelect.e("prefix"))
          }, [
            vue.renderSlot(_ctx.$slots, "prefix")
          ], 2)) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("div", {
            ref: "selectionRef",
            class: vue.normalizeClass([
              _ctx.nsSelect.e("selection"),
              _ctx.nsSelect.is("near", _ctx.multiple && !_ctx.$slots.prefix && !!_ctx.modelValue.length)
            ])
          }, [
            _ctx.multiple ? vue.renderSlot(_ctx.$slots, "tag", { key: 0 }, () => [
              (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.showTagList, (item) => {
                return vue.openBlock(), vue.createElementBlock("div", {
                  key: _ctx.getValueKey(_ctx.getValue(item)),
                  class: vue.normalizeClass(_ctx.nsSelect.e("selected-item"))
                }, [
                  vue.createVNode(_component_el_tag, {
                    closable: !_ctx.selectDisabled && !_ctx.getDisabled(item),
                    size: _ctx.collapseTagSize,
                    type: _ctx.tagType,
                    effect: _ctx.tagEffect,
                    "disable-transitions": "",
                    style: vue.normalizeStyle(_ctx.tagStyle),
                    onClose: ($event) => _ctx.deleteTag($event, item)
                  }, {
                    default: vue.withCtx(() => [
                      vue.createElementVNode("span", {
                        class: vue.normalizeClass(_ctx.nsSelect.e("tags-text"))
                      }, [
                        vue.renderSlot(_ctx.$slots, "label", {
                          label: _ctx.getLabel(item),
                          value: _ctx.getValue(item)
                        }, () => [
                          vue.createTextVNode(vue.toDisplayString(_ctx.getLabel(item)), 1)
                        ])
                      ], 2)
                    ]),
                    _: 2
                  }, 1032, ["closable", "size", "type", "effect", "style", "onClose"])
                ], 2);
              }), 128)),
              _ctx.collapseTags && _ctx.modelValue.length > _ctx.maxCollapseTags ? (vue.openBlock(), vue.createBlock(_component_el_tooltip, {
                key: 0,
                ref: "tagTooltipRef",
                disabled: _ctx.dropdownMenuVisible || !_ctx.collapseTagsTooltip,
                "fallback-placements": ["bottom", "top", "right", "left"],
                effect: _ctx.effect,
                placement: "bottom",
                teleported: _ctx.teleported
              }, {
                default: vue.withCtx(() => [
                  vue.createElementVNode("div", {
                    ref: "collapseItemRef",
                    class: vue.normalizeClass(_ctx.nsSelect.e("selected-item"))
                  }, [
                    vue.createVNode(_component_el_tag, {
                      closable: false,
                      size: _ctx.collapseTagSize,
                      type: _ctx.tagType,
                      effect: _ctx.tagEffect,
                      style: vue.normalizeStyle(_ctx.collapseTagStyle),
                      "disable-transitions": ""
                    }, {
                      default: vue.withCtx(() => [
                        vue.createElementVNode("span", {
                          class: vue.normalizeClass(_ctx.nsSelect.e("tags-text"))
                        }, " + " + vue.toDisplayString(_ctx.modelValue.length - _ctx.maxCollapseTags), 3)
                      ]),
                      _: 1
                    }, 8, ["size", "type", "effect", "style"])
                  ], 2)
                ]),
                content: vue.withCtx(() => [
                  vue.createElementVNode("div", {
                    ref: "tagMenuRef",
                    class: vue.normalizeClass(_ctx.nsSelect.e("selection"))
                  }, [
                    (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.collapseTagList, (selected) => {
                      return vue.openBlock(), vue.createElementBlock("div", {
                        key: _ctx.getValueKey(_ctx.getValue(selected)),
                        class: vue.normalizeClass(_ctx.nsSelect.e("selected-item"))
                      }, [
                        vue.createVNode(_component_el_tag, {
                          class: "in-tooltip",
                          closable: !_ctx.selectDisabled && !_ctx.getDisabled(selected),
                          size: _ctx.collapseTagSize,
                          type: _ctx.tagType,
                          effect: _ctx.tagEffect,
                          "disable-transitions": "",
                          onClose: ($event) => _ctx.deleteTag($event, selected)
                        }, {
                          default: vue.withCtx(() => [
                            vue.createElementVNode("span", {
                              class: vue.normalizeClass(_ctx.nsSelect.e("tags-text"))
                            }, [
                              vue.renderSlot(_ctx.$slots, "label", {
                                label: _ctx.getLabel(selected),
                                value: _ctx.getValue(selected)
                              }, () => [
                                vue.createTextVNode(vue.toDisplayString(_ctx.getLabel(selected)), 1)
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
              }, 8, ["disabled", "effect", "teleported"])) : vue.createCommentVNode("v-if", true)
            ]) : vue.createCommentVNode("v-if", true),
            vue.createElementVNode("div", {
              class: vue.normalizeClass([
                _ctx.nsSelect.e("selected-item"),
                _ctx.nsSelect.e("input-wrapper"),
                _ctx.nsSelect.is("hidden", !_ctx.filterable)
              ])
            }, [
              vue.withDirectives(vue.createElementVNode("input", {
                id: _ctx.inputId,
                ref: "inputRef",
                "onUpdate:modelValue": ($event) => _ctx.states.inputValue = $event,
                style: vue.normalizeStyle(_ctx.inputStyle),
                autocomplete: _ctx.autocomplete,
                tabindex: _ctx.tabindex,
                "aria-autocomplete": "list",
                "aria-haspopup": "listbox",
                autocapitalize: "off",
                "aria-expanded": _ctx.expanded,
                "aria-label": _ctx.ariaLabel,
                class: vue.normalizeClass([_ctx.nsSelect.e("input"), _ctx.nsSelect.is(_ctx.selectSize)]),
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
                  vue.withKeys(vue.withModifiers(($event) => _ctx.onKeyboardNavigate("backward"), ["stop", "prevent"]), ["up"]),
                  vue.withKeys(vue.withModifiers(($event) => _ctx.onKeyboardNavigate("forward"), ["stop", "prevent"]), ["down"]),
                  vue.withKeys(vue.withModifiers(_ctx.onKeyboardSelect, ["stop", "prevent"]), ["enter"]),
                  vue.withKeys(vue.withModifiers(_ctx.handleEsc, ["stop", "prevent"]), ["esc"]),
                  vue.withKeys(vue.withModifiers(_ctx.handleDel, ["stop"]), ["delete"])
                ],
                onClick: vue.withModifiers(_ctx.toggleMenu, ["stop"])
              }, null, 46, ["id", "onUpdate:modelValue", "autocomplete", "tabindex", "aria-expanded", "aria-label", "disabled", "readonly", "name", "onInput", "onCompositionstart", "onCompositionupdate", "onCompositionend", "onKeydown", "onClick"]), [
                [vue.vModelText, _ctx.states.inputValue]
              ]),
              _ctx.filterable ? (vue.openBlock(), vue.createElementBlock("span", {
                key: 0,
                ref: "calculatorRef",
                "aria-hidden": "true",
                class: vue.normalizeClass(_ctx.nsSelect.e("input-calculator")),
                textContent: vue.toDisplayString(_ctx.states.inputValue)
              }, null, 10, ["textContent"])) : vue.createCommentVNode("v-if", true)
            ], 2),
            _ctx.shouldShowPlaceholder ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 1,
              class: vue.normalizeClass([
                _ctx.nsSelect.e("selected-item"),
                _ctx.nsSelect.e("placeholder"),
                _ctx.nsSelect.is("transparent", !_ctx.hasModelValue || _ctx.expanded && !_ctx.states.inputValue)
              ])
            }, [
              _ctx.hasModelValue ? vue.renderSlot(_ctx.$slots, "label", {
                key: 0,
                label: _ctx.currentPlaceholder,
                value: _ctx.modelValue
              }, () => [
                vue.createElementVNode("span", null, vue.toDisplayString(_ctx.currentPlaceholder), 1)
              ]) : (vue.openBlock(), vue.createElementBlock("span", { key: 1 }, vue.toDisplayString(_ctx.currentPlaceholder), 1))
            ], 2)) : vue.createCommentVNode("v-if", true)
          ], 2),
          vue.createElementVNode("div", {
            ref: "suffixRef",
            class: vue.normalizeClass(_ctx.nsSelect.e("suffix"))
          }, [
            _ctx.iconComponent ? vue.withDirectives((vue.openBlock(), vue.createBlock(_component_el_icon, {
              key: 0,
              class: vue.normalizeClass([_ctx.nsSelect.e("caret"), _ctx.nsInput.e("icon"), _ctx.iconReverse])
            }, {
              default: vue.withCtx(() => [
                (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.iconComponent)))
              ]),
              _: 1
            }, 8, ["class"])), [
              [vue.vShow, !_ctx.showClearBtn]
            ]) : vue.createCommentVNode("v-if", true),
            _ctx.showClearBtn && _ctx.clearIcon ? (vue.openBlock(), vue.createBlock(_component_el_icon, {
              key: 1,
              class: vue.normalizeClass([
                _ctx.nsSelect.e("caret"),
                _ctx.nsInput.e("icon"),
                _ctx.nsSelect.e("clear")
              ]),
              onClick: vue.withModifiers(_ctx.handleClear, ["prevent", "stop"])
            }, {
              default: vue.withCtx(() => [
                (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.clearIcon)))
              ]),
              _: 1
            }, 8, ["class", "onClick"])) : vue.createCommentVNode("v-if", true),
            _ctx.validateState && _ctx.validateIcon && _ctx.needStatusIcon ? (vue.openBlock(), vue.createBlock(_component_el_icon, {
              key: 2,
              class: vue.normalizeClass([
                _ctx.nsInput.e("icon"),
                _ctx.nsInput.e("validateIcon"),
                _ctx.nsInput.is("loading", _ctx.validateState === "validating")
              ])
            }, {
              default: vue.withCtx(() => [
                (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.validateIcon)))
              ]),
              _: 1
            }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
          ], 2)
        ], 10, ["onClick"])
      ]),
      content: vue.withCtx(() => [
        vue.createVNode(_component_el_select_menu, {
          ref: "menuRef",
          data: _ctx.filteredOptions,
          width: _ctx.popperSize,
          "hovering-index": _ctx.states.hoveringIndex,
          "scrollbar-always-on": _ctx.scrollbarAlwaysOn
        }, vue.createSlots({
          default: vue.withCtx((scope) => [
            vue.renderSlot(_ctx.$slots, "default", vue.normalizeProps(vue.guardReactiveProps(scope)))
          ]),
          _: 2
        }, [
          _ctx.$slots.header ? {
            name: "header",
            fn: vue.withCtx(() => [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "header"))
              }, [
                vue.renderSlot(_ctx.$slots, "header")
              ], 2)
            ])
          } : void 0,
          _ctx.$slots.loading && _ctx.loading ? {
            name: "loading",
            fn: vue.withCtx(() => [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "loading"))
              }, [
                vue.renderSlot(_ctx.$slots, "loading")
              ], 2)
            ])
          } : _ctx.loading || _ctx.filteredOptions.length === 0 ? {
            name: "empty",
            fn: vue.withCtx(() => [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "empty"))
              }, [
                vue.renderSlot(_ctx.$slots, "empty", {}, () => [
                  vue.createElementVNode("span", null, vue.toDisplayString(_ctx.emptyText), 1)
                ])
              ], 2)
            ])
          } : void 0,
          _ctx.$slots.footer ? {
            name: "footer",
            fn: vue.withCtx(() => [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "footer"))
              }, [
                vue.renderSlot(_ctx.$slots, "footer")
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
var Select = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "select.vue"]]);

exports["default"] = Select;
//# sourceMappingURL=select.js.map
