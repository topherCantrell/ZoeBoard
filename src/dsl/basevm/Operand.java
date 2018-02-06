package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;

public class Operand {
	
	public static void parseOperand(CodeLine c, String op) {
		if(op.equals("__RETVAL__")) {
			c.data.add(0x82);
			c.data.add(0x00);
			return;
		}
		try {
			int v = Integer.parseInt(op);
			if(v<-16384 || v>16383) {
				throw new CompileException("Constant out of range",c);
			}
			v = v&0x7FFF;
			c.data.add((v>>8)&0xFF);
			c.data.add(v&0xFF);			
		} catch (Exception e) {
			int i = c.function.prog.findGlobal(op);
			
			if(i>=0) {
				c.data.add(0x80);
				c.data.add(i);
				return;
			}
			
			i = c.function.arguments.indexOf(op);
			if(i>=0) {
				c.data.add(0x81);
				c.data.add(i);
				return;
			}
			
			i = c.function.localVars.indexOf(op);
			if(i>=0) {
				c.data.add(0x81);
				c.data.add(i+c.function.arguments.size());
				return;
			}
			
			throw new CompileException("Unknown operand '"+op+"'",c);
			
		}
	}
}
