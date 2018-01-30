package dsl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
	
	String fileName;
	
	List<CodeLine> globalLines;
	Map<String,Function> functions;
	
	/**
	 * Remove non-needed spaces from a string. Spaces between tokens are necessary, like "var color".
	 * Non-needed spaces are things like "x,  y,  z".
	 * @param s the string to collapse
	 * @return the fixed string
	 */
	static String removeSpaces(String s) {
		s = s.trim();
		
		// TODO ... better job than this. Look for between-tokens explicitly instead
		// of hard-coded words. Handle quoted string.
		if(s.startsWith("var ")) {
			s = "var "+s.substring(4).replaceAll(" ", "");
		} else if(s.startsWith("goto ")) {
			s = "goto "+s.substring(5).replaceAll(" ", "");
		} else if(s.startsWith("function ")) {
			s = "function "+s.substring(9).replaceAll(" ", "");
		} else if(s.startsWith("return ")) {
			s = "return "+s.substring(7).replaceAll(" ", "");
		} else if(s.startsWith("include ")) {
			s = "include "+s.substring(8).replaceAll(" ", "");
		} else {
			s = s.replaceAll(" ", "");
		}
		
		//System.out.println(":"+s+":");
		
		return s;
	}

	public static Program load(String fileName) throws IOException {
		Program ret = new Program();
		
		List<String> rawLines = Files.readAllLines(Paths.get(fileName));
		
		int lineNumber = 0;
		
		ret.globalLines = new ArrayList<CodeLine>();
		ret.functions = new HashMap<String,Function>();
		
		Function currentFunction = null;
		
		for(String raw : rawLines) {
			++lineNumber;
			String s = raw.trim();
			int i = s.indexOf("//");
			if(i>=0) {
				s = s.substring(0, i).trim();
			}
			if(s.isEmpty()) continue;
			
			CodeLine c = new CodeLine();
			c.fileName = fileName;
			c.lineNumber = lineNumber;
			c.originalText = raw;
			
			s = removeSpaces(s);
			c.text = s;
			
			if(s.startsWith("function ")) {
				if(currentFunction!=null) {
					if(currentFunction.codeLines.size()==0 || 
							!currentFunction.codeLines.get(currentFunction.codeLines.size()-1).text.equals("}")) 
					{
						throw new RuntimeException("Function '"+currentFunction.name+"' must end with a '}'");
					}
					currentFunction.codeLines.remove(currentFunction.codeLines.size()-1);
				}
				
				s = s.substring(9); // Take off "function "
				i = s.indexOf("(");
				if(i<0) {
					throw new RuntimeException("Expected opening parenthesis: "+raw);
				}
				int j = s.indexOf(")",i);
				if(j<0) {
					throw new RuntimeException("Expected closing parenthesis: "+raw);
				}
				if(!s.substring(j+1).equals("{")) {
					throw new RuntimeException("Expected '{' after ')':"+raw);
				}
				currentFunction = new Function();
				currentFunction.name = s.substring(0,i);
				ret.functions.put(currentFunction.name, currentFunction);
				currentFunction.codeLines = new ArrayList<CodeLine>();
				currentFunction.arguments = new ArrayList<String>();
				String [] args = s.substring(i+1,j).split(",");
				for(String arg : args) {
					currentFunction.arguments.add(arg);
				}				
			} else if(currentFunction==null) {
				ret.globalLines.add(c);
			} else {
				currentFunction.codeLines.add(c);				
			}
			
			
			
		}
		
		if(currentFunction==null) {
			throw new RuntimeException("Program must have at least one function (init).");
		}
		
		if(currentFunction.codeLines.size()==0 || 
				!currentFunction.codeLines.get(currentFunction.codeLines.size()-1).text.equals("}")) 
		{
			throw new RuntimeException("Function '"+currentFunction.name+"' must end with a '}'");
		}
		currentFunction.codeLines.remove(currentFunction.codeLines.size()-1);
		
		return ret;
	}

}
