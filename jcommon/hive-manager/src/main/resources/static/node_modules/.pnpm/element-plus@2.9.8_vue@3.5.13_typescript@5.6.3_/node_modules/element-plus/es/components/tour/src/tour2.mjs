import { defineComponent, ref, computed, toRef, watch, useSlots, provide, openBlock, createElementBlock, Fragment, createVNode, unref, withCtx, createElementVNode, mergeProps, normalizeStyle, createBlock, renderSlot, createCommentVNode } from 'vue';
import { useVModel } from '@vueuse/core';
import { ElTeleport } from '../../teleport/index.mjs';
import ElTourMask from './mask2.mjs';
import ElTourContent from './content2.mjs';
import ElTourSteps from './steps.mjs';
import { tourProps, tourEmits } from './tour.mjs';
import { useTarget, tourKey } from './helper.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isBoolean } from '../../../utils/types.mjs';
import { useZIndex } from '../../../hooks/use-z-index/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElTour"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: tourProps,
  emits: tourEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = useNamespace("tour");
    const total = ref(0);
    const currentStep = ref();
    const current = useVModel(props, "current", emit, {
      passive: true
    });
    const currentTarget = computed(() => {
      var _a;
      return (_a = currentStep.value) == null ? void 0 : _a.target;
    });
    const kls = computed(() => [
      ns.b(),
      mergedType.value === "primary" ? ns.m("primary") : ""
    ]);
    const mergedPlacement = computed(() => {
      var _a;
      return ((_a = currentStep.value) == null ? void 0 : _a.placement) || props.placement;
    });
    const mergedContentStyle = computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.contentStyle) != null ? _b : props.contentStyle;
    });
    const mergedMask = computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.mask) != null ? _b : props.mask;
    });
    const mergedShowMask = computed(() => !!mergedMask.value && props.modelValue);
    const mergedMaskStyle = computed(() => isBoolean(mergedMask.value) ? void 0 : mergedMask.value);
    const mergedShowArrow = computed(() => {
      var _a, _b;
      return !!currentTarget.value && ((_b = (_a = currentStep.value) == null ? void 0 : _a.showArrow) != null ? _b : props.showArrow);
    });
    const mergedScrollIntoViewOptions = computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.scrollIntoViewOptions) != null ? _b : props.scrollIntoViewOptions;
    });
    const mergedType = computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.type) != null ? _b : props.type;
    });
    const { nextZIndex } = useZIndex();
    const nowZIndex = nextZIndex();
    const mergedZIndex = computed(() => {
      var _a;
      return (_a = props.zIndex) != null ? _a : nowZIndex;
    });
    const { mergedPosInfo: pos, triggerTarget } = useTarget(currentTarget, toRef(props, "modelValue"), toRef(props, "gap"), mergedMask, mergedScrollIntoViewOptions);
    watch(() => props.modelValue, (val) => {
      if (!val) {
        current.value = 0;
      }
    });
    const onEscClose = () => {
      if (props.closeOnPressEscape) {
        emit(UPDATE_MODEL_EVENT, false);
        emit("close", current.value);
      }
    };
    const onUpdateTotal = (val) => {
      total.value = val;
    };
    const slots = useSlots();
    provide(tourKey, {
      currentStep,
      current,
      total,
      showClose: toRef(props, "showClose"),
      closeIcon: toRef(props, "closeIcon"),
      mergedType,
      ns,
      slots,
      updateModelValue(modelValue) {
        emit(UPDATE_MODEL_EVENT, modelValue);
      },
      onClose() {
        emit("close", current.value);
      },
      onFinish() {
        emit("finish");
      },
      onChange() {
        emit(CHANGE_EVENT, current.value);
      }
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock(Fragment, null, [
        createVNode(unref(ElTeleport), { to: _ctx.appendTo }, {
          default: withCtx(() => {
            var _a, _b;
            return [
              createElementVNode("div", mergeProps({ class: unref(kls) }, _ctx.$attrs), [
                createVNode(ElTourMask, {
                  visible: unref(mergedShowMask),
                  fill: (_a = unref(mergedMaskStyle)) == null ? void 0 : _a.color,
                  style: normalizeStyle((_b = unref(mergedMaskStyle)) == null ? void 0 : _b.style),
                  pos: unref(pos),
                  "z-index": unref(mergedZIndex),
                  "target-area-clickable": _ctx.targetAreaClickable
                }, null, 8, ["visible", "fill", "style", "pos", "z-index", "target-area-clickable"]),
                _ctx.modelValue ? (openBlock(), createBlock(ElTourContent, {
                  key: unref(current),
                  reference: unref(triggerTarget),
                  placement: unref(mergedPlacement),
                  "show-arrow": unref(mergedShowArrow),
                  "z-index": unref(mergedZIndex),
                  style: normalizeStyle(unref(mergedContentStyle)),
                  onClose: onEscClose
                }, {
                  default: withCtx(() => [
                    createVNode(unref(ElTourSteps), {
                      current: unref(current),
                      onUpdateTotal
                    }, {
                      default: withCtx(() => [
                        renderSlot(_ctx.$slots, "default")
                      ]),
                      _: 3
                    }, 8, ["current"])
                  ]),
                  _: 3
                }, 8, ["reference", "placement", "show-arrow", "z-index", "style"])) : createCommentVNode("v-if", true)
              ], 16)
            ];
          }),
          _: 3
        }, 8, ["to"]),
        createCommentVNode(" just for IDE "),
        createCommentVNode("v-if", true)
      ], 64);
    };
  }
});
var Tour = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "tour.vue"]]);

export { Tour as default };
//# sourceMappingURL=tour2.mjs.map
