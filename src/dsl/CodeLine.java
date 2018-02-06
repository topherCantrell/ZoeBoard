package dsl;

import java.util.ArrayList;
import java.util.List;

public class CodeLine {
	
	public CodeLine(Function function, String fileName, int lineNumber, String text) {
		this.function = function;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.originalText = text;
		this.text = text;
	}

	public String fileName;
	public int lineNumber;
	public String originalText;
	
	public String text;
	
	public boolean isLabel;
	public int address;
	
	public Function function;
	
	public List<Integer> data = new ArrayList<Integer>();
	
	public String toString() {
		return "File:"+fileName+" Line:"+lineNumber+" '"+originalText+"'";
	}

}
