package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;
import dsl.basevm.Operand;

public class OpPAUSE {
	
	public static final int OPCODE = 0x08;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			
			Parameters p = new Parameters(c);
			if(p.params.length != 1) {
				throw new CompileException("Expected 1 operand (time)",c);
			}
						
			c.data.add(OPCODE);
			Operand.parseOperand(c,p.params[0]);			
		}
	}

}
