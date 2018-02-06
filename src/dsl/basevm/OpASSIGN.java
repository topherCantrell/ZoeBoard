package dsl.basevm;

import dsl.CodeLine;

public class OpASSIGN {
	
	public static final int OPCODE = 0x01;
	
	public static void parse(CodeLine c, boolean firstPass) {		
		if(firstPass) {
			int i = c.text.indexOf('=');
			c.data.add(OPCODE);
			Operand.parseOperand(c,c.text.substring(i+1));
			Operand.parseOperand(c,c.text.substring(0, i));			
		} 	
	}

}
