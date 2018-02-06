package dsl.basevm;

import dsl.CodeLine;

public class OpMATH {
	
	public static String [] MATHOPS = {"+",  "-",  "*",  "/",  "%",  "&",  "|",  "^",  "<<", ">>"};
	private static int [] MATHOPSVAL = {0x20, 0x21, 0x00, 0x01, 0x02, 0x18, 0x1A, 0x1B, 0x0B, 0x0A};
	
	// Longer matches defined first giving match-precedence for things like ">=" over ">"
	public static String [] LOGICOPS = {"==", "!=", ">=", "<=", ">",  "<"};
	public static int [] LOGICOPSVAL = {0x0A, 0x05, 0x03, 0x0E, 0x01, 0x0C};
	
	public static void parse(CodeLine c, int op, boolean firstPass) {
		if(firstPass) {
			c.data.add(0x02);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(0x00);			
		} else {
			int i = c.text.indexOf("=");
			int j = c.text.indexOf(MATHOPS[op]);
			c.data.clear();
			c.data.add(0x02);
			Operand.parseOperand(c,c.text.substring(i+1, j));
			Operand.parseOperand(c,c.text.substring(j+MATHOPS[op].length()));
			c.data.add(MATHOPSVAL[op]);
			Operand.parseOperand(c,c.text.substring(0, i));
		}		
	}

}
