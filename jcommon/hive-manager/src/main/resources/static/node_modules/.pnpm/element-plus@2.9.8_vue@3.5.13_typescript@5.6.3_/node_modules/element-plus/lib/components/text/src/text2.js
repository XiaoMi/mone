'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var text = require('./text.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var index = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');

const __default__ = vue.defineComponent({
  name: "ElText"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: text.textProps,
  setup(__props) {
    const props = __props;
    const textRef = vue.ref();
    const textSize = useFormCommonProps.useFormSize();
    const ns = index.useNamespace("text");
    const textKls = vue.computed(() => [
      ns.b(),
      ns.m(props.type),
      ns.m(textSize.value),
      ns.is("truncated", props.truncated),
      ns.is("line-clamp", !types.isUndefined(props.lineClamp))
    ]);
    const inheritTitle = vue.useAttrs().title;
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
      } else if (!types.isUndefined(props.lineClamp)) {
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
    vue.onMounted(bindTitle);
    vue.onUpdated(bindTitle);
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.tag), {
        ref_key: "textRef",
        ref: textRef,
        class: vue.normalizeClass(vue.unref(textKls)),
        style: vue.normalizeStyle({ "-webkit-line-clamp": _ctx.lineClamp })
      }, {
        default: vue.withCtx(() => [
          vue.renderSlot(_ctx.$slots, "default")
        ]),
        _: 3
      }, 8, ["class", "style"]);
    };
  }
});
var Text = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "text.vue"]]);

exports["default"] = Text;
//# sourceMappingURL=text2.js.map
