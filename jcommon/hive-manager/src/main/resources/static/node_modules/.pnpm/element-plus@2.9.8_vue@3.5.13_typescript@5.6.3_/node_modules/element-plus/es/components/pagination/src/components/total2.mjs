import { defineComponent, openBlock, createElementBlock, normalizeClass, unref, toDisplayString } from 'vue';
import { usePagination } from '../usePagination.mjs';
import { paginationTotalProps } from './total.mjs';
import _export_sfc from '../../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElPaginationTotal"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: paginationTotalProps,
  setup(__props) {
    const { t } = useLocale();
    const ns = useNamespace("pagination");
    const { disabled } = usePagination();
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("span", {
        class: normalizeClass(unref(ns).e("total")),
        disabled: unref(disabled)
      }, toDisplayString(unref(t)("el.pagination.total", {
        total: _ctx.total
      })), 11, ["disabled"]);
    };
  }
});
var Total = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "total.vue"]]);

export { Total as default };
//# sourceMappingURL=total2.mjs.map
