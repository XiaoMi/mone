'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');

const breadcrumbProps = runtime.buildProps({
  separator: {
    type: String,
    default: "/"
  },
  separatorIcon: {
    type: icon.iconPropType
  }
});

exports.breadcrumbProps = breadcrumbProps;
//# sourceMappingURL=breadcrumb.js.map
