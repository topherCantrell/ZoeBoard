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
	
	var ret = { // new Program
			"globalLines" : [],
			"functions" : []
	}
	
	// Method to find a function in a program
	ret.findFunction = function(name) {
		for(var x=0;x<this.functions.length;++x) {
			if(this.functions[x].name == name) {
				return x;
			}
		}		
		return -1;
	};
	
	// Method to find a global variable
	ret.findGlobal = function(name) {
		return this.vars.indexOf(name);
	};
	
	// Read the raw lines
	var lines = fs.readFileSync(name,'utf8').split('\n');;
	
	var currentFunction = null;
	var currentFunctionLabels = [];
	
	for(var x=0;x<lines.length;++x) {
		var s = lines[x].trim();
		
		// Pull out comments
		var i = s.indexOf("//");
		if(i>=0) {
			s = s.substring(0,i).trim();
		}
		
		// Ignore blank lines
		if(s.length==0) continue;
		
		// Proper scanning
		s = removeSpaces(s);
		
		// Handle included files
		if(s.startsWith("include ")) {
			var incName = s.substring(8);
			var inc = exports.load(incName);
			ret.globalLines = ret.globalLines.concat(inc.globalLines);
			ret.functions = ret.functions.concat(inc.functions);
			continue;
		}
		
		var c = { // new CodeLine
		    "text":s
		};
		
		// Handle labels within a function
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
		
		// Handle starting a new function
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
			
			currentFunction = { // new Function()
					"name" : s.substring(0,i),
					"codeLines" : [],
					"arguments" : [],
					"isEvent" : false
			};
			
			if(ret.findFunction(currentFunction.name)>=0) {
				throw ["Function name already exists",c];
			}
			
			ret.functions.push(currentFunction);
			
			// Count the arguments
			var args = s.substring(i+1,j).split(",");
			for(var y=0;y<args.length;++y) {
				if(args[y].length>0) {
					currentFunction.arguments.push(args[y]);
				}
			}
			
			// Event handlers may have no arguments
			if(currentFunction.name == currentFunction.name.toUpperCase()) {
				currentFunction.isEvent = true;
				if(currentFunction.arguments.length!==0) {
					throw ["Event functions can take no arguments",c];
				}
			}
			
		} else if(currentFunction===null) {
			ret.globalLines.push(c);
		} else {
			currentFunction.codeLines.push(c);
		}
		
	}
	
	return ret;

}