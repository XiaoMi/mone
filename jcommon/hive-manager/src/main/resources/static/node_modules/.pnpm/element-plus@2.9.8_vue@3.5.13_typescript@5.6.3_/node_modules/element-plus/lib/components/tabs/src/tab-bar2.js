'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var constants = require('./constants.js');
var tabBar = require('./tab-bar.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var error = require('../../../utils/error.js');
var index = require('../../../hooks/use-namespace/index.js');
var strings = require('../../../utils/strings.js');

const COMPONENT_NAME = "ElTabBar";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: tabBar.tabBarProps,
  setup(__props, { expose }) {
    const props = __props;
    const instance = vue.getCurrentInstance();
    const rootTabs = vue.inject(constants.tabsRootContextKey);
    if (!rootTabs)
      error.throwError(COMPONENT_NAME, "<el-tabs><el-tab-bar /></el-tabs>");
    const ns = index.useNamespace("tabs");
    const barRef = vue.ref();
    const barStyle = vue.ref();
    const getBarStyle = () => {
      let offset = 0;
      let tabSize = 0;
      const sizeName = ["top", "bottom"].includes(rootTabs.props.tabPosition) ? "width" : "height";
      const sizeDir = sizeName === "width" ? "x" : "y";
      const position = sizeDir === "x" ? "left" : "top";
      props.tabs.every((tab) => {
        var _a, _b;
        const $el = (_b = (_a = instance.parent) == null ? void 0 : _a.refs) == null ? void 0 : _b[`tab-${tab.uid}`];
        if (!$el)
          return false;
        if (!tab.active) {
          return true;
        }
        offset = $el[`offset${strings.capitalize(position)}`];
        tabSize = $el[`client${strings.capitalize(sizeName)}`];
        const tabStyles = window.getComputedStyle($el);
        if (sizeName === "width") {
          tabSize -= Number.parseFloat(tabStyles.paddingLeft) + Number.parseFloat(tabStyles.paddingRight);
          offset += Number.parseFloat(tabStyles.paddingLeft);
        }
        return false;
      });
      return {
        [sizeName]: `${tabSize}px`,
        transform: `translate${strings.capitalize(sizeDir)}(${offset}px)`
      };
    };
    const update = () => barStyle.value = getBarStyle();
    const saveObserver = [];
    const observerTabs = () => {
      var _a;
      saveObserver.forEach((observer) => observer.stop());
      saveObserver.length = 0;
      const list = (_a = instance.parent) == null ? void 0 : _a.refs;
      if (!list)
        return;
      for (const key in list) {
        if (key.startsWith("tab-")) {
          const _el = list[key];
          if (_el) {
            saveObserver.push(core.useResizeObserver(_el, update));
          }
        }
      }
    };
    vue.watch(() => props.tabs, async () => {
      await vue.nextTick();
      update();
      observerTabs();
    }, { immediate: true });
    const barObserever = core.useResizeObserver(barRef, () => update());
    vue.onBeforeUnmount(() => {
      saveObserver.forEach((observer) => observer.stop());
      saveObserver.length = 0;
      barObserever.stop();
    });
    expose({
      ref: barRef,
      update
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        ref_key: "barRef",
        ref: barRef,
        class: vue.normalizeClass([vue.unref(ns).e("active-bar"), vue.unref(ns).is(vue.unref(rootTabs).props.tabPosition)]),
        style: vue.normalizeStyle(barStyle.value)
      }, null, 6);
    };
  }
});
var TabBar = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "tab-bar.vue"]]);

exports["default"] = TabBar;
//# sourceMappingURL=tab-bar2.js.map
