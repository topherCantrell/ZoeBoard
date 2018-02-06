package dsl;

import java.util.List;

public class Function {	
	
	public Program prog;
	
	public String name;
	public List<String> arguments;
	
	public List<String> localVars;

	public List<CodeLine> codeLines;
	
	public Function(Program prog, String name) {
		this.prog = prog;
		this.name = name;
	}
	
	public int findLabel(String lab) {
		for(int x=0;x<codeLines.size();++x) {
			CodeLine c = codeLines.get(x);
			if(c.isLabel && c.text.equals(lab)) {
				return x;
			}
		}
		return -1;
	}

}
