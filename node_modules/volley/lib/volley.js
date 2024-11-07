var utils = require('./utils');
var bps = require('./breakpoints');
var triggers = require('./triggers');
var comparators = require('./comparators');

function init() {
  bps.readIn();
  bps.listen();
}

if (utils.isBrowser() && utils.isSupported()) {
  init();
}


// deprecated triggers
module.exports.at = triggers.at;
module.exports.below = triggers.below;
module.exports.above = triggers.above;
module.exports.between = triggers.between;

// new trigger method names
module.exports.triggerAt = triggers.triggerAt;
module.exports.triggerAtAndBelow = triggers.triggerAtAndBelow;
module.exports.triggerAtAndAbove = triggers.triggerAtAndAbove;
module.exports.triggerAtAndBetween = triggers.triggerAtAndBetween;

// comparators
module.exports.isGreaterThan = comparators.isGreaterThan;
module.exports.isGreaterThanOrEqual = comparators.isGreaterThanOrEqual;
module.exports.isLessThan = comparators.isLessThan;
module.exports.isLessThanOrEqual = comparators.isLessThanOrEqual;
module.exports.isEqual = comparators.isEqual;

// getters
module.exports.getCurrentBreakpoint = comparators.getCurrentBreakpoint;
