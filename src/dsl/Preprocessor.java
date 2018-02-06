package dsl;

public class Preprocessor {
	
	Program prog;
	
	public Preprocessor(Program prog) {
		this.prog = prog;
	}
	
	public void preprocess() {
		// TODO
		// Add RESLOCALs
		// Make sure functions have a RETURN
		// Change "a = function()" to "function();a=__RETVAL__"
		// Convert complex math expressions to a series of simple math calls
		// Convert DO and WHILE to pure IF (complex logic expressions)
		// Convert ELSE-IF to nested IF
		// Convert IF/ELSE to pure IF-THEN
	}

}
