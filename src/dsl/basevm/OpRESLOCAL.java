package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;

public class OpRESLOCAL {
	
	public static final int OPCODE = 0x04;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {			
			String s = c.text.substring(9);
			if(!s.endsWith(")")) {
				throw new CompileException("Expected ')'",c);
			}
			s = s.substring(0, s.length()-1);
			try {
				int num = Integer.parseInt(s);
				c.data.add(OPCODE);
				c.data.add(num);
			} catch(Exception e) {
				throw new CompileException("Invalid number",c);
			}			
		}
	}

}
