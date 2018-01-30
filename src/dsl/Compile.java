package dsl;

public class Compile {

	public static void main(String[] args) throws Exception {
		
		Program prog = Program.load("New.zoe");
		System.out.println(prog);

	}

}
