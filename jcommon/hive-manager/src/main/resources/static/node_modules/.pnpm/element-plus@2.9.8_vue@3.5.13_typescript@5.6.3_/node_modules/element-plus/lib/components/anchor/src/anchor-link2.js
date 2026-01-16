'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var anchorLink = require('./anchor-link.js');
var constants = require('./constants.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');

const __default__ = vue.defineComponent({
  name: "ElAnchorLink"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: anchorLink.anchorLinkProps,
  setup(__props) {
    const props = __props;
    const linkRef = vue.ref(null);
    const {
      ns,
      direction,
      currentAnchor,
      addLink,
      removeLink,
      handleClick: contextHandleClick
    } = vue.inject(constants.anchorKey);
    const cls = vue.computed(() => [
      ns.e("link"),
      ns.is("active", currentAnchor.value === props.href)
    ]);
    const handleClick = (e) => {
      contextHandleClick(e, props.href);
    };
    vue.watch(() => props.href, (val, oldVal) => {
      vue.nextTick(() => {
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
    vue.onMounted(() => {
      const { href } = props;
      if (href) {
        addLink({
          href,
          el: linkRef.value
        });
      }
    });
    vue.onBeforeUnmount(() => {
      const { href } = props;
      if (href) {
        removeLink(href);
      }
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(ns).e("item"))
      }, [
        vue.createElementVNode("a", {
          ref_key: "linkRef",
          ref: linkRef,
          class: vue.normalizeClass(vue.unref(cls)),
          href: _ctx.href,
          onClick: handleClick
        }, [
          vue.renderSlot(_ctx.$slots, "default", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.title), 1)
          ])
        ], 10, ["href"]),
        _ctx.$slots["sub-link"] && vue.unref(direction) === "vertical" ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass(vue.unref(ns).e("list"))
        }, [
          vue.renderSlot(_ctx.$slots, "sub-link")
        ], 2)) : vue.createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var AnchorLink = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "anchor-link.vue"]]);

exports["default"] = AnchorLink;
//# sourceMappingURL=anchor-link2.js.map
