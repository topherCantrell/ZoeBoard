package dsl;

import java.util.ArrayList;
import java.util.List;

public class CodeLine {
	
	public CodeLine(Function function, String text) {
		this.function = function;
		this.fileName = "";
		this.lineNumber = 0;
		this.originalText = text;
		this.text = text;
		this.isLabel = false;
		this.changed = true;
	}
	
	public CodeLine(Function function, String text, boolean isLabel) {
		this.function = function;
		this.fileName = "";
		this.lineNumber = 0;
		this.originalText = text;
		this.text = text;
		this.isLabel = isLabel;
		this.changed = true;
	}
	
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
	
	public boolean changed;
	
	public List<Integer> data = new ArrayList<Integer>();
	
	public String toString() {
		String ret = "File:"+fileName+" Line:"+lineNumber;
		if(changed) {
			ret+=" mod:"+text;
		} else {
			ret+=" '"+originalText+"'";
		}
		return ret;
	}

}
