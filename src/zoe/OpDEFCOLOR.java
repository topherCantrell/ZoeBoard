package zoe;

import dsl.CodeLine;

public class OpDEFCOLOR {
	
	public static final int OPCODE = 0x09;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(OPCODE);
			
			throw new RuntimeException("IMPLEMENT ME");
			
			/*
			 if(commandNospace.startsWith("DEFINECOLOR(")) {
					line.data = new ArrayList<Integer>();
					String col = getParam(params,"COLOR",true);
					String w = getParam(params,"W",hasWhite);
					if(w==null) w="0";
					String r = getParam(params,"R",true);
					String g = getParam(params,"G",true);
					String b = getParam(params,"B",true);
					line.data.add(0x09);event.codeLength+=1;					
					addParam(line.data,col);event.codeLength+=2;
					
					if(hasWhite) {
						addParam(line.data,g);event.codeLength+=2;
						addParam(line.data,r);event.codeLength+=2;
						addParam(line.data,b);event.codeLength+=2;
						addParam(line.data,w);event.codeLength+=2;
					} else {
						addParam(line.data,w);event.codeLength+=2;
						addParam(line.data,g);event.codeLength+=2;
						addParam(line.data,r);event.codeLength+=2;
						addParam(line.data,b);event.codeLength+=2;
					}
					
										
					continue;
				}
			 */
		}
	}

}
