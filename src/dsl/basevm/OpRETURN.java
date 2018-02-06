package dsl.basevm;

import dsl.CodeLine;

public class OpRETURN {
	
	public static final int OPCODE = 0x06;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(OPCODE);
		}
	}

}
