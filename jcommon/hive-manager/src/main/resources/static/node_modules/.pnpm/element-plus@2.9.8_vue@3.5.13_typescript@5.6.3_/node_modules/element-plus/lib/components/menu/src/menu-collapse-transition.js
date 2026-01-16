'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var style = require('../../../utils/dom/style.js');

const __default__ = vue.defineComponent({
  name: "ElMenuCollapseTransition"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  setup(__props) {
    const ns = index.useNamespace("menu");
    const listeners = {
      onBeforeEnter: (el) => el.style.opacity = "0.2",
      onEnter(el, done) {
        style.addClass(el, `${ns.namespace.value}-opacity-transition`);
        el.style.opacity = "1";
        done();
      },
      onAfterEnter(el) {
        style.removeClass(el, `${ns.namespace.value}-opacity-transition`);
        el.style.opacity = "";
      },
      onBeforeLeave(el) {
        if (!el.dataset)
          el.dataset = {};
        if (style.hasClass(el, ns.m("collapse"))) {
          style.removeClass(el, ns.m("collapse"));
          el.dataset.oldOverflow = el.style.overflow;
          el.dataset.scrollWidth = el.clientWidth.toString();
          style.addClass(el, ns.m("collapse"));
        } else {
          style.addClass(el, ns.m("collapse"));
          el.dataset.oldOverflow = el.style.overflow;
          el.dataset.scrollWidth = el.clientWidth.toString();
          style.removeClass(el, ns.m("collapse"));
        }
        el.style.width = `${el.scrollWidth}px`;
        el.style.overflow = "hidden";
      },
      onLeave(el) {
        style.addClass(el, "horizontal-collapse-transition");
        el.style.width = `${el.dataset.scrollWidth}px`;
      }
    };
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.Transition, vue.mergeProps({ mode: "out-in" }, vue.unref(listeners)), {
        default: vue.withCtx(() => [
          vue.renderSlot(_ctx.$slots, "default")
        ]),
        _: 3
      }, 16);
    };
  }
});
var ElMenuCollapseTransition = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "menu-collapse-transition.vue"]]);

exports["default"] = ElMenuCollapseTransition;
//# sourceMappingURL=menu-collapse-transition.js.map
