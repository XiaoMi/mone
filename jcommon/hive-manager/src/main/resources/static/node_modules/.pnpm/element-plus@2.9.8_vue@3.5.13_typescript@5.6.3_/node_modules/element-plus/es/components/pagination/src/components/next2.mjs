import { defineComponent, computed, openBlock, createElementBlock, unref, toDisplayString, createBlock, withCtx, resolveDynamicComponent } from 'vue';
import { ElIcon } from '../../../icon/index.mjs';
import { paginationNextProps } from './next.mjs';
import _export_sfc from '../../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../../hooks/use-locale/index.mjs';

const __default__ = defineComponent({
  name: "ElPaginationNext"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: paginationNextProps,
  emits: ["click"],
  setup(__props) {
    const props = __props;
    const { t } = useLocale();
    const internalDisabled = computed(() => props.disabled || props.currentPage === props.pageCount || props.pageCount === 0);
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("button", {
        type: "button",
        class: "btn-next",
        disabled: unref(internalDisabled),
        "aria-label": _ctx.nextText || unref(t)("el.pagination.next"),
        "aria-disabled": unref(internalDisabled),
        onClick: ($event) => _ctx.$emit("click", $event)
      }, [
        _ctx.nextText ? (openBlock(), createElementBlock("span", { key: 0 }, toDisplayString(_ctx.nextText), 1)) : (openBlock(), createBlock(unref(ElIcon), { key: 1 }, {
          default: withCtx(() => [
            (openBlock(), createBlock(resolveDynamicComponent(_ctx.nextIcon)))
          ]),
          _: 1
        }))
      ], 8, ["disabled", "aria-label", "aria-disabled", "onClick"]);
    };
  }
});
var Next = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "next.vue"]]);

export { Next as default };
//# sourceMappingURL=next2.mjs.map
