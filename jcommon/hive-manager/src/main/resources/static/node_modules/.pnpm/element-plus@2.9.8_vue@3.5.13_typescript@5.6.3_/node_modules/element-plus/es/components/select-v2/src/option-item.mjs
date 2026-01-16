import { defineComponent, inject, openBlock, createElementBlock, normalizeStyle, normalizeClass, withModifiers, renderSlot, createElementVNode, toDisplayString } from 'vue';
import { useOption } from './useOption.mjs';
import { useProps } from './useProps.mjs';
import { OptionProps, optionEmits } from './defaults.mjs';
import { selectV2InjectionKey } from './token.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const _sfc_main = defineComponent({
  props: OptionProps,
  emits: optionEmits,
  setup(props, { emit }) {
    const select = inject(selectV2InjectionKey);
    const ns = useNamespace("select");
    const { hoverItem, selectOptionClick } = useOption(props, { emit });
    const { getLabel } = useProps(select.props);
    return {
      ns,
      hoverItem,
      selectOptionClick,
      getLabel
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return openBlock(), createElementBlock("li", {
    "aria-selected": _ctx.selected,
    style: normalizeStyle(_ctx.style),
    class: normalizeClass([
      _ctx.ns.be("dropdown", "item"),
      _ctx.ns.is("selected", _ctx.selected),
      _ctx.ns.is("disabled", _ctx.disabled),
      _ctx.ns.is("created", _ctx.created),
      _ctx.ns.is("hovering", _ctx.hovering)
    ]),
    onMousemove: _ctx.hoverItem,
    onClick: withModifiers(_ctx.selectOptionClick, ["stop"])
  }, [
    renderSlot(_ctx.$slots, "default", {
      item: _ctx.item,
      index: _ctx.index,
      disabled: _ctx.disabled
    }, () => [
      createElementVNode("span", null, toDisplayString(_ctx.getLabel(_ctx.item)), 1)
    ])
  ], 46, ["aria-selected", "onMousemove", "onClick"]);
}
var OptionItem = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "option-item.vue"]]);

export { OptionItem as default };
//# sourceMappingURL=option-item.mjs.map
