'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var lodashUnified = require('lodash-unified');
var runtime = require('../../utils/vue/props/runtime.js');

const ariaProps = runtime.buildProps({
  ariaLabel: String,
  ariaOrientation: {
    type: String,
    values: ["horizontal", "vertical", "undefined"]
  },
  ariaControls: String
});
const useAriaProps = (arias) => {
  return lodashUnified.pick(ariaProps, arias);
};

exports.ariaProps = ariaProps;
exports.useAriaProps = useAriaProps;
//# sourceMappingURL=index.js.map
