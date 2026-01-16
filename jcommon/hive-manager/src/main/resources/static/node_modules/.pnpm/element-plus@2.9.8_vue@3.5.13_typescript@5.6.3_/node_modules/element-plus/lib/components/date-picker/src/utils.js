'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var dayjs = require('dayjs');
var shared = require('@vue/shared');
var utils = require('../../time-picker/src/utils.js');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

var dayjs__default = /*#__PURE__*/_interopDefaultLegacy(dayjs);

const isValidRange = (range) => {
  if (!shared.isArray(range))
    return false;
  const [left, right] = range;
  return dayjs__default["default"].isDayjs(left) && dayjs__default["default"].isDayjs(right) && dayjs__default["default"](left).isValid() && dayjs__default["default"](right).isValid() && left.isSameOrBefore(right);
};
const getDefaultValue = (defaultValue, { lang, unit, unlinkPanels }) => {
  let start;
  if (shared.isArray(defaultValue)) {
    let [left, right] = defaultValue.map((d) => dayjs__default["default"](d).locale(lang));
    if (!unlinkPanels) {
      right = left.add(1, unit);
    }
    return [left, right];
  } else if (defaultValue) {
    start = dayjs__default["default"](defaultValue);
  } else {
    start = dayjs__default["default"]();
  }
  start = start.locale(lang);
  return [start, start.add(1, unit)];
};
const buildPickerTable = (dimension, rows, {
  columnIndexOffset,
  startDate,
  nextEndDate,
  now,
  unit,
  relativeDateGetter,
  setCellMetadata,
  setRowMetadata
}) => {
  for (let rowIndex = 0; rowIndex < dimension.row; rowIndex++) {
    const row = rows[rowIndex];
    for (let columnIndex = 0; columnIndex < dimension.column; columnIndex++) {
      let cell = row[columnIndex + columnIndexOffset];
      if (!cell) {
        cell = {
          row: rowIndex,
          column: columnIndex,
          type: "normal",
          inRange: false,
          start: false,
          end: false
        };
      }
      const index = rowIndex * dimension.column + columnIndex;
      const nextStartDate = relativeDateGetter(index);
      cell.dayjs = nextStartDate;
      cell.date = nextStartDate.toDate();
      cell.timestamp = nextStartDate.valueOf();
      cell.type = "normal";
      cell.inRange = !!(startDate && nextStartDate.isSameOrAfter(startDate, unit) && nextEndDate && nextStartDate.isSameOrBefore(nextEndDate, unit)) || !!(startDate && nextStartDate.isSameOrBefore(startDate, unit) && nextEndDate && nextStartDate.isSameOrAfter(nextEndDate, unit));
      if (startDate == null ? void 0 : startDate.isSameOrAfter(nextEndDate)) {
        cell.start = !!nextEndDate && nextStartDate.isSame(nextEndDate, unit);
        cell.end = startDate && nextStartDate.isSame(startDate, unit);
      } else {
        cell.start = !!startDate && nextStartDate.isSame(startDate, unit);
        cell.end = !!nextEndDate && nextStartDate.isSame(nextEndDate, unit);
      }
      const isToday = nextStartDate.isSame(now, unit);
      if (isToday) {
        cell.type = "today";
      }
      setCellMetadata == null ? void 0 : setCellMetadata(cell, { rowIndex, columnIndex });
      row[columnIndex + columnIndexOffset] = cell;
    }
    setRowMetadata == null ? void 0 : setRowMetadata(row);
  }
};
const datesInMonth = (year, month, lang) => {
  const firstDay = dayjs__default["default"]().locale(lang).startOf("month").month(month).year(year);
  const numOfDays = firstDay.daysInMonth();
  return utils.rangeArr(numOfDays).map((n) => firstDay.add(n, "day").toDate());
};
const getValidDateOfMonth = (year, month, lang, disabledDate) => {
  const _value = dayjs__default["default"]().year(year).month(month).startOf("month");
  const _date = datesInMonth(year, month, lang).find((date) => {
    return !(disabledDate == null ? void 0 : disabledDate(date));
  });
  if (_date) {
    return dayjs__default["default"](_date).locale(lang);
  }
  return _value.locale(lang);
};
const getValidDateOfYear = (value, lang, disabledDate) => {
  const year = value.year();
  if (!(disabledDate == null ? void 0 : disabledDate(value.toDate()))) {
    return value.locale(lang);
  }
  const month = value.month();
  if (!datesInMonth(year, month, lang).every(disabledDate)) {
    return getValidDateOfMonth(year, month, lang, disabledDate);
  }
  for (let i = 0; i < 12; i++) {
    if (!datesInMonth(year, i, lang).every(disabledDate)) {
      return getValidDateOfMonth(year, i, lang, disabledDate);
    }
  }
  return value;
};
const correctlyParseUserInput = (value, format, lang, defaultFormat) => {
  if (shared.isArray(value)) {
    return value.map((v) => correctlyParseUserInput(v, format, lang, defaultFormat));
  }
  if (shared.isString(value)) {
    const dayjsValue = defaultFormat.value ? dayjs__default["default"](value) : dayjs__default["default"](value, format);
    if (!dayjsValue.isValid()) {
      return dayjsValue;
    }
  }
  return dayjs__default["default"](value, format).locale(lang);
};

exports.buildPickerTable = buildPickerTable;
exports.correctlyParseUserInput = correctlyParseUserInput;
exports.datesInMonth = datesInMonth;
exports.getDefaultValue = getDefaultValue;
exports.getValidDateOfMonth = getValidDateOfMonth;
exports.getValidDateOfYear = getValidDateOfYear;
exports.isValidRange = isValidRange;
//# sourceMappingURL=utils.js.map
