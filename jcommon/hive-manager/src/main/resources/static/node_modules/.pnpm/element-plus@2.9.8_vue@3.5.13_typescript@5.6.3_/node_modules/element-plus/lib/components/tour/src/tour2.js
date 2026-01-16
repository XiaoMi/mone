'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var index$2 = require('../../teleport/index.js');
var mask = require('./mask2.js');
var content = require('./content2.js');
var steps = require('./steps.js');
var tour = require('./tour.js');
var helper = require('./helper.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');
var index$1 = require('../../../hooks/use-z-index/index.js');
var event = require('../../../constants/event.js');

const __default__ = vue.defineComponent({
  name: "ElTour"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: tour.tourProps,
  emits: tour.tourEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = index.useNamespace("tour");
    const total = vue.ref(0);
    const currentStep = vue.ref();
    const current = core.useVModel(props, "current", emit, {
      passive: true
    });
    const currentTarget = vue.computed(() => {
      var _a;
      return (_a = currentStep.value) == null ? void 0 : _a.target;
    });
    const kls = vue.computed(() => [
      ns.b(),
      mergedType.value === "primary" ? ns.m("primary") : ""
    ]);
    const mergedPlacement = vue.computed(() => {
      var _a;
      return ((_a = currentStep.value) == null ? void 0 : _a.placement) || props.placement;
    });
    const mergedContentStyle = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.contentStyle) != null ? _b : props.contentStyle;
    });
    const mergedMask = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.mask) != null ? _b : props.mask;
    });
    const mergedShowMask = vue.computed(() => !!mergedMask.value && props.modelValue);
    const mergedMaskStyle = vue.computed(() => types.isBoolean(mergedMask.value) ? void 0 : mergedMask.value);
    const mergedShowArrow = vue.computed(() => {
      var _a, _b;
      return !!currentTarget.value && ((_b = (_a = currentStep.value) == null ? void 0 : _a.showArrow) != null ? _b : props.showArrow);
    });
    const mergedScrollIntoViewOptions = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.scrollIntoViewOptions) != null ? _b : props.scrollIntoViewOptions;
    });
    const mergedType = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = currentStep.value) == null ? void 0 : _a.type) != null ? _b : props.type;
    });
    const { nextZIndex } = index$1.useZIndex();
    const nowZIndex = nextZIndex();
    const mergedZIndex = vue.computed(() => {
      var _a;
      return (_a = props.zIndex) != null ? _a : nowZIndex;
    });
    const { mergedPosInfo: pos, triggerTarget } = helper.useTarget(currentTarget, vue.toRef(props, "modelValue"), vue.toRef(props, "gap"), mergedMask, mergedScrollIntoViewOptions);
    vue.watch(() => props.modelValue, (val) => {
      if (!val) {
        current.value = 0;
      }
    });
    const onEscClose = () => {
      if (props.closeOnPressEscape) {
        emit(event.UPDATE_MODEL_EVENT, false);
        emit("close", current.value);
      }
    };
    const onUpdateTotal = (val) => {
      total.value = val;
    };
    const slots = vue.useSlots();
    vue.provide(helper.tourKey, {
      currentStep,
      current,
      total,
      showClose: vue.toRef(props, "showClose"),
      closeIcon: vue.toRef(props, "closeIcon"),
      mergedType,
      ns,
      slots,
      updateModelValue(modelValue) {
        emit(event.UPDATE_MODEL_EVENT, modelValue);
      },
      onClose() {
        emit("close", current.value);
      },
      onFinish() {
        emit("finish");
      },
      onChange() {
        emit(event.CHANGE_EVENT, current.value);
      }
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock(vue.Fragment, null, [
        vue.createVNode(vue.unref(index$2.ElTeleport), { to: _ctx.appendTo }, {
          default: vue.withCtx(() => {
            var _a, _b;
            return [
              vue.createElementVNode("div", vue.mergeProps({ class: vue.unref(kls) }, _ctx.$attrs), [
                vue.createVNode(mask["default"], {
                  visible: vue.unref(mergedShowMask),
                  fill: (_a = vue.unref(mergedMaskStyle)) == null ? void 0 : _a.color,
                  style: vue.normalizeStyle((_b = vue.unref(mergedMaskStyle)) == null ? void 0 : _b.style),
                  pos: vue.unref(pos),
                  "z-index": vue.unref(mergedZIndex),
                  "target-area-clickable": _ctx.targetAreaClickable
                }, null, 8, ["visible", "fill", "style", "pos", "z-index", "target-area-clickable"]),
                _ctx.modelValue ? (vue.openBlock(), vue.createBlock(content["default"], {
                  key: vue.unref(current),
                  reference: vue.unref(triggerTarget),
                  placement: vue.unref(mergedPlacement),
                  "show-arrow": vue.unref(mergedShowArrow),
                  "z-index": vue.unref(mergedZIndex),
                  style: vue.normalizeStyle(vue.unref(mergedContentStyle)),
                  onClose: onEscClose
                }, {
                  default: vue.withCtx(() => [
                    vue.createVNode(vue.unref(steps["default"]), {
                      current: vue.unref(current),
                      onUpdateTotal
                    }, {
                      default: vue.withCtx(() => [
                        vue.renderSlot(_ctx.$slots, "default")
                      ]),
                      _: 3
                    }, 8, ["current"])
                  ]),
                  _: 3
                }, 8, ["reference", "placement", "show-arrow", "z-index", "style"])) : vue.createCommentVNode("v-if", true)
              ], 16)
            ];
          }),
          _: 3
        }, 8, ["to"]),
        vue.createCommentVNode(" just for IDE "),
        vue.createCommentVNode("v-if", true)
      ], 64);
    };
  }
});
var Tour = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "tour.vue"]]);

exports["default"] = Tour;
//# sourceMappingURL=tour2.js.map
