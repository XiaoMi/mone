import { placements } from '@popperjs/core';
import { CircleClose, ArrowDown } from '@element-plus/icons-vue';
import { defaultProps } from './useProps.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { iconPropType } from '../../../utils/vue/icon.mjs';
import { useTooltipContentProps } from '../../tooltip/src/content.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { tagProps } from '../../tag/src/tag.mjs';
import { isBoolean, isNumber } from '../../../utils/types.mjs';
import { useEmptyValuesProps } from '../../../hooks/use-empty-values/index.mjs';
import { useAriaProps } from '../../../hooks/use-aria/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';

const SelectProps = buildProps({
  allowCreate: Boolean,
  autocomplete: {
    type: definePropType(String),
    default: "none"
  },
  automaticDropdown: Boolean,
  clearable: Boolean,
  clearIcon: {
    type: iconPropType,
    default: CircleClose
  },
  effect: {
    type: definePropType(String),
    default: "light"
  },
  collapseTags: Boolean,
  collapseTagsTooltip: Boolean,
  maxCollapseTags: {
    type: Number,
    default: 1
  },
  defaultFirstOption: Boolean,
  disabled: Boolean,
  estimatedOptionHeight: {
    type: Number,
    default: void 0
  },
  filterable: Boolean,
  filterMethod: Function,
  height: {
    type: Number,
    default: 274
  },
  itemHeight: {
    type: Number,
    default: 34
  },
  id: String,
  loading: Boolean,
  loadingText: String,
  modelValue: {
    type: definePropType([Array, String, Number, Boolean, Object])
  },
  multiple: Boolean,
  multipleLimit: {
    type: Number,
    default: 0
  },
  name: String,
  noDataText: String,
  noMatchText: String,
  remoteMethod: Function,
  reserveKeyword: {
    type: Boolean,
    default: true
  },
  options: {
    type: definePropType(Array),
    required: true
  },
  placeholder: {
    type: String
  },
  teleported: useTooltipContentProps.teleported,
  persistent: {
    type: Boolean,
    default: true
  },
  popperClass: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: definePropType(Object),
    default: () => ({})
  },
  remote: Boolean,
  size: useSizeProp,
  props: {
    type: definePropType(Object),
    default: () => defaultProps
  },
  valueKey: {
    type: String,
    default: "value"
  },
  scrollbarAlwaysOn: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  offset: {
    type: Number,
    default: 12
  },
  showArrow: {
    type: Boolean,
    default: true
  },
  placement: {
    type: definePropType(String),
    values: placements,
    default: "bottom-start"
  },
  fallbackPlacements: {
    type: definePropType(Array),
    default: ["bottom-start", "top-start", "right", "left"]
  },
  tagType: { ...tagProps.type, default: "info" },
  tagEffect: { ...tagProps.effect, default: "light" },
  tabindex: {
    type: [String, Number],
    default: 0
  },
  appendTo: String,
  fitInputWidth: {
    type: [Boolean, Number],
    default: true,
    validator(val) {
      return isBoolean(val) || isNumber(val);
    }
  },
  suffixIcon: {
    type: iconPropType,
    default: ArrowDown
  },
  ...useEmptyValuesProps,
  ...useAriaProps(["ariaLabel"])
});
const OptionProps = buildProps({
  data: Array,
  disabled: Boolean,
  hovering: Boolean,
  item: {
    type: definePropType(Object),
    required: true
  },
  index: Number,
  style: Object,
  selected: Boolean,
  created: Boolean
});
const selectEmits = {
  [UPDATE_MODEL_EVENT]: (val) => true,
  [CHANGE_EVENT]: (val) => true,
  "remove-tag": (val) => true,
  "visible-change": (visible) => true,
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true
};
const optionEmits = {
  hover: (index) => isNumber(index),
  select: (val, index) => true
};

export { OptionProps, SelectProps, optionEmits, selectEmits };
//# sourceMappingURL=defaults.mjs.map
