import { defineComponent, ref, computed, onMounted, watch, provide, openBlock, createElementBlock, normalizeClass, unref, normalizeStyle, createCommentVNode, createElementVNode, renderSlot } from 'vue';
import { useEventListener } from '@vueuse/core';
import { anchorProps, anchorEmits } from './anchor.mjs';
import { anchorKey } from './constants.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { getElement } from '../../../utils/dom/element.mjs';
import { throttleByRaf } from '../../../utils/throttleByRaf.mjs';
import { isWindow, isUndefined } from '../../../utils/types.mjs';
import { getScrollElement, animateScrollTo, getScrollTop } from '../../../utils/dom/scroll.mjs';
import { getOffsetTopDistance } from '../../../utils/dom/position.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElAnchor"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: anchorProps,
  emits: anchorEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const currentAnchor = ref("");
    const anchorRef = ref(null);
    const markerRef = ref(null);
    const containerEl = ref();
    const links = {};
    let isScrolling = false;
    let currentScrollTop = 0;
    const ns = useNamespace("anchor");
    const cls = computed(() => [
      ns.b(),
      props.type === "underline" ? ns.m("underline") : "",
      ns.m(props.direction)
    ]);
    const addLink = (state) => {
      links[state.href] = state.el;
    };
    const removeLink = (href) => {
      delete links[href];
    };
    const setCurrentAnchor = (href) => {
      const activeHref = currentAnchor.value;
      if (activeHref !== href) {
        currentAnchor.value = href;
        emit(CHANGE_EVENT, href);
      }
    };
    let clearAnimate = null;
    const scrollToAnchor = (href) => {
      if (!containerEl.value)
        return;
      const target = getElement(href);
      if (!target)
        return;
      if (clearAnimate)
        clearAnimate();
      isScrolling = true;
      const scrollEle = getScrollElement(target, containerEl.value);
      const distance = getOffsetTopDistance(target, scrollEle);
      const max = scrollEle.scrollHeight - scrollEle.clientHeight;
      const to = Math.min(distance - props.offset, max);
      clearAnimate = animateScrollTo(containerEl.value, currentScrollTop, to, props.duration, () => {
        setTimeout(() => {
          isScrolling = false;
        }, 20);
      });
    };
    const scrollTo = (href) => {
      if (href) {
        setCurrentAnchor(href);
        scrollToAnchor(href);
      }
    };
    const handleClick = (e, href) => {
      emit("click", e, href);
      scrollTo(href);
    };
    const handleScroll = throttleByRaf(() => {
      if (containerEl.value) {
        currentScrollTop = getScrollTop(containerEl.value);
      }
      const currentHref = getCurrentHref();
      if (isScrolling || isUndefined(currentHref))
        return;
      setCurrentAnchor(currentHref);
    });
    const getCurrentHref = () => {
      if (!containerEl.value)
        return;
      const scrollTop = getScrollTop(containerEl.value);
      const anchorTopList = [];
      for (const href of Object.keys(links)) {
        const target = getElement(href);
        if (!target)
          continue;
        const scrollEle = getScrollElement(target, containerEl.value);
        const distance = getOffsetTopDistance(target, scrollEle);
        anchorTopList.push({
          top: distance - props.offset - props.bound,
          href
        });
      }
      anchorTopList.sort((prev, next) => prev.top - next.top);
      for (let i = 0; i < anchorTopList.length; i++) {
        const item = anchorTopList[i];
        const next = anchorTopList[i + 1];
        if (i === 0 && scrollTop === 0) {
          return props.selectScrollTop ? item.href : "";
        }
        if (item.top <= scrollTop && (!next || next.top > scrollTop)) {
          return item.href;
        }
      }
    };
    const getContainer = () => {
      const el = getElement(props.container);
      if (!el || isWindow(el)) {
        containerEl.value = window;
      } else {
        containerEl.value = el;
      }
    };
    useEventListener(containerEl, "scroll", handleScroll);
    const markerStyle = computed(() => {
      if (!anchorRef.value || !markerRef.value || !currentAnchor.value)
        return {};
      const currentLinkEl = links[currentAnchor.value];
      if (!currentLinkEl)
        return {};
      const anchorRect = anchorRef.value.getBoundingClientRect();
      const markerRect = markerRef.value.getBoundingClientRect();
      const linkRect = currentLinkEl.getBoundingClientRect();
      if (props.direction === "horizontal") {
        const left = linkRect.left - anchorRect.left;
        return {
          left: `${left}px`,
          width: `${linkRect.width}px`,
          opacity: 1
        };
      } else {
        const top = linkRect.top - anchorRect.top + (linkRect.height - markerRect.height) / 2;
        return {
          top: `${top}px`,
          opacity: 1
        };
      }
    });
    onMounted(() => {
      getContainer();
      const hash = decodeURIComponent(window.location.hash);
      const target = getElement(hash);
      if (target) {
        scrollTo(hash);
      } else {
        handleScroll();
      }
    });
    watch(() => props.container, () => {
      getContainer();
    });
    provide(anchorKey, {
      ns,
      direction: props.direction,
      currentAnchor,
      addLink,
      removeLink,
      handleClick
    });
    expose({
      scrollTo
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        ref_key: "anchorRef",
        ref: anchorRef,
        class: normalizeClass(unref(cls))
      }, [
        _ctx.marker ? (openBlock(), createElementBlock("div", {
          key: 0,
          ref_key: "markerRef",
          ref: markerRef,
          class: normalizeClass(unref(ns).e("marker")),
          style: normalizeStyle(unref(markerStyle))
        }, null, 6)) : createCommentVNode("v-if", true),
        createElementVNode("div", {
          class: normalizeClass(unref(ns).e("list"))
        }, [
          renderSlot(_ctx.$slots, "default")
        ], 2)
      ], 2);
    };
  }
});
var Anchor = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "anchor.vue"]]);

export { Anchor as default };
//# sourceMappingURL=anchor2.mjs.map
