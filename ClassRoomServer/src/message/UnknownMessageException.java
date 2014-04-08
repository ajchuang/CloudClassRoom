package message;
public class UnknownMessageException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownMessageException(final String msg) {
		super(msg);
	}
}
