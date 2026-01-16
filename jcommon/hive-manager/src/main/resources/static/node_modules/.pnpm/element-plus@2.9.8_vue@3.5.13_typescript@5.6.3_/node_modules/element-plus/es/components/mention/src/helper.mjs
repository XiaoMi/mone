import { castArray } from 'lodash-unified';
import { isFirefox } from '../../../utils/browser.mjs';

const filterOption = (pattern, option) => {
  const lowerCase = pattern.toLowerCase();
  const label = option.label || option.value;
  return label.toLowerCase().includes(lowerCase);
};
const getMentionCtx = (inputEl, prefix, split) => {
  const { selectionEnd } = inputEl;
  if (selectionEnd === null)
    return;
  const inputValue = inputEl.value;
  const prefixArray = castArray(prefix);
  let splitIndex = -1;
  let mentionCtx;
  for (let i = selectionEnd - 1; i >= 0; --i) {
    const char = inputValue[i];
    if (char === split || char === "\n" || char === "\r") {
      splitIndex = i;
      continue;
    }
    if (prefixArray.includes(char)) {
      const end = splitIndex === -1 ? selectionEnd : splitIndex;
      const pattern = inputValue.slice(i + 1, end);
      mentionCtx = {
        pattern,
        start: i + 1,
        end,
        prefix: char,
        prefixIndex: i,
        splitIndex,
        selectionEnd
      };
      break;
    }
  }
  return mentionCtx;
};
const getCursorPosition = (element, options = {
  debug: false,
  useSelectionEnd: false
}) => {
  const selectionStart = element.selectionStart !== null ? element.selectionStart : 0;
  const selectionEnd = element.selectionEnd !== null ? element.selectionEnd : 0;
  const position = options.useSelectionEnd ? selectionEnd : selectionStart;
  const properties = [
    "direction",
    "boxSizing",
    "width",
    "height",
    "overflowX",
    "overflowY",
    "borderTopWidth",
    "borderRightWidth",
    "borderBottomWidth",
    "borderLeftWidth",
    "borderStyle",
    "paddingTop",
    "paddingRight",
    "paddingBottom",
    "paddingLeft",
    "fontStyle",
    "fontVariant",
    "fontWeight",
    "fontStretch",
    "fontSize",
    "fontSizeAdjust",
    "lineHeight",
    "fontFamily",
    "textAlign",
    "textTransform",
    "textIndent",
    "textDecoration",
    "letterSpacing",
    "wordSpacing",
    "tabSize",
    "MozTabSize"
  ];
  if (options.debug) {
    const el = document.querySelector("#input-textarea-caret-position-mirror-div");
    if (el == null ? void 0 : el.parentNode)
      el.parentNode.removeChild(el);
  }
  const div = document.createElement("div");
  div.id = "input-textarea-caret-position-mirror-div";
  document.body.appendChild(div);
  const style = div.style;
  const computed = window.getComputedStyle(element);
  const isInput = element.nodeName === "INPUT";
  style.whiteSpace = isInput ? "nowrap" : "pre-wrap";
  if (!isInput)
    style.wordWrap = "break-word";
  style.position = "absolute";
  if (!options.debug)
    style.visibility = "hidden";
  properties.forEach((prop) => {
    if (isInput && prop === "lineHeight") {
      if (computed.boxSizing === "border-box") {
        const height = Number.parseInt(computed.height);
        const outerHeight = Number.parseInt(computed.paddingTop) + Number.parseInt(computed.paddingBottom) + Number.parseInt(computed.borderTopWidth) + Number.parseInt(computed.borderBottomWidth);
        const targetHeight = outerHeight + Number.parseInt(computed.lineHeight);
        if (height > targetHeight) {
          style.lineHeight = `${height - outerHeight}px`;
        } else if (height === targetHeight) {
          style.lineHeight = computed.lineHeight;
        } else {
          style.lineHeight = "0";
        }
      } else {
        style.lineHeight = computed.height;
      }
    } else {
      style[prop] = computed[prop];
    }
  });
  if (isFirefox()) {
    if (element.scrollHeight > Number.parseInt(computed.height)) {
      style.overflowY = "scroll";
    }
  } else {
    style.overflow = "hidden";
  }
  div.textContent = element.value.slice(0, Math.max(0, position));
  if (isInput && div.textContent) {
    div.textContent = div.textContent.replace(/\s/g, "\xA0");
  }
  const span = document.createElement("span");
  span.textContent = element.value.slice(Math.max(0, position)) || ".";
  span.style.position = "relative";
  span.style.left = `${-element.scrollLeft}px`;
  span.style.top = `${-element.scrollTop}px`;
  div.appendChild(span);
  const relativePosition = {
    top: span.offsetTop + Number.parseInt(computed.borderTopWidth),
    left: span.offsetLeft + Number.parseInt(computed.borderLeftWidth),
    height: Number.parseInt(computed.fontSize) * 1.5
  };
  if (options.debug) {
    span.style.backgroundColor = "#aaa";
  } else {
    document.body.removeChild(div);
  }
  if (relativePosition.left >= element.clientWidth) {
    relativePosition.left = element.clientWidth;
  }
  return relativePosition;
};

export { filterOption, getCursorPosition, getMentionCtx };
//# sourceMappingURL=helper.mjs.map
