'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('./constants.js');
var types = require('../../../utils/types.js');
var event = require('../../../constants/event.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var index = require('../../../hooks/use-deprecated/index.js');

const useRadio = (props, emit) => {
  const radioRef = vue.ref();
  const radioGroup = vue.inject(constants.radioGroupKey, void 0);
  const isGroup = vue.computed(() => !!radioGroup);
  const actualValue = vue.computed(() => {
    if (!types.isPropAbsent(props.value)) {
      return props.value;
    }
    return props.label;
  });
  const modelValue = vue.computed({
    get() {
      return isGroup.value ? radioGroup.modelValue : props.modelValue;
    },
    set(val) {
      if (isGroup.value) {
        radioGroup.changeEvent(val);
      } else {
        emit && emit(event.UPDATE_MODEL_EVENT, val);
      }
      radioRef.value.checked = props.modelValue === actualValue.value;
    }
  });
  const size = useFormCommonProps.useFormSize(vue.computed(() => radioGroup == null ? void 0 : radioGroup.size));
  const disabled = useFormCommonProps.useFormDisabled(vue.computed(() => radioGroup == null ? void 0 : radioGroup.disabled));
  const focus = vue.ref(false);
  const tabIndex = vue.computed(() => {
    return disabled.value || isGroup.value && modelValue.value !== actualValue.value ? -1 : 0;
  });
  index.useDeprecated({
    from: "label act as value",
    replacement: "value",
    version: "3.0.0",
    scope: "el-radio",
    ref: "https://element-plus.org/en-US/component/radio.html"
  }, vue.computed(() => isGroup.value && types.isPropAbsent(props.value)));
  return {
    radioRef,
    isGroup,
    radioGroup,
    focus,
    size,
    disabled,
    tabIndex,
    modelValue,
    actualValue
  };
};

exports.useRadio = useRadio;
//# sourceMappingURL=use-radio.js.map
