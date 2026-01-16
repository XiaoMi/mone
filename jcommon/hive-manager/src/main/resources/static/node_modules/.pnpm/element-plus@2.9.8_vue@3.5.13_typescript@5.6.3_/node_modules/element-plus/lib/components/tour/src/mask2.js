'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var mask = require('./mask.js');
var helper = require('./helper.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-lockscreen/index.js');

const __default__ = vue.defineComponent({
  name: "ElTourMask",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: mask.maskProps,
  setup(__props) {
    const props = __props;
    const { ns } = vue.inject(helper.tourKey);
    const radius = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = props.pos) == null ? void 0 : _a.radius) != null ? _b : 2;
    });
    const roundInfo = vue.computed(() => {
      const v = radius.value;
      const baseInfo = `a${v},${v} 0 0 1`;
      return {
        topRight: `${baseInfo} ${v},${v}`,
        bottomRight: `${baseInfo} ${-v},${v}`,
        bottomLeft: `${baseInfo} ${-v},${-v}`,
        topLeft: `${baseInfo} ${v},${-v}`
      };
    });
    const path = vue.computed(() => {
      const width = window.innerWidth;
      const height = window.innerHeight;
      const info = roundInfo.value;
      const _path = `M${width},0 L0,0 L0,${height} L${width},${height} L${width},0 Z`;
      const _radius = radius.value;
      return props.pos ? `${_path} M${props.pos.left + _radius},${props.pos.top} h${props.pos.width - _radius * 2} ${info.topRight} v${props.pos.height - _radius * 2} ${info.bottomRight} h${-props.pos.width + _radius * 2} ${info.bottomLeft} v${-props.pos.height + _radius * 2} ${info.topLeft} z` : _path;
    });
    const pathStyle = vue.computed(() => {
      return {
        fill: props.fill,
        pointerEvents: "auto",
        cursor: "auto"
      };
    });
    index.useLockscreen(vue.toRef(props, "visible"), {
      ns
    });
    return (_ctx, _cache) => {
      return _ctx.visible ? (vue.openBlock(), vue.createElementBlock("div", vue.mergeProps({
        key: 0,
        class: vue.unref(ns).e("mask"),
        style: {
          position: "fixed",
          left: 0,
          right: 0,
          top: 0,
          bottom: 0,
          zIndex: _ctx.zIndex,
          pointerEvents: _ctx.pos && _ctx.targetAreaClickable ? "none" : "auto"
        }
      }, _ctx.$attrs), [
        (vue.openBlock(), vue.createElementBlock("svg", { style: {
          width: "100%",
          height: "100%"
        } }, [
          vue.createElementVNode("path", {
            class: vue.normalizeClass(vue.unref(ns).e("hollow")),
            style: vue.normalizeStyle(vue.unref(pathStyle)),
            d: vue.unref(path)
          }, null, 14, ["d"])
        ]))
      ], 16)) : vue.createCommentVNode("v-if", true);
    };
  }
});
var ElTourMask = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "mask.vue"]]);

exports["default"] = ElTourMask;
//# sourceMappingURL=mask2.js.map
