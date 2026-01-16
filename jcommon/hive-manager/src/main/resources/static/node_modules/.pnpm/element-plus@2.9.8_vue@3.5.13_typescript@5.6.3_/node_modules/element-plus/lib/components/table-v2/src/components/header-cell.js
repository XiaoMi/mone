'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const HeaderCell = (props, {
  slots
}) => vue.renderSlot(slots, "default", props, () => {
  var _a, _b;
  return [vue.createVNode("div", {
    "class": props.class,
    "title": (_a = props.column) == null ? void 0 : _a.title
  }, [(_b = props.column) == null ? void 0 : _b.title])];
});
HeaderCell.displayName = "ElTableV2HeaderCell";
HeaderCell.inheritAttrs = false;
var HeaderCell$1 = HeaderCell;

exports["default"] = HeaderCell$1;
//# sourceMappingURL=header-cell.js.map
