'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const TableV2Cell = (props, {
  slots
}) => {
  var _a;
  const {
    cellData,
    style
  } = props;
  const displayText = ((_a = cellData == null ? void 0 : cellData.toString) == null ? void 0 : _a.call(cellData)) || "";
  const defaultSlot = vue.renderSlot(slots, "default", props, () => [displayText]);
  return vue.createVNode("div", {
    "class": props.class,
    "title": displayText,
    "style": style
  }, [defaultSlot]);
};
TableV2Cell.displayName = "ElTableV2Cell";
TableV2Cell.inheritAttrs = false;
var TableCell = TableV2Cell;

exports["default"] = TableCell;
//# sourceMappingURL=cell.js.map
