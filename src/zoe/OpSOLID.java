package zoe;

import dsl.CodeLine;

public class OpSOLID {
	
	public static final int OPCODE = 0x0C;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(OPCODE);
			
			throw new RuntimeException("IMPLEMENT ME");
			
		} 
	}

}
