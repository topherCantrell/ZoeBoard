package dsl.basevm;

import dsl.CodeLine;

public class OpASSIGN {
	
	public static void parse(CodeLine c, boolean firstPass) {
		int i = c.text.indexOf('=');
		if(firstPass) {
			c.data.add(0x01);
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
		} else {
			c.data.clear();
			c.data.add(0x01);
			Operand.parseOperand(c,c.text.substring(i+1));
			Operand.parseOperand(c,c.text.substring(0, i));			
		}		
	}

}
