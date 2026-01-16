import { createVNode, mergeProps, isVNode } from 'vue';
import Table from '../table-grid.mjs';

function _isSlot(s) {
  return typeof s === "function" || Object.prototype.toString.call(s) === "[object Object]" && !isVNode(s);
}
const LeftTable = (props, {
  slots
}) => {
  if (!props.columns.length)
    return;
  const {
    rightTableRef,
    ...rest
  } = props;
  return createVNode(Table, mergeProps({
    "ref": rightTableRef
  }, rest), _isSlot(slots) ? slots : {
    default: () => [slots]
  });
};
var RightTable = LeftTable;

export { RightTable as default };
//# sourceMappingURL=right-table.mjs.map
