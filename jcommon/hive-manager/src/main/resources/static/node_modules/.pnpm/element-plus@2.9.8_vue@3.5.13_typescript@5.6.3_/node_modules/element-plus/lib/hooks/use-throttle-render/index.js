'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var types = require('../../utils/types.js');
var shared = require('@vue/shared');

const useThrottleRender = (loading, throttle = 0) => {
  if (throttle === 0)
    return loading;
  const initVal = shared.isObject(throttle) && Boolean(throttle.initVal);
  const throttled = vue.ref(initVal);
  let timeoutHandle = null;
  const dispatchThrottling = (timer) => {
    if (types.isUndefined(timer)) {
      throttled.value = loading.value;
      return;
    }
    if (timeoutHandle) {
      clearTimeout(timeoutHandle);
    }
    timeoutHandle = setTimeout(() => {
      throttled.value = loading.value;
    }, timer);
  };
  const dispatcher = (type) => {
    if (type === "leading") {
      if (types.isNumber(throttle)) {
        dispatchThrottling(throttle);
      } else {
        dispatchThrottling(throttle.leading);
      }
    } else {
      if (shared.isObject(throttle)) {
        dispatchThrottling(throttle.trailing);
      } else {
        throttled.value = false;
      }
    }
  };
  vue.onMounted(() => dispatcher("leading"));
  vue.watch(() => loading.value, (val) => {
    dispatcher(val ? "leading" : "trailing");
  });
  return throttled;
};

exports.useThrottleRender = useThrottleRender;
//# sourceMappingURL=index.js.map
