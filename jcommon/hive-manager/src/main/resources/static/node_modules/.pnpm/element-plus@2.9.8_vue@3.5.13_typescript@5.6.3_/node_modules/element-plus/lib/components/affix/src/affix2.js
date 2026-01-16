'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var affix = require('./affix.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var scroll = require('../../../utils/dom/scroll.js');
var style = require('../../../utils/dom/style.js');
var event = require('../../../constants/event.js');
var error = require('../../../utils/error.js');

const COMPONENT_NAME = "ElAffix";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: affix.affixProps,
  emits: affix.affixEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = index.useNamespace("affix");
    const target = vue.shallowRef();
    const root = vue.shallowRef();
    const scrollContainer = vue.shallowRef();
    const { height: windowHeight } = core.useWindowSize();
    const {
      height: rootHeight,
      width: rootWidth,
      top: rootTop,
      bottom: rootBottom,
      update: updateRoot
    } = core.useElementBounding(root, { windowScroll: false });
    const targetRect = core.useElementBounding(target);
    const fixed = vue.ref(false);
    const scrollTop = vue.ref(0);
    const transform = vue.ref(0);
    const rootStyle = vue.computed(() => {
      return {
        height: fixed.value ? `${rootHeight.value}px` : "",
        width: fixed.value ? `${rootWidth.value}px` : ""
      };
    });
    const affixStyle = vue.computed(() => {
      if (!fixed.value)
        return {};
      const offset = props.offset ? style.addUnit(props.offset) : 0;
      return {
        height: `${rootHeight.value}px`,
        width: `${rootWidth.value}px`,
        top: props.position === "top" ? offset : "",
        bottom: props.position === "bottom" ? offset : "",
        transform: transform.value ? `translateY(${transform.value}px)` : "",
        zIndex: props.zIndex
      };
    });
    const update = () => {
      if (!scrollContainer.value)
        return;
      scrollTop.value = scrollContainer.value instanceof Window ? document.documentElement.scrollTop : scrollContainer.value.scrollTop || 0;
      const { position, target: target2, offset } = props;
      const rootHeightOffset = offset + rootHeight.value;
      if (position === "top") {
        if (target2) {
          const difference = targetRect.bottom.value - rootHeightOffset;
          fixed.value = offset > rootTop.value && targetRect.bottom.value > 0;
          transform.value = difference < 0 ? difference : 0;
        } else {
          fixed.value = offset > rootTop.value;
        }
      } else if (target2) {
        const difference = windowHeight.value - targetRect.top.value - rootHeightOffset;
        fixed.value = windowHeight.value - offset < rootBottom.value && windowHeight.value > targetRect.top.value;
        transform.value = difference < 0 ? -difference : 0;
      } else {
        fixed.value = windowHeight.value - offset < rootBottom.value;
      }
    };
    const handleScroll = async () => {
      updateRoot();
      await vue.nextTick();
      emit("scroll", {
        scrollTop: scrollTop.value,
        fixed: fixed.value
      });
    };
    vue.watch(fixed, (val) => emit(event.CHANGE_EVENT, val));
    vue.onMounted(() => {
      var _a;
      if (props.target) {
        target.value = (_a = document.querySelector(props.target)) != null ? _a : void 0;
        if (!target.value)
          error.throwError(COMPONENT_NAME, `Target does not exist: ${props.target}`);
      } else {
        target.value = document.documentElement;
      }
      scrollContainer.value = scroll.getScrollContainer(root.value, true);
      updateRoot();
    });
    core.useEventListener(scrollContainer, "scroll", handleScroll);
    vue.watchEffect(update);
    expose({
      update,
      updateRoot
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "root",
        ref: root,
        class: vue.normalizeClass(vue.unref(ns).b()),
        style: vue.normalizeStyle(vue.unref(rootStyle))
      }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass({ [vue.unref(ns).m("fixed")]: fixed.value }),
          style: vue.normalizeStyle(vue.unref(affixStyle))
        }, [
          vue.renderSlot(_ctx.$slots, "default")
        ], 6)
      ], 6);
    };
  }
});
var Affix = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "affix.vue"]]);

exports["default"] = Affix;
//# sourceMappingURL=affix2.js.map
