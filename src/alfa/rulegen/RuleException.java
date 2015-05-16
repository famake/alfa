package alfa.rulegen;

import alfa.rules.RuleComponent;

public class RuleException extends Exception {

	private static final long serialVersionUID = 1L;

	private RuleComponent box;
	
	public RuleException() {
	}
	
	public RuleException(String message) {
		super(message);
	}

	public RuleException(String message, RuleComponent rc) {
		super(message);
		this.box = rc;
	}
	
}
