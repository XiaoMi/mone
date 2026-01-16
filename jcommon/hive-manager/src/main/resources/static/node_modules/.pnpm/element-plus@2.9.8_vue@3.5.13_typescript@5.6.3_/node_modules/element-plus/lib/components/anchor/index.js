'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var anchor$1 = require('./src/anchor2.js');
var anchorLink = require('./src/anchor-link2.js');
var anchor = require('./src/anchor.js');
var install = require('../../utils/vue/install.js');

const ElAnchor = install.withInstall(anchor$1["default"], {
  AnchorLink: anchorLink["default"]
});
const ElAnchorLink = install.withNoopInstall(anchorLink["default"]);

exports.anchorEmits = anchor.anchorEmits;
exports.anchorProps = anchor.anchorProps;
exports.ElAnchor = ElAnchor;
exports.ElAnchorLink = ElAnchorLink;
exports["default"] = ElAnchor;
//# sourceMappingURL=index.js.map
