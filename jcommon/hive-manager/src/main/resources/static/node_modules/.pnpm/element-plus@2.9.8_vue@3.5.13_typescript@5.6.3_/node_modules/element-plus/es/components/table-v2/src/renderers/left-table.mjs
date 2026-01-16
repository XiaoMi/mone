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
    leftTableRef,
    ...rest
  } = props;
  return createVNode(Table, mergeProps({
    "ref": leftTableRef
  }, rest), _isSlot(slots) ? slots : {
    default: () => [slots]
  });
};
var LeftTable$1 = LeftTable;

export { LeftTable$1 as default };
//# sourceMappingURL=left-table.mjs.map
