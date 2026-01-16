import { defineComponent, getCurrentInstance, inject, ref, reactive, watch, onMounted, nextTick, h } from 'vue';
import { ElCheckbox } from '../../../checkbox/index.mjs';
import FilterPanel from '../filter-panel.mjs';
import useLayoutObserver from '../layout-observer.mjs';
import { TABLE_INJECTION_KEY } from '../tokens.mjs';
import useEvent from './event-helper.mjs';
import useStyle from './style.helper.mjs';
import useUtils from './utils-helper.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';

var TableHeader = defineComponent({
  name: "ElTableHeader",
  components: {
    ElCheckbox
  },
  props: {
    fixed: {
      type: String,
      default: ""
    },
    store: {
      required: true,
      type: Object
    },
    border: Boolean,
    defaultSort: {
      type: Object,
      default: () => {
        return {
          prop: "",
          order: ""
        };
      }
    },
    appendFilterPanelTo: {
      type: String
    },
    allowDragLastColumn: {
      type: Boolean
    }
  },
  setup(props, { emit }) {
    const instance = getCurrentInstance();
    const parent = inject(TABLE_INJECTION_KEY);
    const ns = useNamespace("table");
    const filterPanels = ref({});
    const { onColumnsChange, onScrollableChange } = useLayoutObserver(parent);
    const isTableLayoutAuto = (parent == null ? void 0 : parent.props.tableLayout) === "auto";
    const saveIndexSelection = reactive(/* @__PURE__ */ new Map());
    const theadRef = ref();
    const updateFixedColumnStyle = () => {
      setTimeout(() => {
        if (saveIndexSelection.size > 0) {
          saveIndexSelection.forEach((column, key) => {
            const el = theadRef.value.querySelector(`.${key.replace(/\s/g, ".")}`);
            if (el) {
              const width = el.getBoundingClientRect().width;
              column.width = width;
            }
          });
          saveIndexSelection.clear();
        }
      });
    };
    watch(saveIndexSelection, updateFixedColumnStyle);
    onMounted(async () => {
      await nextTick();
      await nextTick();
      const { prop, order } = props.defaultSort;
      parent == null ? void 0 : parent.store.commit("sort", { prop, order, init: true });
      updateFixedColumnStyle();
    });
    const {
      handleHeaderClick,
      handleHeaderContextMenu,
      handleMouseDown,
      handleMouseMove,
      handleMouseOut,
      handleSortClick,
      handleFilterClick
    } = useEvent(props, emit);
    const {
      getHeaderRowStyle,
      getHeaderRowClass,
      getHeaderCellStyle,
      getHeaderCellClass
    } = useStyle(props);
    const { isGroup, toggleAllSelection, columnRows } = useUtils(props);
    instance.state = {
      onColumnsChange,
      onScrollableChange
    };
    instance.filterPanels = filterPanels;
    return {
      ns,
      filterPanels,
      onColumnsChange,
      onScrollableChange,
      columnRows,
      getHeaderRowClass,
      getHeaderRowStyle,
      getHeaderCellClass,
      getHeaderCellStyle,
      handleHeaderClick,
      handleHeaderContextMenu,
      handleMouseDown,
      handleMouseMove,
      handleMouseOut,
      handleSortClick,
      handleFilterClick,
      isGroup,
      toggleAllSelection,
      saveIndexSelection,
      isTableLayoutAuto,
      theadRef,
      updateFixedColumnStyle
    };
  },
  render() {
    const {
      ns,
      isGroup,
      columnRows,
      getHeaderCellStyle,
      getHeaderCellClass,
      getHeaderRowClass,
      getHeaderRowStyle,
      handleHeaderClick,
      handleHeaderContextMenu,
      handleMouseDown,
      handleMouseMove,
      handleSortClick,
      handleMouseOut,
      store,
      $parent,
      saveIndexSelection,
      isTableLayoutAuto
    } = this;
    let rowSpan = 1;
    return h("thead", {
      ref: "theadRef",
      class: { [ns.is("group")]: isGroup }
    }, columnRows.map((subColumns, rowIndex) => h("tr", {
      class: getHeaderRowClass(rowIndex),
      key: rowIndex,
      style: getHeaderRowStyle(rowIndex)
    }, subColumns.map((column, cellIndex) => {
      if (column.rowSpan > rowSpan) {
        rowSpan = column.rowSpan;
      }
      const _class = getHeaderCellClass(rowIndex, cellIndex, subColumns, column);
      if (isTableLayoutAuto && column.fixed) {
        saveIndexSelection.set(_class, column);
      }
      return h("th", {
        class: _class,
        colspan: column.colSpan,
        key: `${column.id}-thead`,
        rowspan: column.rowSpan,
        style: getHeaderCellStyle(rowIndex, cellIndex, subColumns, column),
        onClick: ($event) => {
          if ($event.currentTarget.classList.contains("noclick")) {
            return;
          }
          handleHeaderClick($event, column);
        },
        onContextmenu: ($event) => handleHeaderContextMenu($event, column),
        onMousedown: ($event) => handleMouseDown($event, column),
        onMousemove: ($event) => handleMouseMove($event, column),
        onMouseout: handleMouseOut
      }, [
        h("div", {
          class: [
            "cell",
            column.filteredValue && column.filteredValue.length > 0 ? "highlight" : ""
          ]
        }, [
          column.renderHeader ? column.renderHeader({
            column,
            $index: cellIndex,
            store,
            _self: $parent
          }) : column.label,
          column.sortable && h("span", {
            onClick: ($event) => handleSortClick($event, column),
            class: "caret-wrapper"
          }, [
            h("i", {
              onClick: ($event) => handleSortClick($event, column, "ascending"),
              class: "sort-caret ascending"
            }),
            h("i", {
              onClick: ($event) => handleSortClick($event, column, "descending"),
              class: "sort-caret descending"
            })
          ]),
          column.filterable && h(FilterPanel, {
            store,
            placement: column.filterPlacement || "bottom-start",
            appendTo: $parent.appendFilterPanelTo,
            column,
            upDataColumn: (key, value) => {
              column[key] = value;
            }
          }, {
            "filter-icon": () => column.renderFilterIcon ? column.renderFilterIcon({
              filterOpened: column.filterOpened
            }) : null
          })
        ])
      ]);
    }))));
  }
});

export { TableHeader as default };
//# sourceMappingURL=index.mjs.map
