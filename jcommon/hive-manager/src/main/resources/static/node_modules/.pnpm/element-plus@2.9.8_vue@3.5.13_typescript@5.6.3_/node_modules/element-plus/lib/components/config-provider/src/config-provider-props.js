'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var index$1 = require('../../../hooks/use-empty-values/index.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');

const configProviderProps = runtime.buildProps({
  a11y: {
    type: Boolean,
    default: true
  },
  locale: {
    type: runtime.definePropType(Object)
  },
  size: index.useSizeProp,
  button: {
    type: runtime.definePropType(Object)
  },
  experimentalFeatures: {
    type: runtime.definePropType(Object)
  },
  keyboardNavigation: {
    type: Boolean,
    default: true
  },
  message: {
    type: runtime.definePropType(Object)
  },
  zIndex: Number,
  namespace: {
    type: String,
    default: "el"
  },
  ...index$1.useEmptyValuesProps
});

exports.configProviderProps = configProviderProps;
//# sourceMappingURL=config-provider-props.js.map
