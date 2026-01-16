'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var dom = require('@floating-ui/dom');
var shared = require('@vue/shared');
var core = require('@vueuse/core');
var objects = require('../../../utils/objects.js');

const useTarget = (target, open, gap, mergedMask, scrollIntoViewOptions) => {
  const posInfo = vue.ref(null);
  const getTargetEl = () => {
    let targetEl;
    if (shared.isString(target.value)) {
      targetEl = document.querySelector(target.value);
    } else if (shared.isFunction(target.value)) {
      targetEl = target.value();
    } else {
      targetEl = target.value;
    }
    return targetEl;
  };
  const updatePosInfo = () => {
    const targetEl = getTargetEl();
    if (!targetEl || !open.value) {
      posInfo.value = null;
      return;
    }
    if (!isInViewPort(targetEl)) {
      targetEl.scrollIntoView(scrollIntoViewOptions.value);
    }
    const { left, top, width, height } = targetEl.getBoundingClientRect();
    posInfo.value = {
      left,
      top,
      width,
      height,
      radius: 0
    };
  };
  vue.onMounted(() => {
    vue.watch([open, target], () => {
      updatePosInfo();
    }, {
      immediate: true
    });
    window.addEventListener("resize", updatePosInfo);
  });
  vue.onBeforeUnmount(() => {
    window.removeEventListener("resize", updatePosInfo);
  });
  const getGapOffset = (index) => {
    var _a;
    return (_a = shared.isArray(gap.value.offset) ? gap.value.offset[index] : gap.value.offset) != null ? _a : 6;
  };
  const mergedPosInfo = vue.computed(() => {
    var _a;
    if (!posInfo.value)
      return posInfo.value;
    const gapOffsetX = getGapOffset(0);
    const gapOffsetY = getGapOffset(1);
    const gapRadius = ((_a = gap.value) == null ? void 0 : _a.radius) || 2;
    return {
      left: posInfo.value.left - gapOffsetX,
      top: posInfo.value.top - gapOffsetY,
      width: posInfo.value.width + gapOffsetX * 2,
      height: posInfo.value.height + gapOffsetY * 2,
      radius: gapRadius
    };
  });
  const triggerTarget = vue.computed(() => {
    const targetEl = getTargetEl();
    if (!mergedMask.value || !targetEl || !window.DOMRect) {
      return targetEl || void 0;
    }
    return {
      getBoundingClientRect() {
        var _a, _b, _c, _d;
        return window.DOMRect.fromRect({
          width: ((_a = mergedPosInfo.value) == null ? void 0 : _a.width) || 0,
          height: ((_b = mergedPosInfo.value) == null ? void 0 : _b.height) || 0,
          x: ((_c = mergedPosInfo.value) == null ? void 0 : _c.left) || 0,
          y: ((_d = mergedPosInfo.value) == null ? void 0 : _d.top) || 0
        });
      }
    };
  });
  return {
    mergedPosInfo,
    triggerTarget
  };
};
const tourKey = Symbol("ElTour");
function isInViewPort(element) {
  const viewWidth = window.innerWidth || document.documentElement.clientWidth;
  const viewHeight = window.innerHeight || document.documentElement.clientHeight;
  const { top, right, bottom, left } = element.getBoundingClientRect();
  return top >= 0 && left >= 0 && right <= viewWidth && bottom <= viewHeight;
}
const useFloating = (referenceRef, contentRef, arrowRef, placement, strategy, offset, zIndex, showArrow) => {
  const x = vue.ref();
  const y = vue.ref();
  const middlewareData = vue.ref({});
  const states = {
    x,
    y,
    placement,
    strategy,
    middlewareData
  };
  const middleware = vue.computed(() => {
    const _middleware = [
      dom.offset(vue.unref(offset)),
      dom.flip(),
      dom.shift(),
      overflowMiddleware()
    ];
    if (vue.unref(showArrow) && vue.unref(arrowRef)) {
      _middleware.push(dom.arrow({
        element: vue.unref(arrowRef)
      }));
    }
    return _middleware;
  });
  const update = async () => {
    if (!core.isClient)
      return;
    const referenceEl = vue.unref(referenceRef);
    const contentEl = vue.unref(contentRef);
    if (!referenceEl || !contentEl)
      return;
    const data = await dom.computePosition(referenceEl, contentEl, {
      placement: vue.unref(placement),
      strategy: vue.unref(strategy),
      middleware: vue.unref(middleware)
    });
    objects.keysOf(states).forEach((key) => {
      states[key].value = data[key];
    });
  };
  const contentStyle = vue.computed(() => {
    if (!vue.unref(referenceRef)) {
      return {
        position: "fixed",
        top: "50%",
        left: "50%",
        transform: "translate3d(-50%, -50%, 0)",
        maxWidth: "100vw",
        zIndex: vue.unref(zIndex)
      };
    }
    const { overflow } = vue.unref(middlewareData);
    return {
      position: vue.unref(strategy),
      zIndex: vue.unref(zIndex),
      top: vue.unref(y) != null ? `${vue.unref(y)}px` : "",
      left: vue.unref(x) != null ? `${vue.unref(x)}px` : "",
      maxWidth: (overflow == null ? void 0 : overflow.maxWidth) ? `${overflow == null ? void 0 : overflow.maxWidth}px` : ""
    };
  });
  const arrowStyle = vue.computed(() => {
    if (!vue.unref(showArrow))
      return {};
    const { arrow: arrow2 } = vue.unref(middlewareData);
    return {
      left: (arrow2 == null ? void 0 : arrow2.x) != null ? `${arrow2 == null ? void 0 : arrow2.x}px` : "",
      top: (arrow2 == null ? void 0 : arrow2.y) != null ? `${arrow2 == null ? void 0 : arrow2.y}px` : ""
    };
  });
  let cleanup;
  vue.onMounted(() => {
    const referenceEl = vue.unref(referenceRef);
    const contentEl = vue.unref(contentRef);
    if (referenceEl && contentEl) {
      cleanup = dom.autoUpdate(referenceEl, contentEl, update);
    }
    vue.watchEffect(() => {
      update();
    });
  });
  vue.onBeforeUnmount(() => {
    cleanup && cleanup();
  });
  return {
    update,
    contentStyle,
    arrowStyle
  };
};
const overflowMiddleware = () => {
  return {
    name: "overflow",
    async fn(state) {
      const overflow = await dom.detectOverflow(state);
      let overWidth = 0;
      if (overflow.left > 0)
        overWidth = overflow.left;
      if (overflow.right > 0)
        overWidth = overflow.right;
      const floatingWidth = state.rects.floating.width;
      return {
        data: {
          maxWidth: floatingWidth - overWidth
        }
      };
    }
  };
};

exports.tourKey = tourKey;
exports.useFloating = useFloating;
exports.useTarget = useTarget;
//# sourceMappingURL=helper.js.map
