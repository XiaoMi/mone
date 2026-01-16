'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$1 = require('../../collapse-transition/index.js');
var index = require('../../icon/index.js');
var collapseItem = require('./collapse-item.js');
var useCollapseItem = require('./use-collapse-item.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');

const __default__ = vue.defineComponent({
  name: "ElCollapseItem"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: collapseItem.collapseItemProps,
  setup(__props, { expose }) {
    const props = __props;
    const {
      focusing,
      id,
      isActive,
      handleFocus,
      handleHeaderClick,
      handleEnterClick
    } = useCollapseItem.useCollapseItem(props);
    const {
      arrowKls,
      headKls,
      rootKls,
      itemWrapperKls,
      itemContentKls,
      scopedContentId,
      scopedHeadId
    } = useCollapseItem.useCollapseItemDOM(props, { focusing, isActive, id });
    expose({
      isActive
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(rootKls))
      }, [
        vue.createElementVNode("button", {
          id: vue.unref(scopedHeadId),
          class: vue.normalizeClass(vue.unref(headKls)),
          "aria-expanded": vue.unref(isActive),
          "aria-controls": vue.unref(scopedContentId),
          "aria-describedby": vue.unref(scopedContentId),
          tabindex: _ctx.disabled ? -1 : 0,
          type: "button",
          onClick: vue.unref(handleHeaderClick),
          onKeydown: vue.withKeys(vue.withModifiers(vue.unref(handleEnterClick), ["stop", "prevent"]), ["space", "enter"]),
          onFocus: vue.unref(handleFocus),
          onBlur: ($event) => focusing.value = false
        }, [
          vue.renderSlot(_ctx.$slots, "title", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.title), 1)
          ]),
          vue.renderSlot(_ctx.$slots, "icon", { isActive: vue.unref(isActive) }, () => [
            vue.createVNode(vue.unref(index.ElIcon), {
              class: vue.normalizeClass(vue.unref(arrowKls))
            }, {
              default: vue.withCtx(() => [
                (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.icon)))
              ]),
              _: 1
            }, 8, ["class"])
          ])
        ], 42, ["id", "aria-expanded", "aria-controls", "aria-describedby", "tabindex", "onClick", "onKeydown", "onFocus", "onBlur"]),
        vue.createVNode(vue.unref(index$1.ElCollapseTransition), null, {
          default: vue.withCtx(() => [
            vue.withDirectives(vue.createElementVNode("div", {
              id: vue.unref(scopedContentId),
              role: "region",
              class: vue.normalizeClass(vue.unref(itemWrapperKls)),
              "aria-hidden": !vue.unref(isActive),
              "aria-labelledby": vue.unref(scopedHeadId)
            }, [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(vue.unref(itemContentKls))
              }, [
                vue.renderSlot(_ctx.$slots, "default")
              ], 2)
            ], 10, ["id", "aria-hidden", "aria-labelledby"]), [
              [vue.vShow, vue.unref(isActive)]
            ])
          ]),
          _: 3
        })
      ], 2);
    };
  }
});
var CollapseItem = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "collapse-item.vue"]]);

exports["default"] = CollapseItem;
//# sourceMappingURL=collapse-item2.js.map
