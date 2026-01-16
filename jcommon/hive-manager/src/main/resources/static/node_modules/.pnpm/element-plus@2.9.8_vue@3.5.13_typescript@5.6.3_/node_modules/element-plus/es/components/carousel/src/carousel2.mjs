import { defineComponent, computed, unref, openBlock, createElementBlock, normalizeClass, withModifiers, createBlock, Transition, withCtx, withDirectives, createElementVNode, createVNode, vShow, createCommentVNode, normalizeStyle, renderSlot, Fragment, renderList, toDisplayString } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { carouselProps, carouselEmits } from './carousel.mjs';
import { useCarousel } from './use-carousel.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';

const COMPONENT_NAME = "ElCarousel";
const __default__ = defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: carouselProps,
  emits: carouselEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const {
      root,
      activeIndex,
      arrowDisplay,
      hasLabel,
      hover,
      isCardType,
      items,
      isVertical,
      containerStyle,
      handleButtonEnter,
      handleButtonLeave,
      isTransitioning,
      handleIndicatorClick,
      handleMouseEnter,
      handleMouseLeave,
      handleTransitionEnd,
      setActiveItem,
      prev,
      next,
      PlaceholderItem,
      isTwoLengthShow,
      throttledArrowClick,
      throttledIndicatorHover
    } = useCarousel(props, emit, COMPONENT_NAME);
    const ns = useNamespace("carousel");
    const { t } = useLocale();
    const carouselClasses = computed(() => {
      const classes = [ns.b(), ns.m(props.direction)];
      if (unref(isCardType)) {
        classes.push(ns.m("card"));
      }
      return classes;
    });
    const carouselContainer = computed(() => {
      const classes = [ns.e("container")];
      if (props.motionBlur && unref(isTransitioning) && items.value.length > 1) {
        classes.push(unref(isVertical) ? `${ns.namespace.value}-transitioning-vertical` : `${ns.namespace.value}-transitioning`);
      }
      return classes;
    });
    const indicatorsClasses = computed(() => {
      const classes = [ns.e("indicators"), ns.em("indicators", props.direction)];
      if (unref(hasLabel)) {
        classes.push(ns.em("indicators", "labels"));
      }
      if (props.indicatorPosition === "outside") {
        classes.push(ns.em("indicators", "outside"));
      }
      if (unref(isVertical)) {
        classes.push(ns.em("indicators", "right"));
      }
      return classes;
    });
    expose({
      activeIndex,
      setActiveItem,
      prev,
      next
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        ref_key: "root",
        ref: root,
        class: normalizeClass(unref(carouselClasses)),
        onMouseenter: withModifiers(unref(handleMouseEnter), ["stop"]),
        onMouseleave: withModifiers(unref(handleMouseLeave), ["stop"])
      }, [
        unref(arrowDisplay) ? (openBlock(), createBlock(Transition, {
          key: 0,
          name: "carousel-arrow-left",
          persisted: ""
        }, {
          default: withCtx(() => [
            withDirectives(createElementVNode("button", {
              type: "button",
              class: normalizeClass([unref(ns).e("arrow"), unref(ns).em("arrow", "left")]),
              "aria-label": unref(t)("el.carousel.leftArrow"),
              onMouseenter: ($event) => unref(handleButtonEnter)("left"),
              onMouseleave: unref(handleButtonLeave),
              onClick: withModifiers(($event) => unref(throttledArrowClick)(unref(activeIndex) - 1), ["stop"])
            }, [
              createVNode(unref(ElIcon), null, {
                default: withCtx(() => [
                  createVNode(unref(ArrowLeft))
                ]),
                _: 1
              })
            ], 42, ["aria-label", "onMouseenter", "onMouseleave", "onClick"]), [
              [
                vShow,
                (_ctx.arrow === "always" || unref(hover)) && (props.loop || unref(activeIndex) > 0)
              ]
            ])
          ]),
          _: 1
        })) : createCommentVNode("v-if", true),
        unref(arrowDisplay) ? (openBlock(), createBlock(Transition, {
          key: 1,
          name: "carousel-arrow-right",
          persisted: ""
        }, {
          default: withCtx(() => [
            withDirectives(createElementVNode("button", {
              type: "button",
              class: normalizeClass([unref(ns).e("arrow"), unref(ns).em("arrow", "right")]),
              "aria-label": unref(t)("el.carousel.rightArrow"),
              onMouseenter: ($event) => unref(handleButtonEnter)("right"),
              onMouseleave: unref(handleButtonLeave),
              onClick: withModifiers(($event) => unref(throttledArrowClick)(unref(activeIndex) + 1), ["stop"])
            }, [
              createVNode(unref(ElIcon), null, {
                default: withCtx(() => [
                  createVNode(unref(ArrowRight))
                ]),
                _: 1
              })
            ], 42, ["aria-label", "onMouseenter", "onMouseleave", "onClick"]), [
              [
                vShow,
                (_ctx.arrow === "always" || unref(hover)) && (props.loop || unref(activeIndex) < unref(items).length - 1)
              ]
            ])
          ]),
          _: 1
        })) : createCommentVNode("v-if", true),
        createElementVNode("div", {
          class: normalizeClass(unref(carouselContainer)),
          style: normalizeStyle(unref(containerStyle)),
          onTransitionend: unref(handleTransitionEnd)
        }, [
          createVNode(unref(PlaceholderItem)),
          renderSlot(_ctx.$slots, "default")
        ], 46, ["onTransitionend"]),
        _ctx.indicatorPosition !== "none" ? (openBlock(), createElementBlock("ul", {
          key: 2,
          class: normalizeClass(unref(indicatorsClasses))
        }, [
          (openBlock(true), createElementBlock(Fragment, null, renderList(unref(items), (item, index) => {
            return withDirectives((openBlock(), createElementBlock("li", {
              key: index,
              class: normalizeClass([
                unref(ns).e("indicator"),
                unref(ns).em("indicator", _ctx.direction),
                unref(ns).is("active", index === unref(activeIndex))
              ]),
              onMouseenter: ($event) => unref(throttledIndicatorHover)(index),
              onClick: withModifiers(($event) => unref(handleIndicatorClick)(index), ["stop"])
            }, [
              createElementVNode("button", {
                class: normalizeClass(unref(ns).e("button")),
                "aria-label": unref(t)("el.carousel.indicator", { index: index + 1 })
              }, [
                unref(hasLabel) ? (openBlock(), createElementBlock("span", { key: 0 }, toDisplayString(item.props.label), 1)) : createCommentVNode("v-if", true)
              ], 10, ["aria-label"])
            ], 42, ["onMouseenter", "onClick"])), [
              [vShow, unref(isTwoLengthShow)(index)]
            ]);
          }), 128))
        ], 2)) : createCommentVNode("v-if", true),
        props.motionBlur ? (openBlock(), createElementBlock("svg", {
          key: 3,
          xmlns: "http://www.w3.org/2000/svg",
          version: "1.1",
          style: { "display": "none" }
        }, [
          createElementVNode("defs", null, [
            createElementVNode("filter", { id: "elCarouselHorizontal" }, [
              createElementVNode("feGaussianBlur", {
                in: "SourceGraphic",
                stdDeviation: "12,0"
              })
            ]),
            createElementVNode("filter", { id: "elCarouselVertical" }, [
              createElementVNode("feGaussianBlur", {
                in: "SourceGraphic",
                stdDeviation: "0,10"
              })
            ])
          ])
        ])) : createCommentVNode("v-if", true)
      ], 42, ["onMouseenter", "onMouseleave"]);
    };
  }
});
var Carousel = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "carousel.vue"]]);

export { Carousel as default };
//# sourceMappingURL=carousel2.mjs.map
