import { defineComponent, computed, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, normalizeStyle, renderSlot, toDisplayString, createCommentVNode, createBlock, withCtx, resolveDynamicComponent } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { WarningFilled, CircleCheck, CircleClose, Check, Close } from '@element-plus/icons-vue';
import { progressProps } from './progress.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isFunction, isString } from '@vue/shared';

const __default__ = defineComponent({
  name: "ElProgress"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: progressProps,
  setup(__props) {
    const props = __props;
    const STATUS_COLOR_MAP = {
      success: "#13ce66",
      exception: "#ff4949",
      warning: "#e6a23c",
      default: "#20a0ff"
    };
    const ns = useNamespace("progress");
    const barStyle = computed(() => {
      const barStyle2 = {
        width: `${props.percentage}%`,
        animationDuration: `${props.duration}s`
      };
      const color = getCurrentColor(props.percentage);
      if (color.includes("gradient")) {
        barStyle2.background = color;
      } else {
        barStyle2.backgroundColor = color;
      }
      return barStyle2;
    });
    const relativeStrokeWidth = computed(() => (props.strokeWidth / props.width * 100).toFixed(1));
    const radius = computed(() => {
      if (["circle", "dashboard"].includes(props.type)) {
        return Number.parseInt(`${50 - Number.parseFloat(relativeStrokeWidth.value) / 2}`, 10);
      }
      return 0;
    });
    const trackPath = computed(() => {
      const r = radius.value;
      const isDashboard = props.type === "dashboard";
      return `
          M 50 50
          m 0 ${isDashboard ? "" : "-"}${r}
          a ${r} ${r} 0 1 1 0 ${isDashboard ? "-" : ""}${r * 2}
          a ${r} ${r} 0 1 1 0 ${isDashboard ? "" : "-"}${r * 2}
          `;
    });
    const perimeter = computed(() => 2 * Math.PI * radius.value);
    const rate = computed(() => props.type === "dashboard" ? 0.75 : 1);
    const strokeDashoffset = computed(() => {
      const offset = -1 * perimeter.value * (1 - rate.value) / 2;
      return `${offset}px`;
    });
    const trailPathStyle = computed(() => ({
      strokeDasharray: `${perimeter.value * rate.value}px, ${perimeter.value}px`,
      strokeDashoffset: strokeDashoffset.value
    }));
    const circlePathStyle = computed(() => ({
      strokeDasharray: `${perimeter.value * rate.value * (props.percentage / 100)}px, ${perimeter.value}px`,
      strokeDashoffset: strokeDashoffset.value,
      transition: "stroke-dasharray 0.6s ease 0s, stroke 0.6s ease, opacity ease 0.6s"
    }));
    const stroke = computed(() => {
      let ret;
      if (props.color) {
        ret = getCurrentColor(props.percentage);
      } else {
        ret = STATUS_COLOR_MAP[props.status] || STATUS_COLOR_MAP.default;
      }
      return ret;
    });
    const statusIcon = computed(() => {
      if (props.status === "warning") {
        return WarningFilled;
      }
      if (props.type === "line") {
        return props.status === "success" ? CircleCheck : CircleClose;
      } else {
        return props.status === "success" ? Check : Close;
      }
    });
    const progressTextSize = computed(() => {
      return props.type === "line" ? 12 + props.strokeWidth * 0.4 : props.width * 0.111111 + 2;
    });
    const content = computed(() => props.format(props.percentage));
    function getColors(color) {
      const span = 100 / color.length;
      const seriesColors = color.map((seriesColor, index) => {
        if (isString(seriesColor)) {
          return {
            color: seriesColor,
            percentage: (index + 1) * span
          };
        }
        return seriesColor;
      });
      return seriesColors.sort((a, b) => a.percentage - b.percentage);
    }
    const getCurrentColor = (percentage) => {
      var _a;
      const { color } = props;
      if (isFunction(color)) {
        return color(percentage);
      } else if (isString(color)) {
        return color;
      } else {
        const colors = getColors(color);
        for (const color2 of colors) {
          if (color2.percentage > percentage)
            return color2.color;
        }
        return (_a = colors[colors.length - 1]) == null ? void 0 : _a.color;
      }
    };
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        class: normalizeClass([
          unref(ns).b(),
          unref(ns).m(_ctx.type),
          unref(ns).is(_ctx.status),
          {
            [unref(ns).m("without-text")]: !_ctx.showText,
            [unref(ns).m("text-inside")]: _ctx.textInside
          }
        ]),
        role: "progressbar",
        "aria-valuenow": _ctx.percentage,
        "aria-valuemin": "0",
        "aria-valuemax": "100"
      }, [
        _ctx.type === "line" ? (openBlock(), createElementBlock("div", {
          key: 0,
          class: normalizeClass(unref(ns).b("bar"))
        }, [
          createElementVNode("div", {
            class: normalizeClass(unref(ns).be("bar", "outer")),
            style: normalizeStyle({ height: `${_ctx.strokeWidth}px` })
          }, [
            createElementVNode("div", {
              class: normalizeClass([
                unref(ns).be("bar", "inner"),
                { [unref(ns).bem("bar", "inner", "indeterminate")]: _ctx.indeterminate },
                { [unref(ns).bem("bar", "inner", "striped")]: _ctx.striped },
                { [unref(ns).bem("bar", "inner", "striped-flow")]: _ctx.stripedFlow }
              ]),
              style: normalizeStyle(unref(barStyle))
            }, [
              (_ctx.showText || _ctx.$slots.default) && _ctx.textInside ? (openBlock(), createElementBlock("div", {
                key: 0,
                class: normalizeClass(unref(ns).be("bar", "innerText"))
              }, [
                renderSlot(_ctx.$slots, "default", { percentage: _ctx.percentage }, () => [
                  createElementVNode("span", null, toDisplayString(unref(content)), 1)
                ])
              ], 2)) : createCommentVNode("v-if", true)
            ], 6)
          ], 6)
        ], 2)) : (openBlock(), createElementBlock("div", {
          key: 1,
          class: normalizeClass(unref(ns).b("circle")),
          style: normalizeStyle({ height: `${_ctx.width}px`, width: `${_ctx.width}px` })
        }, [
          (openBlock(), createElementBlock("svg", { viewBox: "0 0 100 100" }, [
            createElementVNode("path", {
              class: normalizeClass(unref(ns).be("circle", "track")),
              d: unref(trackPath),
              stroke: `var(${unref(ns).cssVarName("fill-color-light")}, #e5e9f2)`,
              "stroke-linecap": _ctx.strokeLinecap,
              "stroke-width": unref(relativeStrokeWidth),
              fill: "none",
              style: normalizeStyle(unref(trailPathStyle))
            }, null, 14, ["d", "stroke", "stroke-linecap", "stroke-width"]),
            createElementVNode("path", {
              class: normalizeClass(unref(ns).be("circle", "path")),
              d: unref(trackPath),
              stroke: unref(stroke),
              fill: "none",
              opacity: _ctx.percentage ? 1 : 0,
              "stroke-linecap": _ctx.strokeLinecap,
              "stroke-width": unref(relativeStrokeWidth),
              style: normalizeStyle(unref(circlePathStyle))
            }, null, 14, ["d", "stroke", "opacity", "stroke-linecap", "stroke-width"])
          ]))
        ], 6)),
        (_ctx.showText || _ctx.$slots.default) && !_ctx.textInside ? (openBlock(), createElementBlock("div", {
          key: 2,
          class: normalizeClass(unref(ns).e("text")),
          style: normalizeStyle({ fontSize: `${unref(progressTextSize)}px` })
        }, [
          renderSlot(_ctx.$slots, "default", { percentage: _ctx.percentage }, () => [
            !_ctx.status ? (openBlock(), createElementBlock("span", { key: 0 }, toDisplayString(unref(content)), 1)) : (openBlock(), createBlock(unref(ElIcon), { key: 1 }, {
              default: withCtx(() => [
                (openBlock(), createBlock(resolveDynamicComponent(unref(statusIcon))))
              ]),
              _: 1
            }))
          ])
        ], 6)) : createCommentVNode("v-if", true)
      ], 10, ["aria-valuenow"]);
    };
  }
});
var Progress = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "progress.vue"]]);

export { Progress as default };
//# sourceMappingURL=progress2.mjs.map
