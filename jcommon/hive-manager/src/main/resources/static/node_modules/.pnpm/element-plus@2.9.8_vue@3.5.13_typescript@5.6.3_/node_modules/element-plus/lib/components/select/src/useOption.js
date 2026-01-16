'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var token = require('./token.js');
var option = require('./option.js');
var strings = require('../../../utils/strings.js');
var error = require('../../../utils/error.js');
var shared = require('@vue/shared');

function useOption(props, states) {
  const select = vue.inject(token.selectKey);
  if (!select) {
    error.throwError(option.COMPONENT_NAME, "usage: <el-select><el-option /></el-select/>");
  }
  const selectGroup = vue.inject(token.selectGroupKey, { disabled: false });
  const itemSelected = vue.computed(() => {
    return contains(lodashUnified.castArray(select.props.modelValue), props.value);
  });
  const limitReached = vue.computed(() => {
    var _a;
    if (select.props.multiple) {
      const modelValue = lodashUnified.castArray((_a = select.props.modelValue) != null ? _a : []);
      return !itemSelected.value && modelValue.length >= select.props.multipleLimit && select.props.multipleLimit > 0;
    } else {
      return false;
    }
  });
  const currentLabel = vue.computed(() => {
    return props.label || (shared.isObject(props.value) ? "" : props.value);
  });
  const currentValue = vue.computed(() => {
    return props.value || props.label || "";
  });
  const isDisabled = vue.computed(() => {
    return props.disabled || states.groupDisabled || limitReached.value;
  });
  const instance = vue.getCurrentInstance();
  const contains = (arr = [], target) => {
    if (!shared.isObject(props.value)) {
      return arr && arr.includes(target);
    } else {
      const valueKey = select.props.valueKey;
      return arr && arr.some((item) => {
        return vue.toRaw(lodashUnified.get(item, valueKey)) === lodashUnified.get(target, valueKey);
      });
    }
  };
  const hoverItem = () => {
    if (!props.disabled && !selectGroup.disabled) {
      select.states.hoveringIndex = select.optionsArray.indexOf(instance.proxy);
    }
  };
  const updateOption = (query) => {
    const regexp = new RegExp(strings.escapeStringRegexp(query), "i");
    states.visible = regexp.test(String(currentLabel.value)) || props.created;
  };
  vue.watch(() => currentLabel.value, () => {
    if (!props.created && !select.props.remote)
      select.setSelected();
  });
  vue.watch(() => props.value, (val, oldVal) => {
    const { remote, valueKey } = select.props;
    const shouldUpdate = remote ? val !== oldVal : !lodashUnified.isEqual(val, oldVal);
    if (shouldUpdate) {
      select.onOptionDestroy(oldVal, instance.proxy);
      select.onOptionCreate(instance.proxy);
    }
    if (!props.created && !remote) {
      if (valueKey && shared.isObject(val) && shared.isObject(oldVal) && val[valueKey] === oldVal[valueKey]) {
        return;
      }
      select.setSelected();
    }
  });
  vue.watch(() => selectGroup.disabled, () => {
    states.groupDisabled = selectGroup.disabled;
  }, { immediate: true });
  return {
    select,
    currentLabel,
    currentValue,
    itemSelected,
    isDisabled,
    hoverItem,
    updateOption
  };
}

exports.useOption = useOption;
//# sourceMappingURL=useOption.js.map
