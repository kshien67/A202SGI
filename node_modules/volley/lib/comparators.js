var bps = require('./breakpoints');

function isGreaterThan(leftBp, rightBp) {
  if (!rightBp) {
    rightBp = leftBp;
    return bps.getCurrentBreakpointIndex() > bps.indexOf(rightBp);
  }

  return bps.indexOf(leftBp) > bps.indexOf(rightBp);
}

function isGreaterThanOrEqual(leftBp, rightBp) {
  if (!rightBp) {
    rightBp = leftBp;
    return bps.getCurrentBreakpointIndex() >= bps.indexOf(rightBp);
  }

  return bps.indexOf(leftBp) >= bps.indexOf(rightBp);
}

function isLessThan(leftBp, rightBp) {
  if (!rightBp) {
    rightBp = leftBp;
    return bps.getCurrentBreakpointIndex() < bps.indexOf(rightBp);
  }

  return bps.indexOf(leftBp) < bps.indexOf(rightBp);
}

function isLessThanOrEqual(leftBp, rightBp) {
  if (!rightBp) {
    rightBp = leftBp;
    return bps.getCurrentBreakpointIndex() <= bps.indexOf(rightBp);
  }

  return bps.indexOf(leftBp) <= bps.indexOf(rightBp);
}

function isEqual(leftBp, rightBp) {
  if (!rightBp) {
    rightBp = leftBp;
    return bps.getCurrentBreakpointIndex() === bps.indexOf(rightBp);
  }

  return bps.indexOf(leftBp) === bps.indexOf(rightBp);
}



module.exports.getCurrentBreakpoint = bps.getCurrentBreakpoint;
module.exports.isGreaterThan = isGreaterThan;
module.exports.isGreaterThanOrEqual = isGreaterThanOrEqual;
module.exports.isLessThan = isLessThan;
module.exports.isLessThanOrEqual = isLessThanOrEqual;
module.exports.isEqual = isEqual;