'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var badge = require('./badge.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');
var style = require('../../../utils/dom/style.js');

const __default__ = vue.defineComponent({
  name: "ElBadge"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: badge.badgeProps,
  setup(__props, { expose }) {
    const props = __props;
    const ns = index.useNamespace("badge");
    const content = vue.computed(() => {
      if (props.isDot)
        return "";
      if (types.isNumber(props.value) && types.isNumber(props.max)) {
        return props.max < props.value ? `${props.max}+` : `${props.value}`;
      }
      return `${props.value}`;
    });
    const style$1 = vue.computed(() => {
      var _a, _b, _c, _d, _e;
      return [
        {
          backgroundColor: props.color,
          marginRight: style.addUnit(-((_b = (_a = props.offset) == null ? void 0 : _a[0]) != null ? _b : 0)),
          marginTop: style.addUnit((_d = (_c = props.offset) == null ? void 0 : _c[1]) != null ? _d : 0)
        },
        (_e = props.badgeStyle) != null ? _e : {}
      ];
    });
    expose({
      content
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(ns).b())
      }, [
        vue.renderSlot(_ctx.$slots, "default"),
        vue.createVNode(vue.Transition, {
          name: `${vue.unref(ns).namespace.value}-zoom-in-center`,
          persisted: ""
        }, {
          default: vue.withCtx(() => [
            vue.withDirectives(vue.createElementVNode("sup", {
              class: vue.normalizeClass([
                vue.unref(ns).e("content"),
                vue.unref(ns).em("content", _ctx.type),
                vue.unref(ns).is("fixed", !!_ctx.$slots.default),
                vue.unref(ns).is("dot", _ctx.isDot),
                vue.unref(ns).is("hide-zero", !_ctx.showZero && props.value === 0),
                _ctx.badgeClass
              ]),
              style: vue.normalizeStyle(vue.unref(style$1))
            }, [
              vue.renderSlot(_ctx.$slots, "content", { value: vue.unref(content) }, () => [
                vue.createTextVNode(vue.toDisplayString(vue.unref(content)), 1)
              ])
            ], 6), [
              [vue.vShow, !_ctx.hidden && (vue.unref(content) || _ctx.isDot || _ctx.$slots.content)]
            ])
          ]),
          _: 3
        }, 8, ["name"])
      ], 2);
    };
  }
});
var Badge = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "badge.vue"]]);

exports["default"] = Badge;
//# sourceMappingURL=badge2.js.map
