'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../../empty/index.js');

const Footer = (props, {
  slots
}) => {
  const defaultSlot = vue.renderSlot(slots, "default", {}, () => [vue.createVNode(index.ElEmpty, null, null)]);
  return vue.createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [defaultSlot]);
};
Footer.displayName = "ElTableV2Empty";
var Empty = Footer;

exports["default"] = Empty;
//# sourceMappingURL=empty.js.map
