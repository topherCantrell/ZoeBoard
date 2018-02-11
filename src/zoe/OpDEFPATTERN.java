package zoe;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Parameters;

public class OpDEFPATTERN {
	
	public static final int OPCODE = 0x0A;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {				
			
			// 0A nn ww hh ... (number, width, height, data)
			
			int num;
			int width;
			int height;
			
			Parameters p= new Parameters(c,false);
			if(p.params.length != 3) {
				throw new CompileException("Expected 3 numbers (n,w,h)",c);
			}
			try {
				num = Integer.parseInt(p.params[0]);
				width = Integer.parseInt(p.params[1]);
				height = Integer.parseInt(p.params[2]);
			} catch (Exception e) {
				throw new CompileException("Invalid number",c);
			}
			
			String data = c.text.substring(p.end+1);
			
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
