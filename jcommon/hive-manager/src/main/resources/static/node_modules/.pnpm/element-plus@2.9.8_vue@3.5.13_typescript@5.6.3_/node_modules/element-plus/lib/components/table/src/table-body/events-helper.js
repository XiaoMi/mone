'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var util = require('../util.js');
var tokens = require('../tokens.js');
var style = require('../../../../utils/dom/style.js');

function isGreaterThan(a, b, epsilon = 0.03) {
  return a - b > epsilon;
}
function useEvents(props) {
  const parent = vue.inject(tokens.TABLE_INJECTION_KEY);
  const tooltipContent = vue.ref("");
  const tooltipTrigger = vue.ref(vue.h("div"));
  const handleEvent = (event, row, name) => {
    var _a;
    const table = parent;
    const cell = util.getCell(event);
    let column;
    const namespace = (_a = table == null ? void 0 : table.vnode.el) == null ? void 0 : _a.dataset.prefix;
    if (cell) {
      column = util.getColumnByCell({
        columns: props.store.states.columns.value
      }, cell, namespace);
      if (column) {
        table == null ? void 0 : table.emit(`cell-${name}`, row, column, cell, event);
      }
    }
    table == null ? void 0 : table.emit(`row-${name}`, row, column, event);
  };
  const handleDoubleClick = (event, row) => {
    handleEvent(event, row, "dblclick");
  };
  const handleClick = (event, row) => {
    props.store.commit("setCurrentRow", row);
    handleEvent(event, row, "click");
  };
  const handleContextMenu = (event, row) => {
    handleEvent(event, row, "contextmenu");
  };
  const handleMouseEnter = lodashUnified.debounce((index) => {
    props.store.commit("setHoverRow", index);
  }, 30);
  const handleMouseLeave = lodashUnified.debounce(() => {
    props.store.commit("setHoverRow", null);
  }, 30);
  const getPadding = (el) => {
    const style = window.getComputedStyle(el, null);
    const paddingLeft = Number.parseInt(style.paddingLeft, 10) || 0;
    const paddingRight = Number.parseInt(style.paddingRight, 10) || 0;
    const paddingTop = Number.parseInt(style.paddingTop, 10) || 0;
    const paddingBottom = Number.parseInt(style.paddingBottom, 10) || 0;
    return {
      left: paddingLeft,
      right: paddingRight,
      top: paddingTop,
      bottom: paddingBottom
    };
  };
  const toggleRowClassByCell = (rowSpan, event, toggle) => {
    let node = event.target.parentNode;
    while (rowSpan > 1) {
      node = node == null ? void 0 : node.nextSibling;
      if (!node || node.nodeName !== "TR")
        break;
      toggle(node, "hover-row hover-fixed-row");
      rowSpan--;
    }
  };
  const handleCellMouseEnter = (event, row, tooltipOptions) => {
    var _a, _b, _c;
    const table = parent;
    const cell = util.getCell(event);
    const namespace = (_a = table == null ? void 0 : table.vnode.el) == null ? void 0 : _a.dataset.prefix;
    let column;
    if (cell) {
      column = util.getColumnByCell({
        columns: props.store.states.columns.value
      }, cell, namespace);
      if (cell.rowSpan > 1) {
        toggleRowClassByCell(cell.rowSpan, event, style.addClass);
      }
      const hoverState = table.hoverState = { cell, column, row };
      table == null ? void 0 : table.emit("cell-mouse-enter", hoverState.row, hoverState.column, hoverState.cell, event);
    }
    if (!tooltipOptions) {
      return;
    }
    const cellChild = event.target.querySelector(".cell");
    if (!(style.hasClass(cellChild, `${namespace}-tooltip`) && cellChild.childNodes.length)) {
      return;
    }
    const range = document.createRange();
    range.setStart(cellChild, 0);
    range.setEnd(cellChild, cellChild.childNodes.length);
    const { width: rangeWidth, height: rangeHeight } = range.getBoundingClientRect();
    const { width: cellChildWidth, height: cellChildHeight } = cellChild.getBoundingClientRect();
    const { top, left, right, bottom } = getPadding(cellChild);
    const horizontalPadding = left + right;
    const verticalPadding = top + bottom;
    if (isGreaterThan(rangeWidth + horizontalPadding, cellChildWidth) || isGreaterThan(rangeHeight + verticalPadding, cellChildHeight) || isGreaterThan(cellChild.scrollWidth, cellChildWidth)) {
      util.createTablePopper(tooltipOptions, cell.innerText || cell.textContent, row, column, cell, table);
    } else if (((_b = util.removePopper) == null ? void 0 : _b.trigger) === cell) {
      (_c = util.removePopper) == null ? void 0 : _c();
    }
  };
  const handleCellMouseLeave = (event) => {
    const cell = util.getCell(event);
    if (!cell)
      return;
    if (cell.rowSpan > 1) {
      toggleRowClassByCell(cell.rowSpan, event, style.removeClass);
    }
    const oldHoverState = parent == null ? void 0 : parent.hoverState;
    parent == null ? void 0 : parent.emit("cell-mouse-leave", oldHoverState == null ? void 0 : oldHoverState.row, oldHoverState == null ? void 0 : oldHoverState.column, oldHoverState == null ? void 0 : oldHoverState.cell, event);
  };
  return {
    handleDoubleClick,
    handleClick,
    handleContextMenu,
    handleMouseEnter,
    handleMouseLeave,
    handleCellMouseEnter,
    handleCellMouseLeave,
    tooltipContent,
    tooltipTrigger
  };
}

exports["default"] = useEvents;
//# sourceMappingURL=events-helper.js.map
