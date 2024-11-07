var debounce = require('lodash.debounce');
var utils = require('./utils');

var breakpoints = [];
var breakpointsToIndexMap = {};
var indexToBreakpointsMap = {};
var listeners = {};
var currentBreakpoint;

function handleResize(evt) {
  var nextBreakpoint = getBreakpointIndex(utils.readPseudo('before'));
  var triggerableListeners = listeners[nextBreakpoint];

  if (nextBreakpoint !== currentBreakpoint) {
    currentBreakpoint = nextBreakpoint;

    for (var i = 0, l = triggerableListeners.length; i < l; i++) {
      if (triggerableListeners[i].nextTick) {
        utils.fire(triggerableListeners[i].fn, evt);
      }
      else {
        triggerableListeners[i].fn(evt);
      }
    }
  }
}

function listen() {
  var handler = debounce(handleResize, 200);

  window.addEventListener('resize', handler);
  window.addEventListener('orientationchange', handler);
}

function addListener(index, fn, nextTick) {
  listeners[index] && listeners[index].push({fn: fn, nextTick: nextTick});
}

function setupListenersAndBreakpointsMap() {
  for (var i = 0, l = breakpoints.length; i < l; i++) {
    breakpointsToIndexMap[breakpoints[i]] = i;
    indexToBreakpointsMap[i] = breakpoints[i];
    listeners[i] = [];
  }
}

function getBreakpointIndex(breakpoint) {
  return breakpointsToIndexMap[breakpoint];
}

function readIn() {
  breakpoints = utils.readPseudo('after').split(',');
  setupListenersAndBreakpointsMap();
  currentBreakpoint = getBreakpointIndex(utils.readPseudo('before'));
}

function getCurrentBreakpoint() {
  return indexToBreakpointsMap[currentBreakpoint];
}

function indexOf(breakpoint) {
  return breakpointsToIndexMap[breakpoint];
}

function getCurrentBreakpointIndex() {
  return currentBreakpoint;
}

function totalBreakpoints() {
  return breakpoints.length;
}

module.exports.readIn = readIn;
module.exports.getBreakpointIndex = getBreakpointIndex;
module.exports.addListener = addListener;
module.exports.getCurrentBreakpoint = getCurrentBreakpoint;
module.exports.getCurrentBreakpointIndex = getCurrentBreakpointIndex;
module.exports.totalBreakpoints = totalBreakpoints;
module.exports.indexOf = indexOf;
module.exports.listen = listen;