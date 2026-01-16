'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var iconsVue = require('@element-plus/icons-vue');
var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');

const collapseItemProps = runtime.buildProps({
  title: {
    type: String,
    default: ""
  },
  name: {
    type: runtime.definePropType([String, Number]),
    default: void 0
  },
  icon: {
    type: icon.iconPropType,
    default: iconsVue.ArrowRight
  },
  disabled: Boolean
});

exports.collapseItemProps = collapseItemProps;
//# sourceMappingURL=collapse-item.js.map
