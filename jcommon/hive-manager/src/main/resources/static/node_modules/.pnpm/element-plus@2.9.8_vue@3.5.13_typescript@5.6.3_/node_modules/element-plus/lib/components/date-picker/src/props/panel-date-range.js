'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var shared = require('./shared.js');
var runtime = require('../../../../utils/vue/props/runtime.js');

const panelDateRangeProps = runtime.buildProps({
  ...shared.panelSharedProps,
  ...shared.panelRangeSharedProps,
  visible: Boolean
});

exports.panelDateRangeProps = panelDateRangeProps;
//# sourceMappingURL=panel-date-range.js.map
