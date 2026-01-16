'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var raf = require('./raf.js');

function throttleByRaf(cb) {
  let timer = 0;
  const throttle = (...args) => {
    if (timer) {
      raf.cAF(timer);
    }
    timer = raf.rAF(() => {
      cb(...args);
      timer = 0;
    });
  };
  throttle.cancel = () => {
    raf.cAF(timer);
    timer = 0;
  };
  return throttle;
}

exports.throttleByRaf = throttleByRaf;
//# sourceMappingURL=throttleByRaf.js.map
