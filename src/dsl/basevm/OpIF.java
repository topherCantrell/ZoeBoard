package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;

public class OpIF {
	
	public static final int OPCODE = 0x07;
	
	// Longer matches defined first giving match-precedence for things like ">=" over ">"
	public static String [] LOGICOPS = {"==", "!=", ">=", "<=", ">",  "<"};	
	private static int [] LOGICOPSVAL = {0x0A, 0x05, 0x03, 0x0E, 0x01, 0x0C};
	public static int [] LOGICREV =     { 1,   0,   5,     4,    3,    2};
	
	public static void parse(CodeLine c, boolean firstPass) {
		int i = c.text.lastIndexOf(")");
		if(i<0) {
			throw new CompileException("Expected ')'",c);
		}
		boolean elseThen=false;
		if(c.text.substring(i+1,i+5).equals("else")) {
			elseThen = true;
		} else if(!c.text.substring(i+1,i+5).equals("then")) {
			throw new CompileException("Expected 'then'",c);
		}
		String lab = c.text.substring(i+5);
		String expr = c.text.substring(3,i);
		
		if(expr.equals("true")) {
			expr = "1==1";
		} else if(expr.equals("false")) {
			expr = "1==0";
		}
		int op = -1;
		int opIndex = -1;
		int orgOp = -1;
		for(int x=0;x<OpIF.LOGICOPS.length;++x) {
			opIndex = expr.indexOf(OpIF.LOGICOPS[x]);
			if(opIndex>=0) {
				orgOp = x;
				op = x;
				if(elseThen) {
					op = LOGICREV[op];
				} 
				break;
			}
		}		
		if(op<0) {
			throw new CompileException("Unknown operation",c);
		}

		String left = expr.substring(0,opIndex);
		String right = expr.substring(opIndex+OpIF.LOGICOPS[orgOp].length());
		
		if(firstPass) {
			c.data.add(OPCODE);
			c.data.add(0x00);
			c.data.add(0x00);
			Operand.parseOperand(c, left);
			c.data.add(OpIF.LOGICOPSVAL[op]);
			Operand.parseOperand(c, right);
		} else {
			i = c.function.findLabel(lab);
			if(i<0) {
				throw new CompileException("Label not found",c);
			}
			
			int from = c.address+c.data.size();
			int to = c.function.codeLines.get(i).address;
			
			int ofs = to - from;		
			
			if(ofs>32767 || ofs<-32768) {
				throw new CompileException("Jump out of range",c);
			}
			c.data.set(1, (ofs>>8)&0xFF);
			c.data.set(2, (ofs&0xFF));	
									
		}
	}

}
