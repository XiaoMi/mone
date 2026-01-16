import { defineComponent, createVNode } from 'vue';
import { autoResizerProps } from '../auto-resizer.mjs';
import { useAutoResize } from '../composables/use-auto-resize.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';

const AutoResizer = defineComponent({
  name: "ElAutoResizer",
  props: autoResizerProps,
  setup(props, {
    slots
  }) {
    const ns = useNamespace("auto-resizer");
    const {
      height,
      width,
      sizer
    } = useAutoResize(props);
    const style = {
      width: "100%",
      height: "100%"
    };
    return () => {
      var _a;
      return createVNode("div", {
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

export { AutoResizer$1 as default };
//# sourceMappingURL=auto-resizer.mjs.map
