import { defineComponent, markRaw, ref, effectScope, shallowRef, computed, watch, nextTick, onMounted, openBlock, createBlock, unref, withCtx, createVNode, Transition, createElementVNode, normalizeClass, normalizeStyle, withModifiers, createCommentVNode, createElementBlock, Fragment, renderSlot, createTextVNode, toDisplayString, resolveDynamicComponent, renderList, withDirectives, vShow } from 'vue';
import { useEventListener } from '@vueuse/core';
import { throttle } from 'lodash-unified';
import ElFocusTrap from '../../focus-trap/src/focus-trap.mjs';
import { ElTeleport } from '../../teleport/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { FullScreen, ScaleToOriginal, Close, ArrowLeft, ArrowRight, ZoomOut, ZoomIn, RefreshLeft, RefreshRight } from '@element-plus/icons-vue';
import { imageViewerProps, imageViewerEmits } from './image-viewer.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useZIndex } from '../../../hooks/use-z-index/index.mjs';
import { EVENT_CODE } from '../../../constants/aria.mjs';
import { keysOf } from '../../../utils/objects.mjs';

const __default__ = defineComponent({
  name: "ElImageViewer"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: imageViewerProps,
  emits: imageViewerEmits,
  setup(__props, { expose, emit }) {
    var _a;
    const props = __props;
    const modes = {
      CONTAIN: {
        name: "contain",
        icon: markRaw(FullScreen)
      },
      ORIGINAL: {
        name: "original",
        icon: markRaw(ScaleToOriginal)
      }
    };
    let stopWheelListener;
    let prevOverflow = "";
    const { t } = useLocale();
    const ns = useNamespace("image-viewer");
    const { nextZIndex } = useZIndex();
    const wrapper = ref();
    const imgRefs = ref([]);
    const scopeEventListener = effectScope();
    const loading = ref(true);
    const activeIndex = ref(props.initialIndex);
    const mode = shallowRef(modes.CONTAIN);
    const transform = ref({
      scale: 1,
      deg: 0,
      offsetX: 0,
      offsetY: 0,
      enableTransition: false
    });
    const zIndex = ref((_a = props.zIndex) != null ? _a : nextZIndex());
    const isSingle = computed(() => {
      const { urlList } = props;
      return urlList.length <= 1;
    });
    const isFirst = computed(() => activeIndex.value === 0);
    const isLast = computed(() => activeIndex.value === props.urlList.length - 1);
    const currentImg = computed(() => props.urlList[activeIndex.value]);
    const arrowPrevKls = computed(() => [
      ns.e("btn"),
      ns.e("prev"),
      ns.is("disabled", !props.infinite && isFirst.value)
    ]);
    const arrowNextKls = computed(() => [
      ns.e("btn"),
      ns.e("next"),
      ns.is("disabled", !props.infinite && isLast.value)
    ]);
    const imgStyle = computed(() => {
      const { scale, deg, offsetX, offsetY, enableTransition } = transform.value;
      let translateX = offsetX / scale;
      let translateY = offsetY / scale;
      const radian = deg * Math.PI / 180;
      const cosRadian = Math.cos(radian);
      const sinRadian = Math.sin(radian);
      translateX = translateX * cosRadian + translateY * sinRadian;
      translateY = translateY * cosRadian - offsetX / scale * sinRadian;
      const style = {
        transform: `scale(${scale}) rotate(${deg}deg) translate(${translateX}px, ${translateY}px)`,
        transition: enableTransition ? "transform .3s" : ""
      };
      if (mode.value.name === modes.CONTAIN.name) {
        style.maxWidth = style.maxHeight = "100%";
      }
      return style;
    });
    const progress = computed(() => `${activeIndex.value + 1} / ${props.urlList.length}`);
    function hide() {
      unregisterEventListener();
      stopWheelListener == null ? void 0 : stopWheelListener();
      document.body.style.overflow = prevOverflow;
      emit("close");
    }
    function registerEventListener() {
      const keydownHandler = throttle((e) => {
        switch (e.code) {
          case EVENT_CODE.esc:
            props.closeOnPressEscape && hide();
            break;
          case EVENT_CODE.space:
            toggleMode();
            break;
          case EVENT_CODE.left:
            prev();
            break;
          case EVENT_CODE.up:
            handleActions("zoomIn");
            break;
          case EVENT_CODE.right:
            next();
            break;
          case EVENT_CODE.down:
            handleActions("zoomOut");
            break;
        }
      });
      const mousewheelHandler = throttle((e) => {
        const delta = e.deltaY || e.deltaX;
        handleActions(delta < 0 ? "zoomIn" : "zoomOut", {
          zoomRate: props.zoomRate,
          enableTransition: false
        });
      });
      scopeEventListener.run(() => {
        useEventListener(document, "keydown", keydownHandler);
        useEventListener(document, "wheel", mousewheelHandler);
      });
    }
    function unregisterEventListener() {
      scopeEventListener.stop();
    }
    function handleImgLoad() {
      loading.value = false;
    }
    function handleImgError(e) {
      loading.value = false;
      e.target.alt = t("el.image.error");
    }
    function handleMouseDown(e) {
      if (loading.value || e.button !== 0 || !wrapper.value)
        return;
      transform.value.enableTransition = false;
      const { offsetX, offsetY } = transform.value;
      const startX = e.pageX;
      const startY = e.pageY;
      const dragHandler = throttle((ev) => {
        transform.value = {
          ...transform.value,
          offsetX: offsetX + ev.pageX - startX,
          offsetY: offsetY + ev.pageY - startY
        };
      });
      const removeMousemove = useEventListener(document, "mousemove", dragHandler);
      useEventListener(document, "mouseup", () => {
        removeMousemove();
      });
      e.preventDefault();
    }
    function reset() {
      transform.value = {
        scale: 1,
        deg: 0,
        offsetX: 0,
        offsetY: 0,
        enableTransition: false
      };
    }
    function toggleMode() {
      if (loading.value)
        return;
      const modeNames = keysOf(modes);
      const modeValues = Object.values(modes);
      const currentMode = mode.value.name;
      const index = modeValues.findIndex((i) => i.name === currentMode);
      const nextIndex = (index + 1) % modeNames.length;
      mode.value = modes[modeNames[nextIndex]];
      reset();
    }
    function setActiveItem(index) {
      const len = props.urlList.length;
      activeIndex.value = (index + len) % len;
    }
    function prev() {
      if (isFirst.value && !props.infinite)
        return;
      setActiveItem(activeIndex.value - 1);
    }
    function next() {
      if (isLast.value && !props.infinite)
        return;
      setActiveItem(activeIndex.value + 1);
    }
    function handleActions(action, options = {}) {
      if (loading.value)
        return;
      const { minScale, maxScale } = props;
      const { zoomRate, rotateDeg, enableTransition } = {
        zoomRate: props.zoomRate,
        rotateDeg: 90,
        enableTransition: true,
        ...options
      };
      switch (action) {
        case "zoomOut":
          if (transform.value.scale > minScale) {
            transform.value.scale = Number.parseFloat((transform.value.scale / zoomRate).toFixed(3));
          }
          break;
        case "zoomIn":
          if (transform.value.scale < maxScale) {
            transform.value.scale = Number.parseFloat((transform.value.scale * zoomRate).toFixed(3));
          }
          break;
        case "clockwise":
          transform.value.deg += rotateDeg;
          emit("rotate", transform.value.deg);
          break;
        case "anticlockwise":
          transform.value.deg -= rotateDeg;
          emit("rotate", transform.value.deg);
          break;
      }
      transform.value.enableTransition = enableTransition;
    }
    function onFocusoutPrevented(event) {
      var _a2;
      if (((_a2 = event.detail) == null ? void 0 : _a2.focusReason) === "pointer") {
        event.preventDefault();
      }
    }
    function onCloseRequested() {
      if (props.closeOnPressEscape) {
        hide();
      }
    }
    function wheelHandler(e) {
      if (!e.ctrlKey)
        return;
      if (e.deltaY < 0) {
        e.preventDefault();
        return false;
      } else if (e.deltaY > 0) {
        e.preventDefault();
        return false;
      }
    }
    watch(currentImg, () => {
      nextTick(() => {
        const $img = imgRefs.value[0];
        if (!($img == null ? void 0 : $img.complete)) {
          loading.value = true;
        }
      });
    });
    watch(activeIndex, (val) => {
      reset();
      emit("switch", val);
    });
    onMounted(() => {
      registerEventListener();
      stopWheelListener = useEventListener("wheel", wheelHandler, {
        passive: false
      });
      prevOverflow = document.body.style.overflow;
      document.body.style.overflow = "hidden";
    });
    expose({
      setActiveItem
    });
    return (_ctx, _cache) => {
      return openBlock(), createBlock(unref(ElTeleport), {
        to: "body",
        disabled: !_ctx.teleported
      }, {
        default: withCtx(() => [
          createVNode(Transition, {
            name: "viewer-fade",
            appear: ""
          }, {
            default: withCtx(() => [
              createElementVNode("div", {
                ref_key: "wrapper",
                ref: wrapper,
                tabindex: -1,
                class: normalizeClass(unref(ns).e("wrapper")),
                style: normalizeStyle({ zIndex: zIndex.value })
              }, [
                createVNode(unref(ElFocusTrap), {
                  loop: "",
                  trapped: "",
                  "focus-trap-el": wrapper.value,
                  "focus-start-el": "container",
                  onFocusoutPrevented,
                  onReleaseRequested: onCloseRequested
                }, {
                  default: withCtx(() => [
                    createElementVNode("div", {
                      class: normalizeClass(unref(ns).e("mask")),
                      onClick: withModifiers(($event) => _ctx.hideOnClickModal && hide(), ["self"])
                    }, null, 10, ["onClick"]),
                    createCommentVNode(" CLOSE "),
                    createElementVNode("span", {
                      class: normalizeClass([unref(ns).e("btn"), unref(ns).e("close")]),
                      onClick: hide
                    }, [
                      createVNode(unref(ElIcon), null, {
                        default: withCtx(() => [
                          createVNode(unref(Close))
                        ]),
                        _: 1
                      })
                    ], 2),
                    createCommentVNode(" ARROW "),
                    !unref(isSingle) ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
                      createElementVNode("span", {
                        class: normalizeClass(unref(arrowPrevKls)),
                        onClick: prev
                      }, [
                        createVNode(unref(ElIcon), null, {
                          default: withCtx(() => [
                            createVNode(unref(ArrowLeft))
                          ]),
                          _: 1
                        })
                      ], 2),
                      createElementVNode("span", {
                        class: normalizeClass(unref(arrowNextKls)),
                        onClick: next
                      }, [
                        createVNode(unref(ElIcon), null, {
                          default: withCtx(() => [
                            createVNode(unref(ArrowRight))
                          ]),
                          _: 1
                        })
                      ], 2)
                    ], 64)) : createCommentVNode("v-if", true),
                    _ctx.$slots.progress || _ctx.showProgress ? (openBlock(), createElementBlock("div", {
                      key: 1,
                      class: normalizeClass([unref(ns).e("btn"), unref(ns).e("progress")])
                    }, [
                      renderSlot(_ctx.$slots, "progress", {
                        activeIndex: activeIndex.value,
                        total: _ctx.urlList.length
                      }, () => [
                        createTextVNode(toDisplayString(unref(progress)), 1)
                      ])
                    ], 2)) : createCommentVNode("v-if", true),
                    createCommentVNode(" ACTIONS "),
                    createElementVNode("div", {
                      class: normalizeClass([unref(ns).e("btn"), unref(ns).e("actions")])
                    }, [
                      createElementVNode("div", {
                        class: normalizeClass(unref(ns).e("actions__inner"))
                      }, [
                        renderSlot(_ctx.$slots, "toolbar", {
                          actions: handleActions,
                          prev,
                          next,
                          reset: toggleMode,
                          activeIndex: activeIndex.value,
                          setActiveItem
                        }, () => [
                          createVNode(unref(ElIcon), {
                            onClick: ($event) => handleActions("zoomOut")
                          }, {
                            default: withCtx(() => [
                              createVNode(unref(ZoomOut))
                            ]),
                            _: 1
                          }, 8, ["onClick"]),
                          createVNode(unref(ElIcon), {
                            onClick: ($event) => handleActions("zoomIn")
                          }, {
                            default: withCtx(() => [
                              createVNode(unref(ZoomIn))
                            ]),
                            _: 1
                          }, 8, ["onClick"]),
                          createElementVNode("i", {
                            class: normalizeClass(unref(ns).e("actions__divider"))
                          }, null, 2),
                          createVNode(unref(ElIcon), { onClick: toggleMode }, {
                            default: withCtx(() => [
                              (openBlock(), createBlock(resolveDynamicComponent(unref(mode).icon)))
                            ]),
                            _: 1
                          }),
                          createElementVNode("i", {
                            class: normalizeClass(unref(ns).e("actions__divider"))
                          }, null, 2),
                          createVNode(unref(ElIcon), {
                            onClick: ($event) => handleActions("anticlockwise")
                          }, {
                            default: withCtx(() => [
                              createVNode(unref(RefreshLeft))
                            ]),
                            _: 1
                          }, 8, ["onClick"]),
                          createVNode(unref(ElIcon), {
                            onClick: ($event) => handleActions("clockwise")
                          }, {
                            default: withCtx(() => [
                              createVNode(unref(RefreshRight))
                            ]),
                            _: 1
                          }, 8, ["onClick"])
                        ])
                      ], 2)
                    ], 2),
                    createCommentVNode(" CANVAS "),
                    createElementVNode("div", {
                      class: normalizeClass(unref(ns).e("canvas"))
                    }, [
                      (openBlock(true), createElementBlock(Fragment, null, renderList(_ctx.urlList, (url, i) => {
                        return withDirectives((openBlock(), createElementBlock("img", {
                          ref_for: true,
                          ref: (el) => imgRefs.value[i] = el,
                          key: url,
                          src: url,
                          style: normalizeStyle(unref(imgStyle)),
                          class: normalizeClass(unref(ns).e("img")),
                          crossorigin: _ctx.crossorigin,
                          onLoad: handleImgLoad,
                          onError: handleImgError,
                          onMousedown: handleMouseDown
                        }, null, 46, ["src", "crossorigin"])), [
                          [vShow, i === activeIndex.value]
                        ]);
                      }), 128))
                    ], 2),
                    renderSlot(_ctx.$slots, "default")
                  ]),
                  _: 3
                }, 8, ["focus-trap-el"])
              ], 6)
            ]),
            _: 3
          })
        ]),
        _: 3
      }, 8, ["disabled"]);
    };
  }
});
var ImageViewer = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "image-viewer.vue"]]);

export { ImageViewer as default };
//# sourceMappingURL=image-viewer2.mjs.map
