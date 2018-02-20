// Program

var fs = require('fs');

var globalLines = []
var functions = []

exports.load = function(name) {
	
	var contents = fs.readFileSync(name,'utf8');
	var lines = contents.split('\n');
	
	globalLines = []
	functions = []	
	
	for(var x=0;x<lines.length;++x) {
		var raw = lines[x];
		var s = raw.trim();
		var i = s.indexOf("//");
		if(i>=0) {
			s = s.substring(0,i).trim();
		}
		if(s.length==0) continue;
		
		console.log(":"+s+":");
	}

}