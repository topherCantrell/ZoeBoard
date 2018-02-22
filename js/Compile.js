
function hoistVars(lines) {
	var ret = []
	
	// TODO
	
	return ret;
}

exports.doCompile = function(prog) {
	
	prog.vars = hoistVars(prog.globalLines);
	for(var x=0;x<prog.functions.length;++x) {
		var fun = prog.functions[x];
		fun.localVars = hoistVars(fun.codeLines);
	}
	
	if(prog.globalLines.length>1) {
		throw ["Not allowed in global area",prog.globalLines[0]];
	}
	
	// TODO Read the "configure"
	// TODO Check the "init" function
	
	
};




