import { getCurrentInstance, inject, ref, computed } from 'vue';
import { buildProps } from '../../utils/vue/props/runtime.mjs';
import { isFunction } from '@vue/shared';
import { debugWarn } from '../../utils/error.mjs';

const emptyValuesContextKey = Symbol("emptyValuesContextKey");
const SCOPE = "use-empty-values";
const DEFAULT_EMPTY_VALUES = ["", void 0, null];
const DEFAULT_VALUE_ON_CLEAR = void 0;
const useEmptyValuesProps = buildProps({
  emptyValues: Array,
  valueOnClear: {
    type: [String, Number, Boolean, Function],
    default: void 0,
    validator: (val) => isFunction(val) ? !val() : !val
  }
});
const useEmptyValues = (props, defaultValue) => {
  const config = getCurrentInstance() ? inject(emptyValuesContextKey, ref({})) : ref({});
  const emptyValues = computed(() => props.emptyValues || config.value.emptyValues || DEFAULT_EMPTY_VALUES);
  const valueOnClear = computed(() => {
    if (isFunction(props.valueOnClear)) {
      return props.valueOnClear();
    } else if (props.valueOnClear !== void 0) {
      return props.valueOnClear;
    } else if (isFunction(config.value.valueOnClear)) {
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
    debugWarn(SCOPE, "value-on-clear should be a value of empty-values");
  }
  return {
    emptyValues,
    valueOnClear,
    isEmptyValue
  };
};

export { DEFAULT_EMPTY_VALUES, DEFAULT_VALUE_ON_CLEAR, SCOPE, emptyValuesContextKey, useEmptyValues, useEmptyValuesProps };
//# sourceMappingURL=index.mjs.map
