import { createVNode, mergeProps, isVNode } from 'vue';
import { tryCall } from '../utils.mjs';
import Row$1 from '../components/row.mjs';

function _isSlot(s) {
  return typeof s === "function" || Object.prototype.toString.call(s) === "[object Object]" && !isVNode(s);
}
const RowRenderer = (props, {
  slots
}) => {
  const {
    columns,
    columnsStyles,
    depthMap,
    expandColumnKey,
    expandedRowKeys,
    estimatedRowHeight,
    hasFixedColumns,
    rowData,
    rowIndex,
    style,
    isScrolling,
    rowProps,
    rowClass,
    rowKey,
    rowEventHandlers,
    ns,
    onRowHovered,
    onRowExpanded
  } = props;
  const rowKls = tryCall(rowClass, {
    columns,
    rowData,
    rowIndex
  }, "");
  const additionalProps = tryCall(rowProps, {
    columns,
    rowData,
    rowIndex
  });
  const _rowKey = rowData[rowKey];
  const depth = depthMap[_rowKey] || 0;
  const canExpand = Boolean(expandColumnKey);
  const isFixedRow = rowIndex < 0;
  const kls = [ns.e("row"), rowKls, {
    [ns.e(`row-depth-${depth}`)]: canExpand && rowIndex >= 0,
    [ns.is("expanded")]: canExpand && expandedRowKeys.includes(_rowKey),
    [ns.is("fixed")]: !depth && isFixedRow,
    [ns.is("customized")]: Boolean(slots.row)
  }];
  const onRowHover = hasFixedColumns ? onRowHovered : void 0;
  const _rowProps = {
    ...additionalProps,
    columns,
    columnsStyles,
    class: kls,
    depth,
    expandColumnKey,
    estimatedRowHeight: isFixedRow ? void 0 : estimatedRowHeight,
    isScrolling,
    rowIndex,
    rowData,
    rowKey: _rowKey,
    rowEventHandlers,
    style
  };
  const handlerMosueEnter = (e) => {
    onRowHover == null ? void 0 : onRowHover({
      hovered: true,
      rowKey: _rowKey,
      event: e,
      rowData,
      rowIndex
    });
  };
  const handlerMouseLeave = (e) => {
    onRowHover == null ? void 0 : onRowHover({
      hovered: false,
      rowKey: _rowKey,
      event: e,
      rowData,
      rowIndex
    });
  };
  return createVNode(Row$1, mergeProps(_rowProps, {
    "onRowExpand": onRowExpanded,
    "onMouseenter": handlerMosueEnter,
    "onMouseleave": handlerMouseLeave,
    "rowkey": _rowKey
  }), _isSlot(slots) ? slots : {
    default: () => [slots]
  });
};
var Row = RowRenderer;

export { Row as default };
//# sourceMappingURL=row.mjs.map
