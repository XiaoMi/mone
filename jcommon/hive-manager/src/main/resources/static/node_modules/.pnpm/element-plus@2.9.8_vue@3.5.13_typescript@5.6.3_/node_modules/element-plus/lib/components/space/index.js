'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var space = require('./src/space.js');
var item = require('./src/item.js');
var useSpace = require('./src/use-space.js');
var install = require('../../utils/vue/install.js');

const ElSpace = install.withInstall(space["default"]);

exports.spaceProps = space.spaceProps;
exports.spaceItemProps = item.spaceItemProps;
exports.useSpace = useSpace.useSpace;
exports.ElSpace = ElSpace;
exports["default"] = ElSpace;
//# sourceMappingURL=index.js.map
