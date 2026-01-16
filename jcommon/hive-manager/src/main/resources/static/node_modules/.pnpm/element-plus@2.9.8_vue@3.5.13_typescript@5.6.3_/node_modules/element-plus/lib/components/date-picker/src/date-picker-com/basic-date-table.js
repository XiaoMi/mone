'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var basicDateTable = require('../props/basic-date-table.js');
var useBasicDateTable = require('../composables/use-basic-date-table.js');
var basicCellRender = require('./basic-cell-render.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');

const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  __name: "basic-date-table",
  props: basicDateTable.basicDateTableProps,
  emits: basicDateTable.basicDateTableEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const {
      WEEKS,
      rows,
      tbodyRef,
      currentCellRef,
      focus,
      isCurrent,
      isWeekActive,
      isSelectedCell,
      handlePickDate,
      handleMouseUp,
      handleMouseDown,
      handleMouseMove,
      handleFocus
    } = useBasicDateTable.useBasicDateTable(props, emit);
    const { tableLabel, tableKls, weekLabel, getCellClasses, getRowKls, t } = useBasicDateTable.useBasicDateTableDOM(props, {
      isCurrent,
      isWeekActive
    });
    let isUnmounting = false;
    vue.onBeforeUnmount(() => {
      isUnmounting = true;
    });
    expose({
      focus
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("table", {
        "aria-label": vue.unref(tableLabel),
        class: vue.normalizeClass(vue.unref(tableKls)),
        cellspacing: "0",
        cellpadding: "0",
        role: "grid",
        onClick: vue.unref(handlePickDate),
        onMousemove: vue.unref(handleMouseMove),
        onMousedown: vue.withModifiers(vue.unref(handleMouseDown), ["prevent"]),
        onMouseup: vue.unref(handleMouseUp)
      }, [
        vue.createElementVNode("tbody", {
          ref_key: "tbodyRef",
          ref: tbodyRef
        }, [
          vue.createElementVNode("tr", null, [
            _ctx.showWeekNumber ? (vue.openBlock(), vue.createElementBlock("th", {
              key: 0,
              scope: "col"
            }, vue.toDisplayString(vue.unref(weekLabel)), 1)) : vue.createCommentVNode("v-if", true),
            (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(WEEKS), (week, key) => {
              return vue.openBlock(), vue.createElementBlock("th", {
                key,
                "aria-label": vue.unref(t)("el.datepicker.weeksFull." + week),
                scope: "col"
              }, vue.toDisplayString(vue.unref(t)("el.datepicker.weeks." + week)), 9, ["aria-label"]);
            }), 128))
          ]),
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(rows), (row, rowKey) => {
            return vue.openBlock(), vue.createElementBlock("tr", {
              key: rowKey,
              class: vue.normalizeClass(vue.unref(getRowKls)(row[1]))
            }, [
              (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(row, (cell, columnKey) => {
                return vue.openBlock(), vue.createElementBlock("td", {
                  key: `${rowKey}.${columnKey}`,
                  ref_for: true,
                  ref: (el) => !vue.unref(isUnmounting) && vue.unref(isSelectedCell)(cell) && (currentCellRef.value = el),
                  class: vue.normalizeClass(vue.unref(getCellClasses)(cell)),
                  "aria-current": cell.isCurrent ? "date" : void 0,
                  "aria-selected": cell.isCurrent,
                  tabindex: vue.unref(isSelectedCell)(cell) ? 0 : -1,
                  onFocus: vue.unref(handleFocus)
                }, [
                  vue.createVNode(vue.unref(basicCellRender["default"]), { cell }, null, 8, ["cell"])
                ], 42, ["aria-current", "aria-selected", "tabindex", "onFocus"]);
              }), 128))
            ], 2);
          }), 128))
        ], 512)
      ], 42, ["aria-label", "onClick", "onMousemove", "onMousedown", "onMouseup"]);
    };
  }
});
var DateTable = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "basic-date-table.vue"]]);

exports["default"] = DateTable;
//# sourceMappingURL=basic-date-table.js.map
