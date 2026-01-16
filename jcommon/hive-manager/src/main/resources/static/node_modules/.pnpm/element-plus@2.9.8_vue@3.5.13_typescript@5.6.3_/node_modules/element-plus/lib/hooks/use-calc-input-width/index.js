'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');

function useCalcInputWidth() {
  const calculatorRef = vue.shallowRef();
  const calculatorWidth = vue.ref(0);
  const MINIMUM_INPUT_WIDTH = 11;
  const inputStyle = vue.computed(() => ({
    minWidth: `${Math.max(calculatorWidth.value, MINIMUM_INPUT_WIDTH)}px`
  }));
  const resetCalculatorWidth = () => {
    var _a, _b;
    calculatorWidth.value = (_b = (_a = calculatorRef.value) == null ? void 0 : _a.getBoundingClientRect().width) != null ? _b : 0;
  };
  core.useResizeObserver(calculatorRef, resetCalculatorWidth);
  return {
    calculatorRef,
    calculatorWidth,
    inputStyle
  };
}

exports.useCalcInputWidth = useCalcInputWidth;
//# sourceMappingURL=index.js.map
