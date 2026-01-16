'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../icon/index.js');
var iconsVue = require('@element-plus/icons-vue');
var carousel = require('./carousel.js');
var useCarousel = require('./use-carousel.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-locale/index.js');

const COMPONENT_NAME = "ElCarousel";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: carousel.carouselProps,
  emits: carousel.carouselEmits,
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
    } = useCarousel.useCarousel(props, emit, COMPONENT_NAME);
    const ns = index.useNamespace("carousel");
    const { t } = index$1.useLocale();
    const carouselClasses = vue.computed(() => {
      const classes = [ns.b(), ns.m(props.direction)];
      if (vue.unref(isCardType)) {
        classes.push(ns.m("card"));
      }
      return classes;
    });
    const carouselContainer = vue.computed(() => {
      const classes = [ns.e("container")];
      if (props.motionBlur && vue.unref(isTransitioning) && items.value.length > 1) {
        classes.push(vue.unref(isVertical) ? `${ns.namespace.value}-transitioning-vertical` : `${ns.namespace.value}-transitioning`);
      }
      return classes;
    });
    const indicatorsClasses = vue.computed(() => {
      const classes = [ns.e("indicators"), ns.em("indicators", props.direction)];
      if (vue.unref(hasLabel)) {
        classes.push(ns.em("indicators", "labels"));
      }
      if (props.indicatorPosition === "outside") {
        classes.push(ns.em("indicators", "outside"));
      }
      if (vue.unref(isVertical)) {
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
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "root",
        ref: root,
        class: vue.normalizeClass(vue.unref(carouselClasses)),
        onMouseenter: vue.withModifiers(vue.unref(handleMouseEnter), ["stop"]),
        onMouseleave: vue.withModifiers(vue.unref(handleMouseLeave), ["stop"])
      }, [
        vue.unref(arrowDisplay) ? (vue.openBlock(), vue.createBlock(vue.Transition, {
          key: 0,
          name: "carousel-arrow-left",
          persisted: ""
        }, {
          default: vue.withCtx(() => [
            vue.withDirectives(vue.createElementVNode("button", {
              type: "button",
              class: vue.normalizeClass([vue.unref(ns).e("arrow"), vue.unref(ns).em("arrow", "left")]),
              "aria-label": vue.unref(t)("el.carousel.leftArrow"),
              onMouseenter: ($event) => vue.unref(handleButtonEnter)("left"),
              onMouseleave: vue.unref(handleButtonLeave),
              onClick: vue.withModifiers(($event) => vue.unref(throttledArrowClick)(vue.unref(activeIndex) - 1), ["stop"])
            }, [
              vue.createVNode(vue.unref(index$2.ElIcon), null, {
                default: vue.withCtx(() => [
                  vue.createVNode(vue.unref(iconsVue.ArrowLeft))
                ]),
                _: 1
              })
            ], 42, ["aria-label", "onMouseenter", "onMouseleave", "onClick"]), [
              [
                vue.vShow,
                (_ctx.arrow === "always" || vue.unref(hover)) && (props.loop || vue.unref(activeIndex) > 0)
              ]
            ])
          ]),
          _: 1
        })) : vue.createCommentVNode("v-if", true),
        vue.unref(arrowDisplay) ? (vue.openBlock(), vue.createBlock(vue.Transition, {
          key: 1,
          name: "carousel-arrow-right",
          persisted: ""
        }, {
          default: vue.withCtx(() => [
            vue.withDirectives(vue.createElementVNode("button", {
              type: "button",
              class: vue.normalizeClass([vue.unref(ns).e("arrow"), vue.unref(ns).em("arrow", "right")]),
              "aria-label": vue.unref(t)("el.carousel.rightArrow"),
              onMouseenter: ($event) => vue.unref(handleButtonEnter)("right"),
              onMouseleave: vue.unref(handleButtonLeave),
              onClick: vue.withModifiers(($event) => vue.unref(throttledArrowClick)(vue.unref(activeIndex) + 1), ["stop"])
            }, [
              vue.createVNode(vue.unref(index$2.ElIcon), null, {
                default: vue.withCtx(() => [
                  vue.createVNode(vue.unref(iconsVue.ArrowRight))
                ]),
                _: 1
              })
            ], 42, ["aria-label", "onMouseenter", "onMouseleave", "onClick"]), [
              [
                vue.vShow,
                (_ctx.arrow === "always" || vue.unref(hover)) && (props.loop || vue.unref(activeIndex) < vue.unref(items).length - 1)
              ]
            ])
          ]),
          _: 1
        })) : vue.createCommentVNode("v-if", true),
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(carouselContainer)),
          style: vue.normalizeStyle(vue.unref(containerStyle)),
          onTransitionend: vue.unref(handleTransitionEnd)
        }, [
          vue.createVNode(vue.unref(PlaceholderItem)),
          vue.renderSlot(_ctx.$slots, "default")
        ], 46, ["onTransitionend"]),
        _ctx.indicatorPosition !== "none" ? (vue.openBlock(), vue.createElementBlock("ul", {
          key: 2,
          class: vue.normalizeClass(vue.unref(indicatorsClasses))
        }, [
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(items), (item, index) => {
            return vue.withDirectives((vue.openBlock(), vue.createElementBlock("li", {
              key: index,
              class: vue.normalizeClass([
                vue.unref(ns).e("indicator"),
                vue.unref(ns).em("indicator", _ctx.direction),
                vue.unref(ns).is("active", index === vue.unref(activeIndex))
              ]),
              onMouseenter: ($event) => vue.unref(throttledIndicatorHover)(index),
              onClick: vue.withModifiers(($event) => vue.unref(handleIndicatorClick)(index), ["stop"])
            }, [
              vue.createElementVNode("button", {
                class: vue.normalizeClass(vue.unref(ns).e("button")),
                "aria-label": vue.unref(t)("el.carousel.indicator", { index: index + 1 })
              }, [
                vue.unref(hasLabel) ? (vue.openBlock(), vue.createElementBlock("span", { key: 0 }, vue.toDisplayString(item.props.label), 1)) : vue.createCommentVNode("v-if", true)
              ], 10, ["aria-label"])
            ], 42, ["onMouseenter", "onClick"])), [
              [vue.vShow, vue.unref(isTwoLengthShow)(index)]
            ]);
          }), 128))
        ], 2)) : vue.createCommentVNode("v-if", true),
        props.motionBlur ? (vue.openBlock(), vue.createElementBlock("svg", {
          key: 3,
          xmlns: "http://www.w3.org/2000/svg",
          version: "1.1",
          style: { "display": "none" }
        }, [
          vue.createElementVNode("defs", null, [
            vue.createElementVNode("filter", { id: "elCarouselHorizontal" }, [
              vue.createElementVNode("feGaussianBlur", {
                in: "SourceGraphic",
                stdDeviation: "12,0"
              })
            ]),
            vue.createElementVNode("filter", { id: "elCarouselVertical" }, [
              vue.createElementVNode("feGaussianBlur", {
                in: "SourceGraphic",
                stdDeviation: "0,10"
              })
            ])
          ])
        ])) : vue.createCommentVNode("v-if", true)
      ], 42, ["onMouseenter", "onMouseleave"]);
    };
  }
});
var Carousel = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "carousel.vue"]]);

exports["default"] = Carousel;
//# sourceMappingURL=carousel2.js.map
