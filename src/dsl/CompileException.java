package dsl;

/**
 * Custom exception to track compile errors and their source code
 */
public class CompileException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	/** The error message */
	public String message;
	
	/** The source code (file and line) */
	public CodeLine code;
	
	public CompileException(String message, CodeLine code) {
		this.message = message;
		this.code = code;
	}

}
