package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.basevm.Operand;

public class OpPAUSE {
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			String t = c.text.substring(6);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			c.data.add(0x08);
			Operand.parseOperand(c,t);			
		}
	}

}
