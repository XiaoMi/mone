'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var index$2 = require('../../button/index.js');
var index$1 = require('../../icon/index.js');
var step = require('./step.js');
var helper = require('./helper.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-locale/index.js');
var icon = require('../../../utils/vue/icon.js');

const __default__ = vue.defineComponent({
  name: "ElTourStep"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: step.tourStepProps,
  emits: step.tourStepEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { Close } = icon.CloseComponents;
    const { t } = index.useLocale();
    const {
      currentStep,
      current,
      total,
      showClose,
      closeIcon,
      mergedType,
      ns,
      slots: tourSlots,
      updateModelValue,
      onClose: tourOnClose,
      onFinish: tourOnFinish,
      onChange
    } = vue.inject(helper.tourKey);
    vue.watch(props, (val) => {
      currentStep.value = val;
    }, {
      immediate: true
    });
    const mergedShowClose = vue.computed(() => {
      var _a;
      return (_a = props.showClose) != null ? _a : showClose.value;
    });
    const mergedCloseIcon = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = props.closeIcon) != null ? _a : closeIcon.value) != null ? _b : Close;
    });
    const filterButtonProps = (btnProps) => {
      if (!btnProps)
        return;
      return lodashUnified.omit(btnProps, ["children", "onClick"]);
    };
    const onPrev = () => {
      var _a, _b;
      current.value -= 1;
      if ((_a = props.prevButtonProps) == null ? void 0 : _a.onClick) {
        (_b = props.prevButtonProps) == null ? void 0 : _b.onClick();
      }
      onChange();
    };
    const onNext = () => {
      var _a;
      if (current.value >= total.value - 1) {
        onFinish();
      } else {
        current.value += 1;
      }
      if ((_a = props.nextButtonProps) == null ? void 0 : _a.onClick) {
        props.nextButtonProps.onClick();
      }
      onChange();
    };
    const onFinish = () => {
      onClose();
      tourOnFinish();
    };
    const onClose = () => {
      updateModelValue(false);
      tourOnClose();
      emit("close");
    };
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock(vue.Fragment, null, [
        vue.unref(mergedShowClose) ? (vue.openBlock(), vue.createElementBlock("button", {
          key: 0,
          "aria-label": "Close",
          class: vue.normalizeClass(vue.unref(ns).e("closebtn")),
          type: "button",
          onClick: onClose
        }, [
          vue.createVNode(vue.unref(index$1.ElIcon), {
            class: vue.normalizeClass(vue.unref(ns).e("close"))
          }, {
            default: vue.withCtx(() => [
              (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(mergedCloseIcon))))
            ]),
            _: 1
          }, 8, ["class"])
        ], 2)) : vue.createCommentVNode("v-if", true),
        vue.createElementVNode("header", {
          class: vue.normalizeClass([vue.unref(ns).e("header"), { "show-close": vue.unref(showClose) }])
        }, [
          vue.renderSlot(_ctx.$slots, "header", {}, () => [
            vue.createElementVNode("span", {
              role: "heading",
              class: vue.normalizeClass(vue.unref(ns).e("title"))
            }, vue.toDisplayString(_ctx.title), 3)
          ])
        ], 2),
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(ns).e("body"))
        }, [
          vue.renderSlot(_ctx.$slots, "default", {}, () => [
            vue.createElementVNode("span", null, vue.toDisplayString(_ctx.description), 1)
          ])
        ], 2),
        vue.createElementVNode("footer", {
          class: vue.normalizeClass(vue.unref(ns).e("footer"))
        }, [
          vue.createElementVNode("div", {
            class: vue.normalizeClass(vue.unref(ns).b("indicators"))
          }, [
            vue.unref(tourSlots).indicators ? (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(vue.unref(tourSlots).indicators), {
              key: 0,
              current: vue.unref(current),
              total: vue.unref(total)
            }, null, 8, ["current", "total"])) : (vue.openBlock(true), vue.createElementBlock(vue.Fragment, { key: 1 }, vue.renderList(vue.unref(total), (item, index) => {
              return vue.openBlock(), vue.createElementBlock("span", {
                key: item,
                class: vue.normalizeClass([vue.unref(ns).b("indicator"), index === vue.unref(current) ? "is-active" : ""])
              }, null, 2);
            }), 128))
          ], 2),
          vue.createElementVNode("div", {
            class: vue.normalizeClass(vue.unref(ns).b("buttons"))
          }, [
            vue.unref(current) > 0 ? (vue.openBlock(), vue.createBlock(vue.unref(index$2.ElButton), vue.mergeProps({
              key: 0,
              size: "small",
              type: vue.unref(mergedType)
            }, filterButtonProps(_ctx.prevButtonProps), { onClick: onPrev }), {
              default: vue.withCtx(() => {
                var _a, _b;
                return [
                  vue.createTextVNode(vue.toDisplayString((_b = (_a = _ctx.prevButtonProps) == null ? void 0 : _a.children) != null ? _b : vue.unref(t)("el.tour.previous")), 1)
                ];
              }),
              _: 1
            }, 16, ["type"])) : vue.createCommentVNode("v-if", true),
            vue.unref(current) <= vue.unref(total) - 1 ? (vue.openBlock(), vue.createBlock(vue.unref(index$2.ElButton), vue.mergeProps({
              key: 1,
              size: "small",
              type: vue.unref(mergedType) === "primary" ? "default" : "primary"
            }, filterButtonProps(_ctx.nextButtonProps), { onClick: onNext }), {
              default: vue.withCtx(() => {
                var _a, _b;
                return [
                  vue.createTextVNode(vue.toDisplayString((_b = (_a = _ctx.nextButtonProps) == null ? void 0 : _a.children) != null ? _b : vue.unref(current) === vue.unref(total) - 1 ? vue.unref(t)("el.tour.finish") : vue.unref(t)("el.tour.next")), 1)
                ];
              }),
              _: 1
            }, 16, ["type"])) : vue.createCommentVNode("v-if", true)
          ], 2)
        ], 2)
      ], 64);
    };
  }
});
var TourStep = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "step.vue"]]);

exports["default"] = TourStep;
//# sourceMappingURL=step2.js.map
