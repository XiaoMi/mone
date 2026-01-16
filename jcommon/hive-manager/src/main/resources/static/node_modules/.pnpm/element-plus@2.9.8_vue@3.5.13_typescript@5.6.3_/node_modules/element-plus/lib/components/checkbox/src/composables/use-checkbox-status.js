'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var constants = require('../constants.js');
var types = require('../../../../utils/types.js');
var shared = require('@vue/shared');
var useFormCommonProps = require('../../../form/src/hooks/use-form-common-props.js');

const useCheckboxStatus = (props, slots, { model }) => {
  const checkboxGroup = vue.inject(constants.checkboxGroupContextKey, void 0);
  const isFocused = vue.ref(false);
  const actualValue = vue.computed(() => {
    if (!types.isPropAbsent(props.value)) {
      return props.value;
    }
    return props.label;
  });
  const isChecked = vue.computed(() => {
    const value = model.value;
    if (types.isBoolean(value)) {
      return value;
    } else if (shared.isArray(value)) {
      if (shared.isObject(actualValue.value)) {
        return value.map(vue.toRaw).some((o) => lodashUnified.isEqual(o, actualValue.value));
      } else {
        return value.map(vue.toRaw).includes(actualValue.value);
      }
    } else if (value !== null && value !== void 0) {
      return value === props.trueValue || value === props.trueLabel;
    } else {
      return !!value;
    }
  });
  const checkboxButtonSize = useFormCommonProps.useFormSize(vue.computed(() => {
    var _a;
    return (_a = checkboxGroup == null ? void 0 : checkboxGroup.size) == null ? void 0 : _a.value;
  }), {
    prop: true
  });
  const checkboxSize = useFormCommonProps.useFormSize(vue.computed(() => {
    var _a;
    return (_a = checkboxGroup == null ? void 0 : checkboxGroup.size) == null ? void 0 : _a.value;
  }));
  const hasOwnLabel = vue.computed(() => {
    return !!slots.default || !types.isPropAbsent(actualValue.value);
  });
  return {
    checkboxButtonSize,
    isChecked,
    isFocused,
    checkboxSize,
    hasOwnLabel,
    actualValue
  };
};

exports.useCheckboxStatus = useCheckboxStatus;
//# sourceMappingURL=use-checkbox-status.js.map
