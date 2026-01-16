'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var dayjs = require('dayjs');
var basicYearTable = require('../props/basic-year-table.js');
var utils$1 = require('../utils.js');
var basicCellRender = require('./basic-cell-render.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../../hooks/use-namespace/index.js');
var index$1 = require('../../../../hooks/use-locale/index.js');
var arrays = require('../../../../utils/arrays.js');
var utils = require('../../../time-picker/src/utils.js');
var style = require('../../../../utils/dom/style.js');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

var dayjs__default = /*#__PURE__*/_interopDefaultLegacy(dayjs);

const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  __name: "basic-year-table",
  props: basicYearTable.basicYearTableProps,
  emits: ["changerange", "pick", "select"],
  setup(__props, { expose, emit }) {
    const props = __props;
    const datesInYear = (year, lang2) => {
      const firstDay = dayjs__default["default"](String(year)).locale(lang2).startOf("year");
      const lastDay = firstDay.endOf("year");
      const numOfDays = lastDay.dayOfYear();
      return utils.rangeArr(numOfDays).map((n) => firstDay.add(n, "day").toDate());
    };
    const ns = index.useNamespace("year-table");
    const { t, lang } = index$1.useLocale();
    const tbodyRef = vue.ref();
    const currentCellRef = vue.ref();
    const startYear = vue.computed(() => {
      return Math.floor(props.date.year() / 10) * 10;
    });
    const tableRows = vue.ref([[], [], []]);
    const lastRow = vue.ref();
    const lastColumn = vue.ref();
    const rows = vue.computed(() => {
      var _a;
      const rows2 = tableRows.value;
      const now = dayjs__default["default"]().locale(lang.value).startOf("year");
      for (let i = 0; i < 3; i++) {
        const row = rows2[i];
        for (let j = 0; j < 4; j++) {
          if (i * 4 + j >= 10) {
            break;
          }
          let cell = row[j];
          if (!cell) {
            cell = {
              row: i,
              column: j,
              type: "normal",
              inRange: false,
              start: false,
              end: false,
              text: -1,
              disabled: false
            };
          }
          cell.type = "normal";
          const index = i * 4 + j + startYear.value;
          const calTime = dayjs__default["default"]().year(index);
          const calEndDate = props.rangeState.endDate || props.maxDate || props.rangeState.selecting && props.minDate || null;
          cell.inRange = !!(props.minDate && calTime.isSameOrAfter(props.minDate, "year") && calEndDate && calTime.isSameOrBefore(calEndDate, "year")) || !!(props.minDate && calTime.isSameOrBefore(props.minDate, "year") && calEndDate && calTime.isSameOrAfter(calEndDate, "year"));
          if ((_a = props.minDate) == null ? void 0 : _a.isSameOrAfter(calEndDate)) {
            cell.start = !!(calEndDate && calTime.isSame(calEndDate, "year"));
            cell.end = !!(props.minDate && calTime.isSame(props.minDate, "year"));
          } else {
            cell.start = !!(props.minDate && calTime.isSame(props.minDate, "year"));
            cell.end = !!(calEndDate && calTime.isSame(calEndDate, "year"));
          }
          const isToday = now.isSame(calTime);
          if (isToday) {
            cell.type = "today";
          }
          cell.text = index;
          const cellDate = calTime.toDate();
          cell.disabled = props.disabledDate && props.disabledDate(cellDate) || false;
          row[j] = cell;
        }
      }
      return rows2;
    });
    const focus = () => {
      var _a;
      (_a = currentCellRef.value) == null ? void 0 : _a.focus();
    };
    const getCellKls = (cell) => {
      const kls = {};
      const today = dayjs__default["default"]().locale(lang.value);
      const year = cell.text;
      kls.disabled = props.disabledDate ? datesInYear(year, lang.value).every(props.disabledDate) : false;
      kls.today = today.year() === year;
      kls.current = arrays.castArray(props.parsedValue).findIndex((d) => d.year() === year) >= 0;
      if (cell.inRange) {
        kls["in-range"] = true;
        if (cell.start) {
          kls["start-date"] = true;
        }
        if (cell.end) {
          kls["end-date"] = true;
        }
      }
      return kls;
    };
    const isSelectedCell = (cell) => {
      const year = cell.text;
      return arrays.castArray(props.date).findIndex((date) => date.year() === year) >= 0;
    };
    const handleYearTableClick = (event) => {
      var _a;
      const target = (_a = event.target) == null ? void 0 : _a.closest("td");
      if (!target || !target.textContent || style.hasClass(target, "disabled"))
        return;
      const column = target.cellIndex;
      const row = target.parentNode.rowIndex;
      const selectedYear = row * 4 + column + startYear.value;
      const newDate = dayjs__default["default"]().year(selectedYear);
      if (props.selectionMode === "range") {
        if (!props.rangeState.selecting) {
          emit("pick", { minDate: newDate, maxDate: null });
          emit("select", true);
        } else {
          if (props.minDate && newDate >= props.minDate) {
            emit("pick", { minDate: props.minDate, maxDate: newDate });
          } else {
            emit("pick", { minDate: newDate, maxDate: props.minDate });
          }
          emit("select", false);
        }
      } else if (props.selectionMode === "years") {
        if (event.type === "keydown") {
          emit("pick", arrays.castArray(props.parsedValue), false);
          return;
        }
        const vaildYear = utils$1.getValidDateOfYear(newDate.startOf("year"), lang.value, props.disabledDate);
        const newValue = style.hasClass(target, "current") ? arrays.castArray(props.parsedValue).filter((d) => (d == null ? void 0 : d.year()) !== selectedYear) : arrays.castArray(props.parsedValue).concat([vaildYear]);
        emit("pick", newValue);
      } else {
        emit("pick", selectedYear);
      }
    };
    const handleMouseMove = (event) => {
      var _a;
      if (!props.rangeState.selecting)
        return;
      const target = (_a = event.target) == null ? void 0 : _a.closest("td");
      if (!target)
        return;
      const row = target.parentNode.rowIndex;
      const column = target.cellIndex;
      if (rows.value[row][column].disabled)
        return;
      if (row !== lastRow.value || column !== lastColumn.value) {
        lastRow.value = row;
        lastColumn.value = column;
        emit("changerange", {
          selecting: true,
          endDate: dayjs__default["default"]().year(startYear.value).add(row * 4 + column, "year")
        });
      }
    };
    vue.watch(() => props.date, async () => {
      var _a, _b;
      if ((_a = tbodyRef.value) == null ? void 0 : _a.contains(document.activeElement)) {
        await vue.nextTick();
        (_b = currentCellRef.value) == null ? void 0 : _b.focus();
      }
    });
    expose({
      focus
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("table", {
        role: "grid",
        "aria-label": vue.unref(t)("el.datepicker.yearTablePrompt"),
        class: vue.normalizeClass(vue.unref(ns).b()),
        onClick: handleYearTableClick,
        onMousemove: handleMouseMove
      }, [
        vue.createElementVNode("tbody", {
          ref_key: "tbodyRef",
          ref: tbodyRef
        }, [
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(rows), (row, rowKey) => {
            return vue.openBlock(), vue.createElementBlock("tr", { key: rowKey }, [
              (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(row, (cell, cellKey) => {
                return vue.openBlock(), vue.createElementBlock("td", {
                  key: `${rowKey}_${cellKey}`,
                  ref_for: true,
                  ref: (el) => isSelectedCell(cell) && (currentCellRef.value = el),
                  class: vue.normalizeClass(["available", getCellKls(cell)]),
                  "aria-selected": isSelectedCell(cell),
                  "aria-label": String(cell.text),
                  tabindex: isSelectedCell(cell) ? 0 : -1,
                  onKeydown: [
                    vue.withKeys(vue.withModifiers(handleYearTableClick, ["prevent", "stop"]), ["space"]),
                    vue.withKeys(vue.withModifiers(handleYearTableClick, ["prevent", "stop"]), ["enter"])
                  ]
                }, [
                  vue.createVNode(vue.unref(basicCellRender["default"]), { cell }, null, 8, ["cell"])
                ], 42, ["aria-selected", "aria-label", "tabindex", "onKeydown"]);
              }), 128))
            ]);
          }), 128))
        ], 512)
      ], 42, ["aria-label"]);
    };
  }
});
var YearTable = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "basic-year-table.vue"]]);

exports["default"] = YearTable;
//# sourceMappingURL=basic-year-table.js.map
