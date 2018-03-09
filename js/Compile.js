pre = require("./Preprocessor");

OpPAUSE = require("./OpPAUSE");

function hoistVars(lines) {
	var ret = []
	
	for(var x=lines.length-1;x>=0;x=x-1) {
		var c = lines[x];
		if(c.text.startsWith("var ")) {
			var s = c.text;
			var i = s.indexOf("=");
			if(i>=0) s=s.substring(0,i);
			s = s.substring(4);
			if(ret.indexOf(s)>=0) {
				throw ["Var already defined",c];
			}
			ret.push(s);
			if(i>=0) {
				// This is an assignment ... keep the X=Y part
				c.text = c.text.substring(4);
				c.changed = true;				
			} else {				
				lines.splice(x,1);
			}
		}
	}
	
	return ret;
}

function compileFunction(fun, firstPass) {
	
	for(var x=0;x<fun.codeLines.length;++x) {
		var c = fun.codeLines[x];
		if(c.isLabel) continue;	
	
		if(c.text.startsWith("PAUSE(")) {
			OpPAUSE.parse(c, firstPass);
			continue;
		}
		
		throw {"message":"Unknown","line":c};
	
	}	
	
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
	
	// TODO Read the "configure" (DSL specific)
	// TODO Check the "init" function (DSL specific)
	
	// Pre-process the highlevel language constructs 
	pre.preprocess(prog);
	
	// First pass ... build the addresses
	var address = 0;
	for(x=0;x<prog.functions.length;++x) {
		var fun = prog.functions[x];
		compileFunction(fun,true);
		for(var y=0;y<fun.codeLines.length;++y) {
			var c = fun.codeLines[y];
			c.address = address;
			address = address + c.data.length;
		}
	}
	
	// Second pass ... now we have the addresses
	for(x=0;x<prog.functions.length;++x) {
		var fun = prog.functions[x];
		compileFunction(fun,false);
	}
	
};




