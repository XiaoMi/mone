'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var token = require('./token.js');
var vnode = require('../../../utils/vue/vnode.js');
var style = require('../../../utils/dom/style.js');
var index = require('../../../hooks/use-namespace/index.js');

var ElDescriptionsCell = vue.defineComponent({
  name: "ElDescriptionsCell",
  props: {
    cell: {
      type: Object
    },
    tag: {
      type: String,
      default: "td"
    },
    type: {
      type: String
    }
  },
  setup() {
    const descriptions = vue.inject(token.descriptionsKey, {});
    return {
      descriptions
    };
  },
  render() {
    var _a;
    const item = vnode.getNormalizedProps(this.cell);
    const directives = (((_a = this.cell) == null ? void 0 : _a.dirs) || []).map((dire) => {
      const { dir, arg, modifiers, value } = dire;
      return [dir, value, arg, modifiers];
    });
    const { border, direction } = this.descriptions;
    const isVertical = direction === "vertical";
    const renderLabel = () => {
      var _a2, _b, _c;
      return ((_c = (_b = (_a2 = this.cell) == null ? void 0 : _a2.children) == null ? void 0 : _b.label) == null ? void 0 : _c.call(_b)) || item.label;
    };
    const renderContent = () => {
      var _a2, _b, _c;
      return (_c = (_b = (_a2 = this.cell) == null ? void 0 : _a2.children) == null ? void 0 : _b.default) == null ? void 0 : _c.call(_b);
    };
    const span = item.span;
    const rowspan = item.rowspan;
    const align = item.align ? `is-${item.align}` : "";
    const labelAlign = item.labelAlign ? `is-${item.labelAlign}` : align;
    const className = item.className;
    const labelClassName = item.labelClassName;
    const width = this.type === "label" ? item.labelWidth || this.descriptions.labelWidth || item.width : item.width;
    const style$1 = {
      width: style.addUnit(width),
      minWidth: style.addUnit(item.minWidth)
    };
    const ns = index.useNamespace("descriptions");
    switch (this.type) {
      case "label":
        return vue.withDirectives(vue.h(this.tag, {
          style: style$1,
          class: [
            ns.e("cell"),
            ns.e("label"),
            ns.is("bordered-label", border),
            ns.is("vertical-label", isVertical),
            labelAlign,
            labelClassName
          ],
          colSpan: isVertical ? span : 1,
          rowspan: isVertical ? 1 : rowspan
        }, renderLabel()), directives);
      case "content":
        return vue.withDirectives(vue.h(this.tag, {
          style: style$1,
          class: [
            ns.e("cell"),
            ns.e("content"),
            ns.is("bordered-content", border),
            ns.is("vertical-content", isVertical),
            align,
            className
          ],
          colSpan: isVertical ? span : span * 2 - 1,
          rowspan: isVertical ? rowspan * 2 - 1 : rowspan
        }, renderContent()), directives);
      default: {
        const label = renderLabel();
        const labelStyle = {};
        const width2 = style.addUnit(item.labelWidth || this.descriptions.labelWidth);
        if (width2) {
          labelStyle.width = width2;
          labelStyle.display = "inline-block";
        }
        return vue.withDirectives(vue.h("td", {
          style: style$1,
          class: [ns.e("cell"), align],
          colSpan: span,
          rowspan
        }, [
          !lodashUnified.isNil(label) ? vue.h("span", {
            style: labelStyle,
            class: [ns.e("label"), labelClassName]
          }, label) : void 0,
          vue.h("span", {
            class: [ns.e("content"), className]
          }, renderContent())
        ]), directives);
      }
    }
  }
});

exports["default"] = ElDescriptionsCell;
//# sourceMappingURL=descriptions-cell.js.map
