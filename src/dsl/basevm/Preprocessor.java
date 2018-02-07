package dsl.basevm;

import dsl.CodeLine;
import dsl.Function;
import dsl.Program;

public class Preprocessor {
	
	Program prog;
	
	public Preprocessor(Program prog) {
		this.prog = prog;
	}
	
	void addRESLOCALs() {
		for(Function fun : prog.functions) {
			if(fun.localVars!=null && fun.localVars.size()>0) {
				fun.codeLines.add(0,new CodeLine(fun,"",0,"reslocal("+fun.localVars.size()+")"));			
			}
		}
	}
	
	void addRETURNs() {
		for(Function fun : prog.functions) {
			if(fun.codeLines.size()>0 && fun.codeLines.get(fun.codeLines.size()-1).text.startsWith("return")) {
				continue;
			}
			fun.codeLines.add(new CodeLine(fun,"",0,"return"));
		}
	}
	
	void fixRETURNVALUES() {
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("return ")) {					
					c.text = "__return__="+c.text.substring(7);
					fun.codeLines.add(x+1,new CodeLine(fun,"",0,"return"));
				}
			}
		}
	}
	
	void fixSTORERETURNs() {
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("if(")) continue;
				if(c.text.contains("=") && c.text.contains("(")) {
					int i = c.text.indexOf("=");
					String v = c.text.substring(0,i);
					c.text = c.text.substring(i+1);
					fun.codeLines.add(x+1,new CodeLine(fun,"",0,v+"=__RETVAL__"));					
				}
			}
		}		
	}
	
	public void preprocess() {
		
		// Reserve space for locals in all functions
		addRESLOCALs();
		
		// Make sure every function has a return at the end
		addRETURNs();
		
		// Change "return value" to "p=value;return"
		fixRETURNVALUES();
		
		// Change "a = function()" to "function();a=__RETVAL__"
		fixSTORERETURNs();
		
		// TODO
		// Convert ELSE-IF to nested IF
		// Convert IF/ELSE to pure IF-THEN		
		// Convert DO and WHILE to pure IF (complex logic expressions)
		
		// Maybe one day:
		// Convert complex math expressions to a series of simple math calls
		
	}

}
