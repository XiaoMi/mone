import { placements } from '@popperjs/core';
import { CircleClose } from '@element-plus/icons-vue';
import { disabledTimeListsProps } from '../props/shared.mjs';
import { buildProps, definePropType } from '../../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../../hooks/use-size/index.mjs';
import { useEmptyValuesProps } from '../../../../hooks/use-empty-values/index.mjs';
import { useAriaProps } from '../../../../hooks/use-aria/index.mjs';

const timePickerDefaultProps = buildProps({
  id: {
    type: definePropType([Array, String])
  },
  name: {
    type: definePropType([Array, String])
  },
  popperClass: {
    type: String,
    default: ""
  },
  format: String,
  valueFormat: String,
  dateFormat: String,
  timeFormat: String,
  type: {
    type: String,
    default: ""
  },
  clearable: {
    type: Boolean,
    default: true
  },
  clearIcon: {
    type: definePropType([String, Object]),
    default: CircleClose
  },
  editable: {
    type: Boolean,
    default: true
  },
  prefixIcon: {
    type: definePropType([String, Object]),
    default: ""
  },
  size: useSizeProp,
  readonly: Boolean,
  disabled: Boolean,
  placeholder: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: definePropType(Object),
    default: () => ({})
  },
  modelValue: {
    type: definePropType([Date, Array, String, Number]),
    default: ""
  },
  rangeSeparator: {
    type: String,
    default: "-"
  },
  startPlaceholder: String,
  endPlaceholder: String,
  defaultValue: {
    type: definePropType([Date, Array])
  },
  defaultTime: {
    type: definePropType([Date, Array])
  },
  isRange: Boolean,
  ...disabledTimeListsProps,
  disabledDate: {
    type: Function
  },
  cellClassName: {
    type: Function
  },
  shortcuts: {
    type: Array,
    default: () => []
  },
  arrowControl: Boolean,
  tabindex: {
    type: definePropType([String, Number]),
    default: 0
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  unlinkPanels: Boolean,
  placement: {
    type: definePropType(String),
    values: placements,
    default: "bottom"
  },
  fallbackPlacements: {
    type: definePropType(Array),
    default: ["bottom", "top", "right", "left"]
  },
  ...useEmptyValuesProps,
  ...useAriaProps(["ariaLabel"]),
  showNow: {
    type: Boolean,
    default: true
  }
});
const timePickerRangeTriggerProps = buildProps({
  id: {
    type: definePropType(Array)
  },
  name: {
    type: definePropType(Array)
  },
  modelValue: {
    type: definePropType([Array, String])
  },
  startPlaceholder: String,
  endPlaceholder: String,
  disabled: Boolean
});
const timePickerRngeTriggerProps = timePickerRangeTriggerProps;

export { timePickerDefaultProps, timePickerRangeTriggerProps, timePickerRngeTriggerProps };
//# sourceMappingURL=props.mjs.map
