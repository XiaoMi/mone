'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const Footer = (props, {
  slots
}) => {
  var _a;
  return vue.createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [(_a = slots.default) == null ? void 0 : _a.call(slots)]);
};
Footer.displayName = "ElTableV2Footer";
var Footer$1 = Footer;

exports["default"] = Footer$1;
//# sourceMappingURL=footer.js.map
