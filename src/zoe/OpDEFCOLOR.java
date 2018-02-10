package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.basevm.Operand;

public class OpDEFCOLOR {
	
	public static final int OPCODE = 0x09;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
						
			boolean hasWhite = c.function.prog.configHasWhite;
			
			String t = c.text.substring(9);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			String [] ops = t.split(",");
			if(hasWhite) {
				if(ops.length!=4) {
					throw new CompileException("Expected 4 operands (colorNumber, red, green, blue)",c);
				}
			} else {
				if(ops.length!=5) {
					throw new CompileException("Expected 5 operands (colorNumber, red, green, blue, white)",c);
				}
			}
			c.data.add(OPCODE);
			Operand.parseOperand(c,ops[0]); // slot
			
			if(ops.length>4) {
				Operand.parseOperand(c, ops[4]); // white
			} else {
				c.data.add(0);
				c.data.add(0);
			}			
			
			Operand.parseOperand(c,ops[2]);
			Operand.parseOperand(c,ops[1]);			
			Operand.parseOperand(c,ops[3]);	
			
		}
	}

}
