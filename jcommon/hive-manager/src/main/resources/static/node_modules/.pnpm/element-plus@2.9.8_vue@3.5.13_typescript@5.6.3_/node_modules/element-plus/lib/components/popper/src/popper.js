'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const Effect = {
  LIGHT: "light",
  DARK: "dark"
};
const roleTypes = [
  "dialog",
  "grid",
  "group",
  "listbox",
  "menu",
  "navigation",
  "tooltip",
  "tree"
];
const popperProps = runtime.buildProps({
  role: {
    type: String,
    values: roleTypes,
    default: "tooltip"
  }
});
const usePopperProps = popperProps;

exports.Effect = Effect;
exports.popperProps = popperProps;
exports.roleTypes = roleTypes;
exports.usePopperProps = usePopperProps;
//# sourceMappingURL=popper.js.map
