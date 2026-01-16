'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$1 = require('../../tooltip/index.js');
var useMenu = require('./use-menu.js');
var menuItem = require('./menu-item.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var error = require('../../../utils/error.js');

const COMPONENT_NAME = "ElMenuItem";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: menuItem.menuItemProps,
  emits: menuItem.menuItemEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const instance = vue.getCurrentInstance();
    const rootMenu = vue.inject("rootMenu");
    const nsMenu = index.useNamespace("menu");
    const nsMenuItem = index.useNamespace("menu-item");
    if (!rootMenu)
      error.throwError(COMPONENT_NAME, "can not inject root menu");
    const { parentMenu, indexPath } = useMenu["default"](instance, vue.toRef(props, "index"));
    const subMenu = vue.inject(`subMenu:${parentMenu.value.uid}`);
    if (!subMenu)
      error.throwError(COMPONENT_NAME, "can not inject sub menu");
    const active = vue.computed(() => props.index === rootMenu.activeIndex);
    const item = vue.reactive({
      index: props.index,
      indexPath,
      active
    });
    const handleClick = () => {
      if (!props.disabled) {
        rootMenu.handleMenuItemClick({
          index: props.index,
          indexPath: indexPath.value,
          route: props.route
        });
        emit("click", item);
      }
    };
    vue.onMounted(() => {
      subMenu.addSubMenu(item);
      rootMenu.addMenuItem(item);
    });
    vue.onBeforeUnmount(() => {
      subMenu.removeSubMenu(item);
      rootMenu.removeMenuItem(item);
    });
    expose({
      parentMenu,
      rootMenu,
      active,
      nsMenu,
      nsMenuItem,
      handleClick
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("li", {
        class: vue.normalizeClass([
          vue.unref(nsMenuItem).b(),
          vue.unref(nsMenuItem).is("active", vue.unref(active)),
          vue.unref(nsMenuItem).is("disabled", _ctx.disabled)
        ]),
        role: "menuitem",
        tabindex: "-1",
        onClick: handleClick
      }, [
        vue.unref(parentMenu).type.name === "ElMenu" && vue.unref(rootMenu).props.collapse && _ctx.$slots.title ? (vue.openBlock(), vue.createBlock(vue.unref(index$1.ElTooltip), {
          key: 0,
          effect: vue.unref(rootMenu).props.popperEffect,
          placement: "right",
          "fallback-placements": ["left"],
          persistent: vue.unref(rootMenu).props.persistent
        }, {
          content: vue.withCtx(() => [
            vue.renderSlot(_ctx.$slots, "title")
          ]),
          default: vue.withCtx(() => [
            vue.createElementVNode("div", {
              class: vue.normalizeClass(vue.unref(nsMenu).be("tooltip", "trigger"))
            }, [
              vue.renderSlot(_ctx.$slots, "default")
            ], 2)
          ]),
          _: 3
        }, 8, ["effect", "persistent"])) : (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 1 }, [
          vue.renderSlot(_ctx.$slots, "default"),
          vue.renderSlot(_ctx.$slots, "title")
        ], 64))
      ], 2);
    };
  }
});
var MenuItem = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "menu-item.vue"]]);

exports["default"] = MenuItem;
//# sourceMappingURL=menu-item2.js.map
