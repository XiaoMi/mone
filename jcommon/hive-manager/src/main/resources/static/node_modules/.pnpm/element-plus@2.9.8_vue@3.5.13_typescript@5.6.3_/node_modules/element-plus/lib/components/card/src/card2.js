'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var card = require('./card.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElCard"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: card.cardProps,
  setup(__props) {
    const ns = index.useNamespace("card");
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass([vue.unref(ns).b(), vue.unref(ns).is(`${_ctx.shadow}-shadow`)])
      }, [
        _ctx.$slots.header || _ctx.header ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass([vue.unref(ns).e("header"), _ctx.headerClass])
        }, [
          vue.renderSlot(_ctx.$slots, "header", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.header), 1)
          ])
        ], 2)) : vue.createCommentVNode("v-if", true),
        vue.createElementVNode("div", {
          class: vue.normalizeClass([vue.unref(ns).e("body"), _ctx.bodyClass]),
          style: vue.normalizeStyle(_ctx.bodyStyle)
        }, [
          vue.renderSlot(_ctx.$slots, "default")
        ], 6),
        _ctx.$slots.footer || _ctx.footer ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 1,
          class: vue.normalizeClass([vue.unref(ns).e("footer"), _ctx.footerClass])
        }, [
          vue.renderSlot(_ctx.$slots, "footer", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.footer), 1)
          ])
        ], 2)) : vue.createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var Card = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "card.vue"]]);

exports["default"] = Card;
//# sourceMappingURL=card2.js.map
