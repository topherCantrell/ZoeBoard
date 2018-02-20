// Program

var fs = require('fs');

var globalLines = []
var functions = []

function removeSpaces(s) {
	s = s.trim();
	
	// TODO
		
	return s;
}

exports.load = function(name) {
	
	var ret = {}
	
	var contents = fs.readFileSync(name,'utf8');
	var lines = contents.split('\n');
	
	ret.globalLines = [];
	ret.functions = [];	
	
	var currentFunction = null;
	var currentFunctionLabels = [];
	
	for(var x=0;x<lines.length;++x) {
		var raw = lines[x];
		var s = raw.trim();
		var i = s.indexOf("//");
		if(i>=0) {
			s = s.substring(0,i).trim();
		}
		if(s.length==0) continue;
		
		s = removeSpaces(s);
		var c = {"text":s};
		
		if(s.startsWith("include ")) {
			var incName = s.substring(8);
			var inc = exports.load(incName);
			ret.globalLines = ret.globalLines.concat(inc.globalLines);
			ret.functions = ret.functions.concat(inc.functions);
			continue;
		}
		
		i = s.indexOf(':');
		if(i>=0) {
			if(i!=s.length-1) {
				throw ["Expected ':' to be last on the line",c];
			}
			if(currentFunction===null) {
				throw ["Global labels are not allowed",c];
			}
			s - s.substring(0,s.length-1);
			if(currentFunctionLabels[s]!=undefined) {
				throw ["Label '"+s+"' has already been used",c];
			}
			currentFunctionLabels.push(s);
			c.text = s;
			c.isLabel = true;
		}
		
		if(s.startsWith("function ")) {
			if(currentFunction!==null) {
				
			}
		}
		
		console.log(":"+s+":");
		
	}
	
	return ret;

}