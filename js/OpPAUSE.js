parameters = require("./Parameters")
operand = require("./Operand")

exports.parse = function(c, firstPass) {
	
	throw "Implement me";
	
	if(firstPass) {
		p = parameters.parse(c);
		if(p.params.length != 1) {
			throw ["Expected 1 operand (time)", c];
		}
		c.data.push(0x08);
		operand.fill(c,p[0]);		
	}
		
}