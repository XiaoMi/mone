'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var autoResizer = require('../auto-resizer.js');
var useAutoResize = require('../composables/use-auto-resize.js');
var index = require('../../../../hooks/use-namespace/index.js');

const AutoResizer = vue.defineComponent({
  name: "ElAutoResizer",
  props: autoResizer.autoResizerProps,
  setup(props, {
    slots
  }) {
    const ns = index.useNamespace("auto-resizer");
    const {
      height,
      width,
      sizer
    } = useAutoResize.useAutoResize(props);
    const style = {
      width: "100%",
      height: "100%"
    };
    return () => {
      var _a;
      return vue.createVNode("div", {
        "ref": sizer,
        "class": ns.b(),
        "style": style
      }, [(_a = slots.default) == null ? void 0 : _a.call(slots, {
        height: height.value,
        width: width.value
      })]);
    };
  }
});
var AutoResizer$1 = AutoResizer;

exports["default"] = AutoResizer$1;
//# sourceMappingURL=auto-resizer.js.map
