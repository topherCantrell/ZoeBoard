package dsl;

public class ToSpin {
	
	public static String toDigitHex(int v) {
		String ret = Integer.toString(v,16).toUpperCase();
		while(ret.length()<2) {
			ret = "0"+ret;
		}
		return "$"+ret;
	}
	
	public static void toSpin(Program prog) {
		
		for(Function fun : prog.functions) {
			System.out.println("' ## Function "+fun.name);
			for(CodeLine c : fun.codeLines) {
				String out = "";
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
				out = out + "' "+c.originalText;
				System.out.println(out);				
			}
			System.out.println();
		}
		
	}

}
