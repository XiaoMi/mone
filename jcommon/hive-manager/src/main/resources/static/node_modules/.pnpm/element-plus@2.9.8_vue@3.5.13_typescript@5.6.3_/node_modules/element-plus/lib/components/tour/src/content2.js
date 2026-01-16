'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var focusTrap = require('../../focus-trap/src/focus-trap.js');
var content = require('./content.js');
var helper = require('./helper.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');

const __default__ = vue.defineComponent({
  name: "ElTourContent"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: content.tourContentProps,
  emits: content.tourContentEmits,
  setup(__props, { emit }) {
    const props = __props;
    const placement = vue.ref(props.placement);
    const strategy = vue.ref(props.strategy);
    const contentRef = vue.ref(null);
    const arrowRef = vue.ref(null);
    vue.watch(() => props.placement, () => {
      placement.value = props.placement;
    });
    const { contentStyle, arrowStyle } = helper.useFloating(vue.toRef(props, "reference"), contentRef, arrowRef, placement, strategy, vue.toRef(props, "offset"), vue.toRef(props, "zIndex"), vue.toRef(props, "showArrow"));
    const side = vue.computed(() => {
      return placement.value.split("-")[0];
    });
    const { ns } = vue.inject(helper.tourKey);
    const onCloseRequested = () => {
      emit("close");
    };
    const onFocusoutPrevented = (event) => {
      if (event.detail.focusReason === "pointer") {
        event.preventDefault();
      }
    };
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "contentRef",
        ref: contentRef,
        style: vue.normalizeStyle(vue.unref(contentStyle)),
        class: vue.normalizeClass(vue.unref(ns).e("content")),
        "data-side": vue.unref(side),
        tabindex: "-1"
      }, [
        vue.createVNode(vue.unref(focusTrap["default"]), {
          loop: "",
          trapped: "",
          "focus-start-el": "container",
          "focus-trap-el": contentRef.value || void 0,
          onReleaseRequested: onCloseRequested,
          onFocusoutPrevented
        }, {
          default: vue.withCtx(() => [
            vue.renderSlot(_ctx.$slots, "default")
          ]),
          _: 3
        }, 8, ["focus-trap-el"]),
        _ctx.showArrow ? (vue.openBlock(), vue.createElementBlock("span", {
          key: 0,
          ref_key: "arrowRef",
          ref: arrowRef,
          style: vue.normalizeStyle(vue.unref(arrowStyle)),
          class: vue.normalizeClass(vue.unref(ns).e("arrow"))
        }, null, 6)) : vue.createCommentVNode("v-if", true)
      ], 14, ["data-side"]);
    };
  }
});
var ElTourContent = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "content.vue"]]);

exports["default"] = ElTourContent;
//# sourceMappingURL=content2.js.map
