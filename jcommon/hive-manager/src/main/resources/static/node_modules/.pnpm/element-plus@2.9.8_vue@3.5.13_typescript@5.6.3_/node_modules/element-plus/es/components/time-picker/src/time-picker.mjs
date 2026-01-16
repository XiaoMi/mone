import { defineComponent, ref, provide, createVNode, mergeProps } from 'vue';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat.js';
import { DEFAULT_FORMATS_TIME } from './constants.mjs';
import CommonPicker from './common/picker.mjs';
import TimePickPanel from './time-picker-com/panel-time-pick.mjs';
import TimeRangePanel from './time-picker-com/panel-time-range.mjs';
import { timePickerDefaultProps } from './common/props.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';

dayjs.extend(customParseFormat);
var TimePicker = defineComponent({
  name: "ElTimePicker",
  install: null,
  props: {
    ...timePickerDefaultProps,
    isRange: {
      type: Boolean,
      default: false
    }
  },
  emits: [UPDATE_MODEL_EVENT],
  setup(props, ctx) {
    const commonPicker = ref();
    const [type, Panel] = props.isRange ? ["timerange", TimeRangePanel] : ["time", TimePickPanel];
    const modelUpdater = (value) => ctx.emit(UPDATE_MODEL_EVENT, value);
    provide("ElPopperOptions", props.popperOptions);
    ctx.expose({
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
    });
    return () => {
      var _a;
      const format = (_a = props.format) != null ? _a : DEFAULT_FORMATS_TIME;
      return createVNode(CommonPicker, mergeProps(props, {
        "ref": commonPicker,
        "type": type,
        "format": format,
        "onUpdate:modelValue": modelUpdater
      }), {
        default: (props2) => createVNode(Panel, props2, null)
      });
    };
  }
});

export { TimePicker as default };
//# sourceMappingURL=time-picker.mjs.map
