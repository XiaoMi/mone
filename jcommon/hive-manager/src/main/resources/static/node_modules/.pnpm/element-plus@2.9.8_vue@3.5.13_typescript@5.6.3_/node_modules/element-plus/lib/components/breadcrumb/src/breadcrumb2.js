'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('./constants.js');
var breadcrumb = require('./breadcrumb.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-locale/index.js');
var index$1 = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElBreadcrumb"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: breadcrumb.breadcrumbProps,
  setup(__props) {
    const props = __props;
    const { t } = index.useLocale();
    const ns = index$1.useNamespace("breadcrumb");
    const breadcrumb = vue.ref();
    vue.provide(constants.breadcrumbKey, props);
    vue.onMounted(() => {
      const items = breadcrumb.value.querySelectorAll(`.${ns.e("item")}`);
      if (items.length) {
        items[items.length - 1].setAttribute("aria-current", "page");
      }
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "breadcrumb",
        ref: breadcrumb,
        class: vue.normalizeClass(vue.unref(ns).b()),
        "aria-label": vue.unref(t)("el.breadcrumb.label"),
        role: "navigation"
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 10, ["aria-label"]);
    };
  }
});
var Breadcrumb = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "breadcrumb.vue"]]);

exports["default"] = Breadcrumb;
//# sourceMappingURL=breadcrumb2.js.map
