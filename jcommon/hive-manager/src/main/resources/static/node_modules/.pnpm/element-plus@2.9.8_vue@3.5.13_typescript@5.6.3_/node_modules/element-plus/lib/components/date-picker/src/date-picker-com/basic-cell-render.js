'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('../constants.js');
var basicCell = require('../props/basic-cell.js');
var index = require('../../../../hooks/use-namespace/index.js');

var ElDatePickerCell = vue.defineComponent({
  name: "ElDatePickerCell",
  props: basicCell.basicCellProps,
  setup(props) {
    const ns = index.useNamespace("date-table-cell");
    const {
      slots
    } = vue.inject(constants.ROOT_PICKER_INJECTION_KEY);
    return () => {
      const {
        cell
      } = props;
      return vue.renderSlot(slots, "default", {
        ...cell
      }, () => {
        var _a;
        return [vue.createVNode("div", {
          "class": ns.b()
        }, [vue.createVNode("span", {
          "class": ns.e("text")
        }, [(_a = cell == null ? void 0 : cell.renderText) != null ? _a : cell == null ? void 0 : cell.text])])];
      });
    };
  }
});

exports["default"] = ElDatePickerCell;
//# sourceMappingURL=basic-cell-render.js.map
