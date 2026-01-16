import { renderSlot, createVNode } from 'vue';
import { ElEmpty } from '../../../empty/index.mjs';

const Footer = (props, {
  slots
}) => {
  const defaultSlot = renderSlot(slots, "default", {}, () => [createVNode(ElEmpty, null, null)]);
  return createVNode("div", {
    "class": props.class,
    "style": props.style
  }, [defaultSlot]);
};
Footer.displayName = "ElTableV2Empty";
var Empty = Footer;

export { Empty as default };
//# sourceMappingURL=empty.mjs.map
