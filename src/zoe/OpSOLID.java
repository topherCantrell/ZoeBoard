package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;
import dsl.basevm.Operand;

//setSolid(...)
public class OpSOLID {
	
	public static final int OPCODE = 0x0C;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {		
			
			Parameters p = new Parameters(c);
			if(p.params.length != 1) {
				throw new CompileException("Expected 1 operand (color)",c);
			}
			
			c.data.add(OPCODE);			
			Operand.parseOperand(c,p.params[0]);			
		} 
	}

}
