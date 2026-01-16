'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../tooltip/index.js');
var index$1 = require('../../scrollbar/index.js');
var index = require('../../tag/index.js');
var index$3 = require('../../icon/index.js');
var option = require('./option2.js');
var selectDropdown = require('./select-dropdown.js');
var useSelect = require('./useSelect.js');
var token = require('./token.js');
var options = require('./options.js');
var select = require('./select.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index$4 = require('../../../directives/click-outside/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var index$5 = require('../../../hooks/use-calc-input-width/index.js');

const COMPONENT_NAME = "ElSelect";
const _sfc_main = vue.defineComponent({
  name: COMPONENT_NAME,
  componentName: COMPONENT_NAME,
  components: {
    ElSelectMenu: selectDropdown["default"],
    ElOption: option["default"],
    ElOptions: options["default"],
    ElTag: index.ElTag,
    ElScrollbar: index$1.ElScrollbar,
    ElTooltip: index$2.ElTooltip,
    ElIcon: index$3.ElIcon
  },
  directives: { ClickOutside: index$4["default"] },
  props: select.SelectProps,
  emits: [
    event.UPDATE_MODEL_EVENT,
    event.CHANGE_EVENT,
    "remove-tag",
    "clear",
    "visible-change",
    "focus",
    "blur",
    "popup-scroll"
  ],
  setup(props, { emit }) {
    const modelValue = vue.computed(() => {
      const { modelValue: rawModelValue, multiple } = props;
      const fallback = multiple ? [] : void 0;
      if (shared.isArray(rawModelValue)) {
        return multiple ? rawModelValue : fallback;
      }
      return multiple ? fallback : rawModelValue;
    });
    const _props = vue.reactive({
      ...vue.toRefs(props),
      modelValue
    });
    const API = useSelect.useSelect(_props, emit);
    const { calculatorRef, inputStyle } = index$5.useCalcInputWidth();
    vue.provide(token.selectKey, vue.reactive({
      props: _props,
      states: API.states,
      selectRef: API.selectRef,
      optionsArray: API.optionsArray,
      setSelected: API.setSelected,
      handleOptionSelect: API.handleOptionSelect,
      onOptionCreate: API.onOptionCreate,
      onOptionDestroy: API.onOptionDestroy
    }));
    const selectedLabel = vue.computed(() => {
      if (!props.multiple) {
        return API.states.selectedLabel;
      }
      return API.states.selected.map((i) => i.currentLabel);
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
function _sfc_render(_ctx, _cache) {
  const _component_el_tag = vue.resolveComponent("el-tag");
  const _component_el_tooltip = vue.resolveComponent("el-tooltip");
  const _component_el_icon = vue.resolveComponent("el-icon");
  const _component_el_option = vue.resolveComponent("el-option");
  const _component_el_options = vue.resolveComponent("el-options");
  const _component_el_scrollbar = vue.resolveComponent("el-scrollbar");
  const _component_el_select_menu = vue.resolveComponent("el-select-menu");
  const _directive_click_outside = vue.resolveDirective("click-outside");
  return vue.withDirectives((vue.openBlock(), vue.createElementBlock("div", {
    ref: "selectRef",
    class: vue.normalizeClass([_ctx.nsSelect.b(), _ctx.nsSelect.m(_ctx.selectSize)]),
    [vue.toHandlerKey(_ctx.mouseEnterEventName)]: ($event) => _ctx.states.inputHovering = true,
    onMouseleave: ($event) => _ctx.states.inputHovering = false
  }, [
    vue.createVNode(_component_el_tooltip, {
      ref: "tooltipRef",
      visible: _ctx.dropdownMenuVisible,
      placement: _ctx.placement,
      teleported: _ctx.teleported,
      "popper-class": [_ctx.nsSelect.e("popper"), _ctx.popperClass],
      "popper-options": _ctx.popperOptions,
      "fallback-placements": _ctx.fallbackPlacements,
      effect: _ctx.effect,
      pure: "",
      trigger: "click",
      transition: `${_ctx.nsSelect.namespace.value}-zoom-in-top`,
      "stop-popper-mouse-event": false,
      "gpu-acceleration": false,
      persistent: _ctx.persistent,
      "append-to": _ctx.appendTo,
      "show-arrow": _ctx.showArrow,
      offset: _ctx.offset,
      onBeforeShow: _ctx.handleMenuEnter,
      onHide: ($event) => _ctx.states.isBeforeHide = false
    }, {
      default: vue.withCtx(() => {
        var _a;
        return [
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
                _ctx.nsSelect.is("near", _ctx.multiple && !_ctx.$slots.prefix && !!_ctx.states.selected.length)
              ])
            }, [
              _ctx.multiple ? vue.renderSlot(_ctx.$slots, "tag", { key: 0 }, () => [
                (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.showTagList, (item) => {
                  return vue.openBlock(), vue.createElementBlock("div", {
                    key: _ctx.getValueKey(item),
                    class: vue.normalizeClass(_ctx.nsSelect.e("selected-item"))
                  }, [
                    vue.createVNode(_component_el_tag, {
                      closable: !_ctx.selectDisabled && !item.isDisabled,
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
                            label: item.currentLabel,
                            value: item.value
                          }, () => [
                            vue.createTextVNode(vue.toDisplayString(item.currentLabel), 1)
                          ])
                        ], 2)
                      ]),
                      _: 2
                    }, 1032, ["closable", "size", "type", "effect", "style", "onClose"])
                  ], 2);
                }), 128)),
                _ctx.collapseTags && _ctx.states.selected.length > _ctx.maxCollapseTags ? (vue.openBlock(), vue.createBlock(_component_el_tooltip, {
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
                        "disable-transitions": "",
                        style: vue.normalizeStyle(_ctx.collapseTagStyle)
                      }, {
                        default: vue.withCtx(() => [
                          vue.createElementVNode("span", {
                            class: vue.normalizeClass(_ctx.nsSelect.e("tags-text"))
                          }, " + " + vue.toDisplayString(_ctx.states.selected.length - _ctx.maxCollapseTags), 3)
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
                      (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.collapseTagList, (item) => {
                        return vue.openBlock(), vue.createElementBlock("div", {
                          key: _ctx.getValueKey(item),
                          class: vue.normalizeClass(_ctx.nsSelect.e("selected-item"))
                        }, [
                          vue.createVNode(_component_el_tag, {
                            class: "in-tooltip",
                            closable: !_ctx.selectDisabled && !item.isDisabled,
                            size: _ctx.collapseTagSize,
                            type: _ctx.tagType,
                            effect: _ctx.tagEffect,
                            "disable-transitions": "",
                            onClose: ($event) => _ctx.deleteTag($event, item)
                          }, {
                            default: vue.withCtx(() => [
                              vue.createElementVNode("span", {
                                class: vue.normalizeClass(_ctx.nsSelect.e("tags-text"))
                              }, [
                                vue.renderSlot(_ctx.$slots, "label", {
                                  label: item.currentLabel,
                                  value: item.value
                                }, () => [
                                  vue.createTextVNode(vue.toDisplayString(item.currentLabel), 1)
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
                  type: "text",
                  name: _ctx.name,
                  class: vue.normalizeClass([_ctx.nsSelect.e("input"), _ctx.nsSelect.is(_ctx.selectSize)]),
                  disabled: _ctx.selectDisabled,
                  autocomplete: _ctx.autocomplete,
                  style: vue.normalizeStyle(_ctx.inputStyle),
                  tabindex: _ctx.tabindex,
                  role: "combobox",
                  readonly: !_ctx.filterable,
                  spellcheck: "false",
                  "aria-activedescendant": ((_a = _ctx.hoverOption) == null ? void 0 : _a.id) || "",
                  "aria-controls": _ctx.contentId,
                  "aria-expanded": _ctx.dropdownMenuVisible,
                  "aria-label": _ctx.ariaLabel,
                  "aria-autocomplete": "none",
                  "aria-haspopup": "listbox",
                  onKeydown: [
                    vue.withKeys(vue.withModifiers(($event) => _ctx.navigateOptions("next"), ["stop", "prevent"]), ["down"]),
                    vue.withKeys(vue.withModifiers(($event) => _ctx.navigateOptions("prev"), ["stop", "prevent"]), ["up"]),
                    vue.withKeys(vue.withModifiers(_ctx.handleEsc, ["stop", "prevent"]), ["esc"]),
                    vue.withKeys(vue.withModifiers(_ctx.selectOption, ["stop", "prevent"]), ["enter"]),
                    vue.withKeys(vue.withModifiers(_ctx.deletePrevTag, ["stop"]), ["delete"])
                  ],
                  onCompositionstart: _ctx.handleCompositionStart,
                  onCompositionupdate: _ctx.handleCompositionUpdate,
                  onCompositionend: _ctx.handleCompositionEnd,
                  onInput: _ctx.onInput,
                  onClick: vue.withModifiers(_ctx.toggleMenu, ["stop"])
                }, null, 46, ["id", "onUpdate:modelValue", "name", "disabled", "autocomplete", "tabindex", "readonly", "aria-activedescendant", "aria-controls", "aria-expanded", "aria-label", "onKeydown", "onCompositionstart", "onCompositionupdate", "onCompositionend", "onInput", "onClick"]), [
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
              _ctx.iconComponent && !_ctx.showClose ? (vue.openBlock(), vue.createBlock(_component_el_icon, {
                key: 0,
                class: vue.normalizeClass([_ctx.nsSelect.e("caret"), _ctx.nsSelect.e("icon"), _ctx.iconReverse])
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.iconComponent)))
                ]),
                _: 1
              }, 8, ["class"])) : vue.createCommentVNode("v-if", true),
              _ctx.showClose && _ctx.clearIcon ? (vue.openBlock(), vue.createBlock(_component_el_icon, {
                key: 1,
                class: vue.normalizeClass([
                  _ctx.nsSelect.e("caret"),
                  _ctx.nsSelect.e("icon"),
                  _ctx.nsSelect.e("clear")
                ]),
                onClick: _ctx.handleClearClick
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
        ];
      }),
      content: vue.withCtx(() => [
        vue.createVNode(_component_el_select_menu, { ref: "menuRef" }, {
          default: vue.withCtx(() => [
            _ctx.$slots.header ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 0,
              class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "header")),
              onClick: vue.withModifiers(() => {
              }, ["stop"])
            }, [
              vue.renderSlot(_ctx.$slots, "header")
            ], 10, ["onClick"])) : vue.createCommentVNode("v-if", true),
            vue.withDirectives(vue.createVNode(_component_el_scrollbar, {
              id: _ctx.contentId,
              ref: "scrollbarRef",
              tag: "ul",
              "wrap-class": _ctx.nsSelect.be("dropdown", "wrap"),
              "view-class": _ctx.nsSelect.be("dropdown", "list"),
              class: vue.normalizeClass([_ctx.nsSelect.is("empty", _ctx.filteredOptionsCount === 0)]),
              role: "listbox",
              "aria-label": _ctx.ariaLabel,
              "aria-orientation": "vertical",
              onScroll: _ctx.popupScroll
            }, {
              default: vue.withCtx(() => [
                _ctx.showNewOption ? (vue.openBlock(), vue.createBlock(_component_el_option, {
                  key: 0,
                  value: _ctx.states.inputValue,
                  created: true
                }, null, 8, ["value"])) : vue.createCommentVNode("v-if", true),
                vue.createVNode(_component_el_options, null, {
                  default: vue.withCtx(() => [
                    vue.renderSlot(_ctx.$slots, "default")
                  ]),
                  _: 3
                })
              ]),
              _: 3
            }, 8, ["id", "wrap-class", "view-class", "class", "aria-label", "onScroll"]), [
              [vue.vShow, _ctx.states.options.size > 0 && !_ctx.loading]
            ]),
            _ctx.$slots.loading && _ctx.loading ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 1,
              class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "loading"))
            }, [
              vue.renderSlot(_ctx.$slots, "loading")
            ], 2)) : _ctx.loading || _ctx.filteredOptionsCount === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 2,
              class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "empty"))
            }, [
              vue.renderSlot(_ctx.$slots, "empty", {}, () => [
                vue.createElementVNode("span", null, vue.toDisplayString(_ctx.emptyText), 1)
              ])
            ], 2)) : vue.createCommentVNode("v-if", true),
            _ctx.$slots.footer ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 3,
              class: vue.normalizeClass(_ctx.nsSelect.be("dropdown", "footer")),
              onClick: vue.withModifiers(() => {
              }, ["stop"])
            }, [
              vue.renderSlot(_ctx.$slots, "footer")
            ], 10, ["onClick"])) : vue.createCommentVNode("v-if", true)
          ]),
          _: 3
        }, 512)
      ]),
      _: 3
    }, 8, ["visible", "placement", "teleported", "popper-class", "popper-options", "fallback-placements", "effect", "transition", "persistent", "append-to", "show-arrow", "offset", "onBeforeShow", "onHide"])
  ], 16, ["onMouseleave"])), [
    [_directive_click_outside, _ctx.handleClickOutside, _ctx.popperRef]
  ]);
}
var Select = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "select.vue"]]);

exports["default"] = Select;
//# sourceMappingURL=select2.js.map
