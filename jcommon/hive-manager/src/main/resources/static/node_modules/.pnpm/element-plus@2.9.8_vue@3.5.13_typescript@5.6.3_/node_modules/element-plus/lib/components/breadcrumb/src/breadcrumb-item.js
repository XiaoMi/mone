'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const breadcrumbItemProps = runtime.buildProps({
  to: {
    type: runtime.definePropType([String, Object]),
    default: ""
  },
  replace: Boolean
});

exports.breadcrumbItemProps = breadcrumbItemProps;
//# sourceMappingURL=breadcrumb-item.js.map
