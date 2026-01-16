'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var tour$1 = require('./src/tour2.js');
var step$1 = require('./src/step2.js');
var tour = require('./src/tour.js');
var step = require('./src/step.js');
var content = require('./src/content.js');
var install = require('../../utils/vue/install.js');

const ElTour = install.withInstall(tour$1["default"], {
  TourStep: step$1["default"]
});
const ElTourStep = install.withNoopInstall(step$1["default"]);

exports.tourEmits = tour.tourEmits;
exports.tourProps = tour.tourProps;
exports.tourStepEmits = step.tourStepEmits;
exports.tourStepProps = step.tourStepProps;
exports.tourContentEmits = content.tourContentEmits;
exports.tourContentProps = content.tourContentProps;
exports.tourPlacements = content.tourPlacements;
exports.tourStrategies = content.tourStrategies;
exports.ElTour = ElTour;
exports.ElTourStep = ElTourStep;
exports["default"] = ElTour;
//# sourceMappingURL=index.js.map
