package zoe;

import dsl.CodeLine;
import dsl.CompileException;

public class OpDEFPATTERN {
	
	public static final int OPCODE = 0x0A;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {				
			
			// 0A nn ww hh ... (number, width, height, data)
			
			// TODO this should be a general purpose function
			int i = c.text.indexOf("(");
			int j = c.text.indexOf(")",i);
			
			String [] params = c.text.substring(i+1,j).split(",");
			if(params.length!=3) {
				throw new CompileException("Expected 3 numbers (n,w,h)",c);
			}
			
			int num = Integer.parseInt(params[0]);
			int width = Integer.parseInt(params[1]);
			int height = Integer.parseInt(params[2]);			
			
			String data = c.text.substring(j+1);
			
			if(data.length() != width*height) {
				throw new CompileException("Expected "+(width*height)+" data values",c);
			}
			
			c.data.add(OPCODE);	
			c.data.add(num);
			c.data.add(width);
			c.data.add(height);
			
			for(int x=0;x<data.length();++x) {
				char h = data.charAt(x);
				if(h=='.') {
					c.data.add(0);
				} else {
					c.data.add(h-'0');
				}
			}						
			
		} 
	}

}
