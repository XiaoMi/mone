import { defineComponent, inject, renderSlot, createVNode } from 'vue';
import { ROOT_PICKER_INJECTION_KEY } from '../constants.mjs';
import { basicCellProps } from '../props/basic-cell.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';

var ElDatePickerCell = defineComponent({
  name: "ElDatePickerCell",
  props: basicCellProps,
  setup(props) {
    const ns = useNamespace("date-table-cell");
    const {
      slots
    } = inject(ROOT_PICKER_INJECTION_KEY);
    return () => {
      const {
        cell
      } = props;
      return renderSlot(slots, "default", {
        ...cell
      }, () => {
        var _a;
        return [createVNode("div", {
          "class": ns.b()
        }, [createVNode("span", {
          "class": ns.e("text")
        }, [(_a = cell == null ? void 0 : cell.renderText) != null ? _a : cell == null ? void 0 : cell.text])])];
      });
    };
  }
});

export { ElDatePickerCell as default };
//# sourceMappingURL=basic-cell-render.mjs.map
