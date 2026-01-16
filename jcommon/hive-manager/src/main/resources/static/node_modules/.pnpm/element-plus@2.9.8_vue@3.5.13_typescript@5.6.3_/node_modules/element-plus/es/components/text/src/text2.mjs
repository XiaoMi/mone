import { defineComponent, ref, computed, useAttrs, onMounted, onUpdated, openBlock, createBlock, resolveDynamicComponent, normalizeClass, unref, normalizeStyle, withCtx, renderSlot } from 'vue';
import { textProps } from './text.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useFormSize } from '../../form/src/hooks/use-form-common-props.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isUndefined } from '../../../utils/types.mjs';

const __default__ = defineComponent({
  name: "ElText"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: textProps,
  setup(__props) {
    const props = __props;
    const textRef = ref();
    const textSize = useFormSize();
    const ns = useNamespace("text");
    const textKls = computed(() => [
      ns.b(),
      ns.m(props.type),
      ns.m(textSize.value),
      ns.is("truncated", props.truncated),
      ns.is("line-clamp", !isUndefined(props.lineClamp))
    ]);
    const inheritTitle = useAttrs().title;
    const bindTitle = () => {
      var _a, _b, _c, _d, _e;
      if (inheritTitle)
        return;
      let shouldAddTitle = false;
      const text = ((_a = textRef.value) == null ? void 0 : _a.textContent) || "";
      if (props.truncated) {
        const width = (_b = textRef.value) == null ? void 0 : _b.offsetWidth;
        const scrollWidth = (_c = textRef.value) == null ? void 0 : _c.scrollWidth;
        if (width && scrollWidth && scrollWidth > width) {
          shouldAddTitle = true;
        }
      } else if (!isUndefined(props.lineClamp)) {
        const height = (_d = textRef.value) == null ? void 0 : _d.offsetHeight;
        const scrollHeight = (_e = textRef.value) == null ? void 0 : _e.scrollHeight;
        if (height && scrollHeight && scrollHeight > height) {
          shouldAddTitle = true;
        }
      }
      if (shouldAddTitle) {
        textRef.value.setAttribute("title", text);
      } else {
        textRef.value.removeAttribute("title");
      }
    };
    onMounted(bindTitle);
    onUpdated(bindTitle);
    return (_ctx, _cache) => {
      return openBlock(), createBlock(resolveDynamicComponent(_ctx.tag), {
        ref_key: "textRef",
        ref: textRef,
        class: normalizeClass(unref(textKls)),
        style: normalizeStyle({ "-webkit-line-clamp": _ctx.lineClamp })
      }, {
        default: withCtx(() => [
          renderSlot(_ctx.$slots, "default")
        ]),
        _: 3
      }, 8, ["class", "style"]);
    };
  }
});
var Text = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "text.vue"]]);

export { Text as default };
//# sourceMappingURL=text2.mjs.map
