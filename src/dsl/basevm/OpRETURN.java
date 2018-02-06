package dsl.basevm;

import dsl.CodeLine;

public class OpRETURN {
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(0x06);
		}
	}

}
