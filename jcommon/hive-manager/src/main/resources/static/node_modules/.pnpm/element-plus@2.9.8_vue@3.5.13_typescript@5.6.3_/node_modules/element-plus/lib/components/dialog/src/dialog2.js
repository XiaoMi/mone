'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$3 = require('../../overlay/index.js');
var focusTrap = require('../../focus-trap/src/focus-trap.js');
var index$2 = require('../../teleport/index.js');
var dialogContent = require('./dialog-content2.js');
var constants = require('./constants.js');
var dialog = require('./dialog.js');
var useDialog = require('./use-dialog.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-deprecated/index.js');
var index$1 = require('../../../hooks/use-namespace/index.js');
var index$4 = require('../../../hooks/use-same-target/index.js');

const __default__ = vue.defineComponent({
  name: "ElDialog",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: dialog.dialogProps,
  emits: dialog.dialogEmits,
  setup(__props, { expose }) {
    const props = __props;
    const slots = vue.useSlots();
    index.useDeprecated({
      scope: "el-dialog",
      from: "the title slot",
      replacement: "the header slot",
      version: "3.0.0",
      ref: "https://element-plus.org/en-US/component/dialog.html#slots"
    }, vue.computed(() => !!slots.title));
    const ns = index$1.useNamespace("dialog");
    const dialogRef = vue.ref();
    const headerRef = vue.ref();
    const dialogContentRef = vue.ref();
    const {
      visible,
      titleId,
      bodyId,
      style,
      overlayDialogStyle,
      rendered,
      zIndex,
      afterEnter,
      afterLeave,
      beforeLeave,
      handleClose,
      onModalClick,
      onOpenAutoFocus,
      onCloseAutoFocus,
      onCloseRequested,
      onFocusoutPrevented
    } = useDialog.useDialog(props, dialogRef);
    vue.provide(constants.dialogInjectionKey, {
      dialogRef,
      headerRef,
      bodyId,
      ns,
      rendered,
      style
    });
    const overlayEvent = index$4.useSameTarget(onModalClick);
    const draggable = vue.computed(() => props.draggable && !props.fullscreen);
    const resetPosition = () => {
      var _a;
      (_a = dialogContentRef.value) == null ? void 0 : _a.resetPosition();
    };
    expose({
      visible,
      dialogContentRef,
      resetPosition,
      handleClose
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.unref(index$2.ElTeleport), {
        to: _ctx.appendTo,
        disabled: _ctx.appendTo !== "body" ? false : !_ctx.appendToBody
      }, {
        default: vue.withCtx(() => [
          vue.createVNode(vue.Transition, {
            name: "dialog-fade",
            onAfterEnter: vue.unref(afterEnter),
            onAfterLeave: vue.unref(afterLeave),
            onBeforeLeave: vue.unref(beforeLeave),
            persisted: ""
          }, {
            default: vue.withCtx(() => [
              vue.withDirectives(vue.createVNode(vue.unref(index$3.ElOverlay), {
                "custom-mask-event": "",
                mask: _ctx.modal,
                "overlay-class": _ctx.modalClass,
                "z-index": vue.unref(zIndex)
              }, {
                default: vue.withCtx(() => [
                  vue.createElementVNode("div", {
                    role: "dialog",
                    "aria-modal": "true",
                    "aria-label": _ctx.title || void 0,
                    "aria-labelledby": !_ctx.title ? vue.unref(titleId) : void 0,
                    "aria-describedby": vue.unref(bodyId),
                    class: vue.normalizeClass(`${vue.unref(ns).namespace.value}-overlay-dialog`),
                    style: vue.normalizeStyle(vue.unref(overlayDialogStyle)),
                    onClick: vue.unref(overlayEvent).onClick,
                    onMousedown: vue.unref(overlayEvent).onMousedown,
                    onMouseup: vue.unref(overlayEvent).onMouseup
                  }, [
                    vue.createVNode(vue.unref(focusTrap["default"]), {
                      loop: "",
                      trapped: vue.unref(visible),
                      "focus-start-el": "container",
                      onFocusAfterTrapped: vue.unref(onOpenAutoFocus),
                      onFocusAfterReleased: vue.unref(onCloseAutoFocus),
                      onFocusoutPrevented: vue.unref(onFocusoutPrevented),
                      onReleaseRequested: vue.unref(onCloseRequested)
                    }, {
                      default: vue.withCtx(() => [
                        vue.unref(rendered) ? (vue.openBlock(), vue.createBlock(dialogContent["default"], vue.mergeProps({
                          key: 0,
                          ref_key: "dialogContentRef",
                          ref: dialogContentRef
                        }, _ctx.$attrs, {
                          center: _ctx.center,
                          "align-center": _ctx.alignCenter,
                          "close-icon": _ctx.closeIcon,
                          draggable: vue.unref(draggable),
                          overflow: _ctx.overflow,
                          fullscreen: _ctx.fullscreen,
                          "header-class": _ctx.headerClass,
                          "body-class": _ctx.bodyClass,
                          "footer-class": _ctx.footerClass,
                          "show-close": _ctx.showClose,
                          title: _ctx.title,
                          "aria-level": _ctx.headerAriaLevel,
                          onClose: vue.unref(handleClose)
                        }), vue.createSlots({
                          header: vue.withCtx(() => [
                            !_ctx.$slots.title ? vue.renderSlot(_ctx.$slots, "header", {
                              key: 0,
                              close: vue.unref(handleClose),
                              titleId: vue.unref(titleId),
                              titleClass: vue.unref(ns).e("title")
                            }) : vue.renderSlot(_ctx.$slots, "title", { key: 1 })
                          ]),
                          default: vue.withCtx(() => [
                            vue.renderSlot(_ctx.$slots, "default")
                          ]),
                          _: 2
                        }, [
                          _ctx.$slots.footer ? {
                            name: "footer",
                            fn: vue.withCtx(() => [
                              vue.renderSlot(_ctx.$slots, "footer")
                            ])
                          } : void 0
                        ]), 1040, ["center", "align-center", "close-icon", "draggable", "overflow", "fullscreen", "header-class", "body-class", "footer-class", "show-close", "title", "aria-level", "onClose"])) : vue.createCommentVNode("v-if", true)
                      ]),
                      _: 3
                    }, 8, ["trapped", "onFocusAfterTrapped", "onFocusAfterReleased", "onFocusoutPrevented", "onReleaseRequested"])
                  ], 46, ["aria-label", "aria-labelledby", "aria-describedby", "onClick", "onMousedown", "onMouseup"])
                ]),
                _: 3
              }, 8, ["mask", "overlay-class", "z-index"]), [
                [vue.vShow, vue.unref(visible)]
              ])
            ]),
            _: 3
          }, 8, ["onAfterEnter", "onAfterLeave", "onBeforeLeave"])
        ]),
        _: 3
      }, 8, ["to", "disabled"]);
    };
  }
});
var Dialog = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "dialog.vue"]]);

exports["default"] = Dialog;
//# sourceMappingURL=dialog2.js.map
