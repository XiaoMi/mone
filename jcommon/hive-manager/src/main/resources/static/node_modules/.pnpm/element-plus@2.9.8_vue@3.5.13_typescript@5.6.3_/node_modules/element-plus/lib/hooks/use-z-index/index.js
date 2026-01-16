'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var types = require('../../utils/types.js');
var core = require('@vueuse/core');
var error = require('../../utils/error.js');

const initial = {
  current: 0
};
const zIndex = vue.ref(0);
const defaultInitialZIndex = 2e3;
const ZINDEX_INJECTION_KEY = Symbol("elZIndexContextKey");
const zIndexContextKey = Symbol("zIndexContextKey");
const useZIndex = (zIndexOverrides) => {
  const increasingInjection = vue.getCurrentInstance() ? vue.inject(ZINDEX_INJECTION_KEY, initial) : initial;
  const zIndexInjection = zIndexOverrides || (vue.getCurrentInstance() ? vue.inject(zIndexContextKey, void 0) : void 0);
  const initialZIndex = vue.computed(() => {
    const zIndexFromInjection = vue.unref(zIndexInjection);
    return types.isNumber(zIndexFromInjection) ? zIndexFromInjection : defaultInitialZIndex;
  });
  const currentZIndex = vue.computed(() => initialZIndex.value + zIndex.value);
  const nextZIndex = () => {
    increasingInjection.current++;
    zIndex.value = increasingInjection.current;
    return currentZIndex.value;
  };
  if (!core.isClient && !vue.inject(ZINDEX_INJECTION_KEY)) {
    error.debugWarn("ZIndexInjection", `Looks like you are using server rendering, you must provide a z-index provider to ensure the hydration process to be succeed
usage: app.provide(ZINDEX_INJECTION_KEY, { current: 0 })`);
  }
  return {
    initialZIndex,
    currentZIndex,
    nextZIndex
  };
};

exports.ZINDEX_INJECTION_KEY = ZINDEX_INJECTION_KEY;
exports.defaultInitialZIndex = defaultInitialZIndex;
exports.useZIndex = useZIndex;
exports.zIndexContextKey = zIndexContextKey;
//# sourceMappingURL=index.js.map
