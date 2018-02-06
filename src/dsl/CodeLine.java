package dsl;

import java.util.ArrayList;
import java.util.List;

public class CodeLine {

	public String fileName;
	public int lineNumber;
	public String originalText;
	
	public String text;
	
	public boolean isLabel;
	public int address;
	
	List<Integer> data = new ArrayList<Integer>();
	
	public String toString() {
		return "File:"+fileName+" Line:"+lineNumber+" '"+originalText+"'";
	}

}
