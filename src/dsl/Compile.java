package dsl;

import java.util.ArrayList;
import java.util.List;

public class Compile {
	
	static List<String> hoistVars(List<CodeLine> lines) {
		List<String> vars = new ArrayList<String>();
		for(CodeLine c : lines) {
			if(c.text.startsWith("var ")) {
				String s = c.text;
				int i = s.indexOf("=");
				if(i>=0) s = s.substring(0,i);
				s = s.substring(4);
				if(vars.contains(s)) {
					throw new CompileException("Var already defined",c);
				}
				vars.add(s);
			}
		}
		return vars;
	}

	public static void main(String[] args) throws Exception {
		
		// Load the lines of code
		Program prog = Program.load("New.zoe");
		
		// Hoist all the "var" definitions
		prog.vars = hoistVars(prog.globalLines);
		System.out.println(prog.vars);
		for(Function fun : prog.functions.values()) {
			fun.localVars = hoistVars(fun.codeLines);
			System.out.println(fun.localVars);
		}
		
		// TODO labels

	}

}
