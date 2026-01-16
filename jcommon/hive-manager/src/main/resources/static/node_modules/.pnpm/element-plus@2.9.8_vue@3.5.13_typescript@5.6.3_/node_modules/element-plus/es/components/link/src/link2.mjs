import { defineComponent, computed, openBlock, createElementBlock, normalizeClass, unref, createBlock, withCtx, resolveDynamicComponent, createCommentVNode, renderSlot } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { linkProps, linkEmits } from './link.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElLink"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: linkProps,
  emits: linkEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = useNamespace("link");
    const linkKls = computed(() => [
      ns.b(),
      ns.m(props.type),
      ns.is("disabled", props.disabled),
      ns.is("underline", props.underline && !props.disabled)
    ]);
    function handleClick(event) {
      if (!props.disabled)
        emit("click", event);
    }
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("a", {
        class: normalizeClass(unref(linkKls)),
        href: _ctx.disabled || !_ctx.href ? void 0 : _ctx.href,
        target: _ctx.disabled || !_ctx.href ? void 0 : _ctx.target,
        onClick: handleClick
      }, [
        _ctx.icon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
          default: withCtx(() => [
            (openBlock(), createBlock(resolveDynamicComponent(_ctx.icon)))
          ]),
          _: 1
        })) : createCommentVNode("v-if", true),
        _ctx.$slots.default ? (openBlock(), createElementBlock("span", {
          key: 1,
          class: normalizeClass(unref(ns).e("inner"))
        }, [
          renderSlot(_ctx.$slots, "default")
        ], 2)) : createCommentVNode("v-if", true),
        _ctx.$slots.icon ? renderSlot(_ctx.$slots, "icon", { key: 2 }) : createCommentVNode("v-if", true)
      ], 10, ["href", "target"]);
    };
  }
});
var Link = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "link.vue"]]);

export { Link as default };
//# sourceMappingURL=link2.mjs.map
