import { defineComponent, computed, unref, withDirectives, openBlock, createElementBlock, normalizeClass, normalizeStyle, vShow, createCommentVNode, renderSlot } from 'vue';
import { carouselItemProps } from './carousel-item.mjs';
import { useCarouselItem } from './use-carousel-item.mjs';
import { CAROUSEL_ITEM_NAME } from './constants.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: CAROUSEL_ITEM_NAME
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: carouselItemProps,
  setup(__props) {
    const props = __props;
    const ns = useNamespace("carousel");
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
    } = useCarouselItem(props);
    const itemKls = computed(() => [
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
    const itemStyle = computed(() => {
      const translateType = `translate${unref(isVertical) ? "Y" : "X"}`;
      const _translate = `${translateType}(${unref(translate)}px)`;
      const _scale = `scale(${unref(scale)})`;
      const transform = [_translate, _scale].join(" ");
      return {
        transform
      };
    });
    return (_ctx, _cache) => {
      return withDirectives((openBlock(), createElementBlock("div", {
        ref_key: "carouselItemRef",
        ref: carouselItemRef,
        class: normalizeClass(unref(itemKls)),
        style: normalizeStyle(unref(itemStyle)),
        onClick: unref(handleItemClick)
      }, [
        unref(isCardType) ? withDirectives((openBlock(), createElementBlock("div", {
          key: 0,
          class: normalizeClass(unref(ns).e("mask"))
        }, null, 2)), [
          [vShow, !unref(active)]
        ]) : createCommentVNode("v-if", true),
        renderSlot(_ctx.$slots, "default")
      ], 14, ["onClick"])), [
        [vShow, unref(ready)]
      ]);
    };
  }
});
var CarouselItem = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "carousel-item.vue"]]);

export { CarouselItem as default };
//# sourceMappingURL=carousel-item2.mjs.map
