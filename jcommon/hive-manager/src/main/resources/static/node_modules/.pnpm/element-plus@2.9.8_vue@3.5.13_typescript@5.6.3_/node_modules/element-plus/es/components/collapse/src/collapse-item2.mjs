import { defineComponent, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, withKeys, withModifiers, renderSlot, createTextVNode, toDisplayString, createVNode, withCtx, createBlock, resolveDynamicComponent, withDirectives, vShow } from 'vue';
import { ElCollapseTransition } from '../../collapse-transition/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { collapseItemProps } from './collapse-item.mjs';
import { useCollapseItem, useCollapseItemDOM } from './use-collapse-item.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';

const __default__ = defineComponent({
  name: "ElCollapseItem"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: collapseItemProps,
  setup(__props, { expose }) {
    const props = __props;
    const {
      focusing,
      id,
      isActive,
      handleFocus,
      handleHeaderClick,
      handleEnterClick
    } = useCollapseItem(props);
    const {
      arrowKls,
      headKls,
      rootKls,
      itemWrapperKls,
      itemContentKls,
      scopedContentId,
      scopedHeadId
    } = useCollapseItemDOM(props, { focusing, isActive, id });
    expose({
      isActive
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        class: normalizeClass(unref(rootKls))
      }, [
        createElementVNode("button", {
          id: unref(scopedHeadId),
          class: normalizeClass(unref(headKls)),
          "aria-expanded": unref(isActive),
          "aria-controls": unref(scopedContentId),
          "aria-describedby": unref(scopedContentId),
          tabindex: _ctx.disabled ? -1 : 0,
          type: "button",
          onClick: unref(handleHeaderClick),
          onKeydown: withKeys(withModifiers(unref(handleEnterClick), ["stop", "prevent"]), ["space", "enter"]),
          onFocus: unref(handleFocus),
          onBlur: ($event) => focusing.value = false
        }, [
          renderSlot(_ctx.$slots, "title", {}, () => [
            createTextVNode(toDisplayString(_ctx.title), 1)
          ]),
          renderSlot(_ctx.$slots, "icon", { isActive: unref(isActive) }, () => [
            createVNode(unref(ElIcon), {
              class: normalizeClass(unref(arrowKls))
            }, {
              default: withCtx(() => [
                (openBlock(), createBlock(resolveDynamicComponent(_ctx.icon)))
              ]),
              _: 1
            }, 8, ["class"])
          ])
        ], 42, ["id", "aria-expanded", "aria-controls", "aria-describedby", "tabindex", "onClick", "onKeydown", "onFocus", "onBlur"]),
        createVNode(unref(ElCollapseTransition), null, {
          default: withCtx(() => [
            withDirectives(createElementVNode("div", {
              id: unref(scopedContentId),
              role: "region",
              class: normalizeClass(unref(itemWrapperKls)),
              "aria-hidden": !unref(isActive),
              "aria-labelledby": unref(scopedHeadId)
            }, [
              createElementVNode("div", {
                class: normalizeClass(unref(itemContentKls))
              }, [
                renderSlot(_ctx.$slots, "default")
              ], 2)
            ], 10, ["id", "aria-hidden", "aria-labelledby"]), [
              [vShow, unref(isActive)]
            ])
          ]),
          _: 3
        })
      ], 2);
    };
  }
});
var CollapseItem = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "collapse-item.vue"]]);

export { CollapseItem as default };
//# sourceMappingURL=collapse-item2.mjs.map
