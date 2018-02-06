package dsl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Program {
	
	public String configStrip;
	public int configLength;
	public boolean configHasWhite;
	
	public String fileName;
	
	public List<String> vars;
	
	public List<CodeLine> globalLines;
	public List<Function> functions;
	
	public int findFunction(String name) {
		for(int x=0;x<functions.size();++x) {
			if(functions.get(x).name.equals(name)) {
				return x;
			}
		}
		return -1;
	}
	
	public int findGlobal(String name) {
		return vars.indexOf(name);
	}
	
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
		ret.functions = new ArrayList<Function>();
		
		Function currentFunction = null;
		List<String> currentFunctionLabels = new ArrayList<String>();
		
		for(String raw : rawLines) {
			++lineNumber;
			String s = raw.trim();
			int i = s.indexOf("//");
			if(i>=0) {
				s = s.substring(0, i).trim();
			}
			if(s.isEmpty()) continue;
			
			CodeLine c = new CodeLine(currentFunction,fileName,lineNumber,raw);			
			
			s = removeSpaces(s);
			c.text = s;
			
			if(s.startsWith("include ")) {
				String incName = s.substring(8);				
				Program incProg = Program.load(incName);
				ret.globalLines.addAll(incProg.globalLines);
				ret.functions.addAll(incProg.functions);
				continue;
			}
			
			i = s.indexOf(':');
			if(i>=0) {
				if(i!=s.length()-1) {
					throw new CompileException("Expected ':' to be last on the line",c);
				}
				if(currentFunction==null) {
					throw new CompileException("Global labels are not allowed",c);
				}
				s = s.substring(0,s.length()-1);
				if(currentFunctionLabels.contains(s)) {
					throw new CompileException("Label '"+s+"' has already been used",c);
				}
				currentFunctionLabels.add(s);
				c.text = s;
				c.isLabel = true;
			}
			
			if(s.startsWith("function ")) {
				if(currentFunction!=null) {
					if(currentFunction.codeLines.size()==0 || 
							!currentFunction.codeLines.get(currentFunction.codeLines.size()-1).text.equals("}")) 
					{
						throw new CompileException("Function must end with a '}'",c);
					}
					currentFunction.codeLines.remove(currentFunction.codeLines.size()-1);
				}
				currentFunctionLabels.clear();
				
				s = s.substring(9); // Take off "function "
				i = s.indexOf("(");
				if(i<0) {
					throw new CompileException("Expected opening parenthesis",c);
				}
				int j = s.indexOf(")",i);
				if(j<0) {
					throw new CompileException("Expected closing parenthesis",c);
				}
				if(!s.substring(j+1).equals("{")) {
					throw new CompileException("Expected '{' after ')'",c);
				}
				currentFunction = new Function(ret,s.substring(0,i));
				int ch = ret.findFunction(currentFunction.name);
				
				if(ch>=0) {
					throw new CompileException("Function name already exists",c);
				}	
				ret.functions.add(currentFunction);
				currentFunction.codeLines = new ArrayList<CodeLine>();
				currentFunction.arguments = new ArrayList<String>();
				String [] args = s.substring(i+1,j).split(",");
				for(String arg : args) {
					if(!arg.isEmpty()) {
						currentFunction.arguments.add(arg);
					}
				}				
			} else if(currentFunction==null) {
				ret.globalLines.add(c);
			} else {
				currentFunction.codeLines.add(c);				
			}			
			
		}
		
		if(currentFunction==null) {
			throw new CompileException("Program must have at least one function (init)",null);
		}
		
		if(currentFunction.codeLines.size()==0 || 
				!currentFunction.codeLines.get(currentFunction.codeLines.size()-1).text.equals("}")) 
		{
			throw new CompileException("Expected '}' to close last function",null);
		}
		currentFunction.codeLines.remove(currentFunction.codeLines.size()-1);
		
		return ret;
	}

}
