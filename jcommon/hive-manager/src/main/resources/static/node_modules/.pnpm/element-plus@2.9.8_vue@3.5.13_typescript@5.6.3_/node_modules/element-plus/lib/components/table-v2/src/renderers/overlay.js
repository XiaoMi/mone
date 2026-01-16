'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const Overlay = (props, {
  slots
}) => {
  var _a;
  return vue.createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [(_a = slots.default) == null ? void 0 : _a.call(slots)]);
};
Overlay.displayName = "ElTableV2Overlay";
var Overlay$1 = Overlay;

exports["default"] = Overlay$1;
//# sourceMappingURL=overlay.js.map
