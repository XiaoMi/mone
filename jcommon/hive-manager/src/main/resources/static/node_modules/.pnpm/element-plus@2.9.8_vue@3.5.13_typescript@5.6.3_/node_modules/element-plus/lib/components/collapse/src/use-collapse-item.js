'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('./constants.js');
var index$1 = require('../../../hooks/use-id/index.js');
var index = require('../../../hooks/use-namespace/index.js');

const useCollapseItem = (props) => {
  const collapse = vue.inject(constants.collapseContextKey);
  const { namespace } = index.useNamespace("collapse");
  const focusing = vue.ref(false);
  const isClick = vue.ref(false);
  const idInjection = index$1.useIdInjection();
  const id = vue.computed(() => idInjection.current++);
  const name = vue.computed(() => {
    var _a;
    return (_a = props.name) != null ? _a : `${namespace.value}-id-${idInjection.prefix}-${vue.unref(id)}`;
  });
  const isActive = vue.computed(() => collapse == null ? void 0 : collapse.activeNames.value.includes(vue.unref(name)));
  const handleFocus = () => {
    setTimeout(() => {
      if (!isClick.value) {
        focusing.value = true;
      } else {
        isClick.value = false;
      }
    }, 50);
  };
  const handleHeaderClick = () => {
    if (props.disabled)
      return;
    collapse == null ? void 0 : collapse.handleItemClick(vue.unref(name));
    focusing.value = false;
    isClick.value = true;
  };
  const handleEnterClick = () => {
    collapse == null ? void 0 : collapse.handleItemClick(vue.unref(name));
  };
  return {
    focusing,
    id,
    isActive,
    handleFocus,
    handleHeaderClick,
    handleEnterClick
  };
};
const useCollapseItemDOM = (props, { focusing, isActive, id }) => {
  const ns = index.useNamespace("collapse");
  const rootKls = vue.computed(() => [
    ns.b("item"),
    ns.is("active", vue.unref(isActive)),
    ns.is("disabled", props.disabled)
  ]);
  const headKls = vue.computed(() => [
    ns.be("item", "header"),
    ns.is("active", vue.unref(isActive)),
    { focusing: vue.unref(focusing) && !props.disabled }
  ]);
  const arrowKls = vue.computed(() => [
    ns.be("item", "arrow"),
    ns.is("active", vue.unref(isActive))
  ]);
  const itemWrapperKls = vue.computed(() => ns.be("item", "wrap"));
  const itemContentKls = vue.computed(() => ns.be("item", "content"));
  const scopedContentId = vue.computed(() => ns.b(`content-${vue.unref(id)}`));
  const scopedHeadId = vue.computed(() => ns.b(`head-${vue.unref(id)}`));
  return {
    arrowKls,
    headKls,
    rootKls,
    itemWrapperKls,
    itemContentKls,
    scopedContentId,
    scopedHeadId
  };
};

exports.useCollapseItem = useCollapseItem;
exports.useCollapseItemDOM = useCollapseItemDOM;
//# sourceMappingURL=use-collapse-item.js.map
