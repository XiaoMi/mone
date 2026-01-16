'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');

const dialogContentProps = runtime.buildProps({
  center: Boolean,
  alignCenter: Boolean,
  closeIcon: {
    type: icon.iconPropType
  },
  draggable: Boolean,
  overflow: Boolean,
  fullscreen: Boolean,
  headerClass: String,
  bodyClass: String,
  footerClass: String,
  showClose: {
    type: Boolean,
    default: true
  },
  title: {
    type: String,
    default: ""
  },
  ariaLevel: {
    type: String,
    default: "2"
  }
});
const dialogContentEmits = {
  close: () => true
};

exports.dialogContentEmits = dialogContentEmits;
exports.dialogContentProps = dialogContentProps;
//# sourceMappingURL=dialog-content.js.map
