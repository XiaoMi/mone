import { placements } from '@popperjs/core';
import { CommonProps } from '../../cascader-panel/src/config.mjs';
import { tagProps } from '../../tag/src/tag.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { useTooltipContentProps } from '../../tooltip/src/content.mjs';
import { useEmptyValuesProps } from '../../../hooks/use-empty-values/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';
import { isBoolean } from '../../../utils/types.mjs';

const cascaderProps = buildProps({
  ...CommonProps,
  size: useSizeProp,
  placeholder: String,
  disabled: Boolean,
  clearable: Boolean,
  filterable: Boolean,
  filterMethod: {
    type: definePropType(Function),
    default: (node, keyword) => node.text.includes(keyword)
  },
  separator: {
    type: String,
    default: " / "
  },
  showAllLevels: {
    type: Boolean,
    default: true
  },
  collapseTags: Boolean,
  maxCollapseTags: {
    type: Number,
    default: 1
  },
  collapseTagsTooltip: {
    type: Boolean,
    default: false
  },
  debounce: {
    type: Number,
    default: 300
  },
  beforeFilter: {
    type: definePropType(Function),
    default: () => true
  },
  placement: {
    type: definePropType(String),
    values: placements,
    default: "bottom-start"
  },
  fallbackPlacements: {
    type: definePropType(Array),
    default: ["bottom-start", "bottom", "top-start", "top", "right", "left"]
  },
  popperClass: {
    type: String,
    default: ""
  },
  teleported: useTooltipContentProps.teleported,
  tagType: { ...tagProps.type, default: "info" },
  tagEffect: { ...tagProps.effect, default: "light" },
  validateEvent: {
    type: Boolean,
    default: true
  },
  persistent: {
    type: Boolean,
    default: true
  },
  ...useEmptyValuesProps
});
const cascaderEmits = {
  [UPDATE_MODEL_EVENT]: (_) => true,
  [CHANGE_EVENT]: (_) => true,
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true,
  visibleChange: (val) => isBoolean(val),
  expandChange: (val) => !!val,
  removeTag: (val) => !!val
};

export { cascaderEmits, cascaderProps };
//# sourceMappingURL=cascader.mjs.map
