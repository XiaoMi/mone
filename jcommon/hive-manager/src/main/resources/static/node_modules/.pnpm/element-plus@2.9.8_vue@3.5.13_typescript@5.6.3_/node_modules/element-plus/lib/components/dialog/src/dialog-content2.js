'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../icon/index.js');
var constants = require('./constants.js');
var dialogContent = require('./dialog-content.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var tokens = require('../../focus-trap/src/tokens.js');
var index$1 = require('../../../hooks/use-draggable/index.js');
var icon = require('../../../utils/vue/icon.js');
var refs = require('../../../utils/vue/refs.js');
var index = require('../../../hooks/use-locale/index.js');

const __default__ = vue.defineComponent({ name: "ElDialogContent" });
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: dialogContent.dialogContentProps,
  emits: dialogContent.dialogContentEmits,
  setup(__props, { expose }) {
    const props = __props;
    const { t } = index.useLocale();
    const { Close } = icon.CloseComponents;
    const { dialogRef, headerRef, bodyId, ns, style } = vue.inject(constants.dialogInjectionKey);
    const { focusTrapRef } = vue.inject(tokens.FOCUS_TRAP_INJECTION_KEY);
    const dialogKls = vue.computed(() => [
      ns.b(),
      ns.is("fullscreen", props.fullscreen),
      ns.is("draggable", props.draggable),
      ns.is("align-center", props.alignCenter),
      { [ns.m("center")]: props.center }
    ]);
    const composedDialogRef = refs.composeRefs(focusTrapRef, dialogRef);
    const draggable = vue.computed(() => props.draggable);
    const overflow = vue.computed(() => props.overflow);
    const { resetPosition } = index$1.useDraggable(dialogRef, headerRef, draggable, overflow);
    expose({
      resetPosition
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref: vue.unref(composedDialogRef),
        class: vue.normalizeClass(vue.unref(dialogKls)),
        style: vue.normalizeStyle(vue.unref(style)),
        tabindex: "-1"
      }, [
        vue.createElementVNode("header", {
          ref_key: "headerRef",
          ref: headerRef,
          class: vue.normalizeClass([vue.unref(ns).e("header"), _ctx.headerClass, { "show-close": _ctx.showClose }])
        }, [
          vue.renderSlot(_ctx.$slots, "header", {}, () => [
            vue.createElementVNode("span", {
              role: "heading",
              "aria-level": _ctx.ariaLevel,
              class: vue.normalizeClass(vue.unref(ns).e("title"))
            }, vue.toDisplayString(_ctx.title), 11, ["aria-level"])
          ]),
          _ctx.showClose ? (vue.openBlock(), vue.createElementBlock("button", {
            key: 0,
            "aria-label": vue.unref(t)("el.dialog.close"),
            class: vue.normalizeClass(vue.unref(ns).e("headerbtn")),
            type: "button",
            onClick: ($event) => _ctx.$emit("close")
          }, [
            vue.createVNode(vue.unref(index$2.ElIcon), {
              class: vue.normalizeClass(vue.unref(ns).e("close"))
            }, {
              default: vue.withCtx(() => [
                (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.closeIcon || vue.unref(Close))))
              ]),
              _: 1
            }, 8, ["class"])
          ], 10, ["aria-label", "onClick"])) : vue.createCommentVNode("v-if", true)
        ], 2),
        vue.createElementVNode("div", {
          id: vue.unref(bodyId),
          class: vue.normalizeClass([vue.unref(ns).e("body"), _ctx.bodyClass])
        }, [
          vue.renderSlot(_ctx.$slots, "default")
        ], 10, ["id"]),
        _ctx.$slots.footer ? (vue.openBlock(), vue.createElementBlock("footer", {
          key: 0,
          class: vue.normalizeClass([vue.unref(ns).e("footer"), _ctx.footerClass])
        }, [
          vue.renderSlot(_ctx.$slots, "footer")
        ], 2)) : vue.createCommentVNode("v-if", true)
      ], 6);
    };
  }
});
var ElDialogContent = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "dialog-content.vue"]]);

exports["default"] = ElDialogContent;
//# sourceMappingURL=dialog-content2.js.map
