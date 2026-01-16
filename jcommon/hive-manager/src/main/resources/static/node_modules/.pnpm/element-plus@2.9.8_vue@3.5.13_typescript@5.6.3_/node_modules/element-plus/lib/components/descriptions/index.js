'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var description$1 = require('./src/description2.js');
var descriptionItem = require('./src/description-item.js');
var description = require('./src/description.js');
var install = require('../../utils/vue/install.js');

const ElDescriptions = install.withInstall(description$1["default"], {
  DescriptionsItem: descriptionItem["default"]
});
const ElDescriptionsItem = install.withNoopInstall(descriptionItem["default"]);

exports.descriptionItemProps = descriptionItem.descriptionItemProps;
exports.descriptionProps = description.descriptionProps;
exports.ElDescriptions = ElDescriptions;
exports.ElDescriptionsItem = ElDescriptionsItem;
exports["default"] = ElDescriptions;
//# sourceMappingURL=index.js.map
