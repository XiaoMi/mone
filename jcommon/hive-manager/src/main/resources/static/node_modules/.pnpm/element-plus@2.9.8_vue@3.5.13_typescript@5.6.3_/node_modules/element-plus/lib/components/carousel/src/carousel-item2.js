'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var carouselItem = require('./carousel-item.js');
var useCarouselItem = require('./use-carousel-item.js');
var constants = require('./constants.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: constants.CAROUSEL_ITEM_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: carouselItem.carouselItemProps,
  setup(__props) {
    const props = __props;
    const ns = index.useNamespace("carousel");
    const {
      carouselItemRef,
      active,
      animating,
      hover,
      inStage,
      isVertical,
      translate,
      isCardType,
      scale,
      ready,
      handleItemClick
    } = useCarouselItem.useCarouselItem(props);
    const itemKls = vue.computed(() => [
      ns.e("item"),
      ns.is("active", active.value),
      ns.is("in-stage", inStage.value),
      ns.is("hover", hover.value),
      ns.is("animating", animating.value),
      {
        [ns.em("item", "card")]: isCardType.value,
        [ns.em("item", "card-vertical")]: isCardType.value && isVertical.value
      }
    ]);
    const itemStyle = vue.computed(() => {
      const translateType = `translate${vue.unref(isVertical) ? "Y" : "X"}`;
      const _translate = `${translateType}(${vue.unref(translate)}px)`;
      const _scale = `scale(${vue.unref(scale)})`;
      const transform = [_translate, _scale].join(" ");
      return {
        transform
      };
    });
    return (_ctx, _cache) => {
      return vue.withDirectives((vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "carouselItemRef",
        ref: carouselItemRef,
        class: vue.normalizeClass(vue.unref(itemKls)),
        style: vue.normalizeStyle(vue.unref(itemStyle)),
        onClick: vue.unref(handleItemClick)
      }, [
        vue.unref(isCardType) ? vue.withDirectives((vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass(vue.unref(ns).e("mask"))
        }, null, 2)), [
          [vue.vShow, !vue.unref(active)]
        ]) : vue.createCommentVNode("v-if", true),
        vue.renderSlot(_ctx.$slots, "default")
      ], 14, ["onClick"])), [
        [vue.vShow, vue.unref(ready)]
      ]);
    };
  }
});
var CarouselItem = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "carousel-item.vue"]]);

exports["default"] = CarouselItem;
//# sourceMappingURL=carousel-item2.js.map
