'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var typescript = require('../../../utils/typescript.js');
var types = require('../../../utils/types.js');
var shared = require('@vue/shared');
var event = require('../../../constants/event.js');

const emitChangeFn = (value) => types.isNumber(value) || shared.isString(value) || shared.isArray(value);
const collapseProps = runtime.buildProps({
  accordion: Boolean,
  modelValue: {
    type: runtime.definePropType([Array, String, Number]),
    default: () => typescript.mutable([])
  }
});
const collapseEmits = {
  [event.UPDATE_MODEL_EVENT]: emitChangeFn,
  [event.CHANGE_EVENT]: emitChangeFn
};

exports.collapseEmits = collapseEmits;
exports.collapseProps = collapseProps;
exports.emitChangeFn = emitChangeFn;
//# sourceMappingURL=collapse.js.map
