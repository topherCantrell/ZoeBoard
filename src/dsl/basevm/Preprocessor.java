package dsl.basevm;

import java.util.List;

import dsl.CodeLine;
import dsl.CompileException;
import dsl.Function;
import dsl.Program;

public class Preprocessor {
	
	Program prog;
	
	public Preprocessor(Program prog) {
		this.prog = prog;
	}
	
	void addRESLOCALs() {
		for(Function fun : prog.functions) {
			if(fun.localVars!=null && fun.localVars.size()>0) {
				fun.codeLines.add(0,new CodeLine(fun,"",0,"reslocal("+fun.localVars.size()+")"));			
			}
		}
	}
	
	void addRETURNs() {
		for(Function fun : prog.functions) {
			if(fun.codeLines.size()>0 && fun.codeLines.get(fun.codeLines.size()-1).text.startsWith("return")) {
				continue;
			}
			fun.codeLines.add(new CodeLine(fun,"",0,"return"));
		}
	}
	
	void fixRETURNVALUES() {
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("return ")) {					
					c.text = "__return__="+c.text.substring(7);
					fun.codeLines.add(x+1,new CodeLine(fun,"",0,"return"));
				}
			}
		}
	}
	
	void fixSTORERETURNs() {
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("if(")) continue;
				if(c.text.startsWith("}elseif(")) continue;
				if(c.text.contains("=") && c.text.contains("(")) {
					int i = c.text.indexOf("=");
					String v = c.text.substring(0,i);
					c.text = c.text.substring(i+1);
					fun.codeLines.add(x+1,new CodeLine(fun,"",0,v+"=__RETVAL__"));					
				}
			}
		}		
	}
	
	int findCloseBrace(Function fun,int index, boolean multi) {
		int level = 1;
		List<CodeLine> lines = fun.codeLines;
		while(true) {
			++index;
			if(index>=lines.size()) {
				throw new CompileException("Expected closing brace",lines.get(index));
			}
			CodeLine n = lines.get(index);			
			
			if(level==1 && n.text.equals("}else{")) {
				if(multi) {
					++level;
				}
			} else {
				if(n.text.contains("{")) {				
					++level;
				}
			}
			
			if(n.text.contains("}")) {
				--level;
				if(level==0) {
					return index;
				}
			}			
		}		
	}
	
	
	void fixELSEIFs() {			
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("}elseif(")) {
					int i = findCloseBrace(fun,x,true);
					// Add a "}" after the last "elseif" or "else" line 
					// The trick here is finding the "end"
					fun.codeLines.add(i+1,new CodeLine(fun,"",0,"}"));
					// Move the "if(..." to the next line
					fun.codeLines.add(x+1,new CodeLine(fun,"",0,c.text.substring(5)));				
					// Change the current line to "}else{"					
					c.text = "}else{";					
				}
			}
		}		
	}
	
	void fixIFs() {		
		int constructNumber = 0;
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);
				if(c.text.startsWith("if(") && c.text.contains("{")) {
					++constructNumber;
					int endOfAll = findCloseBrace(fun,x,true);
					int endOfIf = findCloseBrace(fun,x,false);
					
					// change c to //if(...)elseif1_1
					if(!c.text.endsWith("{")) {
						throw new CompileException("Expected '{'",c);
					}
					if(endOfAll == endOfIf) {
						c.text = c.text.substring(0,c.text.length()-1)+"else__ff"+constructNumber+"_END";
					} else {
						c.text = c.text.substring(0,c.text.length()-1)+"else__ff"+constructNumber+"_ELSE";
					}
										
					// change endOfIf to "if1_1:"
					CodeLine ceoi = fun.codeLines.get(endOfIf);
					ceoi.isLabel = true;
					ceoi.text = "__ff"+constructNumber+"_ELSE";
					
					// change endOfAll to "if1_2:"
					CodeLine ecoa = fun.codeLines.get(endOfAll);
					ecoa.isLabel = true;
					ecoa.text="__ff"+constructNumber+"_END";					
					
					// if has else ... add a "goto if1_2" BEFORE endOfIf
					if(endOfAll!=endOfIf) {						
						fun.codeLines.add(endOfIf,new CodeLine(fun,"",0,"goto __ff"+constructNumber+"_END"));
					}										
				}
			}
		}
	}
	
	public void fixDOs() {
		
		int constructNumber = 0;
		
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);				
				if(c.text.startsWith("do{")) {
					
					int i = findCloseBrace(fun,x,false);
					
					CodeLine wh = fun.codeLines.get(i);
					if(!wh.text.startsWith("}while(")) {
						throw new CompileException("Expected 'while'",wh);
					}
					
					++constructNumber;
					
					// __do?_START:                  - before c
					// __do?_CONTINUE:               - in place of c
					// if(...) then __do1_CONTINUE   - in place of wh
					// __do1_BREAK:                  - after wh
					
					c.isLabel = true;
					c.text = "__do"+constructNumber+"_CONTINUE";
					
					wh.text = "if"+wh.text.substring(6)+"then__do"+constructNumber+"_CONTINUE";
					
					CodeLine brk = new CodeLine(fun,"",0,"__do"+constructNumber+"_BREAK");
					brk.isLabel = true;
					fun.codeLines.add(i+1,brk);
					
					CodeLine startLab = new CodeLine(fun,"",0,"__do"+constructNumber+"_START");
					startLab.isLabel = true;
					fun.codeLines.add(x,startLab);					
															
				}				
			}
		}		
		
	}
	
	public void fixWHILEs() {
		
		int constructNumber = 0;
		
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);				
				if(c.text.startsWith("while(")) {
					
					int i = findCloseBrace(fun,x,false);
					CodeLine wh = fun.codeLines.get(i);
					
					++constructNumber;
					
					// __do?_START:           - before c
					// goto __do?_CONTINUE    - in place of c
					// _do?_TOP:             - after c
					// __do?_CONTINUE:        - before wh
					// if(...) then __do?_TOP - in place of wh
					// __do?_BREAK:           - after wh
					
					CodeLine brk = new CodeLine(fun,"",0,"__do"+constructNumber+"_BREAK");
					brk.isLabel = true;
					fun.codeLines.add(i+1,brk);
					
					wh.text = "if"+c.text.substring(5,c.text.length()-1)+"then__do"+constructNumber+"_TOP";
					
					CodeLine cont = new CodeLine(fun,"",0,"__do"+constructNumber+"_CONTINUE");
					cont.isLabel = true;
					fun.codeLines.add(i,cont);					
					
					CodeLine top = new CodeLine(fun,"",0,"__do"+constructNumber+"_TOP");
					top.isLabel = true;
					fun.codeLines.add(x+1,top);
					
					c.text = "goto __do"+constructNumber+"_CONTINUE";
					
					CodeLine start = new CodeLine(fun,"",0,"__do"+constructNumber+"_START");
					start.isLabel = true;
										
				}
			}
		}
						
	}
	
	public void fixBREAKs() {
		
		// break LAB:
		// look down to find the __doLAB_BREAK (or next __BREAK if no LAB)
		
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);				
				if(c.text.startsWith("break")) {
					String trg = c.text.substring(5);
					
					int y = x;
					while(true) {
						y = y + 1;
						CodeLine b = fun.codeLines.get(y);
						if(b.isLabel && )
					}
					
					
					//System.out.println("::"+c.text+"::");
				}
			}
		}				
	}
	
	public void fixCONTINUEs() {
		
		// continue LAB:
		// look up to find the __doLAB_TOP (or next _TOP if no LAB)
		// look down to find the __doLAB_CONTINUE (or next __CONTINUE if no LAB)
		
		for(Function fun : prog.functions) {
			for(int x=0;x<fun.codeLines.size();++x) {				
				CodeLine c = fun.codeLines.get(x);				
				if(c.text.startsWith("continue ") || c.text.equals("continue")) {
					System.out.println("::"+c.text+"::");
				}
			}
		}				
	}
	
	public void dumpLines() {
		for(Function f : prog.functions) {
			System.out.println("##"+f.name+"##");
			for(CodeLine c : f.codeLines) {
				System.out.println(c.text);
			}
		}
	}
	
	public void preprocess() {
		
		// Convert DO loops to pure IF-THEN-GOTO
		fixDOs();
		fixWHILEs();
		//fixFORs(); // TODO
		fixBREAKs();
		fixCONTINUEs();		
		
		// Convert ELSE-IF to nested IF
		fixELSEIFs();
		
		// Reserve space for locals in all functions
		addRESLOCALs();
		
		// Make sure every function has a return at the end
		addRETURNs();
		
		// Change "return value" to "p=value;return"
		fixRETURNVALUES();
		
		// Change "a = function()" to "function();a=__RETVAL__"
		fixSTORERETURNs();
		
		// Convert IF/ELSE to pure IF-THEN	
		fixIFs();			
				
		//dumpLines();
				
		// Maybe one day:
		// Convert complex math expressions to a series of simple math calls
		// Convert complex logic expressions to a series of simple ifs
		
	}

}
