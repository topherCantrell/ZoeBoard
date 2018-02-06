package dsl.basevm;

import dsl.CodeLine;
import dsl.CompileException;

public class OpGOTO {
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(0x03);
			c.data.add(0x00); // Place holder
			c.data.add(0x00); // Place holder
		} else {
			String dst = c.text.substring(5);
			int i = c.function.findLabel(dst);
			if(i<0) {
				throw new CompileException("Label not found",c);
			}
			
			int from = c.address+c.data.size();
			int to = c.function.codeLines.get(i).address;
			
			int ofs = to - from;		
			
			if(ofs>32767 || ofs<-32768) {
				throw new CompileException("Jump out of range",c);
			}
			c.data.set(1, (ofs>>8)&0xFF);
			c.data.set(2, (ofs&0xFF));										
		}
	}

}
