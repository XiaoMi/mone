'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var bar = require('./bar2.js');
var constants = require('./constants.js');
var scrollbar = require('./scrollbar.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var style = require('../../../utils/dom/style.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');
var error = require('../../../utils/error.js');

const COMPONENT_NAME = "ElScrollbar";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: scrollbar.scrollbarProps,
  emits: scrollbar.scrollbarEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = index.useNamespace("scrollbar");
    let stopResizeObserver = void 0;
    let stopResizeListener = void 0;
    let wrapScrollTop = 0;
    let wrapScrollLeft = 0;
    const scrollbarRef = vue.ref();
    const wrapRef = vue.ref();
    const resizeRef = vue.ref();
    const barRef = vue.ref();
    const wrapStyle = vue.computed(() => {
      const style$1 = {};
      if (props.height)
        style$1.height = style.addUnit(props.height);
      if (props.maxHeight)
        style$1.maxHeight = style.addUnit(props.maxHeight);
      return [props.wrapStyle, style$1];
    });
    const wrapKls = vue.computed(() => {
      return [
        props.wrapClass,
        ns.e("wrap"),
        { [ns.em("wrap", "hidden-default")]: !props.native }
      ];
    });
    const resizeKls = vue.computed(() => {
      return [ns.e("view"), props.viewClass];
    });
    const handleScroll = () => {
      var _a;
      if (wrapRef.value) {
        (_a = barRef.value) == null ? void 0 : _a.handleScroll(wrapRef.value);
        wrapScrollTop = wrapRef.value.scrollTop;
        wrapScrollLeft = wrapRef.value.scrollLeft;
        emit("scroll", {
          scrollTop: wrapRef.value.scrollTop,
          scrollLeft: wrapRef.value.scrollLeft
        });
      }
    };
    function scrollTo(arg1, arg2) {
      if (shared.isObject(arg1)) {
        wrapRef.value.scrollTo(arg1);
      } else if (types.isNumber(arg1) && types.isNumber(arg2)) {
        wrapRef.value.scrollTo(arg1, arg2);
      }
    }
    const setScrollTop = (value) => {
      if (!types.isNumber(value)) {
        error.debugWarn(COMPONENT_NAME, "value must be a number");
        return;
      }
      wrapRef.value.scrollTop = value;
    };
    const setScrollLeft = (value) => {
      if (!types.isNumber(value)) {
        error.debugWarn(COMPONENT_NAME, "value must be a number");
        return;
      }
      wrapRef.value.scrollLeft = value;
    };
    const update = () => {
      var _a;
      (_a = barRef.value) == null ? void 0 : _a.update();
    };
    vue.watch(() => props.noresize, (noresize) => {
      if (noresize) {
        stopResizeObserver == null ? void 0 : stopResizeObserver();
        stopResizeListener == null ? void 0 : stopResizeListener();
      } else {
        ({ stop: stopResizeObserver } = core.useResizeObserver(resizeRef, update));
        stopResizeListener = core.useEventListener("resize", update);
      }
    }, { immediate: true });
    vue.watch(() => [props.maxHeight, props.height], () => {
      if (!props.native)
        vue.nextTick(() => {
          var _a;
          update();
          if (wrapRef.value) {
            (_a = barRef.value) == null ? void 0 : _a.handleScroll(wrapRef.value);
          }
        });
    });
    vue.provide(constants.scrollbarContextKey, vue.reactive({
      scrollbarElement: scrollbarRef,
      wrapElement: wrapRef
    }));
    vue.onActivated(() => {
      if (wrapRef.value) {
        wrapRef.value.scrollTop = wrapScrollTop;
        wrapRef.value.scrollLeft = wrapScrollLeft;
      }
    });
    vue.onMounted(() => {
      if (!props.native)
        vue.nextTick(() => {
          update();
        });
    });
    vue.onUpdated(() => update());
    expose({
      wrapRef,
      update,
      scrollTo,
      setScrollTop,
      setScrollLeft,
      handleScroll
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "scrollbarRef",
        ref: scrollbarRef,
        class: vue.normalizeClass(vue.unref(ns).b())
      }, [
        vue.createElementVNode("div", {
          ref_key: "wrapRef",
          ref: wrapRef,
          class: vue.normalizeClass(vue.unref(wrapKls)),
          style: vue.normalizeStyle(vue.unref(wrapStyle)),
          tabindex: _ctx.tabindex,
          onScroll: handleScroll
        }, [
          (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.tag), {
            id: _ctx.id,
            ref_key: "resizeRef",
            ref: resizeRef,
            class: vue.normalizeClass(vue.unref(resizeKls)),
            style: vue.normalizeStyle(_ctx.viewStyle),
            role: _ctx.role,
            "aria-label": _ctx.ariaLabel,
            "aria-orientation": _ctx.ariaOrientation
          }, {
            default: vue.withCtx(() => [
              vue.renderSlot(_ctx.$slots, "default")
            ]),
            _: 3
          }, 8, ["id", "class", "style", "role", "aria-label", "aria-orientation"]))
        ], 46, ["tabindex"]),
        !_ctx.native ? (vue.openBlock(), vue.createBlock(bar["default"], {
          key: 0,
          ref_key: "barRef",
          ref: barRef,
          always: _ctx.always,
          "min-size": _ctx.minSize
        }, null, 8, ["always", "min-size"])) : vue.createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var Scrollbar = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "scrollbar.vue"]]);

exports["default"] = Scrollbar;
//# sourceMappingURL=scrollbar2.js.map
