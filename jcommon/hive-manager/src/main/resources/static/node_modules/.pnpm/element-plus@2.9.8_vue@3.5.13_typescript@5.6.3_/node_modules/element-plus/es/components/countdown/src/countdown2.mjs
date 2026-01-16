import { defineComponent, ref, computed, onMounted, watch, onBeforeUnmount, openBlock, createBlock, unref, createSlots, renderList, withCtx, renderSlot } from 'vue';
import { ElStatistic } from '../../statistic/index.mjs';
import { countdownProps, countdownEmits } from './countdown.mjs';
import { formatTime, getTime } from './utils.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { rAF, cAF } from '../../../utils/raf.mjs';
import { CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElCountdown"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: countdownProps,
  emits: countdownEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    let timer;
    const rawValue = ref(0);
    const displayValue = computed(() => formatTime(rawValue.value, props.format));
    const formatter = (val) => formatTime(val, props.format);
    const stopTimer = () => {
      if (timer) {
        cAF(timer);
        timer = void 0;
      }
    };
    const startTimer = () => {
      const timestamp = getTime(props.value);
      const frameFunc = () => {
        let diff = timestamp - Date.now();
        emit(CHANGE_EVENT, diff);
        if (diff <= 0) {
          diff = 0;
          stopTimer();
          emit("finish");
        } else {
          timer = rAF(frameFunc);
        }
        rawValue.value = diff;
      };
      timer = rAF(frameFunc);
    };
    onMounted(() => {
      rawValue.value = getTime(props.value) - Date.now();
      watch(() => [props.value, props.format], () => {
        stopTimer();
        startTimer();
      }, {
        immediate: true
      });
    });
    onBeforeUnmount(() => {
      stopTimer();
    });
    expose({
      displayValue
    });
    return (_ctx, _cache) => {
      return openBlock(), createBlock(unref(ElStatistic), {
        value: rawValue.value,
        title: _ctx.title,
        prefix: _ctx.prefix,
        suffix: _ctx.suffix,
        "value-style": _ctx.valueStyle,
        formatter
      }, createSlots({
        _: 2
      }, [
        renderList(_ctx.$slots, (_, name) => {
          return {
            name,
            fn: withCtx(() => [
              renderSlot(_ctx.$slots, name)
            ])
          };
        })
      ]), 1032, ["value", "title", "prefix", "suffix", "value-style"]);
    };
  }
});
var Countdown = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "countdown.vue"]]);

export { Countdown as default };
//# sourceMappingURL=countdown2.mjs.map
