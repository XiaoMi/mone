'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var runtime = require('../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');
var error = require('../../utils/error.js');

const emptyValuesContextKey = Symbol("emptyValuesContextKey");
const SCOPE = "use-empty-values";
const DEFAULT_EMPTY_VALUES = ["", void 0, null];
const DEFAULT_VALUE_ON_CLEAR = void 0;
const useEmptyValuesProps = runtime.buildProps({
  emptyValues: Array,
  valueOnClear: {
    type: [String, Number, Boolean, Function],
    default: void 0,
    validator: (val) => shared.isFunction(val) ? !val() : !val
  }
});
const useEmptyValues = (props, defaultValue) => {
  const config = vue.getCurrentInstance() ? vue.inject(emptyValuesContextKey, vue.ref({})) : vue.ref({});
  const emptyValues = vue.computed(() => props.emptyValues || config.value.emptyValues || DEFAULT_EMPTY_VALUES);
  const valueOnClear = vue.computed(() => {
    if (shared.isFunction(props.valueOnClear)) {
      return props.valueOnClear();
    } else if (props.valueOnClear !== void 0) {
      return props.valueOnClear;
    } else if (shared.isFunction(config.value.valueOnClear)) {
      return config.value.valueOnClear();
    } else if (config.value.valueOnClear !== void 0) {
      return config.value.valueOnClear;
    }
    return defaultValue !== void 0 ? defaultValue : DEFAULT_VALUE_ON_CLEAR;
  });
  const isEmptyValue = (value) => {
    return emptyValues.value.includes(value);
  };
  if (!emptyValues.value.includes(valueOnClear.value)) {
    error.debugWarn(SCOPE, "value-on-clear should be a value of empty-values");
  }
  return {
    emptyValues,
    valueOnClear,
    isEmptyValue
  };
};

exports.DEFAULT_EMPTY_VALUES = DEFAULT_EMPTY_VALUES;
exports.DEFAULT_VALUE_ON_CLEAR = DEFAULT_VALUE_ON_CLEAR;
exports.SCOPE = SCOPE;
exports.emptyValuesContextKey = emptyValuesContextKey;
exports.useEmptyValues = useEmptyValues;
exports.useEmptyValuesProps = useEmptyValuesProps;
//# sourceMappingURL=index.js.map
