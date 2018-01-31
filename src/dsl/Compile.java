package dsl;

import java.util.ArrayList;
import java.util.List;

public class Compile {
	
	public void doCompile(Program prog) {
		
		// Hoist all the "var" definitions
		prog.vars = hoistVars(prog.globalLines);
		System.out.println(prog.vars);
		for(Function fun : prog.functions.values()) {
			fun.localVars = hoistVars(fun.codeLines);
			System.out.println(fun.localVars);
		}
		
		// Read the "configure" command
		readConfigure(prog);
		
		// TODO labels
	}
	
	void readConfigure(Program prog) {
		// Get the configuration
		if(prog.globalLines.size()==0 || !prog.globalLines.get(0).text.startsWith("configure(")) {
			throw new CompileException("The first line of the program must be 'configure'",null);
		}
		CodeLine c = prog.globalLines.get(0);
		String s = c.text;
		if(!s.endsWith(")")) {
			throw new CompileException("Expected end of line to be ')'",c);
		}
		String [] opts = s.substring(10, s.length()-1).split(",");
		if(opts.length<2 || opts.length>3) {
			throw new CompileException("Expected 2 or 3 parameters",c);
		}
		
		if(opts[0].equals("D1") || opts[0].equals("D2") || opts[0].equals("D3") || opts[0].equals("D4")) {
			prog.configStrip = opts[0];
		} else {
			throw new CompileException("Expected first argument to be D1, D2, D3, or D4",c);
		}
		
		try {
			prog.configLength = Integer.parseInt(opts[1]);
		} catch (Exception e) {
			throw new CompileException("Invalid length '"+opts[1]+"'",c);
		}
		
		if(opts.length>2) {
			try {
				prog.configHasWhite = Boolean.parseBoolean(opts[2]);
			} catch(Exception e) {
				throw new CompileException("Has-White must be 'true' or 'false'",c);
			}
		}
		
	}
	
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
		
		try {
		// Load the lines of code
		Program prog = Program.load("New.zoe");
		
		Compile comp = new Compile();		
		comp.doCompile(prog);
		} catch (CompileException e) {
			//System.out.println(e.getMessage());
			System.out.println(e.code);
			e.printStackTrace();
		}
		
		

	}

}
