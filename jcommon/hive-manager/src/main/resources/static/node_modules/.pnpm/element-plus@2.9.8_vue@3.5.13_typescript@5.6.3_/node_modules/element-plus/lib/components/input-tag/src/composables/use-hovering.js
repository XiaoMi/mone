'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

function useHovering() {
  const hovering = vue.ref(false);
  const handleMouseEnter = () => {
    hovering.value = true;
  };
  const handleMouseLeave = () => {
    hovering.value = false;
  };
  return {
    hovering,
    handleMouseEnter,
    handleMouseLeave
  };
}

exports.useHovering = useHovering;
//# sourceMappingURL=use-hovering.js.map
