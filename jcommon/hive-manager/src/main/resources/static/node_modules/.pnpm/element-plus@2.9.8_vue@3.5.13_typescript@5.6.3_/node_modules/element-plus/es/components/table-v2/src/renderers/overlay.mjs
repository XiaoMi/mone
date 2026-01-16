import { createVNode } from 'vue';

const Overlay = (props, {
  slots
}) => {
  var _a;
  return createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [(_a = slots.default) == null ? void 0 : _a.call(slots)]);
};
Overlay.displayName = "ElTableV2Overlay";
var Overlay$1 = Overlay;

export { Overlay$1 as default };
//# sourceMappingURL=overlay.mjs.map
