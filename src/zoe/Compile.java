package zoe;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Function;
import dsl.Program;
import dsl.ToSpin;
import dsl.basevm.OpASSIGN;
import dsl.basevm.OpCALL;
import dsl.basevm.OpGOTO;
import dsl.basevm.OpIF;
import dsl.basevm.OpMATH;
import dsl.basevm.OpRESLOCAL;
import dsl.basevm.OpRETURN;
import dsl.basevm.Preprocessor;

public class Compile {
	
	Program prog;
		
	public void dumpCodeLines(String message, List<CodeLine> lines) {
		System.out.println(message);
		for(CodeLine c : lines) {
			if(c.isLabel) {
				System.out.println("##"+c.text+"##");
			} else {
				System.out.println(c.text);
			}
		}		
	}
	
	public void doCompile(Program prog) {
		
		this.prog = prog;
		
		// Hoist all the "var" definitions
		prog.vars = hoistVars(prog.globalLines);
		//dumpCodeLines("----Global-----",prog.globalLines);
		//System.out.println(prog.vars);
		for(Function fun : prog.functions) {
			fun.localVars = hoistVars(fun.codeLines);
			//dumpCodeLines("----Function "+fun.name+"----",fun.codeLines);
			//System.out.println(fun.localVars);
		}
		
		// Read the "configure" command
		readConfigure(prog);
		
		if(prog.globalLines.size()>1) {
			throw new CompileException("Not allowed in global area",prog.globalLines.get(1));
		}
		
		// Check the entry "init" function
		int ch = prog.findFunction("init");
		if(ch<0) {
			throw new CompileException("Must have an 'init()' function",null);
		}		
		Function init = prog.functions.get(ch);		
		if(init.arguments.size()!=0) {
			throw new CompileException("The 'init()' function must take no arguments",null);
		}
										
		Preprocessor pre = new Preprocessor(prog);
		pre.preprocess();
		
		// Function by function, line by line, pass by pass
		
		int address = 0;
		for(Function fun : prog.functions) {			
			compileFunction(fun,true);
			for(CodeLine c : fun.codeLines) {
				c.address = address;
				address = address + c.data.size();
			}
		}
		// Complete the first pass on everything so we have the function addresses.
		for(Function fun : prog.functions) {
			compileFunction(fun,false);			
		}
				
	}
	
	void compileFunction(Function fun, boolean firstPass) {
		for(int x=0;x<fun.codeLines.size();++x) {
			CodeLine c = fun.codeLines.get(x);
			if(c.isLabel) continue;
			
			// Zoe Specific
			
			if(c.text.startsWith("PAUSE(")) {
				OpPAUSE.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("setSolid(")) {
				OpSOLID.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("setPixel(")) {
				OpSETPIXEL.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("defColor(")) {
				OpDEFCOLOR.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("defPattern(")) {
				OpDEFPATTERN.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("drawPattern(")) {
				OpDRAWPATTERN.parse(c, firstPass);
				continue;
			}
			
			// Base VM Commands				
			
			if(c.text.startsWith("if(")) {
				// if(op ? op) then label
				OpIF.parse(c, firstPass);
				continue;				
			}
			
			if(c.text.contains("=")) {
				boolean isMath = false;
				for(int mx=0;mx<OpMATH.MATHOPS.length;++mx) {
					String s = OpMATH.MATHOPS[mx];
					if(c.text.contains(s)) {
						OpMATH.parse(c,mx,firstPass);						
						isMath = true;
						break;
					}
				}
				
				if(!isMath) {
					OpASSIGN.parse(c,firstPass);
				}
				continue;
			}
			
			if(c.text.startsWith("goto ")) {
				OpGOTO.parse(c, firstPass);
				continue;
			}
			
			if(c.text.startsWith("reslocal(")) {
				OpRESLOCAL.parse(c,firstPass);
				continue;
			}
			
			int i = c.text.indexOf("(");
			if(i>0 && c.text.endsWith(")")) {
				OpCALL.parse(c, firstPass);
				continue;
			}
			
			if(c.text.equals("return")) {
				OpRETURN.parse(c,firstPass);
				continue;
			}
			
			throw new CompileException("Unknown instruction",c);
			
		}
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
		for(int x=lines.size()-1;x>=0;x=x-1) {
			CodeLine c = lines.get(x);		
			if(c.text.startsWith("var ")) {
				String s = c.text;
				int i = s.indexOf("=");
				if(i>=0) s = s.substring(0,i);
				s = s.substring(4);
				if(vars.contains(s)) {
					throw new CompileException("Var already defined",c);
				}
				vars.add(s);
				if(i>=0) {
					// This is an assignment ... keep the X=Y part
					c.text = c.text.substring(4);
					c.changed = true;
				} else {
					// This is a declaration ... remove it from the code
					lines.remove(x);
				}
			}
		}
		return vars;
	}

	public static void main(String[] args) throws Exception {
		
		try {
			
			// Load the lines of code
			Program prog = Program.load("D1.zoe");			
			PrintStream ps = new PrintStream("spin/ProgramDataD1.spin");
			Compile comp = new Compile();		
			comp.doCompile(prog);			
			ToSpin.toSpin(prog,ps);
			ps.flush();
			ps.close();
			
			prog = Program.load("D2.zoe");			
			ps = new PrintStream("spin/ProgramDataD2.spin");
			comp = new Compile();		
			comp.doCompile(prog);			
			ToSpin.toSpin(prog,ps);
			ps.flush();
			ps.close();
			
			prog = Program.load("D3.zoe");			
			ps = new PrintStream("spin/ProgramDataD3.spin");
			comp = new Compile();		
			comp.doCompile(prog);			
			ToSpin.toSpin(prog,ps);
			ps.flush();
			ps.close();
			
			prog = Program.load("D4.zoe");			
			ps = new PrintStream("spin/ProgramDataD4.spin");
			comp = new Compile();		
			comp.doCompile(prog);			
			ToSpin.toSpin(prog,ps);
			ps.flush();
			ps.close();
		
		} catch (CompileException e) {
			//System.out.println(e.getMessage());
			System.out.println(e.code);
			e.printStackTrace();
		}

	}

}
