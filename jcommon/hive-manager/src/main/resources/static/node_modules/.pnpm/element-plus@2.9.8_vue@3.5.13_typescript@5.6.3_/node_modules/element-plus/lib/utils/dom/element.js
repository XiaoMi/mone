'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@vueuse/core');
var shared = require('@vue/shared');

const getElement = (target) => {
  if (!core.isClient || target === "")
    return null;
  if (shared.isString(target)) {
    try {
      return document.querySelector(target);
    } catch (e) {
      return null;
    }
  }
  return target;
};

exports.getElement = getElement;
//# sourceMappingURL=element.js.map
