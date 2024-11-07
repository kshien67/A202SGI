var utils = require('./utils');
var bps = require('./breakpoints');
var isDev = require('./is-dev');

function triggerAt(breakpoint, opts, fn) {
  if (!utils.isBrowser() || !utils.isSupported()) return;
  if (arguments.length === 2) {
    fn = opts;
    opts = {
      fireOnSet: true,
      nextTick: true
    }
  }

  if (typeof opts.fireOnSet === 'undefined') opts.fireOnSet = true;
  if (typeof opts.nextTick === 'undefined') opts.nextTick = true;

  var bpIndex = bps.getBreakpointIndex(breakpoint);
  bps.addListener(bpIndex, fn, opts.nextTick);

  if (opts.fireOnSet && bpIndex === bps.getCurrentBreakpointIndex()) {
    if (opts.nextTick) {
      utils.fire(fn, null);
    }
    else {
      fn(null);
    }
  }
}

function triggerAtAndBelow(breakpoint, opts, fn) {
  if (!utils.isBrowser() || !utils.isSupported()) return;
  if (arguments.length === 2) {
    fn = opts;
    opts = {
      fireOnSet: true,
      nextTick: true
    }
  }
  if (typeof opts.fireOnSet === 'undefined') opts.fireOnSet = true;
  if (typeof opts.nextTick === 'undefined') opts.nextTick = true;

  var bpIndex = bps.getBreakpointIndex(breakpoint);

  for (var i = 0; i <= bpIndex; i++) {
    bps.addListener(i, fn, opts.nextTick);
  }

  if (opts.fireOnSet && bps.getCurrentBreakpointIndex() <= bpIndex) {
    if (opts.nextTick) {
      utils.fire(fn, null);
    }
    else {
      fn(null);
    }
  }
}

function triggerAtAndAbove(breakpoint, opts, fn) {
  if (!utils.isBrowser() || !utils.isSupported()) return;
  if (arguments.length === 2) {
    fn = opts;
    opts = {
      fireOnSet: true,
      nextTick: true
    }
  }
  if (typeof opts.fireOnSet === 'undefined') opts.fireOnSet = true;
  if (typeof opts.nextTick === 'undefined') opts.nextTick = true;

  var bpIndex = bps.getBreakpointIndex(breakpoint);
  var totalBreakpoints = bps.totalBreakpoints();

  for (var i = bpIndex; i <= totalBreakpoints; i++) {
    bps.addListener(i, fn, opts.nextTick);
  }

  if (opts.fireOnSet && bps.getCurrentBreakpointIndex() >= bpIndex) {
    if (opts.nextTick) {
      utils.fire(fn, null);
    }
    else {
      fn(null);
    }
  }
}

function triggerAtAndBetween(breakpoint1, breakpoint2, opts, fn) {
  if (!utils.isBrowser() || !utils.isSupported()) return;
  if (arguments.length === 3) {
    fn = opts;
    opts = {
      fireOnSet: true,
      nextTick: true
    }
  }
  if (typeof opts.fireOnSet === 'undefined') opts.fireOnSet = true;
  if (typeof opts.nextTick === 'undefined') opts.nextTick = true;

  var index1 = bps.getBreakpointIndex(breakpoint1);
  var index2 = bps.getBreakpointIndex(breakpoint2);

  for(var i = index1; i <= index2; i++) {
    bps.addListener(i, fn, opts.nextTick);
  }

  if (opts.fireOnSet
    && index1 <= bps.getCurrentBreakpointIndex()
    && index2 >= bps.getCurrentBreakpointIndex()) {
    if (opts.nextTick) {
      utils.fire(fn, null);
    }
    else {
      fn(null);
    }
  }
}

module.exports.at = function() {
  if (isDev) {
    console.log('[volley] volley.at is deprecated, use volley.triggerAt');
  }
  triggerAt.apply(this, arguments);
};

module.exports.below = function() {
  if (isDev) {
    console.log('[volley] volley.below is deprecated, use volley.triggerAtAndBelow');
  }
  triggerAtAndBelow.apply(this, arguments);
};


module.exports.above = function() {
  if (isDev) {
    console.log('[volley] volley.above is deprecated, use volley.triggerAtAndAbove');
  }
  triggerAtAndAbove.apply(this, arguments);
};


module.exports.between = function() {
  if (isDev) {
    console.log('[volley] volley.between is deprecated, use volley.triggerAtAndBetween');
  }
  triggerAtAndBetween.apply(this, arguments);
};

module.exports.triggerAt = triggerAt;
module.exports.triggerAtAndBelow = triggerAtAndBelow;
module.exports.triggerAtAndAbove = triggerAtAndAbove;
module.exports.triggerAtAndBetween = triggerAtAndBetween;
