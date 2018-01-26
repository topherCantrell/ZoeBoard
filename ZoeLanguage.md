# Language Reference

The ```//``` comments out the rest of the line. Blank lines are ignored.

TODO: functions, init, labels

# Variables

Variables are global to all functions in a program (but not between separate programs).

Variables are 15-bit unsigned values 0-32767.

You define a variable with the ```var``` keyword like:

```js
var color
var count
var delay
```

You can define variables anywhere in the program, but the top of the init function is a good, consistent place.

# Variable Expressions

For instance:
```js
[color] = 1
[count] = 0
[delay] = 1000
```

For instance:
```js
[color] = [count]
[color] = [count] * 5
[count] = [count] - 1
```

TODO table of operations

# CONFIGURE(out=?,length=?,hasWhite=?)

# PAUSE(time=?)

Redraw the pixel array and pause for OP milliseconds.

Remember: You must PAUSE to update the pixel display.

TODO show optional parameters here

# DIEFINECOLOR(color=?, w=?, b=?, g=?, r=?)

# SOLID(color=?)

# SET(pixel=?,color=?)

# PATTERN(number=?)

# DRAWPATTERN(number=? x=?, y=?, colorOffset=?)

# GOTO label

# GOSUB label

# RETURN

# RETURN

# IF

# Sample Program
