'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('../constants.js');
var useFormItem = require('../../../form/src/hooks/use-form-item.js');
var error = require('../../../../utils/error.js');
var event = require('../../../../constants/event.js');

const useCheckboxEvent = (props, {
  model,
  isLimitExceeded,
  hasOwnLabel,
  isDisabled,
  isLabeledByFormItem
}) => {
  const checkboxGroup = vue.inject(constants.checkboxGroupContextKey, void 0);
  const { formItem } = useFormItem.useFormItem();
  const { emit } = vue.getCurrentInstance();
  function getLabeledValue(value) {
    var _a, _b, _c, _d;
    return [true, props.trueValue, props.trueLabel].includes(value) ? (_b = (_a = props.trueValue) != null ? _a : props.trueLabel) != null ? _b : true : (_d = (_c = props.falseValue) != null ? _c : props.falseLabel) != null ? _d : false;
  }
  function emitChangeEvent(checked, e) {
    emit(event.CHANGE_EVENT, getLabeledValue(checked), e);
  }
  function handleChange(e) {
    if (isLimitExceeded.value)
      return;
    const target = e.target;
    emit(event.CHANGE_EVENT, getLabeledValue(target.checked), e);
  }
  async function onClickRoot(e) {
    if (isLimitExceeded.value)
      return;
    if (!hasOwnLabel.value && !isDisabled.value && isLabeledByFormItem.value) {
      const eventTargets = e.composedPath();
      const hasLabel = eventTargets.some((item) => item.tagName === "LABEL");
      if (!hasLabel) {
        model.value = getLabeledValue([false, props.falseValue, props.falseLabel].includes(model.value));
        await vue.nextTick();
        emitChangeEvent(model.value, e);
      }
    }
  }
  const validateEvent = vue.computed(() => (checkboxGroup == null ? void 0 : checkboxGroup.validateEvent) || props.validateEvent);
  vue.watch(() => props.modelValue, () => {
    if (validateEvent.value) {
      formItem == null ? void 0 : formItem.validate("change").catch((err) => error.debugWarn(err));
    }
  });
  return {
    handleChange,
    onClickRoot
  };
};

exports.useCheckboxEvent = useCheckboxEvent;
//# sourceMappingURL=use-checkbox-event.js.map
