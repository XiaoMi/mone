'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var lodashUnified = require('lodash-unified');
var index$3 = require('../../image-viewer/index.js');
var image = require('./image.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var position = require('../../../utils/dom/position.js');
var index = require('../../../hooks/use-locale/index.js');
var index$1 = require('../../../hooks/use-namespace/index.js');
var index$2 = require('../../../hooks/use-attrs/index.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');
var scroll = require('../../../utils/dom/scroll.js');

const __default__ = vue.defineComponent({
  name: "ElImage",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: image.imageProps,
  emits: image.imageEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const { t } = index.useLocale();
    const ns = index$1.useNamespace("image");
    const rawAttrs = vue.useAttrs();
    const containerAttrs = vue.computed(() => {
      return lodashUnified.fromPairs(Object.entries(rawAttrs).filter(([key]) => /^(data-|on[A-Z])/i.test(key) || ["id", "style"].includes(key)));
    });
    const imgAttrs = index$2.useAttrs({
      excludeListeners: true,
      excludeKeys: vue.computed(() => {
        return Object.keys(containerAttrs.value);
      })
    });
    const imageSrc = vue.ref();
    const hasLoadError = vue.ref(false);
    const isLoading = vue.ref(true);
    const showViewer = vue.ref(false);
    const container = vue.ref();
    const _scrollContainer = vue.ref();
    const supportLoading = core.isClient && "loading" in HTMLImageElement.prototype;
    let stopScrollListener;
    const imageKls = vue.computed(() => [
      ns.e("inner"),
      preview.value && ns.e("preview"),
      isLoading.value && ns.is("loading")
    ]);
    const imageStyle = vue.computed(() => {
      const { fit } = props;
      if (core.isClient && fit) {
        return { objectFit: fit };
      }
      return {};
    });
    const preview = vue.computed(() => {
      const { previewSrcList } = props;
      return shared.isArray(previewSrcList) && previewSrcList.length > 0;
    });
    const imageIndex = vue.computed(() => {
      const { previewSrcList, initialIndex } = props;
      let previewIndex = initialIndex;
      if (initialIndex > previewSrcList.length - 1) {
        previewIndex = 0;
      }
      return previewIndex;
    });
    const isManual = vue.computed(() => {
      if (props.loading === "eager")
        return false;
      return !supportLoading && props.loading === "lazy" || props.lazy;
    });
    const loadImage = () => {
      if (!core.isClient)
        return;
      isLoading.value = true;
      hasLoadError.value = false;
      imageSrc.value = props.src;
    };
    function handleLoad(event) {
      isLoading.value = false;
      hasLoadError.value = false;
      emit("load", event);
    }
    function handleError(event) {
      isLoading.value = false;
      hasLoadError.value = true;
      emit("error", event);
    }
    function handleLazyLoad() {
      if (position.isInContainer(container.value, _scrollContainer.value)) {
        loadImage();
        removeLazyLoadListener();
      }
    }
    const lazyLoadHandler = core.useThrottleFn(handleLazyLoad, 200, true);
    async function addLazyLoadListener() {
      var _a;
      if (!core.isClient)
        return;
      await vue.nextTick();
      const { scrollContainer } = props;
      if (types.isElement(scrollContainer)) {
        _scrollContainer.value = scrollContainer;
      } else if (shared.isString(scrollContainer) && scrollContainer !== "") {
        _scrollContainer.value = (_a = document.querySelector(scrollContainer)) != null ? _a : void 0;
      } else if (container.value) {
        _scrollContainer.value = scroll.getScrollContainer(container.value);
      }
      if (_scrollContainer.value) {
        stopScrollListener = core.useEventListener(_scrollContainer, "scroll", lazyLoadHandler);
        setTimeout(() => handleLazyLoad(), 100);
      }
    }
    function removeLazyLoadListener() {
      if (!core.isClient || !_scrollContainer.value || !lazyLoadHandler)
        return;
      stopScrollListener == null ? void 0 : stopScrollListener();
      _scrollContainer.value = void 0;
    }
    function clickHandler() {
      if (!preview.value)
        return;
      showViewer.value = true;
      emit("show");
    }
    function closeViewer() {
      showViewer.value = false;
      emit("close");
    }
    function switchViewer(val) {
      emit("switch", val);
    }
    vue.watch(() => props.src, () => {
      if (isManual.value) {
        isLoading.value = true;
        hasLoadError.value = false;
        removeLazyLoadListener();
        addLazyLoadListener();
      } else {
        loadImage();
      }
    });
    vue.onMounted(() => {
      if (isManual.value) {
        addLazyLoadListener();
      } else {
        loadImage();
      }
    });
    expose({
      showPreview: clickHandler
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", vue.mergeProps({
        ref_key: "container",
        ref: container
      }, vue.unref(containerAttrs), {
        class: [vue.unref(ns).b(), _ctx.$attrs.class]
      }), [
        hasLoadError.value ? vue.renderSlot(_ctx.$slots, "error", { key: 0 }, () => [
          vue.createElementVNode("div", {
            class: vue.normalizeClass(vue.unref(ns).e("error"))
          }, vue.toDisplayString(vue.unref(t)("el.image.error")), 3)
        ]) : (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 1 }, [
          imageSrc.value !== void 0 ? (vue.openBlock(), vue.createElementBlock("img", vue.mergeProps({ key: 0 }, vue.unref(imgAttrs), {
            src: imageSrc.value,
            loading: _ctx.loading,
            style: vue.unref(imageStyle),
            class: vue.unref(imageKls),
            crossorigin: _ctx.crossorigin,
            onClick: clickHandler,
            onLoad: handleLoad,
            onError: handleError
          }), null, 16, ["src", "loading", "crossorigin"])) : vue.createCommentVNode("v-if", true),
          isLoading.value ? (vue.openBlock(), vue.createElementBlock("div", {
            key: 1,
            class: vue.normalizeClass(vue.unref(ns).e("wrapper"))
          }, [
            vue.renderSlot(_ctx.$slots, "placeholder", {}, () => [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(vue.unref(ns).e("placeholder"))
              }, null, 2)
            ])
          ], 2)) : vue.createCommentVNode("v-if", true)
        ], 64)),
        vue.unref(preview) ? (vue.openBlock(), vue.createElementBlock(vue.Fragment, { key: 2 }, [
          showViewer.value ? (vue.openBlock(), vue.createBlock(vue.unref(index$3.ElImageViewer), {
            key: 0,
            "z-index": _ctx.zIndex,
            "initial-index": vue.unref(imageIndex),
            infinite: _ctx.infinite,
            "zoom-rate": _ctx.zoomRate,
            "min-scale": _ctx.minScale,
            "max-scale": _ctx.maxScale,
            "show-progress": _ctx.showProgress,
            "url-list": _ctx.previewSrcList,
            crossorigin: _ctx.crossorigin,
            "hide-on-click-modal": _ctx.hideOnClickModal,
            teleported: _ctx.previewTeleported,
            "close-on-press-escape": _ctx.closeOnPressEscape,
            onClose: closeViewer,
            onSwitch: switchViewer
          }, {
            progress: vue.withCtx((progress) => [
              vue.renderSlot(_ctx.$slots, "progress", vue.normalizeProps(vue.guardReactiveProps(progress)))
            ]),
            toolbar: vue.withCtx((toolbar) => [
              vue.renderSlot(_ctx.$slots, "toolbar", vue.normalizeProps(vue.guardReactiveProps(toolbar)))
            ]),
            default: vue.withCtx(() => [
              _ctx.$slots.viewer ? (vue.openBlock(), vue.createElementBlock("div", { key: 0 }, [
                vue.renderSlot(_ctx.$slots, "viewer")
              ])) : vue.createCommentVNode("v-if", true)
            ]),
            _: 3
          }, 8, ["z-index", "initial-index", "infinite", "zoom-rate", "min-scale", "max-scale", "show-progress", "url-list", "crossorigin", "hide-on-click-modal", "teleported", "close-on-press-escape"])) : vue.createCommentVNode("v-if", true)
        ], 64)) : vue.createCommentVNode("v-if", true)
      ], 16);
    };
  }
});
var Image = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "image.vue"]]);

exports["default"] = Image;
//# sourceMappingURL=image2.js.map
