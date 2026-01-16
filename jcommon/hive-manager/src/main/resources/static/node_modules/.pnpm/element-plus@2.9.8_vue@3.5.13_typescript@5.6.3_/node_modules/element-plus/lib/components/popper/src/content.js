'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@popperjs/core');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-aria/index.js');

const POSITIONING_STRATEGIES = ["fixed", "absolute"];
const popperCoreConfigProps = runtime.buildProps({
  boundariesPadding: {
    type: Number,
    default: 0
  },
  fallbackPlacements: {
    type: runtime.definePropType(Array),
    default: void 0
  },
  gpuAcceleration: {
    type: Boolean,
    default: true
  },
  offset: {
    type: Number,
    default: 12
  },
  placement: {
    type: String,
    values: core.placements,
    default: "bottom"
  },
  popperOptions: {
    type: runtime.definePropType(Object),
    default: () => ({})
  },
  strategy: {
    type: String,
    values: POSITIONING_STRATEGIES,
    default: "absolute"
  }
});
const popperContentProps = runtime.buildProps({
  ...popperCoreConfigProps,
  id: String,
  style: {
    type: runtime.definePropType([String, Array, Object])
  },
  className: {
    type: runtime.definePropType([String, Array, Object])
  },
  effect: {
    type: runtime.definePropType(String),
    default: "dark"
  },
  visible: Boolean,
  enterable: {
    type: Boolean,
    default: true
  },
  pure: Boolean,
  focusOnShow: {
    type: Boolean,
    default: false
  },
  trapping: {
    type: Boolean,
    default: false
  },
  popperClass: {
    type: runtime.definePropType([String, Array, Object])
  },
  popperStyle: {
    type: runtime.definePropType([String, Array, Object])
  },
  referenceEl: {
    type: runtime.definePropType(Object)
  },
  triggerTargetEl: {
    type: runtime.definePropType(Object)
  },
  stopPopperMouseEvent: {
    type: Boolean,
    default: true
  },
  virtualTriggering: Boolean,
  zIndex: Number,
  ...index.useAriaProps(["ariaLabel"])
});
const popperContentEmits = {
  mouseenter: (evt) => evt instanceof MouseEvent,
  mouseleave: (evt) => evt instanceof MouseEvent,
  focus: () => true,
  blur: () => true,
  close: () => true
};
const usePopperCoreConfigProps = popperCoreConfigProps;
const usePopperContentProps = popperContentProps;
const usePopperContentEmits = popperContentEmits;

exports.popperContentEmits = popperContentEmits;
exports.popperContentProps = popperContentProps;
exports.popperCoreConfigProps = popperCoreConfigProps;
exports.usePopperContentEmits = usePopperContentEmits;
exports.usePopperContentProps = usePopperContentProps;
exports.usePopperCoreConfigProps = usePopperCoreConfigProps;
//# sourceMappingURL=content.js.map
