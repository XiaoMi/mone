'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var table = require('./src/table.js');
var index = require('./src/table-column/index.js');
var install = require('../../utils/vue/install.js');

const ElTable = install.withInstall(table["default"], {
  TableColumn: index["default"]
});
const ElTableColumn = install.withNoopInstall(index["default"]);

exports.ElTable = ElTable;
exports.ElTableColumn = ElTableColumn;
exports["default"] = ElTable;
//# sourceMappingURL=index.js.map
