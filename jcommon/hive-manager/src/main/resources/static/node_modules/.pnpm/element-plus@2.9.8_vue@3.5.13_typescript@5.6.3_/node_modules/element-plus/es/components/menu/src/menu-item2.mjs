import { defineComponent, getCurrentInstance, inject, toRef, computed, reactive, onMounted, onBeforeUnmount, openBlock, createElementBlock, normalizeClass, unref, createBlock, withCtx, renderSlot, createElementVNode, Fragment } from 'vue';
import { ElTooltip } from '../../tooltip/index.mjs';
import useMenu from './use-menu.mjs';
import { menuItemProps, menuItemEmits } from './menu-item.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { throwError } from '../../../utils/error.mjs';

const COMPONENT_NAME = "ElMenuItem";
const __default__ = defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: menuItemProps,
  emits: menuItemEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const instance = getCurrentInstance();
    const rootMenu = inject("rootMenu");
    const nsMenu = useNamespace("menu");
    const nsMenuItem = useNamespace("menu-item");
    if (!rootMenu)
      throwError(COMPONENT_NAME, "can not inject root menu");
    const { parentMenu, indexPath } = useMenu(instance, toRef(props, "index"));
    const subMenu = inject(`subMenu:${parentMenu.value.uid}`);
    if (!subMenu)
      throwError(COMPONENT_NAME, "can not inject sub menu");
    const active = computed(() => props.index === rootMenu.activeIndex);
    const item = reactive({
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
    onMounted(() => {
      subMenu.addSubMenu(item);
      rootMenu.addMenuItem(item);
    });
    onBeforeUnmount(() => {
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
      return openBlock(), createElementBlock("li", {
        class: normalizeClass([
          unref(nsMenuItem).b(),
          unref(nsMenuItem).is("active", unref(active)),
          unref(nsMenuItem).is("disabled", _ctx.disabled)
        ]),
        role: "menuitem",
        tabindex: "-1",
        onClick: handleClick
      }, [
        unref(parentMenu).type.name === "ElMenu" && unref(rootMenu).props.collapse && _ctx.$slots.title ? (openBlock(), createBlock(unref(ElTooltip), {
          key: 0,
          effect: unref(rootMenu).props.popperEffect,
          placement: "right",
          "fallback-placements": ["left"],
          persistent: unref(rootMenu).props.persistent
        }, {
          content: withCtx(() => [
            renderSlot(_ctx.$slots, "title")
          ]),
          default: withCtx(() => [
            createElementVNode("div", {
              class: normalizeClass(unref(nsMenu).be("tooltip", "trigger"))
            }, [
              renderSlot(_ctx.$slots, "default")
            ], 2)
          ]),
          _: 3
        }, 8, ["effect", "persistent"])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [
          renderSlot(_ctx.$slots, "default"),
          renderSlot(_ctx.$slots, "title")
        ], 64))
      ], 2);
    };
  }
});
var MenuItem = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "menu-item.vue"]]);

export { MenuItem as default };
//# sourceMappingURL=menu-item2.mjs.map
