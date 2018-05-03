package dsl;

import java.io.PrintStream;

public class ToSpin {
	
	static int NUMSTACK    = 64; // Maybe these are tweakable one day
	static int NUMCOLORS   = 64; 
	static int NUMPATTERNS = 16;
	
	public static String toDigitHex(int v) {
		String ret = Integer.toString(v,16).toUpperCase();
		while(ret.length()<2) {
			ret = "0"+ret;
		}
		return "$"+ret;
	}	
	
	static void writeZeros(String pre, int num, int perLine, PrintStream ps) {
		for(int y=0;y<num/perLine;++y) {
			ps.print(pre);
			for(int x=0;x<perLine;++x) {
				ps.print("0");
				if(x!=perLine-1) ps.print(",");
			}
			ps.println();
		}
		if(num%perLine !=0) {
			ps.print(pre);
			for(int x=0;x<num%perLine;++x) {
				ps.print("0");
				if(x!=num%perLine-1) ps.print(",");
			}
			ps.println();
		}
	}
	
	public static void writeHeader(Program prog, PrintStream os) {
		
		String iopin;
		switch(prog.configStrip) {
		case "D1":
			iopin = "%00000000_00000000_10000000_00000000"; // P15
			break;
		case "D2":
			iopin = "%00000000_00000000_01000000_00000000"; // P14
			break;
		case "D3":
			iopin = "%00000000_00000000_00100000_00000000"; // P13
			break;
		case "D4":
			iopin = "%00000000_00000000_00010000_00000000"; // P12
			break;
		default:
			throw new RuntimeException("Unknown pin "+prog.configStrip);
		}
		
		int bitsToSend = 24;
		if(prog.configHasWhite) {
			bitsToSend = 32;
		}
		
		int eventTabSize = 1;
		for(Function fun : prog.functions) {
			if(fun.isEvent) {
				int sz = fun.name.length();
				if(sz>31) throw new RuntimeException("Event name won't fit in buffer");
				eventTabSize = eventTabSize + sz + 3;				
			}			
		}
		
		Function init = prog.functions.get(prog.findFunction("init"));
		int pcofs = init.codeLines.get(0).address;
		
		os.println("CON");
		os.println("");
		os.println("  IOPIN   = "+iopin);
		os.println("  NUMPIXELS      = "+prog.configLength);  
		os.println("  BITSTOSEND     = "+bitsToSend);  
		os.println("  NUMGLOBALS     = "+prog.vars.size());  
		os.println("  TABSIZE        = "+eventTabSize);
		os.println("");
		os.println("  OFS_EVENTINPUT = 0"); 
		os.println("  OFS_PALETTE    = OFS_EVENTINPUT + 32"); 
		os.println("  OFS_PATTERNS   = OFS_PALETTE    + "+NUMCOLORS+"*4");  
		os.println("  OFS_STACK      = OFS_PATTERNS   + "+NUMPATTERNS+"*2");
		os.println("  OFS_VARIABLES  = OFS_STACK      + "+NUMSTACK+"*2");
		os.println("  OFS_PIXBUF     = OFS_VARIABLES  + NUMGLOBALS*2");  
		os.println("  OFS_EVENTTAB   = OFS_PIXBUF     + NUMPIXELS");
		os.println("  OFS_CODE       = OFS_EVENTTAB   + TABSIZE");
		os.println("");
		os.println("  OFS_PC         = OFS_CODE + "+pcofs);
		os.println("" );
		os.println("pub getProgram");  
		os.println("  return @zoeProgram");  
		os.println("");
				
		os.println("DAT" );
		os.println("zoeProgram");
		
		os.println("'eventInput");
		writeZeros("  byte ",32,32,os);
		os.println();		
		
		os.println("'palette  ' "+NUMCOLORS+" colors)  ");
		writeZeros("  long ",NUMCOLORS,16,os);		
		os.println();
		
		os.println("'patterns ' "+NUMPATTERNS+" pointers");
		writeZeros("  word ", NUMPATTERNS, 64, os);
		os.println();
		
		os.println("'callstack ' "+NUMSTACK+" slots");
		writeZeros("  word ", NUMSTACK, 64, os);
		os.println();
		
		os.println("'variables ' "+prog.vars.size()+" variables");
		writeZeros("  word ", prog.vars.size(), 64, os);
		os.println();
		
		os.println("'pixelbuffer ' "+prog.configLength+" pixels");
		writeZeros("  byte ", prog.configLength, 64, os);
		os.println();
		
		os.println("'eventTable (offsets from start of code)");
		for(Function fun : prog.functions) {
			if(fun.isEvent) {
				int adr = fun.codeLines.get(0).address;
				os.println("  byte \""+fun.name+"\", 0, "+toDigitHex(adr/256)+", "+toDigitHex(adr&255)); 
			}
		}
		os.println("  byte 0");		
		
	}
	
	public static void toSpin(Program prog, PrintStream ps) {
		
		writeHeader(prog,ps);
		
		ps.println();
		ps.println("' Code");
		ps.println();
		
		for(Function fun : prog.functions) {
			ps.println("' ## Function "+fun.name);
			for(CodeLine c : fun.codeLines) {
				String out = "";
				if(c.isLabel) {
					ps.println("' "+c.text+":");
					continue;
				}
				if(c.data.size()>0) {
					out = out +"    byte ";
					for(int x=0;x<c.data.size();++x) {
						out = out + toDigitHex(c.data.get(x))+",";
					}
					out = out.substring(0, out.length()-1);
					while(out.length()<32) {
						out = out+" ";
					}				
				}
				if(c.changed) {
					out = out + " '# "+c.text;
				} else {
					out = out + " ' "+c.originalText;
				}
				ps.println(out);				
			}
			ps.println();
		}
		
	}

}
