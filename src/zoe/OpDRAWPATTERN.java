package zoe;

import dsl.CodeLine;

public class OpDRAWPATTERN {
	
	public static final int OPCODE = 0x0D;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(OPCODE);			
			
			throw new RuntimeException("IMPLEMENT ME");
			
			/*
			 if(commandNospace.startsWith("DRAWPATTERN(")) {					
					line.data = new ArrayList<Integer>();
					line.data.add(0x0C);event.codeLength+=1;
					String num = getParam(params,"NUMBER",true);
					String x = getParam(params,"X",true);
					String y = getParam(params,"Y",true);
					String ofs = getParam(params,"COLOROFFSET",false);
					if(ofs==null) ofs="0";
					addParam(line.data,num);event.codeLength+=2;
					addParam(line.data,x);event.codeLength+=2;
					addParam(line.data,y);event.codeLength+=2;
					addParam(line.data,ofs);event.codeLength+=2;
					continue;
				}
			 */
		} 
	}

}
