
exports.parse = function(c,noExtra) {
	
	ret = {
		"c" : c,
		"start" : c.text.indexOf("("),
		"end" : c.text.lastIndexOf(")")			
	};
	
	if(noExtra && end!=c.text.length -1) {
		throw ["Unexpected characters after '(...)'",c];
	}
	
	if(ret.start<0 || ret.end<0) {
		throw ["Expected '(...)'",c];
	}
	
	ret.params = c.text.substring(ret.start+1,ret.end).split(',');
	if(ret.params.length==1 && ret.params[0]=='') {
		ret.params = [];
	}
	
	return ret;	
	
};