// Program

var fs = require('fs');

var globalLines = []
var functions = []

function removeSpaces(s) {
	s = s.replace(/\s+/g, ' ').trim();
		
	var ret = '';
	
	for(var x=0;x<s.length;++x) {
		var c = s[x];
		// Not perfect
		if(c==' ') {
			var a = s[x-1].toUpperCase();
			var b = s[x+1].toUpperCase();
			if(a>='A' && a<='Z') a='0';
			if(b>='A' && b<='Z') b='0';
			if(a>='0' && a<='9') a='0';
			if(b>='0' && b<='9') b='0';
			if(a!='0' || b!='0') continue;			
		}
		ret = ret + c;
	}
	
	return ret;
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
				if(currentFunction.codeLines.length==0 ||
						!currentFunction.codeLines[currentFunction.codeLines.length-1].text=='}')
				{
					throw ["Function must end with a '}'",c];
				}
			}
			currentFunctionLabels = [];
			
			s = s.substring(9); // Take off "function "
			i = s.indexOf("(");
			if(i<0) {
				throw ["Expected opening parenthesis",c];
			}
			var j = s.indexOf(")",i);
			if(j<0) {
				throw ["Expected closing parenthesis",c];
			}
			if(s.substring(j+1)!='{') {
				throw ["Expected '{' after ')'",c];
			}
			
			currentFunction = {"name" : s.substring(0,i)};
			// Find function named
			
		}
		
		console.log(":"+s+":");
		
	}
	
	return ret;

}