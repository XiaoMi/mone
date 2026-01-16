'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var core = require('@vueuse/core');
var constants = require('./constants.js');
var trigger = require('./trigger.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-forward-ref/index.js');
var aria = require('../../../utils/dom/aria.js');
var onlyChild = require('../../slot/src/only-child.js');
var types = require('../../../utils/types.js');

const __default__ = vue.defineComponent({
  name: "ElPopperTrigger",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: trigger.popperTriggerProps,
  setup(__props, { expose }) {
    const props = __props;
    const { role, triggerRef } = vue.inject(constants.POPPER_INJECTION_KEY, void 0);
    index.useForwardRef(triggerRef);
    const ariaControls = vue.computed(() => {
      return ariaHaspopup.value ? props.id : void 0;
    });
    const ariaDescribedby = vue.computed(() => {
      if (role && role.value === "tooltip") {
        return props.open && props.id ? props.id : void 0;
      }
      return void 0;
    });
    const ariaHaspopup = vue.computed(() => {
      if (role && role.value !== "tooltip") {
        return role.value;
      }
      return void 0;
    });
    const ariaExpanded = vue.computed(() => {
      return ariaHaspopup.value ? `${props.open}` : void 0;
    });
    let virtualTriggerAriaStopWatch = void 0;
    const TRIGGER_ELE_EVENTS = [
      "onMouseenter",
      "onMouseleave",
      "onClick",
      "onKeydown",
      "onFocus",
      "onBlur",
      "onContextmenu"
    ];
    vue.onMounted(() => {
      vue.watch(() => props.virtualRef, (virtualEl) => {
        if (virtualEl) {
          triggerRef.value = core.unrefElement(virtualEl);
        }
      }, {
        immediate: true
      });
      vue.watch(triggerRef, (el, prevEl) => {
        virtualTriggerAriaStopWatch == null ? void 0 : virtualTriggerAriaStopWatch();
        virtualTriggerAriaStopWatch = void 0;
        if (types.isElement(el)) {
          TRIGGER_ELE_EVENTS.forEach((eventName) => {
            var _a;
            const handler = props[eventName];
            if (handler) {
              el.addEventListener(eventName.slice(2).toLowerCase(), handler);
              (_a = prevEl == null ? void 0 : prevEl.removeEventListener) == null ? void 0 : _a.call(prevEl, eventName.slice(2).toLowerCase(), handler);
            }
          });
          if (aria.isFocusable(el)) {
            virtualTriggerAriaStopWatch = vue.watch([ariaControls, ariaDescribedby, ariaHaspopup, ariaExpanded], (watches) => {
              [
                "aria-controls",
                "aria-describedby",
                "aria-haspopup",
                "aria-expanded"
              ].forEach((key, idx) => {
                lodashUnified.isNil(watches[idx]) ? el.removeAttribute(key) : el.setAttribute(key, watches[idx]);
              });
            }, { immediate: true });
          }
        }
        if (types.isElement(prevEl) && aria.isFocusable(prevEl)) {
          [
            "aria-controls",
            "aria-describedby",
            "aria-haspopup",
            "aria-expanded"
          ].forEach((key) => prevEl.removeAttribute(key));
        }
      }, {
        immediate: true
      });
    });
    vue.onBeforeUnmount(() => {
      virtualTriggerAriaStopWatch == null ? void 0 : virtualTriggerAriaStopWatch();
      virtualTriggerAriaStopWatch = void 0;
      if (triggerRef.value && types.isElement(triggerRef.value)) {
        const el = triggerRef.value;
        TRIGGER_ELE_EVENTS.forEach((eventName) => {
          const handler = props[eventName];
          if (handler) {
            el.removeEventListener(eventName.slice(2).toLowerCase(), handler);
          }
        });
        triggerRef.value = void 0;
      }
    });
    expose({
      triggerRef
    });
    return (_ctx, _cache) => {
      return !_ctx.virtualTriggering ? (vue.openBlock(), vue.createBlock(vue.unref(onlyChild.OnlyChild), vue.mergeProps({ key: 0 }, _ctx.$attrs, {
        "aria-controls": vue.unref(ariaControls),
        "aria-describedby": vue.unref(ariaDescribedby),
        "aria-expanded": vue.unref(ariaExpanded),
        "aria-haspopup": vue.unref(ariaHaspopup)
      }), {
        default: vue.withCtx(() => [
          vue.renderSlot(_ctx.$slots, "default")
        ]),
        _: 3
      }, 16, ["aria-controls", "aria-describedby", "aria-expanded", "aria-haspopup"])) : vue.createCommentVNode("v-if", true);
    };
  }
});
var ElPopperTrigger = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "trigger.vue"]]);

exports["default"] = ElPopperTrigger;
//# sourceMappingURL=trigger2.js.map
