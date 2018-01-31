package dsl;

public class CodeLine {

	public String fileName;
	public int lineNumber;
	public String originalText;
	
	public String text;
	
	public String toString() {
		return "File:"+fileName+" Line:"+lineNumber+" '"+originalText+"'";
	}

}
