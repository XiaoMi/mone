'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var iconsVue = require('@element-plus/icons-vue');
var index$4 = require('../../overlay/index.js');
var focusTrap = require('../../focus-trap/src/focus-trap.js');
var index$3 = require('../../teleport/index.js');
var index$5 = require('../../icon/index.js');
var drawer = require('./drawer.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useDialog = require('../../dialog/src/use-dialog.js');
var index = require('../../../hooks/use-deprecated/index.js');
var index$1 = require('../../../hooks/use-namespace/index.js');
var index$2 = require('../../../hooks/use-locale/index.js');
var style = require('../../../utils/dom/style.js');

const __default__ = vue.defineComponent({
  name: "ElDrawer",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: drawer.drawerProps,
  emits: drawer.drawerEmits,
  setup(__props, { expose }) {
    const props = __props;
    const slots = vue.useSlots();
    index.useDeprecated({
      scope: "el-drawer",
      from: "the title slot",
      replacement: "the header slot",
      version: "3.0.0",
      ref: "https://element-plus.org/en-US/component/drawer.html#slots"
    }, vue.computed(() => !!slots.title));
    const drawerRef = vue.ref();
    const focusStartRef = vue.ref();
    const ns = index$1.useNamespace("drawer");
    const { t } = index$2.useLocale();
    const {
      afterEnter,
      afterLeave,
      beforeLeave,
      visible,
      rendered,
      titleId,
      bodyId,
      zIndex,
      onModalClick,
      onOpenAutoFocus,
      onCloseAutoFocus,
      onFocusoutPrevented,
      onCloseRequested,
      handleClose
    } = useDialog.useDialog(props, drawerRef);
    const isHorizontal = vue.computed(() => props.direction === "rtl" || props.direction === "ltr");
    const drawerSize = vue.computed(() => style.addUnit(props.size));
    expose({
      handleClose,
      afterEnter,
      afterLeave
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.unref(index$3.ElTeleport), {
        to: _ctx.appendTo,
        disabled: _ctx.appendTo !== "body" ? false : !_ctx.appendToBody
      }, {
        default: vue.withCtx(() => [
          vue.createVNode(vue.Transition, {
            name: vue.unref(ns).b("fade"),
            onAfterEnter: vue.unref(afterEnter),
            onAfterLeave: vue.unref(afterLeave),
            onBeforeLeave: vue.unref(beforeLeave),
            persisted: ""
          }, {
            default: vue.withCtx(() => [
              vue.withDirectives(vue.createVNode(vue.unref(index$4.ElOverlay), {
                mask: _ctx.modal,
                "overlay-class": _ctx.modalClass,
                "z-index": vue.unref(zIndex),
                onClick: vue.unref(onModalClick)
              }, {
                default: vue.withCtx(() => [
                  vue.createVNode(vue.unref(focusTrap["default"]), {
                    loop: "",
                    trapped: vue.unref(visible),
                    "focus-trap-el": drawerRef.value,
                    "focus-start-el": focusStartRef.value,
                    onFocusAfterTrapped: vue.unref(onOpenAutoFocus),
                    onFocusAfterReleased: vue.unref(onCloseAutoFocus),
                    onFocusoutPrevented: vue.unref(onFocusoutPrevented),
                    onReleaseRequested: vue.unref(onCloseRequested)
                  }, {
                    default: vue.withCtx(() => [
                      vue.createElementVNode("div", vue.mergeProps({
                        ref_key: "drawerRef",
                        ref: drawerRef,
                        "aria-modal": "true",
                        "aria-label": _ctx.title || void 0,
                        "aria-labelledby": !_ctx.title ? vue.unref(titleId) : void 0,
                        "aria-describedby": vue.unref(bodyId)
                      }, _ctx.$attrs, {
                        class: [vue.unref(ns).b(), _ctx.direction, vue.unref(visible) && "open"],
                        style: vue.unref(isHorizontal) ? "width: " + vue.unref(drawerSize) : "height: " + vue.unref(drawerSize),
                        role: "dialog",
                        onClick: vue.withModifiers(() => {
                        }, ["stop"])
                      }), [
                        vue.createElementVNode("span", {
                          ref_key: "focusStartRef",
                          ref: focusStartRef,
                          class: vue.normalizeClass(vue.unref(ns).e("sr-focus")),
                          tabindex: "-1"
                        }, null, 2),
                        _ctx.withHeader ? (vue.openBlock(), vue.createElementBlock("header", {
                          key: 0,
                          class: vue.normalizeClass([vue.unref(ns).e("header"), _ctx.headerClass])
                        }, [
                          !_ctx.$slots.title ? vue.renderSlot(_ctx.$slots, "header", {
                            key: 0,
                            close: vue.unref(handleClose),
                            titleId: vue.unref(titleId),
                            titleClass: vue.unref(ns).e("title")
                          }, () => [
                            !_ctx.$slots.title ? (vue.openBlock(), vue.createElementBlock("span", {
                              key: 0,
                              id: vue.unref(titleId),
                              role: "heading",
                              "aria-level": _ctx.headerAriaLevel,
                              class: vue.normalizeClass(vue.unref(ns).e("title"))
                            }, vue.toDisplayString(_ctx.title), 11, ["id", "aria-level"])) : vue.createCommentVNode("v-if", true)
                          ]) : vue.renderSlot(_ctx.$slots, "title", { key: 1 }, () => [
                            vue.createCommentVNode(" DEPRECATED SLOT ")
                          ]),
                          _ctx.showClose ? (vue.openBlock(), vue.createElementBlock("button", {
                            key: 2,
                            "aria-label": vue.unref(t)("el.drawer.close"),
                            class: vue.normalizeClass(vue.unref(ns).e("close-btn")),
                            type: "button",
                            onClick: vue.unref(handleClose)
                          }, [
                            vue.createVNode(vue.unref(index$5.ElIcon), {
                              class: vue.normalizeClass(vue.unref(ns).e("close"))
                            }, {
                              default: vue.withCtx(() => [
                                vue.createVNode(vue.unref(iconsVue.Close))
                              ]),
                              _: 1
                            }, 8, ["class"])
                          ], 10, ["aria-label", "onClick"])) : vue.createCommentVNode("v-if", true)
                        ], 2)) : vue.createCommentVNode("v-if", true),
                        vue.unref(rendered) ? (vue.openBlock(), vue.createElementBlock("div", {
                          key: 1,
                          id: vue.unref(bodyId),
                          class: vue.normalizeClass([vue.unref(ns).e("body"), _ctx.bodyClass])
                        }, [
                          vue.renderSlot(_ctx.$slots, "default")
                        ], 10, ["id"])) : vue.createCommentVNode("v-if", true),
                        _ctx.$slots.footer ? (vue.openBlock(), vue.createElementBlock("div", {
                          key: 2,
                          class: vue.normalizeClass([vue.unref(ns).e("footer"), _ctx.footerClass])
                        }, [
                          vue.renderSlot(_ctx.$slots, "footer")
                        ], 2)) : vue.createCommentVNode("v-if", true)
                      ], 16, ["aria-label", "aria-labelledby", "aria-describedby", "onClick"])
                    ]),
                    _: 3
                  }, 8, ["trapped", "focus-trap-el", "focus-start-el", "onFocusAfterTrapped", "onFocusAfterReleased", "onFocusoutPrevented", "onReleaseRequested"])
                ]),
                _: 3
              }, 8, ["mask", "overlay-class", "z-index", "onClick"]), [
                [vue.vShow, vue.unref(visible)]
              ])
            ]),
            _: 3
          }, 8, ["name", "onAfterEnter", "onAfterLeave", "onBeforeLeave"])
        ]),
        _: 3
      }, 8, ["to", "disabled"]);
    };
  }
});
var Drawer = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "drawer.vue"]]);

exports["default"] = Drawer;
//# sourceMappingURL=drawer2.js.map
