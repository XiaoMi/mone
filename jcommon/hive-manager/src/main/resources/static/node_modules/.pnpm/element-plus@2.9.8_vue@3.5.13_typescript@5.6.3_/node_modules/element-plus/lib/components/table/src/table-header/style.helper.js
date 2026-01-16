'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var util = require('../util.js');
var tokens = require('../tokens.js');
var index = require('../../../../hooks/use-namespace/index.js');
var shared = require('@vue/shared');

function useStyle(props) {
  const parent = vue.inject(tokens.TABLE_INJECTION_KEY);
  const ns = index.useNamespace("table");
  const getHeaderRowStyle = (rowIndex) => {
    const headerRowStyle = parent == null ? void 0 : parent.props.headerRowStyle;
    if (shared.isFunction(headerRowStyle)) {
      return headerRowStyle.call(null, { rowIndex });
    }
    return headerRowStyle;
  };
  const getHeaderRowClass = (rowIndex) => {
    const classes = [];
    const headerRowClassName = parent == null ? void 0 : parent.props.headerRowClassName;
    if (shared.isString(headerRowClassName)) {
      classes.push(headerRowClassName);
    } else if (shared.isFunction(headerRowClassName)) {
      classes.push(headerRowClassName.call(null, { rowIndex }));
    }
    return classes.join(" ");
  };
  const getHeaderCellStyle = (rowIndex, columnIndex, row, column) => {
    var _a;
    let headerCellStyles = (_a = parent == null ? void 0 : parent.props.headerCellStyle) != null ? _a : {};
    if (shared.isFunction(headerCellStyles)) {
      headerCellStyles = headerCellStyles.call(null, {
        rowIndex,
        columnIndex,
        row,
        column
      });
    }
    const fixedStyle = util.getFixedColumnOffset(columnIndex, column.fixed, props.store, row);
    util.ensurePosition(fixedStyle, "left");
    util.ensurePosition(fixedStyle, "right");
    return Object.assign({}, headerCellStyles, fixedStyle);
  };
  const getHeaderCellClass = (rowIndex, columnIndex, row, column) => {
    const fixedClasses = util.getFixedColumnsClass(ns.b(), columnIndex, column.fixed, props.store, row);
    const classes = [
      column.id,
      column.order,
      column.headerAlign,
      column.className,
      column.labelClassName,
      ...fixedClasses
    ];
    if (!column.children) {
      classes.push("is-leaf");
    }
    if (column.sortable) {
      classes.push("is-sortable");
    }
    const headerCellClassName = parent == null ? void 0 : parent.props.headerCellClassName;
    if (shared.isString(headerCellClassName)) {
      classes.push(headerCellClassName);
    } else if (shared.isFunction(headerCellClassName)) {
      classes.push(headerCellClassName.call(null, {
        rowIndex,
        columnIndex,
        row,
        column
      }));
    }
    classes.push(ns.e("cell"));
    return classes.filter((className) => Boolean(className)).join(" ");
  };
  return {
    getHeaderRowStyle,
    getHeaderRowClass,
    getHeaderCellStyle,
    getHeaderCellClass
  };
}

exports["default"] = useStyle;
//# sourceMappingURL=style.helper.js.map
