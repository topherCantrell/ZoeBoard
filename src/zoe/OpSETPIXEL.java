package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.basevm.Operand;

public class OpSETPIXEL {
	
	public static final int OPCODE = 0x0B;
	
	public static void parse(CodeLine c, boolean firstPass) {
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
			c.data.add(OPCODE);
			Operand.parseOperand(c,ops[0]);
			Operand.parseOperand(c,ops[1]);
		}
	}

}
