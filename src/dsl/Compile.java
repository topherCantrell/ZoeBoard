package dsl;

import java.util.ArrayList;
import java.util.List;

public class Compile {
	
	Program prog;
	
	String [] MATHOPS = {"+",  "-",  "*",  "/",  "%",  "&",  "|",  "^",  "<<", ">>"};
	int [] MATHOPSVAL = {0x20, 0x21, 0x00, 0x01, 0x02, 0x18, 0x1A, 0x1B, 0x0B, 0x0A};
	
	// Longer matches defined first giving match-precedence for things like ">=" over ">"
	String [] LOGICOPS = {"==", "!=", ">=", "<=", ">",  "<"};
	int [] LOGICOPSVAL = {0x0A, 0x05, 0x03, 0x0E, 0x01, 0x0C};
	
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
		
		// Function by function, line by line
		
		for(Function fun : prog.functions) {
			compileFunction(fun,true);
			compileFunction(fun,false);
		}
				
	}
	
	int findLabel(Function fun,String lab) {
		for(int x=0;x<fun.codeLines.size();++x) {
			CodeLine c = fun.codeLines.get(x);
			if(c.isLabel && c.text.equals(lab)) {
				return x;
			}
		}
		return -1;
	}
	
	void parseOperand(Function fun, CodeLine c, String op) {
		try {
			int v = Integer.parseInt(op);
			if(v<-16384 || v>16383) {
				throw new CompileException("Constant out of range",c);
			}
			v = v&0x7FFF;
			c.data.add((v>>8)&0xFF);
			c.data.add(v&0xFF);			
		} catch (Exception e) {
			int i = prog.findGlobal(op);
			if(i<0) {
				throw new CompileException("TODO IMPLEMENT MORE ... STACK VARIABLE",c);
			}
			c.data.add(0x80);
			c.data.add(i);
		}
	}
	
	void parsePAUSE(Function fun,CodeLine c,boolean firstPass) {
		if(firstPass) {
			String t = c.text.substring(6);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			c.data.add(0x08);
			parseOperand(fun,c,t);			
		}
	}
	
	void parseSETPIXEL(Function fun, CodeLine c, boolean firstPass) {
		if(firstPass) {
			String t = c.text.substring(9);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			String [] ops = t.split(",");
			if(ops.length!=2) {
				throw new CompileException("Expected 2 operands (pixelNumber and color)",c);
			}
			c.data.add(0x0B);
			parseOperand(fun,c,ops[0]);
			parseOperand(fun,c,ops[1]);
		}
	}
	
	void parseGOTO(Function fun, CodeLine c, int index, boolean firstPass) {
		if(firstPass) {
			c.data.add(0x03);
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
		} else {
			String dst = c.text.substring(5);
			int i = findLabel(fun,dst);
			if(i<0) {
				throw new CompileException("Label not found",c);
			}
			if(index<i) {
				int ofs = 0;
				while(index<i) {
					ofs = ofs + fun.codeLines.get(index).data.size();
					++index;
				}
				if(ofs>32767) {
					throw new CompileException("Jump out of range",c);
				}
				c.data.set(1, (ofs>>8)&0xFF);
				c.data.set(2, (ofs&0xFF));
			} else {
				int ofs = 0;
				do {
					ofs = ofs - fun.codeLines.get(index).data.size();
					index = index - 1;
				} while(index>i);
				if(ofs<-32768) {
					throw new CompileException("Jump out of range",c);
				}
				//System.out.println(ofs);
				c.data.set(1,  (ofs>>8)&0xFF);
				c.data.set(2, (ofs&0xFF));
			}
		}
	}
	
	void parseMATH(Function fun, CodeLine c, int op, boolean firstPass) {
		if(firstPass) {
			c.data.add(0x02);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);			
		} else {
			int i = c.text.indexOf("=");
			int j = c.text.indexOf(MATHOPS[op]);
			c.data.clear();
			c.data.add(0x02);
			parseOperand(fun,c,c.text.substring(i+1, j));
			parseOperand(fun,c,c.text.substring(j+MATHOPS[op].length()));
			c.data.add(MATHOPSVAL[op]);
			parseOperand(fun,c,c.text.substring(0, i));
		}		
	}
	
	void parseASSIGN(Function fun, CodeLine c, boolean firstPass) {
		int i = c.text.indexOf('=');
		if(firstPass) {
			c.data.add(0x01);
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
		} else {
			c.data.clear();
			c.data.add(0x01);
			parseOperand(fun,c,c.text.substring(i+1));
			parseOperand(fun,c,c.text.substring(0, i));			
		}		
	}
	
	void parseIF(Function fun, CodeLine c, boolean firstPass) {
		int i = c.text.lastIndexOf(")");
		if(i<0) {
			throw new CompileException("Expected ')'",c);
		}
		System.out.println(c.text);
		if(!c.text.substring(i+1,i+5).equals("then")) {
			throw new CompileException("Expected 'then'",c);
		}
		String lab = c.text.substring(i+5);
		String expr = c.text.substring(3,i);
		int op = -1;
		int opIndex = -1;
		for(int x=0;x<LOGICOPS.length;++x) {
			opIndex = expr.indexOf(LOGICOPS[x]);
			if(opIndex>=0) {
				op = x;
				break;
			}
		}		
		if(op<0) {
			throw new CompileException("Unknown operation",c);
		}
		String left = expr.substring(0,opIndex);
		String right = expr.substring(opIndex+LOGICOPS[op].length());
		
		System.out.println("::"+left+"::"+LOGICOPS[op]+"::"+right+"::"+lab);
		
		throw new RuntimeException("IMPLEMENT ME");
	}
	
	void compileFunction(Function fun, boolean firstPass) {
		for(int x=0;x<fun.codeLines.size();++x) {
			CodeLine c = fun.codeLines.get(x);
			if(c.isLabel) continue;
				
			// if(op ? op) then label
			if(c.text.startsWith("if(")) {
				parseIF(fun,c,firstPass);
				continue;				
			}
			
			if(c.text.contains("=")) {
				boolean isMath = false;
				for(int mx=0;mx<MATHOPS.length;++mx) {
					String s = MATHOPS[mx];
					if(c.text.contains(s)) {
						parseMATH(fun,c,mx,firstPass);
						isMath = true;
						break;
					}
				}
				
				if(!isMath) {
					parseASSIGN(fun,c,firstPass);
				}
				continue;
			}
			
			if(c.text.startsWith("PAUSE(")) {
				parsePAUSE(fun,c,firstPass);
				continue;
			}
			
			if(c.text.startsWith("setPixel(")) {
				parseSETPIXEL(fun,c,firstPass);
				continue;
			}
			
			if(c.text.startsWith("goto ")) {
				parseGOTO(fun,c, x, firstPass);
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
			Program prog = Program.load("New.zoe");
			
			Compile comp = new Compile();		
			comp.doCompile(prog);
			
			ToSpin.toSpin(prog);
		
		} catch (CompileException e) {
			//System.out.println(e.getMessage());
			System.out.println(e.code);
			e.printStackTrace();
		}
		
		

	}

}
