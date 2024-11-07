# Volley

> A JavaScript CSS breakpoint interpreter

[![npm version](https://badge.fury.io/js/volley.svg)](https://badge.fury.io/js/volley)
[![Build Status](https://travis-ci.org/afram/volley.svg?branch=master)](https://travis-ci.org/afram/volley)

Volley is for those times you need to trigger JavaScript functions when your responsive
media queries fire. This library works with any grid, it reads your CSS, and it
follows your naming conventions. There is no limit on the number of breakpoints
you can have either!

Volley is written using commonjs with browserify/webpack in mind. If you
think you'll find Volley useful as a standalone global library, please let me
know (or submit a PR).

- [Usage](#usage)
  - [CSS](#css)
  - [API](#api)
  - [Triggers](#triggers)
    - [options](#options)
    - [fireOnSet [true]](#fireonset-true)
    - [nextTick [true]](#nexttick-true)
    - [Methods](#methods)
    - [triggerAt](#triggerat)
    - [triggerAtAndAbove](#triggeratandabove)
    - [triggerAtAndBelow](#triggeratandbelow)
    - [triggerAtAndBetween](#triggeratandbetween)
  - [Comparators](#comparators)
    - [isGreaterThan -> Boolean](#isgreaterthan---boolean)
    - [isGreaterThanOrEqual -> Boolean](#isgreaterthanorequal---boolean)
    - [isLessThan -> Boolean](#islessthan---boolean)
    - [isLessThanOrEqual -> Boolean](#islessthanorequal---boolean)
    - [isEqual -> Boolean](#isequal---boolean)
  - [Getters](#getters)
    - [getCurrentBreakpoint -> String](#getcurrentbreakpoint---string)

## Usage
Setting up Volley is simple. You need to configure a little bit of CSS, and then
require the library with either webpack or browserify.

### CSS
In order for Volley to read in your media queries, you need to first configure
your css with a couple of pseudo selectors on the body element as follows. These values
are for demonstration purposes, feel free to substitute with names and breakpoints that
fit your project requirements.

```css
  body:before {
    content: "small";
    display: none;
  }

  body:after {
    content: "small,medium,large,extralarge";
    display: none;
  }

  @media only screen and (min-width: 768px) {
    body:before {
      content: "medium";
    }
  }

  @media only screen and (min-width: 922px) {
    body:before {
      content: "large";
    }
  }

  @media only screen and (min-width: 1200px) {
    body:before {
      content: "extralarge";
    }
  }
```

As you can see, the `before` pseudo selector's content property holds the current
viewport width name (as chosen by you). The `after` pseudo selector's content
property contains all of your viewport width names in ascending order.

When the viewport width changes, the content value changes, and the `resize` event
is triggered, causing the JavaScript event handlers you set up to execute.

### API
With the CSS out of the way, you can now get right into using Volley.

### Triggers

#### options
There are currently 2 options you can pass to the trigger functions, they are
`fireOnSet` and `nextTick`.

#### fireOnSet [true]
Should the handler function be triggered immediately after it is set, if the current
breakpoint meets conditions of the handler. True by default.

#### nextTick [true]
Should the handler be executed on the next JavaScript frame. True by default.

#### Methods

#### triggerAt
The `triggerAt` method is for setting handlers to execute at a specific breakpoint.

```js
import volley from 'volley';

volley.triggerAt('medium', (evt) => { /* handle medium viewport */ });

// options as a second argument should you want/need to change them
volley.triggerAt(
  'medium',
  {fireOnSet: true, nextTick: true},
  () => { /* handle medium viewport */ }
);
```

#### triggerAtAndAbove
The `triggerAtAndAbove` method is for setting handlers to execute at the stated breakpoint and
above (inclusive).

```js
import volley from 'volley';

// This will trigger for medium, large, and extralarge, but not small viewport
volley.triggerAtAndAbove('medium', (evt) => { triggerAtAndAbove });

// options as a second argument should you want/need to change them
volley.triggerAtAndAbove(
  'medium',
  {fireOnSet: true, nextTick: true},
  () => { /* handle medium viewport */ }
);
```

#### triggerAtAndBelow
The `triggerAtAndBelow` method is for setting handlers to execute at the stated breakpoint and
below (inclusive).

```js
import volley from 'volley';

// This will trigger for small and medium, but not large or extralarge viewports
volley.triggerAtAndBelow('medium', (evt) => { triggerAtAndBelow });

// options as a second argument should you want/need to change them
volley.triggerAtAndBelow(
  'medium',
  {fireOnSet: true, nextTick: true},
  () => { /* handle medium viewport */ }
);
```

#### triggerAtAndBetween
The `triggerAtAndBetween` method is for setting handlers to execute between the stated breakpoints
(inclusive).

```js
import volley from 'volley';

// This will trigger for medium and large, but not small or extralarge viewports
volley.triggerAtAndBetween('medium', 'large', (evt) => { /* handle medium and large viewport */ });

// options as a second argument should you want/need to change them
volley.triggerAtAndBetween(
  'medium',
  'large',
  {fireOnSet: true, nextTick: true},
  () => { /* handle medium viewport */ }
);
```

### Comparators
For comparing the current breakpoint against the others

#### isGreaterThan -> Boolean
The `isGreaterThan` method is testing whether the first argument (breakpoint A) is greater than
the second argument (breakpoint B). The first argument can be omitted, the current breakpoint
is treated as the left hand of the expression when only one argument is passed in.
`isGreaterThan` returns a Boolean, and will return false if either breakpoint does not exist.

```js
import volley from 'volley';
// given that the current breakpoint is medium

// if only one arg, will test if currentBreakpoint > arg1
let breakpoint = volley.isGreaterThan('small'); // true
let breakpoint = volley.isGreaterThan('medium'); // false
let breakpoint = volley.isGreaterThan('nonexistent'); // false

// if 2 args, will test if arg1 > arg2
let breakpoint = volley.isGreaterThan('medium', 'small'); // true
let breakpoint = volley.isGreaterThan('medium','medium'); // false
let breakpoint = volley.isGreaterThan('medium', 'nonexistent'); // false
let breakpoint = volley.isGreaterThan('nonexistent', 'medium'); // false
```

#### isGreaterThanOrEqual -> Boolean
The `isGreaterThanOrEqual` method is testing whether the first argument (breakpoint A) is greater than
or equal to the second argument (breakpoint B). The first argument can be omitted, the current breakpoint
is treated as the left hand of the expression when only one argument is passed in.
`isGreaterThanOrEqual` returns a Boolean, and will return false if either breakpoint does not exist.

```js
import volley from 'volley';
// given that the current breakpoint is medium

// if only one arg, will test if currentBreakpoint >= arg1
let breakpoint = volley.isGreaterThanOrEqual('small'); // true
let breakpoint = volley.isGreaterThanOrEqual('medium'); // true
let breakpoint = volley.isGreaterThanOrEqual('large'); // false
let breakpoint = volley.isGreaterThanOrEqual('nonexistent'); // false

// if 2 args, will test if arg1 >= arg2
let breakpoint = volley.isGreaterThanOrEqual('medium', 'small'); // true
let breakpoint = volley.isGreaterThanOrEqual('medium', 'medium'); // true
let breakpoint = volley.isGreaterThanOrEqual('medium', 'large'); // false
let breakpoint = volley.isGreaterThanOrEqual('medium', 'nonexistent'); // false
let breakpoint = volley.isGreaterThanOrEqual('nonexistent', 'medium'); // false
```

#### isLessThan -> Boolean
The `isLessThan` method is testing whether the first argument (breakpoint A) is less than
the second argument (breakpoint B). The first argument can be omitted, the current breakpoint
is treated as the left hand of the expression when only one argument is passed in.
`isLessThan` returns a Boolean, and will return false if either breakpoint does not exist.

```js
import volley from 'volley';
// given that the current breakpoint is medium

// if only one arg, will test if currentBreakpoint < arg1
let breakpoint = volley.isLessThan('large'); // true
let breakpoint = volley.isLessThan('medium'); // false
let breakpoint = volley.isLessThan('nonexistent'); // false

// if 2 args, will test if arg1 < arg2
let breakpoint = volley.isLessThan('medium', 'large'); // true
let breakpoint = volley.isLessThan('medium', 'medium'); // false
let breakpoint = volley.isLessThan('medium', 'nonexistent'); // false
let breakpoint = volley.isLessThan('nonexistent', 'medium'); // false
```

#### isLessThanOrEqual -> Boolean
The `isLessThanOrEqual` method is testing whether the first argument (breakpoint A) is less than
or equal to the second argument (breakpoint B). The first argument can be omitted, the current breakpoint
is treated as the left hand of the expression when only one argument is passed in.
`isLessThanOrEqual` returns a Boolean, and will return false if either breakpoint does not exist.

```js
import volley from 'volley';
// given that the current breakpoint is medium

// if only one arg, will test if currentBreakpoint <= arg1
let breakpoint = volley.isLessThanOrEqual('large'); // true
let breakpoint = volley.isLessThanOrEqual('medium'); // true
let breakpoint = volley.isLessThanOrEqual('small'); // false
let breakpoint = volley.isLessThanOrEqual('nonexistent'); // false

// if 2 args, will test if arg1 <= arg2
let breakpoint = volley.isLessThanOrEqual('medium', 'large'); // true
let breakpoint = volley.isLessThanOrEqual('medium', 'medium'); // true
let breakpoint = volley.isLessThanOrEqual('medium', 'small'); // false
let breakpoint = volley.isLessThanOrEqual('medium', 'nonexistent'); // false
let breakpoint = volley.isLessThanOrEqual('nonexistent', 'medium'); // false
```

#### isEqual -> Boolean
The `isEqual` method is testing whether the first argument (breakpoint A) is equal to
the second argument (breakpoint B). The first argument can be omitted, the current breakpoint
is treated as the left hand of the expression when only one argument is passed in.
`isEqual` returns a Boolean, and will return false if either breakpoint does not exist.

```js
import volley from 'volley';
// given that the current breakpoint is medium

// if only one arg, will test if currentBreakpoint === arg1
let breakpoint = volley.isEqual('large'); // false
let breakpoint = volley.isEqual('medium'); // true
let breakpoint = volley.isEqual('nonexistent'); // false

// if 2 args, will test if arg1 === arg2
let breakpoint = volley.isEqual('medium', 'large'); // false
let breakpoint = volley.isEqual('medium', 'medium'); // true
let breakpoint = volley.isEqual('medium', 'nonexistent'); // false
let breakpoint = volley.isEqual('nonexistent', 'medium'); // false
```

### Getters

#### getCurrentBreakpoint -> String
The `getCurrentBreakpoint` method is for retrieving the current breakpoint
programmatically. It returns a String of the current breakpoint.

```js
import volley from 'volley';
// given that the current breakpoint is medium


let breakpoint = volley.getCurrentBreakpoint(); // "medium"
```
