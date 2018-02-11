package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;

public class OpRESLOCAL {
	
	public static final int OPCODE = 0x04;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {			
			
			Parameters p = new Parameters(c);
			if(p.params.length!=1) {
				throw new CompileException("Expected one parameter (numLocals)",c);
			}
						
			try {
				int num = Integer.parseInt(p.params[0]);
				c.data.add(OPCODE);
				c.data.add(num);
			} catch(Exception e) {
				throw new CompileException("Invalid number",c);
			}			
		}
	}

}
