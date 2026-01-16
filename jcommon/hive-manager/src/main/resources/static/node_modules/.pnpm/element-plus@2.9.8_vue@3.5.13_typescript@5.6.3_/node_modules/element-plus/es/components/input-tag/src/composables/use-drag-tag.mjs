import { shallowRef, ref } from 'vue';
import { getStyle, setStyle } from '../../../../utils/dom/style.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';
import { isUndefined } from '../../../../utils/types.mjs';

function useDragTag({
  wrapperRef,
  handleDragged,
  afterDragged
}) {
  const ns = useNamespace("input-tag");
  const dropIndicatorRef = shallowRef();
  const showDropIndicator = ref(false);
  let draggingIndex;
  let draggingTag;
  let dropIndex;
  let dropType;
  function getTagClassName(index) {
    return `.${ns.e("inner")} .${ns.namespace.value}-tag:nth-child(${index + 1})`;
  }
  function handleDragStart(event, index) {
    draggingIndex = index;
    draggingTag = wrapperRef.value.querySelector(getTagClassName(index));
    if (draggingTag) {
      draggingTag.style.opacity = "0.5";
    }
    event.dataTransfer.effectAllowed = "move";
  }
  function handleDragOver(event, index) {
    dropIndex = index;
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
    if (isUndefined(draggingIndex) || draggingIndex === index) {
      showDropIndicator.value = false;
      return;
    }
    const dropPosition = wrapperRef.value.querySelector(getTagClassName(index)).getBoundingClientRect();
    const dropPrev = !(draggingIndex + 1 === index);
    const dropNext = !(draggingIndex - 1 === index);
    const distance = event.clientX - dropPosition.left;
    const prevPercent = dropPrev ? dropNext ? 0.5 : 1 : -1;
    const nextPercent = dropNext ? dropPrev ? 0.5 : 0 : 1;
    if (distance <= dropPosition.width * prevPercent) {
      dropType = "before";
    } else if (distance > dropPosition.width * nextPercent) {
      dropType = "after";
    } else {
      dropType = void 0;
    }
    const innerEl = wrapperRef.value.querySelector(`.${ns.e("inner")}`);
    const innerPosition = innerEl.getBoundingClientRect();
    const gap = Number.parseFloat(getStyle(innerEl, "gap")) / 2;
    const indicatorTop = dropPosition.top - innerPosition.top;
    let indicatorLeft = -9999;
    if (dropType === "before") {
      indicatorLeft = Math.max(dropPosition.left - innerPosition.left - gap, Math.floor(-gap / 2));
    } else if (dropType === "after") {
      const left = dropPosition.right - innerPosition.left;
      indicatorLeft = left + (innerPosition.width === left ? Math.floor(gap / 2) : gap);
    }
    setStyle(dropIndicatorRef.value, {
      top: `${indicatorTop}px`,
      left: `${indicatorLeft}px`
    });
    showDropIndicator.value = !!dropType;
  }
  function handleDragEnd(event) {
    event.preventDefault();
    if (draggingTag) {
      draggingTag.style.opacity = "";
    }
    if (dropType && !isUndefined(draggingIndex) && !isUndefined(dropIndex) && draggingIndex !== dropIndex) {
      handleDragged(draggingIndex, dropIndex, dropType);
    }
    showDropIndicator.value = false;
    draggingIndex = void 0;
    draggingTag = null;
    dropIndex = void 0;
    dropType = void 0;
    afterDragged == null ? void 0 : afterDragged();
  }
  return {
    dropIndicatorRef,
    showDropIndicator,
    handleDragStart,
    handleDragOver,
    handleDragEnd
  };
}

export { useDragTag };
//# sourceMappingURL=use-drag-tag.mjs.map
