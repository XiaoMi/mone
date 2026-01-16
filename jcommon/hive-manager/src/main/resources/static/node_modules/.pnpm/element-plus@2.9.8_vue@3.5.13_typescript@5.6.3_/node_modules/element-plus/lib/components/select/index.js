'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var select$1 = require('./src/select2.js');
var option = require('./src/option2.js');
var optionGroup = require('./src/option-group.js');
var token = require('./src/token.js');
var select = require('./src/select.js');
var install = require('../../utils/vue/install.js');

const ElSelect = install.withInstall(select$1["default"], {
  Option: option["default"],
  OptionGroup: optionGroup["default"]
});
const ElOption = install.withNoopInstall(option["default"]);
const ElOptionGroup = install.withNoopInstall(optionGroup["default"]);

exports.selectGroupKey = token.selectGroupKey;
exports.selectKey = token.selectKey;
exports.SelectProps = select.SelectProps;
exports.selectEmits = select.selectEmits;
exports.ElOption = ElOption;
exports.ElOptionGroup = ElOptionGroup;
exports.ElSelect = ElSelect;
exports["default"] = ElSelect;
//# sourceMappingURL=index.js.map
