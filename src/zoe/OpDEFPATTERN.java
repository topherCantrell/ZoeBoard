package zoe;

import dsl.CodeLine;

public class OpDEFPATTERN {
	
	public static final int OPCODE = 0x0A;
	
	public static void parse(CodeLine c, boolean firstPass) {
		if(firstPass) {
			c.data.add(OPCODE);			
			
			throw new RuntimeException("IMPLEMENT ME");
			
			/*
			 if(commandNospace.startsWith("PATTERN(")) {
					line.data = new ArrayList<Integer>();
					String num = getParam(params,"NUMBER",true);					
					List<String> patLines = new ArrayList<String>();
					++zp;
					while(true) {
						ZoeLine s = event.lines.get(zp++);
						if(s.command.equals("}")) break;
						patLines.add(s.command.trim());
					}
					--zp;
					int width = patLines.get(0).length();
					int height = patLines.size();
					line.data.add(0x0B);event.codeLength+=1;
					line.data.add(Integer.parseInt(num));event.codeLength+=1;
					line.data.add(width);event.codeLength+=1;
					line.data.add(height);event.codeLength+=1;
					for(String s : patLines) {
						for(int x=0;x<width;++x) {
							char c = s.charAt(x);
							if(c=='.') c='0';
							line.data.add(c-'0');event.codeLength+=1;
						}
					}
					
					continue;
				}
			 */
			
		} 
	}

}
