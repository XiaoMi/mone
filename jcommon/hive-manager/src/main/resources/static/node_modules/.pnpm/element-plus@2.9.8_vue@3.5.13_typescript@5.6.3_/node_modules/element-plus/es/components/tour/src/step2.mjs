import { defineComponent, inject, watch, computed, openBlock, createElementBlock, Fragment, unref, normalizeClass, createVNode, withCtx, createBlock, resolveDynamicComponent, createCommentVNode, createElementVNode, renderSlot, toDisplayString, renderList, mergeProps, createTextVNode } from 'vue';
import { omit } from 'lodash-unified';
import { ElButton } from '../../button/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { tourStepProps, tourStepEmits } from './step.mjs';
import { tourKey } from './helper.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';
import { CloseComponents } from '../../../utils/vue/icon.mjs';

const __default__ = defineComponent({
  name: "ElTourStep"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: tourStepProps,
  emits: tourStepEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { Close } = CloseComponents;
    const { t } = useLocale();
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
    } = inject(tourKey);
    watch(props, (val) => {
      currentStep.value = val;
    }, {
      immediate: true
    });
    const mergedShowClose = computed(() => {
      var _a;
      return (_a = props.showClose) != null ? _a : showClose.value;
    });
    const mergedCloseIcon = computed(() => {
      var _a, _b;
      return (_b = (_a = props.closeIcon) != null ? _a : closeIcon.value) != null ? _b : Close;
    });
    const filterButtonProps = (btnProps) => {
      if (!btnProps)
        return;
      return omit(btnProps, ["children", "onClick"]);
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
      return openBlock(), createElementBlock(Fragment, null, [
        unref(mergedShowClose) ? (openBlock(), createElementBlock("button", {
          key: 0,
          "aria-label": "Close",
          class: normalizeClass(unref(ns).e("closebtn")),
          type: "button",
          onClick: onClose
        }, [
          createVNode(unref(ElIcon), {
            class: normalizeClass(unref(ns).e("close"))
          }, {
            default: withCtx(() => [
              (openBlock(), createBlock(resolveDynamicComponent(unref(mergedCloseIcon))))
            ]),
            _: 1
          }, 8, ["class"])
        ], 2)) : createCommentVNode("v-if", true),
        createElementVNode("header", {
          class: normalizeClass([unref(ns).e("header"), { "show-close": unref(showClose) }])
        }, [
          renderSlot(_ctx.$slots, "header", {}, () => [
            createElementVNode("span", {
              role: "heading",
              class: normalizeClass(unref(ns).e("title"))
            }, toDisplayString(_ctx.title), 3)
          ])
        ], 2),
        createElementVNode("div", {
          class: normalizeClass(unref(ns).e("body"))
        }, [
          renderSlot(_ctx.$slots, "default", {}, () => [
            createElementVNode("span", null, toDisplayString(_ctx.description), 1)
          ])
        ], 2),
        createElementVNode("footer", {
          class: normalizeClass(unref(ns).e("footer"))
        }, [
          createElementVNode("div", {
            class: normalizeClass(unref(ns).b("indicators"))
          }, [
            unref(tourSlots).indicators ? (openBlock(), createBlock(resolveDynamicComponent(unref(tourSlots).indicators), {
              key: 0,
              current: unref(current),
              total: unref(total)
            }, null, 8, ["current", "total"])) : (openBlock(true), createElementBlock(Fragment, { key: 1 }, renderList(unref(total), (item, index) => {
              return openBlock(), createElementBlock("span", {
                key: item,
                class: normalizeClass([unref(ns).b("indicator"), index === unref(current) ? "is-active" : ""])
              }, null, 2);
            }), 128))
          ], 2),
          createElementVNode("div", {
            class: normalizeClass(unref(ns).b("buttons"))
          }, [
            unref(current) > 0 ? (openBlock(), createBlock(unref(ElButton), mergeProps({
              key: 0,
              size: "small",
              type: unref(mergedType)
            }, filterButtonProps(_ctx.prevButtonProps), { onClick: onPrev }), {
              default: withCtx(() => {
                var _a, _b;
                return [
                  createTextVNode(toDisplayString((_b = (_a = _ctx.prevButtonProps) == null ? void 0 : _a.children) != null ? _b : unref(t)("el.tour.previous")), 1)
                ];
              }),
              _: 1
            }, 16, ["type"])) : createCommentVNode("v-if", true),
            unref(current) <= unref(total) - 1 ? (openBlock(), createBlock(unref(ElButton), mergeProps({
              key: 1,
              size: "small",
              type: unref(mergedType) === "primary" ? "default" : "primary"
            }, filterButtonProps(_ctx.nextButtonProps), { onClick: onNext }), {
              default: withCtx(() => {
                var _a, _b;
                return [
                  createTextVNode(toDisplayString((_b = (_a = _ctx.nextButtonProps) == null ? void 0 : _a.children) != null ? _b : unref(current) === unref(total) - 1 ? unref(t)("el.tour.finish") : unref(t)("el.tour.next")), 1)
                ];
              }),
              _: 1
            }, 16, ["type"])) : createCommentVNode("v-if", true)
          ], 2)
        ], 2)
      ], 64);
    };
  }
});
var TourStep = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "step.vue"]]);

export { TourStep as default };
//# sourceMappingURL=step2.mjs.map
