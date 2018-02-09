package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.basevm.Operand;

public class OpDRAWPATTERN {
	
	/*
	 
	  defPattern(number=0) {
          11111
          1....
          1....
          .111.
          ....1
          ....1
          11111
      }
	 
	 */
	
	public static final int OPCODE = 0x0D;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			String t = c.text.substring(9);
			if(!t.endsWith(")")) {
				throw new CompileException("Expected closing ')'",c);
			}
			t = t.substring(0, t.length()-1);
			String [] ops = t.split(",");
			if(ops.length<3) {
				throw new CompileException("Expected 3 operands (x,y,pattern, [collorOffset)",c);
			}
			c.data.add(OPCODE);
			Operand.parseOperand(c,ops[0]);
			Operand.parseOperand(c,ops[1]);
			Operand.parseOperand(c,ops[2]);
			if(ops.length>3) {
				Operand.parseOperand(c, ops[3]);
			} else {
				c.data.add(0);
				c.data.add(0);
			}
		} 
	}

}
