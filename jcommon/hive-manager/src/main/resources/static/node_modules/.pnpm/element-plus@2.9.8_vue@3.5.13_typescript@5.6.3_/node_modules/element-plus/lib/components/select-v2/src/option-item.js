'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var useOption = require('./useOption.js');
var useProps = require('./useProps.js');
var defaults = require('./defaults.js');
var token = require('./token.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const _sfc_main = vue.defineComponent({
  props: defaults.OptionProps,
  emits: defaults.optionEmits,
  setup(props, { emit }) {
    const select = vue.inject(token.selectV2InjectionKey);
    const ns = index.useNamespace("select");
    const { hoverItem, selectOptionClick } = useOption.useOption(props, { emit });
    const { getLabel } = useProps.useProps(select.props);
    return {
      ns,
      hoverItem,
      selectOptionClick,
      getLabel
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return vue.openBlock(), vue.createElementBlock("li", {
    "aria-selected": _ctx.selected,
    style: vue.normalizeStyle(_ctx.style),
    class: vue.normalizeClass([
      _ctx.ns.be("dropdown", "item"),
      _ctx.ns.is("selected", _ctx.selected),
      _ctx.ns.is("disabled", _ctx.disabled),
      _ctx.ns.is("created", _ctx.created),
      _ctx.ns.is("hovering", _ctx.hovering)
    ]),
    onMousemove: _ctx.hoverItem,
    onClick: vue.withModifiers(_ctx.selectOptionClick, ["stop"])
  }, [
    vue.renderSlot(_ctx.$slots, "default", {
      item: _ctx.item,
      index: _ctx.index,
      disabled: _ctx.disabled
    }, () => [
      vue.createElementVNode("span", null, vue.toDisplayString(_ctx.getLabel(_ctx.item)), 1)
    ])
  ], 46, ["aria-selected", "onMousemove", "onClick"]);
}
var OptionItem = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "option-item.vue"]]);

exports["default"] = OptionItem;
//# sourceMappingURL=option-item.js.map
