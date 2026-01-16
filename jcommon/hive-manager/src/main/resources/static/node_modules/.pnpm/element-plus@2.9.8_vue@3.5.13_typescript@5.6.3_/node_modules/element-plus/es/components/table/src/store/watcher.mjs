import { getCurrentInstance, toRefs, ref, computed, watch, unref } from 'vue';
import { getKeysMap, getRowIdentity, toggleRowStatus, getColumnById, getColumnByKey, orderBy } from '../util.mjs';
import useExpand from './expand.mjs';
import useCurrent from './current.mjs';
import useTree from './tree.mjs';
import { hasOwn, isArray, isString } from '@vue/shared';

const sortData = (data, states) => {
  const sortingColumn = states.sortingColumn;
  if (!sortingColumn || isString(sortingColumn.sortable)) {
    return data;
  }
  return orderBy(data, states.sortProp, states.sortOrder, sortingColumn.sortMethod, sortingColumn.sortBy);
};
const doFlattenColumns = (columns) => {
  const result = [];
  columns.forEach((column) => {
    if (column.children && column.children.length > 0) {
      result.push.apply(result, doFlattenColumns(column.children));
    } else {
      result.push(column);
    }
  });
  return result;
};
function useWatcher() {
  var _a;
  const instance = getCurrentInstance();
  const { size: tableSize } = toRefs((_a = instance.proxy) == null ? void 0 : _a.$props);
  const rowKey = ref(null);
  const data = ref([]);
  const _data = ref([]);
  const isComplex = ref(false);
  const _columns = ref([]);
  const originColumns = ref([]);
  const columns = ref([]);
  const fixedColumns = ref([]);
  const rightFixedColumns = ref([]);
  const leafColumns = ref([]);
  const fixedLeafColumns = ref([]);
  const rightFixedLeafColumns = ref([]);
  const updateOrderFns = [];
  const leafColumnsLength = ref(0);
  const fixedLeafColumnsLength = ref(0);
  const rightFixedLeafColumnsLength = ref(0);
  const isAllSelected = ref(false);
  const selection = ref([]);
  const reserveSelection = ref(false);
  const selectOnIndeterminate = ref(false);
  const selectable = ref(null);
  const filters = ref({});
  const filteredData = ref(null);
  const sortingColumn = ref(null);
  const sortProp = ref(null);
  const sortOrder = ref(null);
  const hoverRow = ref(null);
  const selectedMap = computed(() => {
    return rowKey.value ? getKeysMap(selection.value, rowKey.value) : void 0;
  });
  watch(data, () => {
    var _a2;
    if (instance.state) {
      scheduleLayout(false);
      const needUpdateFixed = instance.props.tableLayout === "auto";
      if (needUpdateFixed) {
        (_a2 = instance.refs.tableHeaderRef) == null ? void 0 : _a2.updateFixedColumnStyle();
      }
    }
  }, {
    deep: true
  });
  const assertRowKey = () => {
    if (!rowKey.value)
      throw new Error("[ElTable] prop row-key is required");
  };
  const updateChildFixed = (column) => {
    var _a2;
    (_a2 = column.children) == null ? void 0 : _a2.forEach((childColumn) => {
      childColumn.fixed = column.fixed;
      updateChildFixed(childColumn);
    });
  };
  const updateColumns = () => {
    var _a2, _b;
    _columns.value.forEach((column) => {
      updateChildFixed(column);
    });
    fixedColumns.value = _columns.value.filter((column) => column.type !== "selection" && [true, "left"].includes(column.fixed));
    let selectColFixLeft;
    if (((_b = (_a2 = _columns.value) == null ? void 0 : _a2[0]) == null ? void 0 : _b.type) === "selection") {
      const selectColumn = _columns.value[0];
      selectColFixLeft = [true, "left"].includes(selectColumn.fixed) || fixedColumns.value.length && selectColumn.fixed !== "right";
      if (selectColFixLeft) {
        fixedColumns.value.unshift(selectColumn);
      }
    }
    rightFixedColumns.value = _columns.value.filter((column) => column.fixed === "right");
    const notFixedColumns = _columns.value.filter((column) => (selectColFixLeft ? column.type !== "selection" : true) && !column.fixed);
    originColumns.value = [].concat(fixedColumns.value).concat(notFixedColumns).concat(rightFixedColumns.value);
    const leafColumns2 = doFlattenColumns(notFixedColumns);
    const fixedLeafColumns2 = doFlattenColumns(fixedColumns.value);
    const rightFixedLeafColumns2 = doFlattenColumns(rightFixedColumns.value);
    leafColumnsLength.value = leafColumns2.length;
    fixedLeafColumnsLength.value = fixedLeafColumns2.length;
    rightFixedLeafColumnsLength.value = rightFixedLeafColumns2.length;
    columns.value = [].concat(fixedLeafColumns2).concat(leafColumns2).concat(rightFixedLeafColumns2);
    isComplex.value = fixedColumns.value.length > 0 || rightFixedColumns.value.length > 0;
  };
  const scheduleLayout = (needUpdateColumns, immediate = false) => {
    if (needUpdateColumns) {
      updateColumns();
    }
    if (immediate) {
      instance.state.doLayout();
    } else {
      instance.state.debouncedUpdateLayout();
    }
  };
  const isSelected = (row) => {
    if (selectedMap.value) {
      return !!selectedMap.value[getRowIdentity(row, rowKey.value)];
    } else {
      return selection.value.includes(row);
    }
  };
  const clearSelection = () => {
    isAllSelected.value = false;
    const oldSelection = selection.value;
    selection.value = [];
    if (oldSelection.length) {
      instance.emit("selection-change", []);
    }
  };
  const cleanSelection = () => {
    var _a2, _b;
    let deleted;
    if (rowKey.value) {
      deleted = [];
      const childrenKey = (_b = (_a2 = instance == null ? void 0 : instance.store) == null ? void 0 : _a2.states) == null ? void 0 : _b.childrenColumnName.value;
      const dataMap = getKeysMap(data.value, rowKey.value, true, childrenKey);
      for (const key in selectedMap.value) {
        if (hasOwn(selectedMap.value, key) && !dataMap[key]) {
          deleted.push(selectedMap.value[key].row);
        }
      }
    } else {
      deleted = selection.value.filter((item) => !data.value.includes(item));
    }
    if (deleted.length) {
      const newSelection = selection.value.filter((item) => !deleted.includes(item));
      selection.value = newSelection;
      instance.emit("selection-change", newSelection.slice());
    }
  };
  const getSelectionRows = () => {
    return (selection.value || []).slice();
  };
  const toggleRowSelection = (row, selected, emitChange = true, ignoreSelectable = false) => {
    var _a2, _b, _c, _d;
    const treeProps = {
      children: (_b = (_a2 = instance == null ? void 0 : instance.store) == null ? void 0 : _a2.states) == null ? void 0 : _b.childrenColumnName.value,
      checkStrictly: (_d = (_c = instance == null ? void 0 : instance.store) == null ? void 0 : _c.states) == null ? void 0 : _d.checkStrictly.value
    };
    const changed = toggleRowStatus(selection.value, row, selected, treeProps, ignoreSelectable ? void 0 : selectable.value, data.value.indexOf(row));
    if (changed) {
      const newSelection = (selection.value || []).slice();
      if (emitChange) {
        instance.emit("select", newSelection, row);
      }
      instance.emit("selection-change", newSelection);
    }
  };
  const _toggleAllSelection = () => {
    var _a2, _b;
    const value = selectOnIndeterminate.value ? !isAllSelected.value : !(isAllSelected.value || selection.value.length);
    isAllSelected.value = value;
    let selectionChanged = false;
    let childrenCount = 0;
    const rowKey2 = (_b = (_a2 = instance == null ? void 0 : instance.store) == null ? void 0 : _a2.states) == null ? void 0 : _b.rowKey.value;
    const { childrenColumnName } = instance.store.states;
    const treeProps = {
      children: childrenColumnName.value,
      checkStrictly: false
    };
    data.value.forEach((row, index) => {
      const rowIndex = index + childrenCount;
      if (toggleRowStatus(selection.value, row, value, treeProps, selectable.value, rowIndex)) {
        selectionChanged = true;
      }
      childrenCount += getChildrenCount(getRowIdentity(row, rowKey2));
    });
    if (selectionChanged) {
      instance.emit("selection-change", selection.value ? selection.value.slice() : []);
    }
    instance.emit("select-all", (selection.value || []).slice());
  };
  const updateSelectionByRowKey = () => {
    data.value.forEach((row) => {
      const rowId = getRowIdentity(row, rowKey.value);
      const rowInfo = selectedMap.value[rowId];
      if (rowInfo) {
        selection.value[rowInfo.index] = row;
      }
    });
  };
  const updateAllSelected = () => {
    var _a2;
    if (((_a2 = data.value) == null ? void 0 : _a2.length) === 0) {
      isAllSelected.value = false;
      return;
    }
    const { childrenColumnName } = instance.store.states;
    let rowIndex = 0;
    let selectedCount = 0;
    const checkSelectedStatus = (data2) => {
      var _a3;
      for (const row of data2) {
        const isRowSelectable = selectable.value && selectable.value.call(null, row, rowIndex);
        if (!isSelected(row)) {
          if (!selectable.value || isRowSelectable) {
            return false;
          }
        } else {
          selectedCount++;
        }
        rowIndex++;
        if (((_a3 = row[childrenColumnName.value]) == null ? void 0 : _a3.length) && !checkSelectedStatus(row[childrenColumnName.value])) {
          return false;
        }
      }
      return true;
    };
    const isAllSelected_ = checkSelectedStatus(data.value || []);
    isAllSelected.value = selectedCount === 0 ? false : isAllSelected_;
  };
  const getChildrenCount = (rowKey2) => {
    var _a2;
    if (!instance || !instance.store)
      return 0;
    const { treeData } = instance.store.states;
    let count = 0;
    const children = (_a2 = treeData.value[rowKey2]) == null ? void 0 : _a2.children;
    if (children) {
      count += children.length;
      children.forEach((childKey) => {
        count += getChildrenCount(childKey);
      });
    }
    return count;
  };
  const updateFilters = (columns2, values) => {
    if (!isArray(columns2)) {
      columns2 = [columns2];
    }
    const filters_ = {};
    columns2.forEach((col) => {
      filters.value[col.id] = values;
      filters_[col.columnKey || col.id] = values;
    });
    return filters_;
  };
  const updateSort = (column, prop, order) => {
    if (sortingColumn.value && sortingColumn.value !== column) {
      sortingColumn.value.order = null;
    }
    sortingColumn.value = column;
    sortProp.value = prop;
    sortOrder.value = order;
  };
  const execFilter = () => {
    let sourceData = unref(_data);
    Object.keys(filters.value).forEach((columnId) => {
      const values = filters.value[columnId];
      if (!values || values.length === 0)
        return;
      const column = getColumnById({
        columns: columns.value
      }, columnId);
      if (column && column.filterMethod) {
        sourceData = sourceData.filter((row) => {
          return values.some((value) => column.filterMethod.call(null, value, row, column));
        });
      }
    });
    filteredData.value = sourceData;
  };
  const execSort = () => {
    data.value = sortData(filteredData.value, {
      sortingColumn: sortingColumn.value,
      sortProp: sortProp.value,
      sortOrder: sortOrder.value
    });
  };
  const execQuery = (ignore = void 0) => {
    if (!(ignore && ignore.filter)) {
      execFilter();
    }
    execSort();
  };
  const clearFilter = (columnKeys) => {
    const { tableHeaderRef } = instance.refs;
    if (!tableHeaderRef)
      return;
    const panels = Object.assign({}, tableHeaderRef.filterPanels);
    const keys = Object.keys(panels);
    if (!keys.length)
      return;
    if (isString(columnKeys)) {
      columnKeys = [columnKeys];
    }
    if (isArray(columnKeys)) {
      const columns_ = columnKeys.map((key) => getColumnByKey({
        columns: columns.value
      }, key));
      keys.forEach((key) => {
        const column = columns_.find((col) => col.id === key);
        if (column) {
          column.filteredValue = [];
        }
      });
      instance.store.commit("filterChange", {
        column: columns_,
        values: [],
        silent: true,
        multi: true
      });
    } else {
      keys.forEach((key) => {
        const column = columns.value.find((col) => col.id === key);
        if (column) {
          column.filteredValue = [];
        }
      });
      filters.value = {};
      instance.store.commit("filterChange", {
        column: {},
        values: [],
        silent: true
      });
    }
  };
  const clearSort = () => {
    if (!sortingColumn.value)
      return;
    updateSort(null, null, null);
    instance.store.commit("changeSortCondition", {
      silent: true
    });
  };
  const {
    setExpandRowKeys,
    toggleRowExpansion,
    updateExpandRows,
    states: expandStates,
    isRowExpanded
  } = useExpand({
    data,
    rowKey
  });
  const {
    updateTreeExpandKeys,
    toggleTreeExpansion,
    updateTreeData,
    updateKeyChildren,
    loadOrToggle,
    states: treeStates
  } = useTree({
    data,
    rowKey
  });
  const {
    updateCurrentRowData,
    updateCurrentRow,
    setCurrentRowKey,
    states: currentData
  } = useCurrent({
    data,
    rowKey
  });
  const setExpandRowKeysAdapter = (val) => {
    setExpandRowKeys(val);
    updateTreeExpandKeys(val);
  };
  const toggleRowExpansionAdapter = (row, expanded) => {
    const hasExpandColumn = columns.value.some(({ type }) => type === "expand");
    if (hasExpandColumn) {
      toggleRowExpansion(row, expanded);
    } else {
      toggleTreeExpansion(row, expanded);
    }
  };
  return {
    assertRowKey,
    updateColumns,
    scheduleLayout,
    isSelected,
    clearSelection,
    cleanSelection,
    getSelectionRows,
    toggleRowSelection,
    _toggleAllSelection,
    toggleAllSelection: null,
    updateSelectionByRowKey,
    updateAllSelected,
    updateFilters,
    updateCurrentRow,
    updateSort,
    execFilter,
    execSort,
    execQuery,
    clearFilter,
    clearSort,
    toggleRowExpansion,
    setExpandRowKeysAdapter,
    setCurrentRowKey,
    toggleRowExpansionAdapter,
    isRowExpanded,
    updateExpandRows,
    updateCurrentRowData,
    loadOrToggle,
    updateTreeData,
    updateKeyChildren,
    states: {
      tableSize,
      rowKey,
      data,
      _data,
      isComplex,
      _columns,
      originColumns,
      columns,
      fixedColumns,
      rightFixedColumns,
      leafColumns,
      fixedLeafColumns,
      rightFixedLeafColumns,
      updateOrderFns,
      leafColumnsLength,
      fixedLeafColumnsLength,
      rightFixedLeafColumnsLength,
      isAllSelected,
      selection,
      reserveSelection,
      selectOnIndeterminate,
      selectable,
      filters,
      filteredData,
      sortingColumn,
      sortProp,
      sortOrder,
      hoverRow,
      ...expandStates,
      ...treeStates,
      ...currentData
    }
  };
}

export { useWatcher as default };
//# sourceMappingURL=watcher.mjs.map
