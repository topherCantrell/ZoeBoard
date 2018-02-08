package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.basevm.Operand;

public class OpSOLID {
	
	public static final int OPCODE = 0x0C;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {			
			String t = c.text.substring(9);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			
			c.data.add(OPCODE);			
			Operand.parseOperand(c,t);			
		} 
	}

}
