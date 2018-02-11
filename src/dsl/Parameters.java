package dsl;

public class Parameters {
	
	public CodeLine c;
	
	public int start;
	public int end;
	
	public String [] params;
	
	public Parameters(CodeLine c) {
		this(c,true);
	}
	
	public Parameters(CodeLine c, boolean noExtra) {
		this.c = c;
		start = c.text.indexOf("(");
		end = c.text.lastIndexOf(")");
		if(noExtra && end!=c.text.length()-1) {
			throw new CompileException("Unexpected characters after '(...)'",c);
		}
		
		// TODO we can follow opens and closes as needed
		if(start<0 || end<0) {
			throw new CompileException("Expected '(...)'",c);
		}
		
		params = c.text.substring(start+1, end).split(",");
		if(params.length==1 && params[0].isEmpty()) {
			params = new String[0];
		}
		
	}

}
