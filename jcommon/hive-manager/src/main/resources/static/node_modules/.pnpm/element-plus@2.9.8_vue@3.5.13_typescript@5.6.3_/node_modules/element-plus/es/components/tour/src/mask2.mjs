import { defineComponent, inject, computed, toRef, openBlock, createElementBlock, mergeProps, unref, createElementVNode, normalizeClass, normalizeStyle, createCommentVNode } from 'vue';
import { maskProps } from './mask.mjs';
import { tourKey } from './helper.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useLockscreen } from '../../../hooks/use-lockscreen/index.mjs';

const __default__ = defineComponent({
  name: "ElTourMask",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: maskProps,
  setup(__props) {
    const props = __props;
    const { ns } = inject(tourKey);
    const radius = computed(() => {
      var _a, _b;
      return (_b = (_a = props.pos) == null ? void 0 : _a.radius) != null ? _b : 2;
    });
    const roundInfo = computed(() => {
      const v = radius.value;
      const baseInfo = `a${v},${v} 0 0 1`;
      return {
        topRight: `${baseInfo} ${v},${v}`,
        bottomRight: `${baseInfo} ${-v},${v}`,
        bottomLeft: `${baseInfo} ${-v},${-v}`,
        topLeft: `${baseInfo} ${v},${-v}`
      };
    });
    const path = computed(() => {
      const width = window.innerWidth;
      const height = window.innerHeight;
      const info = roundInfo.value;
      const _path = `M${width},0 L0,0 L0,${height} L${width},${height} L${width},0 Z`;
      const _radius = radius.value;
      return props.pos ? `${_path} M${props.pos.left + _radius},${props.pos.top} h${props.pos.width - _radius * 2} ${info.topRight} v${props.pos.height - _radius * 2} ${info.bottomRight} h${-props.pos.width + _radius * 2} ${info.bottomLeft} v${-props.pos.height + _radius * 2} ${info.topLeft} z` : _path;
    });
    const pathStyle = computed(() => {
      return {
        fill: props.fill,
        pointerEvents: "auto",
        cursor: "auto"
      };
    });
    useLockscreen(toRef(props, "visible"), {
      ns
    });
    return (_ctx, _cache) => {
      return _ctx.visible ? (openBlock(), createElementBlock("div", mergeProps({
        key: 0,
        class: unref(ns).e("mask"),
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
        (openBlock(), createElementBlock("svg", { style: {
          width: "100%",
          height: "100%"
        } }, [
          createElementVNode("path", {
            class: normalizeClass(unref(ns).e("hollow")),
            style: normalizeStyle(unref(pathStyle)),
            d: unref(path)
          }, null, 14, ["d"])
        ]))
      ], 16)) : createCommentVNode("v-if", true);
    };
  }
});
var ElTourMask = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "mask.vue"]]);

export { ElTourMask as default };
//# sourceMappingURL=mask2.mjs.map
