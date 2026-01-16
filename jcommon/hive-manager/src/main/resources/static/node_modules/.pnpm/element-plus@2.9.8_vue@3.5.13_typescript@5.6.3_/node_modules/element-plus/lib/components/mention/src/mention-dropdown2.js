'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../scrollbar/index.js');
var mentionDropdown = require('./mention-dropdown.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-locale/index.js');
var scroll = require('../../../utils/dom/scroll.js');

const __default__ = vue.defineComponent({
  name: "ElMentionDropdown"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: mentionDropdown.mentionDropdownProps,
  emits: mentionDropdown.mentionDropdownEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = index.useNamespace("mention");
    const { t } = index$1.useLocale();
    const hoveringIndex = vue.ref(-1);
    const scrollbarRef = vue.ref();
    const optionRefs = vue.ref();
    const dropdownRef = vue.ref();
    const optionkls = (item, index) => [
      ns.be("dropdown", "item"),
      ns.is("hovering", hoveringIndex.value === index),
      ns.is("disabled", item.disabled || props.disabled)
    ];
    const handleSelect = (item) => {
      if (item.disabled || props.disabled)
        return;
      emit("select", item);
    };
    const handleMouseEnter = (index) => {
      hoveringIndex.value = index;
    };
    const filteredAllDisabled = vue.computed(() => props.disabled || props.options.every((item) => item.disabled));
    const hoverOption = vue.computed(() => props.options[hoveringIndex.value]);
    const selectHoverOption = () => {
      if (!hoverOption.value)
        return;
      emit("select", hoverOption.value);
    };
    const navigateOptions = (direction) => {
      const { options } = props;
      if (options.length === 0 || filteredAllDisabled.value)
        return;
      if (direction === "next") {
        hoveringIndex.value++;
        if (hoveringIndex.value === options.length) {
          hoveringIndex.value = 0;
        }
      } else if (direction === "prev") {
        hoveringIndex.value--;
        if (hoveringIndex.value < 0) {
          hoveringIndex.value = options.length - 1;
        }
      }
      const option = options[hoveringIndex.value];
      if (option.disabled) {
        navigateOptions(direction);
        return;
      }
      vue.nextTick(() => scrollToOption(option));
    };
    const scrollToOption = (option) => {
      var _a, _b, _c, _d;
      const { options } = props;
      const index = options.findIndex((item) => item.value === option.value);
      const target = (_a = optionRefs.value) == null ? void 0 : _a[index];
      if (target) {
        const menu = (_c = (_b = dropdownRef.value) == null ? void 0 : _b.querySelector) == null ? void 0 : _c.call(_b, `.${ns.be("dropdown", "wrap")}`);
        if (menu) {
          scroll.scrollIntoView(menu, target);
        }
      }
      (_d = scrollbarRef.value) == null ? void 0 : _d.handleScroll();
    };
    const resetHoveringIndex = () => {
      if (filteredAllDisabled.value || props.options.length === 0) {
        hoveringIndex.value = -1;
      } else {
        hoveringIndex.value = 0;
      }
    };
    vue.watch(() => props.options, resetHoveringIndex, {
      immediate: true
    });
    expose({
      hoveringIndex,
      navigateOptions,
      selectHoverOption,
      hoverOption
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "dropdownRef",
        ref: dropdownRef,
        class: vue.normalizeClass(vue.unref(ns).b("dropdown"))
      }, [
        _ctx.$slots.header ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass(vue.unref(ns).be("dropdown", "header"))
        }, [
          vue.renderSlot(_ctx.$slots, "header")
        ], 2)) : vue.createCommentVNode("v-if", true),
        vue.withDirectives(vue.createVNode(vue.unref(index$2.ElScrollbar), {
          id: _ctx.contentId,
          ref_key: "scrollbarRef",
          ref: scrollbarRef,
          tag: "ul",
          "wrap-class": vue.unref(ns).be("dropdown", "wrap"),
          "view-class": vue.unref(ns).be("dropdown", "list"),
          role: "listbox",
          "aria-label": _ctx.ariaLabel,
          "aria-orientation": "vertical"
        }, {
          default: vue.withCtx(() => [
            (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.options, (item, index) => {
              return vue.openBlock(), vue.createElementBlock("li", {
                id: `${_ctx.contentId}-${index}`,
                ref_for: true,
                ref_key: "optionRefs",
                ref: optionRefs,
                key: index,
                class: vue.normalizeClass(optionkls(item, index)),
                role: "option",
                "aria-disabled": item.disabled || _ctx.disabled || void 0,
                "aria-selected": hoveringIndex.value === index,
                onMousemove: ($event) => handleMouseEnter(index),
                onClick: vue.withModifiers(($event) => handleSelect(item), ["stop"])
              }, [
                vue.renderSlot(_ctx.$slots, "label", {
                  item,
                  index
                }, () => {
                  var _a;
                  return [
                    vue.createElementVNode("span", null, vue.toDisplayString((_a = item.label) != null ? _a : item.value), 1)
                  ];
                })
              ], 42, ["id", "aria-disabled", "aria-selected", "onMousemove", "onClick"]);
            }), 128))
          ]),
          _: 3
        }, 8, ["id", "wrap-class", "view-class", "aria-label"]), [
          [vue.vShow, _ctx.options.length > 0 && !_ctx.loading]
        ]),
        _ctx.loading ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 1,
          class: vue.normalizeClass(vue.unref(ns).be("dropdown", "loading"))
        }, [
          vue.renderSlot(_ctx.$slots, "loading", {}, () => [
            vue.createTextVNode(vue.toDisplayString(vue.unref(t)("el.mention.loading")), 1)
          ])
        ], 2)) : vue.createCommentVNode("v-if", true),
        _ctx.$slots.footer ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 2,
          class: vue.normalizeClass(vue.unref(ns).be("dropdown", "footer"))
        }, [
          vue.renderSlot(_ctx.$slots, "footer")
        ], 2)) : vue.createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var ElMentionDropdown = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "mention-dropdown.vue"]]);

exports["default"] = ElMentionDropdown;
//# sourceMappingURL=mention-dropdown2.js.map
