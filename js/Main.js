var comp = require('./Compile');
var program = require('./Program');

var prog = program.load("New.zoe");

comp.doCompile(prog);
