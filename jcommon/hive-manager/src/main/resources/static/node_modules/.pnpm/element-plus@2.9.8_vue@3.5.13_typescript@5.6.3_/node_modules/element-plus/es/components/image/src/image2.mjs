import { defineComponent, useAttrs, computed, ref, watch, onMounted, openBlock, createElementBlock, mergeProps, unref, renderSlot, createElementVNode, normalizeClass, toDisplayString, Fragment, createCommentVNode, createBlock, withCtx, normalizeProps, guardReactiveProps, nextTick } from 'vue';
import { isClient, useThrottleFn, useEventListener } from '@vueuse/core';
import { fromPairs } from 'lodash-unified';
import { ElImageViewer } from '../../image-viewer/index.mjs';
import { imageProps, imageEmits } from './image.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { isInContainer } from '../../../utils/dom/position.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useAttrs as useAttrs$1 } from '../../../hooks/use-attrs/index.mjs';
import { isArray, isString } from '@vue/shared';
import { isElement } from '../../../utils/types.mjs';
import { getScrollContainer } from '../../../utils/dom/scroll.mjs';

const __default__ = defineComponent({
  name: "ElImage",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: imageProps,
  emits: imageEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const { t } = useLocale();
    const ns = useNamespace("image");
    const rawAttrs = useAttrs();
    const containerAttrs = computed(() => {
      return fromPairs(Object.entries(rawAttrs).filter(([key]) => /^(data-|on[A-Z])/i.test(key) || ["id", "style"].includes(key)));
    });
    const imgAttrs = useAttrs$1({
      excludeListeners: true,
      excludeKeys: computed(() => {
        return Object.keys(containerAttrs.value);
      })
    });
    const imageSrc = ref();
    const hasLoadError = ref(false);
    const isLoading = ref(true);
    const showViewer = ref(false);
    const container = ref();
    const _scrollContainer = ref();
    const supportLoading = isClient && "loading" in HTMLImageElement.prototype;
    let stopScrollListener;
    const imageKls = computed(() => [
      ns.e("inner"),
      preview.value && ns.e("preview"),
      isLoading.value && ns.is("loading")
    ]);
    const imageStyle = computed(() => {
      const { fit } = props;
      if (isClient && fit) {
        return { objectFit: fit };
      }
      return {};
    });
    const preview = computed(() => {
      const { previewSrcList } = props;
      return isArray(previewSrcList) && previewSrcList.length > 0;
    });
    const imageIndex = computed(() => {
      const { previewSrcList, initialIndex } = props;
      let previewIndex = initialIndex;
      if (initialIndex > previewSrcList.length - 1) {
        previewIndex = 0;
      }
      return previewIndex;
    });
    const isManual = computed(() => {
      if (props.loading === "eager")
        return false;
      return !supportLoading && props.loading === "lazy" || props.lazy;
    });
    const loadImage = () => {
      if (!isClient)
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
      if (isInContainer(container.value, _scrollContainer.value)) {
        loadImage();
        removeLazyLoadListener();
      }
    }
    const lazyLoadHandler = useThrottleFn(handleLazyLoad, 200, true);
    async function addLazyLoadListener() {
      var _a;
      if (!isClient)
        return;
      await nextTick();
      const { scrollContainer } = props;
      if (isElement(scrollContainer)) {
        _scrollContainer.value = scrollContainer;
      } else if (isString(scrollContainer) && scrollContainer !== "") {
        _scrollContainer.value = (_a = document.querySelector(scrollContainer)) != null ? _a : void 0;
      } else if (container.value) {
        _scrollContainer.value = getScrollContainer(container.value);
      }
      if (_scrollContainer.value) {
        stopScrollListener = useEventListener(_scrollContainer, "scroll", lazyLoadHandler);
        setTimeout(() => handleLazyLoad(), 100);
      }
    }
    function removeLazyLoadListener() {
      if (!isClient || !_scrollContainer.value || !lazyLoadHandler)
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
    watch(() => props.src, () => {
      if (isManual.value) {
        isLoading.value = true;
        hasLoadError.value = false;
        removeLazyLoadListener();
        addLazyLoadListener();
      } else {
        loadImage();
      }
    });
    onMounted(() => {
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
      return openBlock(), createElementBlock("div", mergeProps({
        ref_key: "container",
        ref: container
      }, unref(containerAttrs), {
        class: [unref(ns).b(), _ctx.$attrs.class]
      }), [
        hasLoadError.value ? renderSlot(_ctx.$slots, "error", { key: 0 }, () => [
          createElementVNode("div", {
            class: normalizeClass(unref(ns).e("error"))
          }, toDisplayString(unref(t)("el.image.error")), 3)
        ]) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [
          imageSrc.value !== void 0 ? (openBlock(), createElementBlock("img", mergeProps({ key: 0 }, unref(imgAttrs), {
            src: imageSrc.value,
            loading: _ctx.loading,
            style: unref(imageStyle),
            class: unref(imageKls),
            crossorigin: _ctx.crossorigin,
            onClick: clickHandler,
            onLoad: handleLoad,
            onError: handleError
          }), null, 16, ["src", "loading", "crossorigin"])) : createCommentVNode("v-if", true),
          isLoading.value ? (openBlock(), createElementBlock("div", {
            key: 1,
            class: normalizeClass(unref(ns).e("wrapper"))
          }, [
            renderSlot(_ctx.$slots, "placeholder", {}, () => [
              createElementVNode("div", {
                class: normalizeClass(unref(ns).e("placeholder"))
              }, null, 2)
            ])
          ], 2)) : createCommentVNode("v-if", true)
        ], 64)),
        unref(preview) ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [
          showViewer.value ? (openBlock(), createBlock(unref(ElImageViewer), {
            key: 0,
            "z-index": _ctx.zIndex,
            "initial-index": unref(imageIndex),
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
            progress: withCtx((progress) => [
              renderSlot(_ctx.$slots, "progress", normalizeProps(guardReactiveProps(progress)))
            ]),
            toolbar: withCtx((toolbar) => [
              renderSlot(_ctx.$slots, "toolbar", normalizeProps(guardReactiveProps(toolbar)))
            ]),
            default: withCtx(() => [
              _ctx.$slots.viewer ? (openBlock(), createElementBlock("div", { key: 0 }, [
                renderSlot(_ctx.$slots, "viewer")
              ])) : createCommentVNode("v-if", true)
            ]),
            _: 3
          }, 8, ["z-index", "initial-index", "infinite", "zoom-rate", "min-scale", "max-scale", "show-progress", "url-list", "crossorigin", "hide-on-click-modal", "teleported", "close-on-press-escape"])) : createCommentVNode("v-if", true)
        ], 64)) : createCommentVNode("v-if", true)
      ], 16);
    };
  }
});
var Image = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "image.vue"]]);

export { Image as default };
//# sourceMappingURL=image2.mjs.map
