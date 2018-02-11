package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Function;
import dsl.Parameters;

public class OpCALL {
	
	public static final int OPCODE = 0x05;
	
	public static void parse(CodeLine c, boolean firstPass) {
		
		Parameters p = new Parameters(c,false);
		String name = c.text.substring(0,p.start);
				
		int f = c.function.prog.findFunction(name);
		if(f<0) {
			throw new CompileException("Unknown function",c);
		}
		Function fn = c.function.prog.functions.get(f);
		
		if(p.params.length != fn.arguments.size()) {
			throw new CompileException("Incorrect number of arguments",c);
		}
		
		// 05 PP PP NN ..
		if(firstPass) {
			c.data.add(OPCODE);
			c.data.add(p.params.length);				
			for(String pa : p.params) {
				Operand.parseOperand(c, pa);
			}
			c.data.add(0x00);
			c.data.add(0x00);		
		} else {
			int index = c.function.codeLines.indexOf(c);
			int i = 0;
			for(int x=0;x<=index;++x) {
				i = i + c.function.codeLines.get(x).data.size();
			}
			// i = rel address of the next line
			// fun.address = rel address of the target function
			int ofs = fn.codeLines.get(0).address - i;				
			if(ofs>32767 || ofs<-32768) {
				throw new CompileException("Jump out of range",c);
			}
			int pa = p.params.length*2+2;
			c.data.set(pa, (ofs>>8)&0xFF);
			c.data.set(pa+1, (ofs&0xFF));			
		}
	}

}
