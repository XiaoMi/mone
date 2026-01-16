'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var useOption = require('./useOption.js');
var option = require('./option.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-id/index.js');

const _sfc_main = vue.defineComponent({
  name: option.COMPONENT_NAME,
  componentName: option.COMPONENT_NAME,
  props: option.optionProps,
  setup(props) {
    const ns = index.useNamespace("select");
    const id = index$1.useId();
    const containerKls = vue.computed(() => [
      ns.be("dropdown", "item"),
      ns.is("disabled", vue.unref(isDisabled)),
      ns.is("selected", vue.unref(itemSelected)),
      ns.is("hovering", vue.unref(hover))
    ]);
    const states = vue.reactive({
      index: -1,
      groupDisabled: false,
      visible: true,
      hover: false
    });
    const {
      currentLabel,
      itemSelected,
      isDisabled,
      select,
      hoverItem,
      updateOption
    } = useOption.useOption(props, states);
    const { visible, hover } = vue.toRefs(states);
    const vm = vue.getCurrentInstance().proxy;
    select.onOptionCreate(vm);
    vue.onBeforeUnmount(() => {
      const key = vm.value;
      const { selected: selectedOptions } = select.states;
      const doesSelected = selectedOptions.some((item) => {
        return item.value === vm.value;
      });
      vue.nextTick(() => {
        if (select.states.cachedOptions.get(key) === vm && !doesSelected) {
          select.states.cachedOptions.delete(key);
        }
      });
      select.onOptionDestroy(key, vm);
    });
    function selectOptionClick() {
      if (!isDisabled.value) {
        select.handleOptionSelect(vm);
      }
    }
    return {
      ns,
      id,
      containerKls,
      currentLabel,
      itemSelected,
      isDisabled,
      select,
      visible,
      hover,
      states,
      hoverItem,
      updateOption,
      selectOptionClick
    };
  }
});
function _sfc_render(_ctx, _cache) {
  return vue.withDirectives((vue.openBlock(), vue.createElementBlock("li", {
    id: _ctx.id,
    class: vue.normalizeClass(_ctx.containerKls),
    role: "option",
    "aria-disabled": _ctx.isDisabled || void 0,
    "aria-selected": _ctx.itemSelected,
    onMousemove: _ctx.hoverItem,
    onClick: vue.withModifiers(_ctx.selectOptionClick, ["stop"])
  }, [
    vue.renderSlot(_ctx.$slots, "default", {}, () => [
      vue.createElementVNode("span", null, vue.toDisplayString(_ctx.currentLabel), 1)
    ])
  ], 42, ["id", "aria-disabled", "aria-selected", "onMousemove", "onClick"])), [
    [vue.vShow, _ctx.visible]
  ]);
}
var Option = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "option.vue"]]);

exports["default"] = Option;
//# sourceMappingURL=option2.js.map
