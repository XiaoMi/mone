import { createVNode, mergeProps, isVNode } from 'vue';
import Table from '../table-grid.mjs';

function _isSlot(s) {
  return typeof s === "function" || Object.prototype.toString.call(s) === "[object Object]" && !isVNode(s);
}
const MainTable = (props, {
  slots
}) => {
  const {
    mainTableRef,
    ...rest
  } = props;
  return createVNode(Table, mergeProps({
    "ref": mainTableRef
  }, rest), _isSlot(slots) ? slots : {
    default: () => [slots]
  });
};
var MainTable$1 = MainTable;

export { MainTable$1 as default };
//# sourceMappingURL=main-table.mjs.map
