import { defineComponent, ref, inject, computed, watch, nextTick, onMounted, onBeforeUnmount, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, renderSlot, createTextVNode, toDisplayString, createCommentVNode } from 'vue';
import { anchorLinkProps } from './anchor-link.mjs';
import { anchorKey } from './constants.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';

const __default__ = defineComponent({
  name: "ElAnchorLink"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: anchorLinkProps,
  setup(__props) {
    const props = __props;
    const linkRef = ref(null);
    const {
      ns,
      direction,
      currentAnchor,
      addLink,
      removeLink,
      handleClick: contextHandleClick
    } = inject(anchorKey);
    const cls = computed(() => [
      ns.e("link"),
      ns.is("active", currentAnchor.value === props.href)
    ]);
    const handleClick = (e) => {
      contextHandleClick(e, props.href);
    };
    watch(() => props.href, (val, oldVal) => {
      nextTick(() => {
        if (oldVal)
          removeLink(oldVal);
        if (val) {
          addLink({
            href: val,
            el: linkRef.value
          });
        }
      });
    });
    onMounted(() => {
      const { href } = props;
      if (href) {
        addLink({
          href,
          el: linkRef.value
        });
      }
    });
    onBeforeUnmount(() => {
      const { href } = props;
      if (href) {
        removeLink(href);
      }
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        class: normalizeClass(unref(ns).e("item"))
      }, [
        createElementVNode("a", {
          ref_key: "linkRef",
          ref: linkRef,
          class: normalizeClass(unref(cls)),
          href: _ctx.href,
          onClick: handleClick
        }, [
          renderSlot(_ctx.$slots, "default", {}, () => [
            createTextVNode(toDisplayString(_ctx.title), 1)
          ])
        ], 10, ["href"]),
        _ctx.$slots["sub-link"] && unref(direction) === "vertical" ? (openBlock(), createElementBlock("div", {
          key: 0,
          class: normalizeClass(unref(ns).e("list"))
        }, [
          renderSlot(_ctx.$slots, "sub-link")
        ], 2)) : createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var AnchorLink = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "anchor-link.vue"]]);

export { AnchorLink as default };
//# sourceMappingURL=anchor-link2.mjs.map
