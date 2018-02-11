package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;
import dsl.basevm.Operand;

public class OpDRAWPATTERN {
	
	public static final int OPCODE = 0x0D;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			
			Parameters p= new Parameters(c);
			if(p.params.length < 3) {
				throw new CompileException("Expected 3 operands (x,y,pattern, [collorOffset)",c);
			}
									
			c.data.add(OPCODE);
			Operand.parseOperand(c,p.params[0]);
			Operand.parseOperand(c,p.params[1]);
			Operand.parseOperand(c,p.params[2]);
			if(p.params.length>3) {
				Operand.parseOperand(c, p.params[3]);
			} else {
				c.data.add(0);
				c.data.add(0);
			}
		} 
	}

}
