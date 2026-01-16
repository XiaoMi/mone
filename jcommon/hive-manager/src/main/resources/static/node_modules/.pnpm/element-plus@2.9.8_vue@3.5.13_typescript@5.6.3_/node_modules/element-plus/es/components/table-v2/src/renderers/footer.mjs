import { createVNode } from 'vue';

const Footer = (props, {
  slots
}) => {
  var _a;
  return createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [(_a = slots.default) == null ? void 0 : _a.call(slots)]);
};
Footer.displayName = "ElTableV2Footer";
var Footer$1 = Footer;

export { Footer$1 as default };
//# sourceMappingURL=footer.mjs.map
