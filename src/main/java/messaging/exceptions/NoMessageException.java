package messaging.exceptions;

public class NoMessageException extends Exception {
	public NoMessageException() {
		super();
	}
	
	public NoMessageException(String e) {
		super(e);
	}
}
