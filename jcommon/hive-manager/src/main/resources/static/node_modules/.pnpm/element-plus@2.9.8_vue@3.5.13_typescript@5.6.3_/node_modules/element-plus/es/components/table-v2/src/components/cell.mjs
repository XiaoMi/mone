import { renderSlot, createVNode } from 'vue';

const TableV2Cell = (props, {
  slots
}) => {
  var _a;
  const {
    cellData,
    style
  } = props;
  const displayText = ((_a = cellData == null ? void 0 : cellData.toString) == null ? void 0 : _a.call(cellData)) || "";
  const defaultSlot = renderSlot(slots, "default", props, () => [displayText]);
  return createVNode("div", {
    "class": props.class,
    "title": displayText,
    "style": style
  }, [defaultSlot]);
};
TableV2Cell.displayName = "ElTableV2Cell";
TableV2Cell.inheritAttrs = false;
var TableCell = TableV2Cell;

export { TableCell as default };
//# sourceMappingURL=cell.mjs.map
