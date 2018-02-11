package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;
import dsl.basevm.Operand;

public class OpSETPIXEL {
	
	public static final int OPCODE = 0x0B;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			
			Parameters p= new Parameters(c);
			if(p.params.length != 2) {
				throw new CompileException("Expected 2 operands (pixelNumber and color)",c);
			}
						
			c.data.add(OPCODE);
			Operand.parseOperand(c,p.params[0]);
			Operand.parseOperand(c,p.params[1]);
		}
	}

}
