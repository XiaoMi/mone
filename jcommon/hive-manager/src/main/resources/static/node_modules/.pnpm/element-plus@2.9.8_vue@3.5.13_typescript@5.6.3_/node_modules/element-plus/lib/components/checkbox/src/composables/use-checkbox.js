'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var useCheckboxDisabled = require('./use-checkbox-disabled.js');
var useCheckboxEvent = require('./use-checkbox-event.js');
var useCheckboxModel = require('./use-checkbox-model.js');
var useCheckboxStatus = require('./use-checkbox-status.js');
var useFormItem = require('../../../form/src/hooks/use-form-item.js');
var index = require('../../../../hooks/use-deprecated/index.js');
var types = require('../../../../utils/types.js');
var shared = require('@vue/shared');

const useCheckbox = (props, slots) => {
  const { formItem: elFormItem } = useFormItem.useFormItem();
  const { model, isGroup, isLimitExceeded } = useCheckboxModel.useCheckboxModel(props);
  const {
    isFocused,
    isChecked,
    checkboxButtonSize,
    checkboxSize,
    hasOwnLabel,
    actualValue
  } = useCheckboxStatus.useCheckboxStatus(props, slots, { model });
  const { isDisabled } = useCheckboxDisabled.useCheckboxDisabled({ model, isChecked });
  const { inputId, isLabeledByFormItem } = useFormItem.useFormItemInputId(props, {
    formItemContext: elFormItem,
    disableIdGeneration: hasOwnLabel,
    disableIdManagement: isGroup
  });
  const { handleChange, onClickRoot } = useCheckboxEvent.useCheckboxEvent(props, {
    model,
    isLimitExceeded,
    hasOwnLabel,
    isDisabled,
    isLabeledByFormItem
  });
  const setStoreValue = () => {
    function addToStore() {
      var _a, _b;
      if (shared.isArray(model.value) && !model.value.includes(actualValue.value)) {
        model.value.push(actualValue.value);
      } else {
        model.value = (_b = (_a = props.trueValue) != null ? _a : props.trueLabel) != null ? _b : true;
      }
    }
    props.checked && addToStore();
  };
  setStoreValue();
  index.useDeprecated({
    from: "label act as value",
    replacement: "value",
    version: "3.0.0",
    scope: "el-checkbox",
    ref: "https://element-plus.org/en-US/component/checkbox.html"
  }, vue.computed(() => isGroup.value && types.isPropAbsent(props.value)));
  index.useDeprecated({
    from: "true-label",
    replacement: "true-value",
    version: "3.0.0",
    scope: "el-checkbox",
    ref: "https://element-plus.org/en-US/component/checkbox.html"
  }, vue.computed(() => !!props.trueLabel));
  index.useDeprecated({
    from: "false-label",
    replacement: "false-value",
    version: "3.0.0",
    scope: "el-checkbox",
    ref: "https://element-plus.org/en-US/component/checkbox.html"
  }, vue.computed(() => !!props.falseLabel));
  return {
    inputId,
    isLabeledByFormItem,
    isChecked,
    isDisabled,
    isFocused,
    checkboxButtonSize,
    checkboxSize,
    hasOwnLabel,
    model,
    actualValue,
    handleChange,
    onClickRoot
  };
};

exports.useCheckbox = useCheckbox;
//# sourceMappingURL=use-checkbox.js.map
