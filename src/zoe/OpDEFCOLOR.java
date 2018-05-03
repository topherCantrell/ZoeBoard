package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;
import dsl.basevm.Operand;

public class OpDEFCOLOR {
	
	// defColor(...)
	public static final int OPCODE = 0x09;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
						
			boolean hasWhite = c.function.prog.configHasWhite;
			
			Parameters p = new Parameters(c);
			if(!hasWhite && p.params.length != 4) {
				throw new CompileException("Expected 4 operands (colorNumber, red, green, blue)",c);
			}			
			if(hasWhite && p.params.length !=5) {
				throw new CompileException("Expected 5 operands (colorNumber, red, green, blue, white)",c);
			}
						
			c.data.add(OPCODE);
			Operand.parseOperand(c,p.params[0]); // slot
			
			if(p.params.length>4) {
				Operand.parseOperand(c, p.params[4]); // white
			} else {
				c.data.add(0);
				c.data.add(0);
			}			
			
			Operand.parseOperand(c,p.params[2]);
			Operand.parseOperand(c,p.params[1]);			
			Operand.parseOperand(c,p.params[3]);	
			
		}
	}

}
