import { createVNode, renderSlot, mergeProps } from 'vue';
import { oppositeOrderMap, SortOrder, Alignment } from '../constants.mjs';
import { placeholderSign } from '../private.mjs';
import { enforceUnit, componentToSlot, tryCall } from '../utils.mjs';
import HeaderCell$1 from '../components/header-cell.mjs';
import SortIcon from '../components/sort-icon.mjs';

const HeaderCellRenderer = (props, {
  slots
}) => {
  const {
    column,
    ns,
    style,
    onColumnSorted
  } = props;
  const cellStyle = enforceUnit(style);
  if (column.placeholderSign === placeholderSign) {
    return createVNode("div", {
      "class": ns.em("header-row-cell", "placeholder"),
      "style": cellStyle
    }, null);
  }
  const {
    headerCellRenderer,
    headerClass,
    sortable
  } = column;
  const cellProps = {
    ...props,
    class: ns.e("header-cell-text")
  };
  const columnCellRenderer = componentToSlot(headerCellRenderer);
  const Cell = columnCellRenderer ? columnCellRenderer(cellProps) : renderSlot(slots, "default", cellProps, () => [createVNode(HeaderCell$1, cellProps, null)]);
  const {
    sortBy,
    sortState,
    headerCellProps
  } = props;
  let sorting, sortOrder;
  if (sortState) {
    const order = sortState[column.key];
    sorting = Boolean(oppositeOrderMap[order]);
    sortOrder = sorting ? order : SortOrder.ASC;
  } else {
    sorting = column.key === sortBy.key;
    sortOrder = sorting ? sortBy.order : SortOrder.ASC;
  }
  const cellKls = [ns.e("header-cell"), tryCall(headerClass, props, ""), column.align === Alignment.CENTER && ns.is("align-center"), column.align === Alignment.RIGHT && ns.is("align-right"), sortable && ns.is("sortable")];
  const cellWrapperProps = {
    ...tryCall(headerCellProps, props),
    onClick: column.sortable ? onColumnSorted : void 0,
    class: cellKls,
    style: cellStyle,
    ["data-key"]: column.key
  };
  return createVNode("div", mergeProps(cellWrapperProps, {
    "role": "columnheader"
  }), [Cell, sortable && createVNode(SortIcon, {
    "class": [ns.e("sort-icon"), sorting && ns.is("sorting")],
    "sortOrder": sortOrder
  }, null)]);
};
var HeaderCell = HeaderCellRenderer;

export { HeaderCell as default };
//# sourceMappingURL=header-cell.mjs.map
