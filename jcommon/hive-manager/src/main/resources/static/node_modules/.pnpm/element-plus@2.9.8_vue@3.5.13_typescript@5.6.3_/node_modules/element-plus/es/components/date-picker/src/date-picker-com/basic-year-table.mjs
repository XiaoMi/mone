import { defineComponent, ref, computed, watch, nextTick, openBlock, createElementBlock, unref, normalizeClass, createElementVNode, Fragment, renderList, withKeys, withModifiers, createVNode } from 'vue';
import dayjs from 'dayjs';
import { basicYearTableProps } from '../props/basic-year-table.mjs';
import { getValidDateOfYear } from '../utils.mjs';
import ElDatePickerCell from './basic-cell-render.mjs';
import _export_sfc from '../../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';
import { useLocale } from '../../../../hooks/use-locale/index.mjs';
import { castArray } from '../../../../utils/arrays.mjs';
import { rangeArr } from '../../../time-picker/src/utils.mjs';
import { hasClass } from '../../../../utils/dom/style.mjs';

const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "basic-year-table",
  props: basicYearTableProps,
  emits: ["changerange", "pick", "select"],
  setup(__props, { expose, emit }) {
    const props = __props;
    const datesInYear = (year, lang2) => {
      const firstDay = dayjs(String(year)).locale(lang2).startOf("year");
      const lastDay = firstDay.endOf("year");
      const numOfDays = lastDay.dayOfYear();
      return rangeArr(numOfDays).map((n) => firstDay.add(n, "day").toDate());
    };
    const ns = useNamespace("year-table");
    const { t, lang } = useLocale();
    const tbodyRef = ref();
    const currentCellRef = ref();
    const startYear = computed(() => {
      return Math.floor(props.date.year() / 10) * 10;
    });
    const tableRows = ref([[], [], []]);
    const lastRow = ref();
    const lastColumn = ref();
    const rows = computed(() => {
      var _a;
      const rows2 = tableRows.value;
      const now = dayjs().locale(lang.value).startOf("year");
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
          const calTime = dayjs().year(index);
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
      const today = dayjs().locale(lang.value);
      const year = cell.text;
      kls.disabled = props.disabledDate ? datesInYear(year, lang.value).every(props.disabledDate) : false;
      kls.today = today.year() === year;
      kls.current = castArray(props.parsedValue).findIndex((d) => d.year() === year) >= 0;
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
      return castArray(props.date).findIndex((date) => date.year() === year) >= 0;
    };
    const handleYearTableClick = (event) => {
      var _a;
      const target = (_a = event.target) == null ? void 0 : _a.closest("td");
      if (!target || !target.textContent || hasClass(target, "disabled"))
        return;
      const column = target.cellIndex;
      const row = target.parentNode.rowIndex;
      const selectedYear = row * 4 + column + startYear.value;
      const newDate = dayjs().year(selectedYear);
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
          emit("pick", castArray(props.parsedValue), false);
          return;
        }
        const vaildYear = getValidDateOfYear(newDate.startOf("year"), lang.value, props.disabledDate);
        const newValue = hasClass(target, "current") ? castArray(props.parsedValue).filter((d) => (d == null ? void 0 : d.year()) !== selectedYear) : castArray(props.parsedValue).concat([vaildYear]);
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
          endDate: dayjs().year(startYear.value).add(row * 4 + column, "year")
        });
      }
    };
    watch(() => props.date, async () => {
      var _a, _b;
      if ((_a = tbodyRef.value) == null ? void 0 : _a.contains(document.activeElement)) {
        await nextTick();
        (_b = currentCellRef.value) == null ? void 0 : _b.focus();
      }
    });
    expose({
      focus
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("table", {
        role: "grid",
        "aria-label": unref(t)("el.datepicker.yearTablePrompt"),
        class: normalizeClass(unref(ns).b()),
        onClick: handleYearTableClick,
        onMousemove: handleMouseMove
      }, [
        createElementVNode("tbody", {
          ref_key: "tbodyRef",
          ref: tbodyRef
        }, [
          (openBlock(true), createElementBlock(Fragment, null, renderList(unref(rows), (row, rowKey) => {
            return openBlock(), createElementBlock("tr", { key: rowKey }, [
              (openBlock(true), createElementBlock(Fragment, null, renderList(row, (cell, cellKey) => {
                return openBlock(), createElementBlock("td", {
                  key: `${rowKey}_${cellKey}`,
                  ref_for: true,
                  ref: (el) => isSelectedCell(cell) && (currentCellRef.value = el),
                  class: normalizeClass(["available", getCellKls(cell)]),
                  "aria-selected": isSelectedCell(cell),
                  "aria-label": String(cell.text),
                  tabindex: isSelectedCell(cell) ? 0 : -1,
                  onKeydown: [
                    withKeys(withModifiers(handleYearTableClick, ["prevent", "stop"]), ["space"]),
                    withKeys(withModifiers(handleYearTableClick, ["prevent", "stop"]), ["enter"])
                  ]
                }, [
                  createVNode(unref(ElDatePickerCell), { cell }, null, 8, ["cell"])
                ], 42, ["aria-selected", "aria-label", "tabindex", "onKeydown"]);
              }), 128))
            ]);
          }), 128))
        ], 512)
      ], 42, ["aria-label"]);
    };
  }
});
var YearTable = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "basic-year-table.vue"]]);

export { YearTable as default };
//# sourceMappingURL=basic-year-table.mjs.map
