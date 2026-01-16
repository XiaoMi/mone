import { createVNode, renderSlot, mergeProps } from 'vue';
import { get } from 'lodash-unified';
import { Alignment } from '../constants.mjs';
import { placeholderSign } from '../private.mjs';
import { enforceUnit, tryCall, componentToSlot } from '../utils.mjs';
import TableCell from '../components/cell.mjs';
import ExpandIcon from '../components/expand-icon.mjs';
import { isFunction, isObject } from '@vue/shared';

const CellRenderer = ({
  columns,
  column,
  columnIndex,
  depth,
  expandIconProps,
  isScrolling,
  rowData,
  rowIndex,
  style,
  expandedRowKeys,
  ns,
  cellProps: _cellProps,
  expandColumnKey,
  indentSize,
  iconSize,
  rowKey
}, {
  slots
}) => {
  const cellStyle = enforceUnit(style);
  if (column.placeholderSign === placeholderSign) {
    return createVNode("div", {
      "class": ns.em("row-cell", "placeholder"),
      "style": cellStyle
    }, null);
  }
  const {
    cellRenderer,
    dataKey,
    dataGetter
  } = column;
  const cellData = isFunction(dataGetter) ? dataGetter({
    columns,
    column,
    columnIndex,
    rowData,
    rowIndex
  }) : get(rowData, dataKey != null ? dataKey : "");
  const extraCellProps = tryCall(_cellProps, {
    cellData,
    columns,
    column,
    columnIndex,
    rowIndex,
    rowData
  });
  const cellProps = {
    class: ns.e("cell-text"),
    columns,
    column,
    columnIndex,
    cellData,
    isScrolling,
    rowData,
    rowIndex
  };
  const columnCellRenderer = componentToSlot(cellRenderer);
  const Cell = columnCellRenderer ? columnCellRenderer(cellProps) : renderSlot(slots, "default", cellProps, () => [createVNode(TableCell, cellProps, null)]);
  const kls = [ns.e("row-cell"), column.class, column.align === Alignment.CENTER && ns.is("align-center"), column.align === Alignment.RIGHT && ns.is("align-right")];
  const expandable = rowIndex >= 0 && expandColumnKey && column.key === expandColumnKey;
  const expanded = rowIndex >= 0 && expandedRowKeys.includes(rowData[rowKey]);
  let IconOrPlaceholder;
  const iconStyle = `margin-inline-start: ${depth * indentSize}px;`;
  if (expandable) {
    if (isObject(expandIconProps)) {
      IconOrPlaceholder = createVNode(ExpandIcon, mergeProps(expandIconProps, {
        "class": [ns.e("expand-icon"), ns.is("expanded", expanded)],
        "size": iconSize,
        "expanded": expanded,
        "style": iconStyle,
        "expandable": true
      }), null);
    } else {
      IconOrPlaceholder = createVNode("div", {
        "style": [iconStyle, `width: ${iconSize}px; height: ${iconSize}px;`].join(" ")
      }, null);
    }
  }
  return createVNode("div", mergeProps({
    "class": kls,
    "style": cellStyle
  }, extraCellProps, {
    "role": "cell"
  }), [IconOrPlaceholder, Cell]);
};
CellRenderer.inheritAttrs = false;
var Cell = CellRenderer;

export { Cell as default };
//# sourceMappingURL=cell.mjs.map
