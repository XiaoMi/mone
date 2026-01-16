'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var index = require('../../tooltip/index.js');
var shared = require('@vue/shared');
var error = require('../../../utils/error.js');
var types = require('../../../utils/types.js');
var objects = require('../../../utils/objects.js');

const getCell = function(event) {
  var _a;
  return (_a = event.target) == null ? void 0 : _a.closest("td");
};
const orderBy = function(array, sortKey, reverse, sortMethod, sortBy) {
  if (!sortKey && !sortMethod && (!sortBy || shared.isArray(sortBy) && !sortBy.length)) {
    return array;
  }
  if (shared.isString(reverse)) {
    reverse = reverse === "descending" ? -1 : 1;
  } else {
    reverse = reverse && reverse < 0 ? -1 : 1;
  }
  const getKey = sortMethod ? null : function(value, index) {
    if (sortBy) {
      if (!shared.isArray(sortBy)) {
        sortBy = [sortBy];
      }
      return sortBy.map((by) => {
        if (shared.isString(by)) {
          return lodashUnified.get(value, by);
        } else {
          return by(value, index, array);
        }
      });
    }
    if (sortKey !== "$key") {
      if (shared.isObject(value) && "$value" in value)
        value = value.$value;
    }
    return [shared.isObject(value) ? lodashUnified.get(value, sortKey) : value];
  };
  const compare = function(a, b) {
    if (sortMethod) {
      return sortMethod(a.value, b.value);
    }
    for (let i = 0, len = a.key.length; i < len; i++) {
      if (a.key[i] < b.key[i]) {
        return -1;
      }
      if (a.key[i] > b.key[i]) {
        return 1;
      }
    }
    return 0;
  };
  return array.map((value, index) => {
    return {
      value,
      index,
      key: getKey ? getKey(value, index) : null
    };
  }).sort((a, b) => {
    let order = compare(a, b);
    if (!order) {
      order = a.index - b.index;
    }
    return order * +reverse;
  }).map((item) => item.value);
};
const getColumnById = function(table, columnId) {
  let column = null;
  table.columns.forEach((item) => {
    if (item.id === columnId) {
      column = item;
    }
  });
  return column;
};
const getColumnByKey = function(table, columnKey) {
  let column = null;
  for (let i = 0; i < table.columns.length; i++) {
    const item = table.columns[i];
    if (item.columnKey === columnKey) {
      column = item;
      break;
    }
  }
  if (!column)
    error.throwError("ElTable", `No column matching with column-key: ${columnKey}`);
  return column;
};
const getColumnByCell = function(table, cell, namespace) {
  const matches = (cell.className || "").match(new RegExp(`${namespace}-table_[^\\s]+`, "gm"));
  if (matches) {
    return getColumnById(table, matches[0]);
  }
  return null;
};
const getRowIdentity = (row, rowKey) => {
  if (!row)
    throw new Error("Row is required when get row identity");
  if (shared.isString(rowKey)) {
    if (!rowKey.includes(".")) {
      return `${row[rowKey]}`;
    }
    const key = rowKey.split(".");
    let current = row;
    for (const element of key) {
      current = current[element];
    }
    return `${current}`;
  } else if (shared.isFunction(rowKey)) {
    return rowKey.call(null, row);
  }
};
const getKeysMap = function(array, rowKey, flatten = false, childrenKey = "children") {
  const data = array || [];
  const arrayMap = {};
  data.forEach((row, index) => {
    arrayMap[getRowIdentity(row, rowKey)] = { row, index };
    if (flatten) {
      const children = row[childrenKey];
      if (shared.isArray(children)) {
        Object.assign(arrayMap, getKeysMap(children, rowKey, true, childrenKey));
      }
    }
  });
  return arrayMap;
};
function mergeOptions(defaults, config) {
  const options = {};
  let key;
  for (key in defaults) {
    options[key] = defaults[key];
  }
  for (key in config) {
    if (shared.hasOwn(config, key)) {
      const value = config[key];
      if (!types.isUndefined(value)) {
        options[key] = value;
      }
    }
  }
  return options;
}
function parseWidth(width) {
  if (width === "")
    return width;
  if (!types.isUndefined(width)) {
    width = Number.parseInt(width, 10);
    if (Number.isNaN(width)) {
      width = "";
    }
  }
  return width;
}
function parseMinWidth(minWidth) {
  if (minWidth === "")
    return minWidth;
  if (!types.isUndefined(minWidth)) {
    minWidth = parseWidth(minWidth);
    if (Number.isNaN(minWidth)) {
      minWidth = 80;
    }
  }
  return minWidth;
}
function parseHeight(height) {
  if (types.isNumber(height)) {
    return height;
  }
  if (shared.isString(height)) {
    if (/^\d+(?:px)?$/.test(height)) {
      return Number.parseInt(height, 10);
    } else {
      return height;
    }
  }
  return null;
}
function compose(...funcs) {
  if (funcs.length === 0) {
    return (arg) => arg;
  }
  if (funcs.length === 1) {
    return funcs[0];
  }
  return funcs.reduce((a, b) => (...args) => a(b(...args)));
}
function toggleRowStatus(statusArr, row, newVal, tableTreeProps, selectable, rowIndex) {
  let _rowIndex = rowIndex != null ? rowIndex : 0;
  let changed = false;
  const index = statusArr.indexOf(row);
  const included = index !== -1;
  const isRowSelectable = selectable == null ? void 0 : selectable.call(null, row, _rowIndex);
  const toggleStatus = (type) => {
    if (type === "add") {
      statusArr.push(row);
    } else {
      statusArr.splice(index, 1);
    }
    changed = true;
  };
  const getChildrenCount = (row2) => {
    let count = 0;
    const children = (tableTreeProps == null ? void 0 : tableTreeProps.children) && row2[tableTreeProps.children];
    if (children && shared.isArray(children)) {
      count += children.length;
      children.forEach((item) => {
        count += getChildrenCount(item);
      });
    }
    return count;
  };
  if (!selectable || isRowSelectable) {
    if (types.isBoolean(newVal)) {
      if (newVal && !included) {
        toggleStatus("add");
      } else if (!newVal && included) {
        toggleStatus("remove");
      }
    } else {
      included ? toggleStatus("remove") : toggleStatus("add");
    }
  }
  if (!(tableTreeProps == null ? void 0 : tableTreeProps.checkStrictly) && (tableTreeProps == null ? void 0 : tableTreeProps.children) && shared.isArray(row[tableTreeProps.children])) {
    row[tableTreeProps.children].forEach((item) => {
      const childChanged = toggleRowStatus(statusArr, item, newVal != null ? newVal : !included, tableTreeProps, selectable, _rowIndex + 1);
      _rowIndex += getChildrenCount(item) + 1;
      if (childChanged) {
        changed = childChanged;
      }
    });
  }
  return changed;
}
function walkTreeNode(root, cb, childrenKey = "children", lazyKey = "hasChildren") {
  const isNil = (array) => !(shared.isArray(array) && array.length);
  function _walker(parent, children, level) {
    cb(parent, children, level);
    children.forEach((item) => {
      if (item[lazyKey]) {
        cb(item, null, level + 1);
        return;
      }
      const children2 = item[childrenKey];
      if (!isNil(children2)) {
        _walker(item, children2, level + 1);
      }
    });
  }
  root.forEach((item) => {
    if (item[lazyKey]) {
      cb(item, null, 0);
      return;
    }
    const children = item[childrenKey];
    if (!isNil(children)) {
      _walker(item, children, 0);
    }
  });
}
const getTableOverflowTooltipProps = (props, innerText, row, column) => {
  const popperOptions = {
    strategy: "fixed",
    ...props.popperOptions
  };
  const tooltipFormatterContent = shared.isFunction(column.tooltipFormatter) ? column.tooltipFormatter({
    row,
    column,
    cellValue: objects.getProp(row, column.property).value
  }) : void 0;
  if (vue.isVNode(tooltipFormatterContent)) {
    return {
      slotContent: tooltipFormatterContent,
      content: null,
      ...props,
      popperOptions
    };
  }
  return {
    slotContent: null,
    content: tooltipFormatterContent != null ? tooltipFormatterContent : innerText,
    ...props,
    popperOptions
  };
};
exports.removePopper = null;
function createTablePopper(props, popperContent, row, column, trigger, table) {
  const tableOverflowTooltipProps = getTableOverflowTooltipProps(props, popperContent, row, column);
  const mergedProps = {
    ...tableOverflowTooltipProps,
    slotContent: void 0
  };
  if ((exports.removePopper == null ? void 0 : exports.removePopper.trigger) === trigger) {
    const comp = exports.removePopper.vm.component;
    lodashUnified.merge(comp.props, mergedProps);
    if (tableOverflowTooltipProps.slotContent) {
      comp.slots.content = () => [tableOverflowTooltipProps.slotContent];
    }
    return;
  }
  exports.removePopper == null ? void 0 : exports.removePopper();
  const parentNode = table == null ? void 0 : table.refs.tableWrapper;
  const ns = parentNode == null ? void 0 : parentNode.dataset.prefix;
  const vm = vue.createVNode(index.ElTooltip, {
    virtualTriggering: true,
    virtualRef: trigger,
    appendTo: parentNode,
    placement: "top",
    transition: "none",
    offset: 0,
    hideAfter: 0,
    ...mergedProps
  }, tableOverflowTooltipProps.slotContent ? {
    content: () => tableOverflowTooltipProps.slotContent
  } : void 0);
  vm.appContext = { ...table.appContext, ...table };
  const container = document.createElement("div");
  vue.render(vm, container);
  vm.component.exposed.onOpen();
  const scrollContainer = parentNode == null ? void 0 : parentNode.querySelector(`.${ns}-scrollbar__wrap`);
  exports.removePopper = () => {
    vue.render(null, container);
    scrollContainer == null ? void 0 : scrollContainer.removeEventListener("scroll", exports.removePopper);
    exports.removePopper = null;
  };
  exports.removePopper.trigger = trigger;
  exports.removePopper.vm = vm;
  scrollContainer == null ? void 0 : scrollContainer.addEventListener("scroll", exports.removePopper);
}
function getCurrentColumns(column) {
  if (column.children) {
    return lodashUnified.flatMap(column.children, getCurrentColumns);
  } else {
    return [column];
  }
}
function getColSpan(colSpan, column) {
  return colSpan + column.colSpan;
}
const isFixedColumn = (index, fixed, store, realColumns) => {
  let start = 0;
  let after = index;
  const columns = store.states.columns.value;
  if (realColumns) {
    const curColumns = getCurrentColumns(realColumns[index]);
    const preColumns = columns.slice(0, columns.indexOf(curColumns[0]));
    start = preColumns.reduce(getColSpan, 0);
    after = start + curColumns.reduce(getColSpan, 0) - 1;
  } else {
    start = index;
  }
  let fixedLayout;
  switch (fixed) {
    case "left":
      if (after < store.states.fixedLeafColumnsLength.value) {
        fixedLayout = "left";
      }
      break;
    case "right":
      if (start >= columns.length - store.states.rightFixedLeafColumnsLength.value) {
        fixedLayout = "right";
      }
      break;
    default:
      if (after < store.states.fixedLeafColumnsLength.value) {
        fixedLayout = "left";
      } else if (start >= columns.length - store.states.rightFixedLeafColumnsLength.value) {
        fixedLayout = "right";
      }
  }
  return fixedLayout ? {
    direction: fixedLayout,
    start,
    after
  } : {};
};
const getFixedColumnsClass = (namespace, index, fixed, store, realColumns, offset = 0) => {
  const classes = [];
  const { direction, start, after } = isFixedColumn(index, fixed, store, realColumns);
  if (direction) {
    const isLeft = direction === "left";
    classes.push(`${namespace}-fixed-column--${direction}`);
    if (isLeft && after + offset === store.states.fixedLeafColumnsLength.value - 1) {
      classes.push("is-last-column");
    } else if (!isLeft && start - offset === store.states.columns.value.length - store.states.rightFixedLeafColumnsLength.value) {
      classes.push("is-first-column");
    }
  }
  return classes;
};
function getOffset(offset, column) {
  return offset + (lodashUnified.isNull(column.realWidth) || Number.isNaN(column.realWidth) ? Number(column.width) : column.realWidth);
}
const getFixedColumnOffset = (index, fixed, store, realColumns) => {
  const {
    direction,
    start = 0,
    after = 0
  } = isFixedColumn(index, fixed, store, realColumns);
  if (!direction) {
    return;
  }
  const styles = {};
  const isLeft = direction === "left";
  const columns = store.states.columns.value;
  if (isLeft) {
    styles.left = columns.slice(0, start).reduce(getOffset, 0);
  } else {
    styles.right = columns.slice(after + 1).reverse().reduce(getOffset, 0);
  }
  return styles;
};
const ensurePosition = (style, key) => {
  if (!style)
    return;
  if (!Number.isNaN(style[key])) {
    style[key] = `${style[key]}px`;
  }
};

exports.compose = compose;
exports.createTablePopper = createTablePopper;
exports.ensurePosition = ensurePosition;
exports.getCell = getCell;
exports.getColumnByCell = getColumnByCell;
exports.getColumnById = getColumnById;
exports.getColumnByKey = getColumnByKey;
exports.getFixedColumnOffset = getFixedColumnOffset;
exports.getFixedColumnsClass = getFixedColumnsClass;
exports.getKeysMap = getKeysMap;
exports.getRowIdentity = getRowIdentity;
exports.isFixedColumn = isFixedColumn;
exports.mergeOptions = mergeOptions;
exports.orderBy = orderBy;
exports.parseHeight = parseHeight;
exports.parseMinWidth = parseMinWidth;
exports.parseWidth = parseWidth;
exports.toggleRowStatus = toggleRowStatus;
exports.walkTreeNode = walkTreeNode;
//# sourceMappingURL=util.js.map
