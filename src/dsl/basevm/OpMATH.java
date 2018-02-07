package dsl.basevm;

import dsl.CodeLine;

public class OpMATH {
	
	public static final int OPCODE = 0x02;
	
	public static String [] MATHOPS = {"+",  "-",  "*",  "/",  "%",  "&",  "|",  "^",  "<<", ">>"};
	private static int [] MATHOPSVAL = {0x20, 0x21, 0x00, 0x01, 0x02, 0x18, 0x1A, 0x1B, 0x0B, 0x0A};	
	
	public static void parse(CodeLine c, int op, boolean firstPass) {
		if(firstPass) {
			int i = c.text.indexOf("=");
			int j = c.text.indexOf(MATHOPS[op]);
			c.data.add(OPCODE);
			Operand.parseOperand(c,c.text.substring(i+1, j));
			Operand.parseOperand(c,c.text.substring(j+MATHOPS[op].length()));
			c.data.add(MATHOPSVAL[op]);
			Operand.parseOperand(c,c.text.substring(0, i));
		} 	
	}

}
