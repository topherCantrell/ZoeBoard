package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Function;

public class OpCALL {
	
	public static final int OPCODE = 0x05;
	
	public static void parse(CodeLine c, boolean firstPass) {
		int i = c.text.indexOf("(");
		String name = c.text.substring(0,i);
		String params = c.text.substring(i+1,c.text.length()-1);
		
		int f = c.function.prog.findFunction(name);
		if(f<0) {
			throw new CompileException("Unknown function",c);
		}
		Function fn = c.function.prog.functions.get(f);
		String [] ps = {};
		if(!params.isEmpty()) ps = params.split(",");
		
		if(ps.length != fn.arguments.size()) {
			throw new CompileException("Incorrect number of arguments",c);
		}
		
		// 05 PP PP NN ..
		if(firstPass) {
			c.data.add(OPCODE);
			c.data.add(0x00);
			c.data.add(0x00);
			c.data.add(ps.length);
			for(String p : ps) {
				Operand.parseOperand(c, p);
			}
		} else {
			int index = c.function.codeLines.indexOf(c);
			i = 0;
			for(int x=0;x<=index;++x) {
				i = i + c.function.codeLines.get(x).data.size();
			}
			// i = rel address of the next line
			// fun.address = rel address of the target function
			int ofs = fn.codeLines.get(0).address - i;				
			if(ofs>32767 || ofs<-32768) {
				throw new CompileException("Jump out of range",c);
			}
			c.data.set(1, (ofs>>8)&0xFF);
			c.data.set(2, (ofs&0xFF));			
		}
	}

}
