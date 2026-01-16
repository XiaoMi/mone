'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var upload = require('./upload.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');

const uploadContentProps = runtime.buildProps({
  ...upload.uploadBaseProps,
  beforeUpload: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onRemove: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onStart: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onSuccess: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onProgress: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onError: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  },
  onExceed: {
    type: runtime.definePropType(Function),
    default: shared.NOOP
  }
});

exports.uploadContentProps = uploadContentProps;
//# sourceMappingURL=upload-content.js.map
