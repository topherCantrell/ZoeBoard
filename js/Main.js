var comp = require('./Compile');
var program = require('./Program');

var prog = program.load("New.zoe");

try {
    comp.doCompile(prog);
} catch(e) {
	console.log(e);
	throw e;
}
