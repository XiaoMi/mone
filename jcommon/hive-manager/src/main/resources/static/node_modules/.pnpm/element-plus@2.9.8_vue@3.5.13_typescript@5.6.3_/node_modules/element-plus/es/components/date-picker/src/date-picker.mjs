import { defineComponent, computed, provide, reactive, toRef, ref, createVNode, mergeProps } from 'vue';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat.js';
import advancedFormat from 'dayjs/plugin/advancedFormat.js';
import localeData from 'dayjs/plugin/localeData.js';
import weekOfYear from 'dayjs/plugin/weekOfYear.js';
import weekYear from 'dayjs/plugin/weekYear.js';
import dayOfYear from 'dayjs/plugin/dayOfYear.js';
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter.js';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore.js';
import '../../time-picker/index.mjs';
import { ROOT_PICKER_INJECTION_KEY } from './constants.mjs';
import { datePickerProps } from './props/date-picker.mjs';
import { getPanel } from './panel-utils.mjs';
import { DEFAULT_FORMATS_DATEPICKER, DEFAULT_FORMATS_DATE } from '../../time-picker/src/constants.mjs';
import CommonPicker from '../../time-picker/src/common/picker.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

dayjs.extend(localeData);
dayjs.extend(advancedFormat);
dayjs.extend(customParseFormat);
dayjs.extend(weekOfYear);
dayjs.extend(weekYear);
dayjs.extend(dayOfYear);
dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);
var DatePicker = defineComponent({
  name: "ElDatePicker",
  install: null,
  props: datePickerProps,
  emits: [UPDATE_MODEL_EVENT],
  setup(props, {
    expose,
    emit,
    slots
  }) {
    const ns = useNamespace("picker-panel");
    const isDefaultFormat = computed(() => {
      return !props.format;
    });
    provide("ElIsDefaultFormat", isDefaultFormat);
    provide("ElPopperOptions", reactive(toRef(props, "popperOptions")));
    provide(ROOT_PICKER_INJECTION_KEY, {
      slots,
      pickerNs: ns
    });
    const commonPicker = ref();
    const refProps = {
      focus: () => {
        var _a;
        (_a = commonPicker.value) == null ? void 0 : _a.focus();
      },
      blur: () => {
        var _a;
        (_a = commonPicker.value) == null ? void 0 : _a.blur();
      },
      handleOpen: () => {
        var _a;
        (_a = commonPicker.value) == null ? void 0 : _a.handleOpen();
      },
      handleClose: () => {
        var _a;
        (_a = commonPicker.value) == null ? void 0 : _a.handleClose();
      }
    };
    expose(refProps);
    const onModelValueUpdated = (val) => {
      emit(UPDATE_MODEL_EVENT, val);
    };
    return () => {
      var _a;
      const format = (_a = props.format) != null ? _a : DEFAULT_FORMATS_DATEPICKER[props.type] || DEFAULT_FORMATS_DATE;
      const Component = getPanel(props.type);
      return createVNode(CommonPicker, mergeProps(props, {
        "format": format,
        "type": props.type,
        "ref": commonPicker,
        "onUpdate:modelValue": onModelValueUpdated
      }), {
        default: (scopedProps) => createVNode(Component, scopedProps, {
          "prev-month": slots["prev-month"],
          "next-month": slots["next-month"],
          "prev-year": slots["prev-year"],
          "next-year": slots["next-year"]
        }),
        "range-separator": slots["range-separator"]
      });
    };
  }
});

export { DatePicker as default };
//# sourceMappingURL=date-picker.mjs.map
