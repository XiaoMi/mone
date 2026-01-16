'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var upload = require('./upload.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var typescript = require('../../../utils/typescript.js');
var shared = require('@vue/shared');

const uploadListProps = runtime.buildProps({
  files: {
    type: runtime.definePropType(Array),
    default: () => typescript.mutable([])
  },
  disabled: {
    type: Boolean,
    default: false
  },
  handlePreview: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  listType: {
    type: String,
    values: upload.uploadListTypes,
    default: "text"
  },
  crossorigin: {
    type: runtime.definePropType(String)
  }
});
const uploadListEmits = {
  remove: (file) => !!file
};

exports.uploadListEmits = uploadListEmits;
exports.uploadListProps = uploadListProps;
//# sourceMappingURL=upload-list.js.map
